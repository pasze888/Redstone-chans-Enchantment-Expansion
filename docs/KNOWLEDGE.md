# KNOWLEDGE.md — Redstone-chan's Enchantment Expansion

记录已验证（编译通过 / runData 通过 / 源码核对）的 API 事实与踩坑，供后续会话复用。

## NeoForge 1.21.1 附魔效果组件体系（2026-08 附魔重构批次 1 验证）

- 1.21.1 附魔 JSON 的效果字段名是 `effects`（不是早期快照的 `components`）。
- `RegistrySetBuilder` 在 `net.minecraft.core`；`BootstrapContext` 在 `net.minecraft.data.worldgen`（凭 1.20.x 记忆写会找不到符号）。
- 自定义效果组件：`DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MODID)`，
  组件 codec 用 `ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()`，
  标记类组件用 `DataComponentType<Unit>builder().persistent(Unit.CODEC)`。
- 运行期求值（消费点写法，confluence `EnchantmentUtils` 同款）：
  `EnchantmentHelper.runIterationOnItem(stack, (ench, lvl) -> ench.value().modifyItemFilteredCount(component, serverLevel, lvl, stack, mutableFloat))`；
  标记组件用 `EnchantmentHelper.has(ItemStack, DataComponentType<?>)`。
- `Enchantment.Builder`：`withEffect(DataComponentType<List<ConditionalEffect<E>>>, E)`、`withEffect(DataComponentType<Unit>)`、
  `withEffect(EnchantmentEffectComponents.ATTRIBUTES, EnchantmentAttributeEffect)`、`withCustomName(UnaryOperator)`（默认名是
  `enchantment.<modid>.<path>` 转译键，加色用 `withStyle(style -> style.withColor(0xFF55FF))`）。
  `build(ResourceLocation)` 由 `context.register(key, builder.build(key.location()))` 调用。
- `Enchantment.definition` 有**两个重载**（api-sources Enchantment.java:85/100）：
  带 primary 的 `definition(supported, primary, weight, maxLevel, minCost, maxCost, anvilCost, slots...)`
  与不带的 `definition(supported, weight, maxLevel, minCost, maxCost, anvilCost, slots...)`（→ `primaryItems=Optional.empty`）。
  ~~"无 primary 重载"~~（批次 1 记载有误，已修正）。`isPrimaryItem`（Enchantment.java:151）对 empty 回退为
  "全部 supported 均视为 primary"——旧 JSON `primary_items == supported_items` 时两种写法等价；
  旧 JSON primary 是子集（如 swords 批次的 `primary_items: #swords`）时**必须用带 primary 的重载**才能完整保留。
- `LevelBasedValue.perLevel(x)` = `Linear(x, x)` = **x × 等级**；`constant(x)` 序列化为裸浮点数。
- 1.21.1 的 `minecraft:attributes` 组件类型是 `List<EnchantmentAttributeEffect>`
  （`enchantment.effects.EnchantmentAttributeEffect`：id / attribute Holder / amount LevelBasedValue / operation），
  由 `EnchantmentHelper` 在持有时自动挂/卸属性修饰符——急迫类附魔无需每 tick 代码。
- datagen `exclusiveWith`：`context.lookup(Registries.ENCHANTMENT).getOrThrow(TagKey<Enchantment>)` 能解析
  main/resources 下手写 tag JSON（runData 实测通过，无需先把 tag 也迁入 datagen）。
- `@EventBusSubscriber(modid)`（无 bus 参数）在 21.1 可同时收 mod bus 的 `GatherDataEvent`（实测 runData 生效）。
- `Enchantments.FORTUNE` 在 1.21.1 是 `ResourceKey<Enchantment>`，取 Holder：
  `level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)`，
  再 `EnchantmentHelper.getItemEnchantmentLevel(holder, stack)`。

## 本次重构确立的架构（后续批次照此迁移）

- 附魔效果组件注册：`init/ModEnchantmentEffectComponents`；ResourceKey 常量：`init/ModEnchantments`；
  组件求值工具：`util/EnchantmentUtil`；服务端配置：`config/ModConfigData`
  （`ModConfigSpec` + `modContainer.registerConfig(ModConfig.Type.SERVER, SPEC)`，
  落在 `<world>/serverconfig/redstone_enchants-server.toml`，供服务器所有者控制数值，
  如 `timberChainLimit` 控制连锁砍树单次上限）。
