# 附魔迁移批次记录 — Redstone-chan's Enchantment Expansion

各批次附魔迁移（旧逐附魔 handler / 手写 JSON → 组件 + 家族分发器）已验证的 API 事实与有意为之的行为变更。
组件体系与架构见 `enchantment-components.md`；本文件中的纠错教训见 `../ai/gotchas.md`。

## swords/swords_and_bow 批次（2026-09 迁移验证，boons→decapitation 共 11 个全部完成）

已验证（compileJava / build / runData 全通过，生成 JSON 逐字段核对）的 API 事实：

- `Enchantment` 是 record，`effects()`（DataComponentMap）公开——单值（复合）组件直接
  `ench.value().effects().get(type)` 读取（已封装为 `EnchantmentUtil.specialValue`；
  `EnchantmentHelper.has(stack, DataComponentType)` 判存在，api-sources EnchantmentHelper.java:460）。
- `EnchantmentHelper.getEnchantmentLevel(holder, entity)`（EnchantmentHelper.java:288）=
  在该附魔 slots 对应装备上取**最大等级**（对仅 mainhand 槽的附魔等价于检查主手）。
- `EnchantmentValueEffect` 两个实现的语义（求值入口 `Enchantment.modifyItemFilteredCount`，Enchantment.java:388，
  从 0 起的 MutableFloat 逐 effect `setValue(process(...))`）：
  `AddValue.process = value + value.calculate(level)`（AddValue.java:16）→ `AddValue(perLevel(x))` 求值 = x×级；
  `SetValue.process = value.calculate(level)`（SetValue.java:15）→ 恒定值。序列化分别为 `minecraft:add`、`minecraft:set`。
- `EquipmentSlotGroup.OFFHAND` 存在（gambler 双槽已用）。
- `LivingDamageEvent.Pre`：`getOriginalDamage/getNewDamage/setNewDamage`（载体 DamageContainer，可变）；
  `LivingDamageEvent.Post` 是**不可变快照**：`getNewDamage()` = 本次实际扣血量、`getOriginalDamage()` = hurt() 原始伤害
  （LivingDamageEvent.java 源码核对）。
- `LivingEntity.getHealth()`（:1126）/ `getMaxHealth()`（:1822，final float）/ `heal(float)`（:1117）；
  `isCrouching()` 定义在 `Entity`（Entity.java:2367）。
- POST_ATTACK 消费链（源码核对）：`Player.attack → EnchantmentHelper.doPostAttackEffects →
  doPostAttackEffectsWithItemSource`；武器附魔（enchanted=ATTACKER）走 itemSource 路径；
  `affected` 解析（Enchantment.doPostAttack 静态方法）：ATTACKER→damageSource.getEntity()、
  DAMAGING_ENTITY→getDirectEntity()、VICTIM→被击实体；effect.apply 的 entity 参数=受效果实体、origin=其 position。
  Mob 持械攻击（Mob.java:1511）同样触发 POST_ATTACK。
- 头颅查找：`BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath()` + `BuiltInRegistries.ITEM
  .containsKey/get/keySet`（`ItemStack(Item)` 构造器）——按路径全注册表遍历支持模组头颅。

### 行为变更备忘（swords 批次，有意为之）

- **旧 11 个 handler 是独立订阅者**：执行顺序取决于注册顺序，且"以 original 为基数"的附魔互相
  覆盖（实际只有一个生效）。新分发器按**固定顺序**执行、每条公式原样保留（基数语义不动）：
  Pre `赌徒 → 伏击 → 背刺 → 均衡器`，Post `处决 → 生命吸取`，Drops `屠夫 → 斩首`（处决原在 Pre 末段，2026-09-24 移入 Post，见下方补记）。
- 攻击者解析照抄各旧 handler：伏击/均衡器 = `getDirectEntity() instanceof Player`（投射物不触发）；
  赌徒/处决/背刺/生命吸取/屠夫/斩首 = `getEntity() instanceof LivingEntity`。
  **用户决定**：不添加 Player 限制——接受持械 Mob 攻击者也触发（原版 POST_ATTACK 对 Mob 攻击同样生效）。
- life_steal **修复 ID bug**：旧 handler 引用不存在的 `leeching`（真 ID `life_steal`），getHolder 恒 null →
  从未生效；修复后从"无效果"变为生效，数值基数由 original 改为实际伤害（Post getNewDamage）×10%。
- life_steal **数值变更（2026-09-20）**：`life_steal_ratio` 由 `SetValue(LevelBasedValue.constant(0.1F))`
  改为 `SetValue(LevelBasedValue.perLevel(0.1F))` → 回血比例 10%×级（Lv5 = 50%）。此前 max_level 5
  但 `constant` 使 5 个等级全部恒为 10%（等级实际无效）；改后需重跑 runData 刷新生成 JSON。
- 处决照旧实现语义：`setNewDamage(目标当前生命值)`（旧注释写"设为 0"但实现是设为当前血量，照实现）。
  - **补记（2026-09-24）**：处决改为在 `LivingDamageEvent.Post` 里清血（门槛 = 这一刀结算后血量 <25%，`setHealth(0)`），
    Pre 顺序变为 `赌徒 → 伏击 → 背刺 → 均衡器`、Post 顺序 `处决 → 生命吸取`；语义由"两刀"变"一击补刀"，
    吸收不再能救，图腾/死亡消息/经验/掉落仍走原版。详见 `../design/enchantment-authoring.md` 的 P1-8。
- 伏击每玩家状态（~~Map<UUID,Boolean>~~ → 见下方补记：非潜行攻击置位/潜行首击 ×(1+0.2×级) 后置位/
  PlayerTickEvent.Post 非潜行重置）迁入分发器；组件求值需 ServerLevel，伏击/背刺/均衡器/生命吸取/屠夫/斩首均只服务端执行
  （旧版伏击在双侧维护 Map 副本，结果行为不变）。
  - **补记（2026-09-23 后续批）**：那个静态 `Map<UUID, Boolean>` 是本家族最后一个 per-player 静态容器，
    已换成 `ModAttachments.AMBUSH_HAS_ATTACKED` 附件（`init/ModAttachments.java`）。除上述语义外新增的行为差异：
    潜行中下线时旧实现会把 `true` 留在 Map 里，该玩家重进后第一次潜行攻击被误判为"已攻击过"而**丢掉加成**；
    附件随实体生灭，重进即复位（顺带：跨维度/重生也用新实体，同样复位）。数值与判定条件未动。
- 背刺基数是 `getNewDamage()`（非 original，与其他附魔叠加方式不同，公式原样保留）。

## unbreaking 家族批次（2026-09 迁移验证，advanced_unbreaking/sacrifice/indestructible/sturdy/preservation 共 5 个）

已验证（compileJava / build / runData 全通过，生成 JSON 逐字段核对）的 API 事实：

- `LevelBasedValue.perLevel(base, perLevelAfterFirst)` = `Linear(base, perLevelAboveFirst)`，
  `calculate = base + perLevelAfterFirst × (级 - 1)`（LevelBasedValue.java:38）。
  sacrifice 修复量 `floor(1.0 + 0.5×(级-1))` 用 `SetValue(LevelBasedValue.perLevel(1.0F, 0.5F))` 表达。
- `LevelBasedValue.Fraction(numerator, denominator)`（LevelBasedValue.java:98，分子分母都是 LevelBasedValue，
  序列化为 `minecraft:fraction`，常量序列化成裸数）。
- `RemoveBinomial(LevelBasedValue chance)`（RemoveBinomial.java）：对每一"点"耐久消耗按 chance 判定是否不消耗
  （advanced_unbreaking 的 `minecraft:item_damage` + `remove_binomial`，chance = fraction 4/5）。
- `EquipmentSlotGroup.ANY`（序列化 `"any"`，EquipmentSlotGroup.java:13）。
- `#c:enchantables`：provider 里 `TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "enchantables"))`，
  datagen `items.getOrThrow(...)` 直接解析（NeoForge 公共标签，runData 实测）。
- `Enchantment.Builder.exclusiveWith(HolderSet<Enchantment>)`（Enchantment.java:561）+ 手写 exclusive_set tag 的
  `enchantments.getOrThrow(...)` 解析（批次 1 模式复用，本次 indestructible/unbreaking 两个标签同样通过）。
- Mixin 内读附魔组件与事件侧一致：`EnchantmentHelper.has(stack, 标记组件)` 替代"registry 按名 getHolder + getLevel"
  （PreservationMixin 已改造，mixin 注册配置未动）。
- unbreaking 家族组件全部是"标记/单值"形态：advanced_unbreaking 纯原版组件、sacrifice 数值、其余 3 个标记。

### unbreaking 批次行为变更备忘（有意为之）

- 旧 4 个 handler → 4 个按钩子分发器：`UnbreakingDamageEvents`（Pre 坚固免疫 + Post 牺牲自修）、
  `UnbreakingEquipmentEvents`（坚不可摧挂/卸 UNBREAKABLE）、`UnbreakingItemEntityEvents`（坚固掉落物
  EntityTick/爆炸/闪电）、`UnbreakingPlayerEvents`（保全 tick 特效/阻止挖掘/tooltip）。
- 新增效果只在服务端执行（sacrifice 旧版双侧写 setDamageValue，客户端为无效写，结果行为不变）。
- **已修复（2026-09 怪癖修复批次，见本文件「怪癖修复批次」小节）**：
  - ~~indestructible 的 else 分支剥其它来源的 UNBREAKABLE~~ → 标记组件精确清理；
  - ~~sturdy 的 EntityTick else 分支剥其它来源的 FIRE_RESISTANT~~（原会剥掉下界合金自带防火）→ 同上；
  - ~~sturdy 的 Pre 段本体整次免疫~~ → 缩小为"本体照常受伤，仅装备耐久不受损"（快照附件恢复）。

## 旧式 handler 全量迁移批次（2026-09，potential_conversion→spirit 共 35 个全部完成）

至此 `event/` 目录下**不再有任何逐附魔 handler**，全部为家族分发器（每家族按钩子建类，
`@EventBusSubscriber(modid = MOD_ID)` + 私有构造 + 固定顺序段）。本批覆盖：potential_conversion、
boltbringer、echoes_battle、sea_breeze、searing、curse_of_rust、curse_of_water_source、daynight_cycle、
revive_ward、snipe、volt、fishing 3（angler/conductive_line/tide_sense）、shear 4（endless_wool/
experience_shear/harvest_echo/shepherd）、armor_head 4（adaptive/against_all_odds/anti_camouflage/
desperate_counter）、armor_foot 4（crop_dance/flame_walker/pegasus/wave_walker）、armor_chest 2
（berserk/bulletproof）、armor_leg 2（invisibility_cloak/tactical_knee）、armor_wolf 3（carrion_eater/
pack_leader/tracker）、armor_horse 2（pasture/spirit）。