- 事件侧只保留**按钩子集中的分发器**（`event/tool/ToolBlockBreakEvents` 等），行为参数全部从附魔 JSON 组件读取；
  禁止再写"一个附魔一个 @EventBusSubscriber + ResourceLocation 字符串 + getEnchantments().getLevel()"。
- 迁移一个附魔的步骤：① JSON 迁入 `data/provider/ModEnchantmentProvider`（数值照抄原 JSON）并声明效果组件 →
  ② 删除 main/resources 下对应手写 JSON → ③ 分发器读取组件 → ④ 删除旧 handler。
- 构建：`JAVA_HOME=C:/Users/lzp/scoop/apps/dragonwell21-jdk/current` 后 `./gradlew build` / `runData`（build.gradle 已配好
  `--output src/generated/resources --existing src/main/resources`）。

## 行为变更备忘（批次 1，有意为之）

- haste：每 tick 施加/移除急迫药水效果 → 原版 `attributes` 组件（无药水图标，切换即生效；修复了误删信标急迫的 bug）。
- magnet / chain_haste：只在服务端 tick 执行（旧版客户端也跑一遍产生无效扰动）。
- timber：原木判定从 `asItem().toString().contains("log")` 改为 `minecraft:logs` 方块标签；
  单次连锁上限由服务端配置 `timberChainLimit` 控制（默认 512，范围 1-4096）。
- 挖掘类效果的执行顺序从"订阅者注册顺序（不确定）"改为固定：连锁急迫 → 自动熔炼 → 概率掉落 → 连锁砍树 → 区域挖掘。
- 连锁急迫上限（80% 封顶）仍在代码：它是全局平衡规则而非逐附魔数值。

## 战斗类附魔基建（2026-08 补骨架，为 swords 批次铺路；参考神化 Apothic Enchanting + confluence）

已验证（compileJava / build / runData 全通过）的 1.21.1 API 事实：

- 两个自定义"序列化器"注册表都在 `Registries`：
  `ENCHANTMENT_ENTITY_EFFECT_TYPE` = `ResourceKey<Registry<MapCodec<? extends EnchantmentEntityEffect>>>`、
  `ENCHANTMENT_LEVEL_BASED_VALUE_TYPE` = `ResourceKey<Registry<MapCodec<? extends LevelBasedValue>>>`。
  **注册的是 MapCodec（不是实例）**：`DeferredRegister.create(Registries.X, MODID)` 后 `register("name", () -> X.CODEC)`。
- 自定义实体效果：`implements EnchantmentEntityEffect`（方法签名 `apply(ServerLevel, int level, EnchantedItemInUse, Entity, Vec3 origin)` + `MapCodec<? extends EnchantmentEntityEffect> codec()`）。
  `EnchantedItemInUse` 是 record：`(ItemStack, @Nullable EquipmentSlot, @Nullable LivingEntity owner, Consumer<Item> onBreak)`。
  模板见 `enchantment/effect/SummonItemEffect`（照 confluence 同款，1.21.1 签名核对过）。
- `TargetedConditionalEffect<T>(enchanted, affected, effect, requirements)`（record）+ `codec(S, LootContextParamSet)`；
  `Enchantment.Builder.withEffect(DataComponentType<List<TargetedConditionalEffect<E>>>, EnchantmentTarget, EnchantmentTarget, E[, requirements])`。
  `EnchantmentTarget` 枚举：`ATTACKER / DAMAGING_ENTITY / VICTIM`（StringRepresentable）。
  `LootContextParamSets` 有 `ENCHANTED_DAMAGE / ENCHANTED_ITEM / ENCHANTED_LOCATION / ENCHANTED_ENTITY`。
- 裸值组件：`Enchantment.Builder.withSpecialEffect(DataComponentType<E>, E)`（神化 MINERS_FERVOR/BERSERKING 形态），
  组件 codec 直接 `DataComponentType.builder().persistent(codec)`。