已验证（每附魔独立提交，compileJava / runData / build 全通过，生成 JSON 逐字段核对）的 API 事实：

- `EnchantmentUtil.itemValue(ServerLevel, ItemStack, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>)`
  签名要求 **ServerLevel**（不是 Level）——含数值求值的段一律 `instanceof ServerLevel` guard 后再求值。
- 等级信息全部进组件后，事件侧不再需要 registry `getHolder` + `getLevel`：
  `EnchantmentHelper.has(stack, 标记组件)` 判存在，`itemValue` 取每级数值。
  `EnchantmentUtil.levelOn(EnchantmentUtil.holder(RegistryAccess, ResourceKey), stack)` 保留给
  "需要确切等级"的场景（本批 35 个均未用到）。
- 原版 exclusive_set 复刻：`Enchantment.Builder.exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.DEPTH_STRIDER)))`
  （flame_walker/wave_walker；provider 需 import `net.minecraft.core.HolderSet` + `Enchantments`），
  序列化输出 `"exclusive_set": "minecraft:depth_strider"`。
- 原版组件复刻：tide_sense 直接 `.withEffect(EnchantmentEffectComponents.FISHING_TIME_REDUCTION, new AddValue(perLevel(10.0F)))`
  （10×级 每级减 10t 钓鱼等待），datagen 与原版 JSON 同构。
- `TagKey` 在 provider 里必须 `items.getOrThrow(tagKey)` 解析（sea_breeze 踩坑：把 TagKey 直接传给
  `definition(...)` 编译错）。
- `EquipmentSlotGroup.LEGS / BODY / FEET` 均存在（armor_leg/armor_wolf/armor_horse/armor_foot 已用）；
  `#redstone_enchants:armors_head/armors_chest/armors_leg/wolf_armor/horse_armor` 标签照抄手写 JSON。
- 钩子签名已用例：`LivingFallEvent.setCanceled(true)`（tactical_knee）、`LivingDeathEvent`（carrion_eater）、
  `LivingEquipmentChangeEvent.getSlot()`（armor_head/armor_horse 摘除清理）、
  `AbstractHorse.isSaddled()` + BODY 槽（armor_horse）、
  `AttributeInstance.removeModifier(ResourceLocation)` / `addPermanentModifier(modifier)`（1.21.1 按
  ResourceLocation 移除的重载存在，spirit 已编译验证）、`LivingIncomingDamageEvent.setCanceled()`
  （bulletproof）、`EntityTickEvent.Post` 双用途（玩家 tick 与马 tick）。
- `LevelBasedValue.perLevel(base, per)` 系列照抄汇总：crop_dance `perLevel(0.2,0.1)`=0.1+0.1×级、
  bulletproof `perLevel(0.5,0.25)`、berserk `perLevel(0.03,0)`（等价 perLevel(0.03F)）——
  **旧"×级"公式必须写成 `perLevel(v)`（base=v, per=v）而不是 `perLevel(0,v)`**，二者序列化不同但语义同；
  pasture/heal 类 `heal(级×0.5)` 写 `SetValue(perLevel(0.5F))`。

### 行为变更备忘（本批，有意为之 / 怪癖原样保留）

- 逐附魔 handler 全删；每家族分发器固定顺序（旧版为多个独立订阅者，顺序未定义）：
  armor_head tick `adaptive → against_all_odds → anti_camouflage → desperate_counter`，
  armor_foot tick `crop_dance → flame_walker → pegasus → wave_walker`，
  sword Pre `赌徒 → 伏击 → 背刺 → 均衡器`（处决 2026-09-24 移入 Post），armor_wolf Pre `pack_leader → tracker`，
  armor_horse tick `pasture → spirit`。
- **仅服务端执行**（旧版双侧跑，行为不变）：所有 itemValue 段（armor 全家族、berserk/bulletproof、
  snipe/volt、fishing 3、shear 4、sea_breeze/searing、potential_conversion、echoes_battle、~~daynight_cycle~~、
  revive_ward）。纯标记/纯事件段保真双侧：tactical_knee（客户端取消本地坠落预测）、
  invisibility_cloak（本地效果预览）。
  > ~~daynight_cycle 属此列~~ → 2026-09-23 订正：当时 `ArmorEntityTickEvents` **并没有侧判断**
  > （只判了 `Player`），客户端确实整套跑；侧判断是 2026-09-23 那批才补上的。
- 怪癖清单（迁移时原样照抄，2026-09 修复批次处理结果见本文件「怪癖修复批次」小节）：
  - ~~armor_foot `LAST_SNEAKING` 泄漏~~ → 已修（EntityLeaveLevelEvent 清理）；
  - ~~invisibility_cloak 移除隐身不分来源~~ → 已修（附件标记精确移除）；
  - ~~pack_leader 旧注释写"每级每只狼+5%"但常量是 **0.5（=50%）**，**行为以 50% 为准保持原样**
    （用户决定：数值与注释均不动，矛盾已知）~~ → 2026-09-20 用户改判：**数值不动，注释改为 50%**
    （`ArmorWolfDamageEvents` 计算处 / `ModEnchantmentEffectComponents` 组件声明处，详见
    `enchantment-pack-leader.md`）；
  - spirit 用永久修饰符（摘除马铠靠 LivingEquipmentChangeEvent 清理，modifier id `spirit_speed` 照抄）；
    ~~每 tick 先 `removeModifier(spirit_speed)` 再加~~ → 2026-09-23 改为值变了才写
    （`AttributeUtil.applyPermanent`）；
  - armor_head 的 AAO_DAMAGE/AAO_ARMOR attribute modifier id 照抄；~~against_all_odds 不过滤
    死亡生物~~ → 已修（isAlive 谓词）；
  - ~~revive_ward 判死时点~~（迁移时实际用的是 Pre + `getOriginalDamage()`，先前沉淀误记为
    getNewDamage）→ 已修（改 Post 实际扣血后判定）；
  - experience_shear/harvest_echo 的"检查目标是否玩家/已被剪"等内嵌条件改为组件存在性读取后语义不变；
  - boltbringer/echoes_battle 的数值与状态字段照抄（详见各自提交）。
- **bug 修复（已注明）**：life_steal ID `leeching`→`life_steal`（swords 批次）；本批无新修复。
- 组件命名沿用 `组件名 = 效果语义`：数值组件带 `_bonus/_heal/_chance/_penalty` 等后缀，标记组件
  （unit）用附魔名本身（`CROP_DANCE/FLAME_WALKER/PEGASUS/WAVE_WALKER/INVISIBILITY_CLOAK/TACTICAL_KNEE`
  等），datagen 侧与事件侧一一对应。

## 怪癖修复批次（2026-09，6 项经用户逐项确认的行为修复）

提交序列（每项独立提交、build 验证）：lastSneakingMap 泄漏清理、against_all_odds isAlive 过滤、
invisibility_cloak 附件标记精确移除、sturdy/indestructible 标记组件精确清理、sturdy 免疫缩小为
仅装备耐久、revive_ward 改实际扣血判死。~~**pack_leader 0.5 注释矛盾经用户决定保持原样。**~~
（2026-09-20 改判：数值不动，注释改为 50%，见上节怪癖清单。）

已验证的新 API 事实：

- 实体附件：`DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID)`，
  `AttachmentType.builder(Supplier<T>).build()` 不带 serialize = 纯 transient（不存档、不同步）；
  `IAttachmentHolder`（Entity 实现）方法 `setData/hasData/removeData(AttachmentType<T>)`（api-sources
  IAttachmentHolder.java:24/39/90/106）。
- 物品标记组件：`DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID)`（注意与
  附魔效果组件的 `ENCHANTMENT_EFFECT_COMPONENT_TYPE` 不同注册表）；unit 标记写法照抄原版
  FIRE_RESISTANT：`DataComponentType.<Unit>builder().persistent(Unit.CODEC)
  .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build()`（DataComponents.java:120）。
- `Item.components()` 返回物品默认组件 `DataComponentMap`（Item.java:106），`has(type)` 可判
  "物品天然自带的组件"——区分天生防火与运行时写入用。
- **1.21.1 NeoForge 没有装备耐久事件**（living 包只有 LivingEquipmentChangeEvent）——
  "仅装备不损耗"只能用 Pre 快照 + Post 恢复（transient 附件传状态）实现。
- `LivingDamageEvent.Post` 触发点在 `LivingEntity.actuallyHurt` 末尾（LivingEntity.java:1805，
  `CommonHooks.onLivingDamagePost`），此时血量已扣但 **`die()` 尚未调用**（die 在 hurt() 的
  isDeadOrDying 检查处）→ Post 里 `setHealth(0.5F)` 可阻止死亡（revive_ward 守护的合法实现点）。
- `EntityLeaveLevelEvent`（`net.neoforged.neoforge.event.entity`，extends EntityEvent）可用于
  按实体清理内存缓存；换维度也会触发（对"重新进入时重算"类缓存语义无损）。
- `LivingDamageEvent.Pre` 判死用 `getOriginalDamage()` 与 `getNewDamage()` 的取舍：getNewDamage
  可读"当前累积值"但晚于本事件的修改不算；需要严格"实际扣血后"语义必须用 Post。

## 伤害事件优先级定序批次（2026-09-20）

此前 6 个 `LivingDamageEvent.Pre` 订阅者都未设 priority，跨家族顺序取决于注册顺序（未定义），
导致"以 `getNewDamage()` 连乘"的段可能被"以 `getOriginalDamage()` 覆盖"的段整段抹掉。
现按"覆盖段先、连乘段后"显式定序（`@SubscribeEvent(priority = ...)`）：

| 优先级 | 订阅者 | 段 | 基数 |
|---|---|---|---|
| HIGHEST | `UnbreakingDamageEvents` | 坚固（耐久快照，不改伤害） | — |
| HIGH | `MaceLivingDamageEvents` | 势能转化 | getNewDamage（连乘） |
| NORMAL | `BowDamageEvents` | 狙击 / 伏特 | getNewDamage（连乘） |
| LOW | `SwordLivingDamageEvents` | 赌徒/伏击/背刺/均衡器 | getNewDamage（连乘） |
| LOW | `SwordLivingDamageEvents` | ~~处决~~ | ~~绝对值覆盖（设为目标当前生命）~~ → 2026-09-24 移入 Post（`setHealth(0)`），不再参与 Pre 定序，见下补记 |
| LOWEST | `ArmorChestLivingDamageEvents` | 狂战士 | getNewDamage（连乘） |
| LOWEST | `ArmorWolfDamageEvents` | 狼群领袖 | getNewDamage（连乘） |

> 上表是**基数统一之后**的状态；武器族原先以 original 为基数的段已在同一天全部改为连乘，见下一节。