- 自定义数值函数：`implements LevelBasedValue`（`calculate(int)` + `codec()`），
  用 `LevelBasedValue.CODEC` 作子字段可复用（神化 ExponentialLevelBasedValue 同款，已按 1.21.1 适配）。
  1.21.1 内置 `Linear/Constant/Clamped/LevelsSquared/Fraction/Lookup`。

### 落地骨架（本轮，均已构建验证）

- `init/ModEnchantmentEntityEffects`（注册 entity effect codec）+ `enchantment/effect/SummonItemEffect`（模板）
- `init/ModEnchantmentLevelBasedValues`（注册数值函数 codec）+ `enchantment/value/ExponentialLevelBasedValue`（模板）
- `ModEnchantmentEffectComponents` 新增 `targeted(...)` / `special(...)` helper + 示例组件
  `POST_ATTACK_SUMMON`（targeted）/ `RAW_VALUE`（裸 LevelBasedValue）。
- 全部注册在 `ModEnchantments.register(eventBus)` 接线。

### 重要判断（swords 迁移前必读）

swords 的 11 个旧 handler 行为核对结论：**绝大多数无法用原版 post_attack 零代码声明化**
（ambush 带每玩家状态、backstab 需要方向点积、boons/calamity 随机遍历全药水表、butcher/decapitation 走
LivingDropsEvent、life_steal 要 heal 攻击者、execution 条件秒杀、nullify 移除目标效果、equalizer 伤害随血量变化、
gambler 双分支随机）。迁移它们 = 每个附魔写自定义 effect 类（部分还需保留事件分发器处理 drops），
按"行为保真"逐个做，不能一次批量声明化。

> 2026-09 swords 批次完成后补充：实际落地形态是**单值/标记/数值组件 + 事件分发器**
> （boons/calamity/nullify 用自定义实体效果 + 原版 POST_ATTACK；其余 8 个用
> `unit()`/`value()`/`special()` 组件 + `SwordLivingDamageEvents`/`SwordDropsEvents` 分发器），
> 未再新增 effect 类。组件求值需 `ServerLevel` 的效果只在服务端执行。

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
  Pre `赌徒 → 伏击 → 背刺 → 均衡器 → 处决`，Post `生命吸取`，Drops `屠夫 → 斩首`。
- 攻击者解析照抄各旧 handler：伏击/均衡器 = `getDirectEntity() instanceof Player`（投射物不触发）；
  赌徒/处决/背刺/生命吸取/屠夫/斩首 = `getEntity() instanceof LivingEntity`。
  **用户决定**：不添加 Player 限制——接受持械 Mob 攻击者也触发（原版 POST_ATTACK 对 Mob 攻击同样生效）。
- life_steal **修复 ID bug**：旧 handler 引用不存在的 `leeching`（真 ID `life_steal`），getHolder 恒 null →
  从未生效；修复后从"无效果"变为生效，数值基数由 original 改为实际伤害（Post getNewDamage）×10%。
- 处决照旧实现语义：`setNewDamage(目标当前生命值)`（旧注释写"设为 0"但实现是设为当前血量，照实现）。
- 伏击每玩家状态（Map<UUID,Boolean>：非潜行攻击置位/潜行首击 ×(1+0.2×级) 后置位/PlayerTickEvent.Post
  非潜行重置）迁入分发器；组件求值需 ServerLevel，伏击/背刺/均衡器/生命吸取/屠夫/斩首均只服务端执行
  （旧版伏击在双侧维护 Map 副本，结果行为不变）。
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
- **已知副作用原样保留（潜在问题，待用户决定是否修）**：
  - indestructible 的 else 分支：任何未带附魔的已装备物品都会被 `remove(UNBREAKABLE)`，会剥离其它来源的组件；
  - sturdy 的 EntityTick else 分支：任何不带坚固的掉落物都会被 `remove(FIRE_RESISTANT)`，会剥离其它来源的
    组件（如下界合金物品）；
  - sturdy 的 Pre 段语义：穿戴坚固 → 本体对爆炸/闪电/火**整次伤害免疫**（不只保护装备）——旧行为照抄。