- **优先级方向**：高优先级先执行（官方 `Documentation` 仓库 capabilities.md 注释
  "use HIGH priority to register before NeoForge!"，HIGHEST 最早、LOWEST 最晚）。
- **同档不冲突**：胸甲与狼铠同为 LOWEST，但判定互斥（狂战士要求 `getDirectEntity()` 是
  Player、狼群领袖要求是 Wolf），同档顺序不影响结果。
- **行为变化**：狂战士 / 狼群领袖现在**必定**吃到武器段的加成结果（此前取决于注册顺序）。
- 本批只定序 Pre；Post 侧（`life_steal` / `revive_ward` / `sea_breeze` / `TeleportSwapEvents` /
  `UnbreakingDamageEvents` Post）不改伤害数值，顺序问题留待观察。
  > **补记（2026-09-24）**：Post 侧顺序问题已处理一次——处决移入 Post 后与 `revive_ward`（`ArmorDamageEvents`）
  > 同挂 `LivingDamageEvent.Post`，跨类顺序原本未定义。定为处决 `HIGHEST`（先执行，把血量归零）、
  > `revive_ward` `LOWEST`（最后执行）→ **重生护盾救得下被处决的目标**（护盾看到濒死状态后拉回 0.5 血并消耗附魔）。
  > 注：初版把护盾标成 `HIGHEST`——那是**最早**执行（见本节优先级方向），护盾先跑、处决后跑再归零，结果与预期相反。

## Pre 伤害基数统一批次（2026-09-20）

武器族 5 段从"以 `getOriginalDamage()` 覆盖"改为"以 `getNewDamage()` 连乘"：赌徒（`SwordLivingDamageEvents`）、
伏击、均衡器、狙击 / 伏特（`BowDamageEvents`）、势能转化（`MaceLivingDamageEvents`）。处决当时保持绝对值语义
（设为目标当前生命），与基数无关（2026-09-24 改为 Post 清血，见上文补记）。

- **为什么必须改**：`originalDamage` 是 `hurt()` 收到的原始伤害（`DamageContainer` 创建时定格），
  而 Pre 之前已算进 `newDamage` 的东西会被覆盖式写入整段抹掉——神化系的暴击正是如此
  （Apothic Attributes `AttributeEvents#apothCriticalStrike` 在 `LivingIncomingDamageEvent` 里
  `setAmount`，只写 newDamage），护甲/抗性/保护减伤同理。实测：100% 暴击、暴击伤害 400%、
  无附魔下界合金剑 32.7，带赌徒一 roll 只剩 8×1.4=11.2 / 8×0.8=6.4（8 = 剑攻击力）。
- **数值影响（相对旧版会变强）**：原来同族内"以 original 为基数"的段互相覆盖、只生效一段，
  改连乘后**会叠加**——同一把剑的赌徒×伏击×背刺×均衡器、同一张弓的狙击×伏特。
  与狂战士/狼群领袖的连乘段相乘也保持原意（那些段本来就是连乘）。
- **上游语义偏离**：这几处原注释写着"公式原样"（照搬 fork 前上游行为），本批有意偏离。

## tick 与属性写入收敛批次（2026-09-23）

审查清单（`../design/enchantment-authoring.md` §4）的 P0-1 与 P1-1/2/3/6 落地，外加清单漏记的同类项：

- **属性修饰符改为"值变了才写"**：21.1.219 的 `AttributeInstance.addPermanentModifier` 走
  `addModifier`（`putIfAbsent`，同 id 重复即抛 `IllegalArgumentException`），所以每 tick 改值只能
  "先 remove 再加"——而每次 add 都 `setDirty()`，触发属性重算并向客户端刷同步包。新增
  `util/AttributeUtil.applyPermanent(entity, attribute, id, amount, op)`：`amount` 传 `null` 表示
  "不该有该修饰符"（存在则移除），数值/运算都相同则完全不碰。落点三处：`ArmorEntityTickEvents`
  （昼夜流转）、`ArmorHorseTickEvents` 精神段、`ArmorHeadTickEvents` 的以寡敌众/绝境逆袭。
- **节流统一**：新增 `util/TickUtil`（`ONE_SECOND = 20`、`isDue(entity, period)`，按实体 `tickCount`
  对齐，不需要自存时间戳）。`ArmorEntityTickEvents`（同时补上服务端侧判断）、
  `ArmorHeadTickEvents.againstAllOdds`（原先每 tick 一次 8 格 AABB 实体查询）改为每秒一次；
  `ArmorHorseTickEvents.pasture` 的 `tickCount % 20` 换成同一写法。`CurseTickEvents` 顺带删掉
  `LAST_DAMAGE_TIME` / `LAST_EFFECT_TIME` 两个无清理路径的静态 Map（P0-4）。
- **每件 5% 的昼夜流转**：`BONUS_PER_LEVEL = 0.05` 仍是代码常量（附魔 JSON 只有 `daynight_cycle`
  标记组件，没有数值组件），已登记进 `enchantments.md` 的机制备注。
- **等级读取入口**：`EnchantmentUtil` 增 `levelOf(registryAccess, stack, key)`，替换 7 处手写
  `holder(...) + levelOn(...)`；`levelOn` 从 `EnchantmentHelper.getItemEnchantmentLevel` 换成
  `ItemStack#getEnchantmentLevel`（NeoForge 已弃用前者，其实现就是委托到后者，含
  `GetEnchantmentLevelEvent` 钩子）。
- **随机源**：庄稼舞的逐格判定从类内静态 `java.util.Random` 改为 `serverLevel.random`。
- **负面光环不再作用于玩家**：`AreaMobEffectEffect.Target` 增 `OTHERS_NON_PLAYER`（JSON
  `"others_non_player"`），中毒/缓慢/虚弱/凋零/寄生五个光环改用它（寄生已核对为 `HARMFUL` 类）；
  发光的 `others`、增益类的 `all` 不变。**行为变更**：联机时这些光环不再波及路过的玩家。
- **静态状态全部改走附件**（P0-2/P0-3/P0-4 收尾）：
  - 保全的"上一次耐久"从静态 `Map<String,Integer>`（key = UUID + identityHashCode(stack)、无删除路径）
    改为按玩家的 `ModAttachments.PRESERVATION_LAST_DAMAGE`；每 tick 用本 tick 见到的物品
    `retainAll` 裁剪一次，条目数随背包大小有界。**行为变更**：该段补了服务端侧判断，
    破损音效/粒子不再双侧各放一遍。
  - 庄稼舞的潜行状态从 `ConcurrentHashMap<Player, Boolean>`（强引用 Player）改为
    `ModAttachments.CROP_DANCE_SNEAKING`；`EntityLeaveLevelEvent` 清理随之删除（不再需要）。
  - 诅咒的两个时间戳 Map 由 `TickUtil.isDue` 取代（见上一条）。
- **effect 类合并**：`RandomBeneficialMobEffect` / `RandomHarmfulMobEffect` 合并为
  `RandomMobEffectEffect(LevelBasedValue chance, Pool pool)`：注册名从
  `random_beneficial_mob_effect` / `random_harmful_mob_effect` 改为 `random_mob_effect`，
  JSON 多一个 `"pool": "beneficial" | "harmful"`；候选池过滤（`isBeneficial()` 的正反 +
  不祥之兆 / 试炼之兆黑名单）搬进 `Pool` 枚举。**旧注册名不再注册**，引用旧名的第三方数据包会失效
  （本仓库生成的 JSON 已同步重生成，效果与数值不变）。
  另外两项经核实**不合并**：`IgniteAreaEffect`（点燃命中点周围的空气方块）与 `AreaIgniteEffect`
  （点燃范围内生物）功能不同；`SummonItemEffect`（无任何附魔使用的模板类）与 `GiveItemEffect`
  （检索，箭直接入玩家背包）语义不同。
- **鱼钩去重状态**：导电鱼线的静态 `Set<UUID>` 改为挂在鱼钩上的附件
  `ModAttachments.CONDUCTIVE_LINE_STRUCK`（松钩即复位），顺带修掉"勾住状态下鱼钩消失导致该生物
  再也劈不到"与"任意空钩 tick 把全局去重集合整个清空"两个毛病。行为差异只剩一条：
  两条鱼钩同时勾住同一生物时，旧版只劈 1 次、新版各劈 1 次。
- **范围效果的作用对象提成共用枚举**：`AreaMobEffectEffect.Target` → 顶层 `enchantment/effect/AreaTarget`
  （含 `includes(source, candidate)` 判定），`AreaIgniteEffect` 也带上 `target` 字段
  （可选，默认 `others`＝除触发者自己，保持既有行为）。燃烧光环（aura_burning）声明
  `others_non_player`：原 mcfunction 的 `data merge entity {Fire:...}` 对玩家静默失败，
  Java 化时用 `igniteForTicks` 才意外变成对玩家也生效，本次是回到原行为。
- **范围索敌收进 `util/TargetingUtil`**：`nearestNonPlayers(level, source, minRange, maxRange, limit)`
  与 `nearestNonPlayer(...)` 统一"排除玩家 + 按距离取前 N"，`ChainBindEffect` / `RicochetEffect` /
  `SnowballBurstEffect` 三处改用（语义逐条核对过：索敌范围、最小距离、数量上限都不变）。
- **`Block.getDrops(...)` 弃用**：改成 `BlockState#getDrops(LootParams.Builder)`，参数与原调用一一对应
  （ORIGIN / TOOL / 可选 THIS_ENTITY / 可选 BLOCK_ENTITY）；`ToolBlockBreakEvents` 四处统一走私有
  helper `blockDrops`。
- **导电鱼线补侧判断**：客户端不再本地生成闪电。
- **延迟代价**：节流带来 ≤1 秒的生效/失效延迟（昼夜切换、换装、怪数变化、诅咒首跳），
  本次有意接受。

已验证的 API 事实：

- `AttributeInstance`：`getModifier(ResourceLocation)`（无则 null）、`hasModifier(ResourceLocation)`、
  `addOrReplacePermanentModifier(AttributeModifier)`、`removeModifier(ResourceLocation)` 在 21.1.219
  都存在（`javap` 核对 `compiledWithNeoForge_50f69430…jar`，即本仓库实际使用的版本）。
  `addOrReplacePermanentModifier` 内部先 `removeModifier(id)` 再 `addModifier`，可以在同一 id 上反复收敛。
- `ItemStack#getEnchantmentLevel(Holder)` 是 NeoForge 在 `IItemStackExtension` 上的默认方法，会触发
  `GetEnchantmentLevelEvent`（`api-sources/net/neoforged/neoforge/common/extensions/IItemStackExtension.java:162`）。
- `MobEffects.INFESTED` 的构造参数是 `MobEffectCategory.HARMFUL`（`javap` 核对静态初始化器）。
- 附件的默认值是**每个持有者惰性创建**的：`getData` 在没存过值时调用
  `defaultValueSupplier.apply(holder)` 并写进该持有者自己的表
  （`api-sources/net/neoforged/neoforge/attachment/AttachmentHolder.java:74-83`），
  所以 `AttachmentType.builder(() -> new HashMap<>())` 每个实体拿到独立实例，可以安全地就地改。
  注意 `builder(Supplier)` 与 `builder(Function<IAttachmentHolder,T>)` 在传构造器引用
  （如 `HashMap::new`）时会"reference to builder is ambiguous"，写成零参 lambda 即可消歧。
- `location_changed` 的 `requirements` 只在 `onChangedBlock` **之前**对穿戴者求值一次
  （`Enchantment.java:490-512`、`locationContext:453-461`），`LootContextParamSets.ENCHANTED_LOCATION`
  只带 `THIS_ENTITY / ENCHANTMENT_LEVEL / ORIGIN / ENCHANTMENT_ACTIVE`，**无法逐个筛选范围内的目标实体**——
  逐目标过滤只能写在 effect 里（本次 `AreaMobEffectEffect` 就是这么做的）。
- `Block.getDrops(BlockState, ServerLevel, BlockPos, BlockEntity, Entity, ItemStack)` 在 21.1.219 已弃用
  （`ToolBlockBreakEvents` 三处仍在用，见设计文档 §4 P2 观察项）。

## mcfunction 收尾批次（2026-09-24，freeze_pic 动画）

A/B/C 三批之后剩下的最后 5 个 mcfunction（`function/enchantment/eternal_frost.mcfunction` +
`function/libs/animation/freeze_pic/{start,first_step,second_step,finished}.mcfunction`）迁为
`EternalFrostAnimationEffect`（注册名 `eternal_frost_animation`）：
`POST_ATTACK` 与 `HIT_BLOCK` 里的 `RunFunction` 换成它，`data/redstone_enchants/function/`
整个目录删除，**本仓库已无 `.mcfunction`**。

结构对应：`summon` → `new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)` +
`setBlockState`/`setTransformation`/`setPos`/`addFreshEntity`；`data merge` → `setTransformation` +
`setTransformationInterpolationDuration` + `setTransformationInterpolationDelay`；
`schedule function ... Nt` → `server.tell(new TickTask(server.getTickCount() + N, ...))`；
`kill @e[tag=...]` → 逐个体 `kill()`。13 组手调四元数/缩放/两段插值时长逐行照搬到 `KEYFRAMES` 表。

### 行为变更备忘（本批，有意为之）

- **两处本就失效的音效被修好**（用户确认的行为变更）：`first_step` 的
  `execute at @e[...] run playsound ... @s` 中 `at` 不换执行实体、而 `schedule` 的命令源无实体，
  `@s` 不指向任何实体，该音效从来是死代码；`start` 的三声同理只发给 `@s`——本附魔声明
  `affected=VICTIM`，即 POST_ATTACK 路径下 `@s` 是受击者（多为生物）、HIT_BLOCK 路径下是弹射物，
  所以实际上也几乎不响。现在四声都在命中点对附近所有玩家播放（`level.playSound(null, ...)`）。
- **并发触发不再互相清理**：原 `finished` 是
  `kill @e[tag=redstone_enchants.block_display.animation.finished]`，全局匹配——两次动画时间重叠时
  先结束的那次会把后一次的霜冰一起杀掉。现在每片霜冰由本次调用的闭包持有，只清自己的 13 片。
- **重启不再续播，但残留会被清扫**：`schedule function` 会把待执行函数写进
  `overworldData().getScheduledEvents()` 并随存档保存、重启后继续；`TickTask` 是纯内存队列，
  服务器在动画的约 50 tick 内重启（或区块在此期间卸载）会断链，13 片霜冰会以存档里的目标变换
  永久留在原地。为此新增 `event/freeze/FreezeShardCleanupEvents`：实体**从存档**加入世界时
  （`EntityJoinLevelEvent#loadedFromDisk()`）若带霜冰 tag 就取消加入——这个标志天然区分
  "自己 `addFreshEntity` 的"（false）与"从区块 NBT 恢复的"（true），故不需要任何静态状态。
  **区块卸载这条在原实现里同样会留残留**（`kill @e[tag=…]` 匹配不到已卸载的实体），本次一并修掉。
  手工兜底仍是 `/kill @e[tag=redstone_enchants.block_display.freezing]`。
- **生长那声爆响整段只播一次**：原 `execute at @e[...]` 是每片一次，但 13 片共用同一坐标、
  同一 tick，叠播只是变响；用户确认改为整段一次（初态的三声本来就各只有一次）。

### 已验证的 API 事实

- `Display` 四个写入 setter 的访问级别、AT 方案、`Entity#load` 不能当 `data merge` 用、
  `execute at`/`schedule function` 下 `@s` 的归属等通用结论，见
  `enchantment-runtime-effects.md` 的「D 批（2026-09-24）」条目，此处不重复。
- 本批新增代码用到的签名（`javap` 核对 `compiledWithNeoForge_50f69430…jar`，即 21.1.219）：
  `new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, Level)` 公开；
  `com.mojang.math.Transformation(Vector3f, Quaternionf, Vector3f, Quaternionf)`；
  `org.joml.Quaternionf(float, float, float, float)`（joml 1.10.5 / 1.10.8 同）；
  `ServerLevel#sendParticles(T, double, double, double, int, double, double, double, double)`；
  `Level#playSound(Player, double, double, double, SoundEvent, SoundSource, float, float)`；
  `SoundEvents.AMETHYST_BLOCK_STEP/BREAK/FALL`；`SoundSource.MASTER`；
  `FrostedIceBlock.AGE`（= `BlockStateProperties.AGE_3`）；`Entity#addTag(String)` / `kill()`。

## auto_smelt 改用 BlockDropsEvent（2026-09-23）

- `ToolBlockBreakEvents` 不再在 `BlockEvent.BreakEvent` 中取消破坏、手动调用 `BlockState#getDrops`、生成掉落并手动扣除 1 点耐久。
- 自动熔炼改在 `BlockDropsEvent` 中处理已经确定的掉落；这样保留方块经验、时运结果、精准采集结果以及其他模组的掉落修改。
- 配方查找改为 `RecipeManager#getRecipeFor(RecipeType.SMELTING, SingleRecipeInput, Level)`，按 `输入数量 × 配方产出数量` 生成结果，并在超过物品最大堆叠数时拆成多个 `ItemEntity`。
- 启用 `auto_smelt` 时仍跳过本分发器的地质学、点石成金、伐木和挖掘机效果，保持原有附魔组合语义；创造模式不触发自动熔炼。
  （精通采集后来改排在自动熔炼之前，不再被跳过——见下节「挖掘加成掉落批次」，该条为 2026-09-24 的有意行为变更。）
- 未复制 Apotheosis 或 AnvilCraft 的实现代码；仅参考公开的“破坏后处理掉落”思路，使用 NeoForge/Minecraft 公共 API 独立实现。

## 挖掘加成掉落批次（2026-09-24，geology / goldfinger / master_gatherer）

三项从 `BlockEvent.BreakEvent`（取消破坏 → 自行 `Block.getDrops` 重算 → 手动生成掉落物）迁到
`BlockDropsEvent`，处理**已经算好的掉落列表**。数值、掉落池与判定条件一律不变（用户选定方案 1）。

- **地质学（geology）**：命中石块后仍按 `stone_to_ore_chance` 掷骰，从 `#c:ores` 方块清单里随机取一项、
  过时运计数、作为**追加**掉落项放进事件列表。方块清单与旧实现同为 `#c:ores` 方块标签的 `asItem()`，
  只是不再经过 `Block.getDrops`。
- **点石成金（goldfinger）**：同上，判定标签为 `#c:stones`，掉落为 1~3 个金粒。
- **精通采集（master_gatherer）**：改为**复制已有掉落**（`ItemEntity#copy()`，位置与初速度随副本保留），
  不再重新调用方块掉落表。旧实现把 `setPickUpDelay(0)` 用在重算出来的掉落上；迁移后掉落项的拾取延迟
  由原版流程决定，副本与原项一致——旧实现的"0 延迟"本就是重算路径的副作用，不是附魔意图。
- 触发顺序：**精通采集 → 自动熔炼 →（未启用自动熔炼时）地质学 → 点石成金**；创造模式仍全部跳过。
  - 精通采集排最前是**有意的行为变更**（用户 2026-09-24 指定）：同时附自动熔炼与精通采集时，先复制掉落、
    再把复制出的原矿一起熔炼，即"先结算双倍掉落，再熔炼双倍的数量"。旧实现是自动熔炼启用时跳过精通采集，
    只会熔炼单份。
  - 地质学 / 点石成金仍排在自动熔炼之后，因此自动熔炼启用时它们不追加掉落（这条保持旧语义）。
- **仍未采用**：神化的数据驱动 loot table（`BoonData.lootTable` + 精准采集分支）会让掉落池与数量
  脱离现有 `stone_to_ore_chance` / `stone_to_gold_chance` 数值体系，本次不做。

### 参考出处（均为只读参考仓库，未复制其代码）

查阅的是各仓库的 `origin/1.21` 分支（MC 1.21.1），不是本地检出的 26.1 工作分支。两份上游都是 MIT。

| 用途 | 仓库 | 提交 | 文件与行号 |
|---|---|---|---|
| 在 `BlockDropsEvent` 中追加额外掉落、用事件已有掉落的位置生成新 `ItemEntity` | `Shadows-of-Fire/Apothic-Enchanting` | `origin/1.21` `00fbcf0` | `enchantments/components/BoonComponent.java:43-73` |
| 该分发器的订阅优先级与挂载点 | 同上 | 同上 | `ApothEnchEvents.java:272-276`（`@SubscribeEvent(priority = EventPriority.HIGH)`） |
| 方块类别 → loot table 与等级概率的声明形态（了解其数据驱动程度） | 同上 | 同上 | `data/ApothEnchantmentProvider.java`（Boon of the Earth 条目） |
| 复制已有掉落列表、而不是重算掉落 | 同上 | 同上 | `enchantments/ShearsEnchantments.java:57-65`（`applyExploitation`） |
| 命中后逐项替换已有掉落并保留数量 | `Shadows-of-Fire/Apotheosis` | `origin/1.21` `ad719e8` | `socket/gem/bonus/special/DropTransformBonus.java:78-91` |
| 在已有掉落上增加数量、处理小数随机与最大堆叠拆分（本次**未**采用，见上） | 同上 | 同上 | `socket/gem/bonus/special/FrozenDropsBonus.java:66-115` |

本批代码全部按上述思路重写，未搬运上游 Java 片段；上游与本项目同为 MIT，若将来搬运需连带保留其许可声明。
