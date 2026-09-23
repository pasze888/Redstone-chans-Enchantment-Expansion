# 附魔编写规范与审查清单

> 本文是**约定**：新增或修改本模组附魔时，按这里的分层决策树定位落点，按这里的流程收尾。
> API 事实与初版验证批次见 `../reference/enchantment-components.md`；迁移批次见
> `../reference/enchantment-migrations.md`；JSON 迁 datagen 见 `../reference/enchantment-datagen.md`；
> 全表与行为备注见 `../reference/enchantments.md`。
>
> **本文中的 file:line 于 2026-09-23 逐份读源码核对过**（含 P0/P1 清单）。行号会随提交漂移，
> 动工前重新读一遍对应片段再改。§4 已区分「已修」与「待办」。

## 1. 先分层：这个行为该落在哪一层

从上往下第一条命中的就算数，不要跳层。

| 层 | 适用条件 | 产出物 | 例子 |
|---|---|---|---|
| L0 纯声明 | 原版组件能完整表达：`attributes`、`tick`、`post_attack`、`hit_block`、`location_changed`、`damage_protection`、`item_damage`、`projectile_*` 等，必要时加 `requirements` | 只改 `data/provider/*.java` | `vitality`、`fortress_stance`、`retrieval` |
| L1 数值/标记组件 + 现有分发器 | 只是"条件成立时给个按等级变化的数"或"有无此附魔"，现有 `event/**` 分发器已挂好钩子 | provider + `ModEnchantmentEffectComponents` 的一类组件；分发器读值 | `magnet`、`snipe`、`life_steal` |
| L2 参数化自定义 effect | 需要新"行为"，但同族会出现第二个成员（看不见也按出现算） | 一个 `record + MapCodec` 的 `EnchantmentEntityEffect` / `EnchantmentLocationBasedEffect` / `LevelBasedValue`，在 `ModEnchantment*Effects` 注册 codec | `AreaMobEffectEffect`（撑起 13 个 `aura_*`）、`SplashCloudEffect`（14 个 `splash_*`） |
| L3 事件代码 | 需要跨 tick 状态、连带破坏多个方块、取消并重跑破坏掉落流程、改附魔等级本身 | `event/**` 分发器；必要时 Mixin | `timber`、`auto_smelt`、`preservation`（Mixin） |

**家族优先，禁止一魔一类。** L2 的范式是 `AreaMobEffectEffect`：13 个光环附魔共用它，
只有 provider 里传的 `radius / effect / target` 不同（`data/provider/BootsEnchantments.java:171-275`，
类定义在 `enchantment/effect/AreaMobEffectEffect.java:30`）。族内再出现新差异也照这个方式加参数，
不要另起一个类——"负面光环不碰玩家"就是这么做的（`target=others_non_player`，见 §4 P1-6）。
给同族第三个成员写新类之前，先回头把前两个合并成一个带参数的类。已知可合并项见 §4 P1。

**已有的硬性禁令**（继承 `../reference/enchantment-components.md:45-49`）：不再写
"一个附魔一个 `@EventBusSubscriber` + ResourceLocation 字符串 + `getEnchantments().getLevel()`"。
`ModEnchantments` 里的 key 常量只用于：注册（`ModEnchantments.java:231` 的 `key()`）、datagen 引用、
**按 key 精确取等级或改等级本身**（`EnchantmentUtil.levelOf`；如 `revive_ward` 触发后扣一级，
`event/all_armor/ArmorDamageEvents.java:117`）——纯"有没有此附魔 / 条件成立给个数"应当走组件
（`EnchantmentHelper.has` / `itemValue`），不要为了读个等级把 key 和注册表搬进事件类。

## 2. 新增 / 修改附魔的标准流程

1. **定层**：用 §1 的表判定。判定不下先用 L1 试，不要直接写 L3。
2. **落 provider**：按 `supported_items` 主标签进 `data/provider/` 对应的分类类（共 13 个；
   17 个诅咒按名称家族聚合在 `CurseEnchantments`）。数值照抄原 JSON，不得"顺手调整"。
3. **数值全进 JSON**：Java 里不留伤害系数、概率、半径这类常量。确有全局平衡规则需要留常量的
   （如 `TIMBER_CHAIN_LIMIT`），必须在 `../reference/enchantments.md` 的「机制备注」表登记，
   否则全表会失真——最近几次返工都是描述与 JSON 不一致。纯**美术/动画数据**（如
   `EternalFrostAnimationEffect` 的 13 组变换矩阵与两段插值时长）不受此条约束，逐行照搬在
   Java 里即可；这类常量的落点记在 `../reference/enchantment-runtime-effects.md`。
4. **读值走 util**：组件求值统一走 `util/EnchantmentUtil`（`itemValue:27` / `specialValue:39` /
   `levelOn:55` / `levelOf:63` / `holder:67`）。缺 helper 就补 helper，不要在事件类里手写
   `holder()` + `levelOn()` 两连——需要确切等级时用 `levelOf(registryAccess, stack, key)` 一步到位。
5. **tick 钩子过四件套**：见 §3。
6. **收尾三步**：`./gradlew runData` → 跑 `scripts/gen_ench_doc.py` 刷新
   `../reference/enchantments.md` → 同步 `zh_cn` 与 `en_us`。**改了行为常量就补机制备注表。**
7. **fork 专项（必做）**：这是 `WitherRedstone/Redstone-chans-Enchantment-Expansion` 的 fork，
   动手前先看上游是否已前进同类改动，避免白改。

## 3. tick 钩子纪律（四件套）

凡是挂在 `EntityTickEvent` / `PlayerTickEvent` 上的分发器，四条都要满足：

1. **侧判断**：入口第一行判 ServerLevel 或 ServerPlayer（范式：`event/tool/ToolPlayerTickEvents.java:27`、
   `event/curse/CurseTickEvents.java:38`）。分别写在各分段里也要补。
2. **节流**：明确周期，别每 tick 做重活。周期常量与判定统一用 `util/TickUtil`：
   `if (!TickUtil.isDue(entity, TickUtil.ONE_SECOND)) return;`（范式
   `event/armor_horse/ArmorHorseTickEvents.java:69`、`event/curse/CurseTickEvents.java:52`）。
   它按实体 tickCount 对齐，不需要自己存时间戳——自建时间戳静态 Map 正是 P0-4 的来源。
3. **值变了才写属性**：禁止每 tick `removeModifier` + `addPermanentModifier`。属性写脏会触发重算
   并向客户端刷同步包（反例见 §4 P0-1）。统一写法是把修饰符"收敛"到目标值：
   `AttributeUtil.applyPermanent(entity, Attributes.X, MODIFIER_ID, amount /* null = 不该有 */, op)`
   （`util/AttributeUtil.java`），只有数值/运算不同或需要移除时才动属性。
4. **per-player 状态不用静态容器**：要用 `ModAttachments`（`init/ModAttachments.java`，不带
   `serialize` 就是随实体生灭的 transient 值）。静态 Map 只允许存容量天然有界的全局量。

另外：随机数统一用 `RandomSource`（范式 `enchantment/effect/RandomBeneficialMobEffect.java:53`），
不要用 `java.util.Random` 静态实例。

## 4. 审查待办清单

> 「已修」是 2026-09-23 那批改动处理掉的，保留当时的描述与出处，方便回看结论是怎么来的；
> 「待办」是仍未动的。

### P0 — 正确性 / 资源

**已修**

| # | 位置 | 当时的描述与处置 |
|---|---|---|
| P0-1 | `event/all_armor/ArmorEntityTickEvents.java:39-76` | 每 tick 无条件 `removeModifier` + `addPermanentModifier`（原 `:68,:80,:88`），且**无侧判断**（原 `:37` 只判了 `Player`）。已补 ServerLevel 侧判断 + `TickUtil.isDue` 每秒收敛一次 + `AttributeUtil.applyPermanent` 值变了才写。**同一写法还在马铠精神段（`event/armor_horse/ArmorHorseTickEvents.java:111`）与头盔的以寡敌众/绝境逆袭（`event/armor_head/ArmorHeadTickEvents.java`），本次一并改掉** |
| P0-2 | `event/unbreaking/UnbreakingPlayerEvents.java:30-83` | `LAST_DAMAGE_MAP` 静态 `HashMap`（key = UUID + identityHashCode(stack)）无删除路径，随服务器时长无界增长；`:30` 也无侧判断（音效/粒子双侧跑两遍）。已改为按玩家的 `ModAttachments.PRESERVATION_LAST_DAMAGE` 附件存"上一次见到的耐久"，每 tick 用本 tick 见到的物品裁剪（容量随背包大小有界），并补服务端侧判断 |
| P0-3 | `event/armor_foot/ArmorFootTickEvents.java` | `LAST_SNEAKING` 是 `ConcurrentHashMap<Player, Boolean>`，强引用 Player（原文"从不清理"已过时：`EntityLeaveLevelEvent` 清理在本批之前就有）。已整体换成 `ModAttachments.CROP_DANCE_SNEAKING` 附件，Map 与 `EntityLeaveLevelEvent` 清理一起删除 |
| P0-4 | `event/curse/CurseTickEvents.java` | `LAST_DAMAGE_TIME` / `LAST_EFFECT_TIME` 两个静态 `Map<UUID, Long>` 无清理，玩家退出后条目永久驻留。已改用按 tickCount 对齐的 `TickUtil.isDue`，两个 Map 一起删除 |
| P0-5 | `event/sword/SwordLivingDamageEvents.java:41` | 伏击的 `AMBUSH_HAS_ATTACKED`（`Map<UUID, Boolean>`）是同一批漏网的第 4 个静态 per-player 容器：潜行中下线会留下 `true`，该玩家重进后第一次潜行攻击被误判为"已攻击过"而丢掉加成。已改为 `ModAttachments.AMBUSH_HAS_ATTACKED` 附件（脱离潜行仍复位），Map 与 `UUID`/`HashMap` 导入一起删除 |

**待办**

P0 已全部处理完。新增状态照 §3 第 4 条走 `ModAttachments`；静态 Map 只允许存容量天然有界的全局量。
另有**两个已知的静态 per-player 容器**尚未迁移（不属本批，先登记）：

- `event/mace/MaceLivingDamageEvents.java:29` `LAST_STRIKE_TIME`（`Map<UUID, Long>`）：只有当 size > 100 时
  整体 `clear()`，粗糙但对容量有界；用 `serverLevel.getGameTime()` 计时，退出后条目驻留到下次清空。
- `event/tool/ToolBlockBreakEvents.java:70` `MINING_STREAKS`（`Map<UUID, MiningStreak>`）：由
  `decayChainHaste` 在 2s 超时后删除，但只在玩家在线 tick 时跑到——挖完立刻下线会留下条目；
  计时用的还是 `System.currentTimeMillis()` 而非 tickCount（§3 第 2 条）。

### P1 — 一致性与设计

**已修**

| # | 位置 | 当时的描述与处置 |
|---|---|---|
| P1-1 | `event/**` | `holder(...)` + `levelOn(...)` 两连已收成 `EnchantmentUtil.levelOf`。原文记的 7 处组成有误：`TeleportSwapEvents` 用的是原版 `EnchantmentHelper.getItemEnchantmentLevel`、`ArmorDamageEvents` 只有 `holder` 没有 `levelOn`（它要 Holder 本身做 `removeIf` 比对，保持不动），又漏了 `ToolBlockBreakEvents` 的 `fortuneLevel`。实际改动的 7 处：`ToolPlayerTickEvents:52`、`ToolBlockBreakEvents:184` 与 `:362`、`TeleportSwapEvents:51`、`ShearBlockInteractEvents:73`、`ArmorFootTickEvents:78`、`ShieldIncomingDamageEvents:57` |
| P1-2 | 节流写法散落 | `tickCount % 20` 与自建 `PERIOD_TICKS` 已统一到 `util/TickUtil`（见 §3 第 2 条） |
| P1-3 | `event/armor_foot/ArmorFootTickEvents.java` | `java.util.Random` 静态实例已删，逐格催熟判定改用 `serverLevel.random` |
| P1-4 | `event/all_fishing/FishingHookTickEvents.java` | `STRUCK_ENTITIES` 静态集合有两个毛病：勾住状态下鱼钩消失（断竿/收线）时条目不清理，那生物**再也劈不到**；任意空钩 tick 又会把全局集合整个 `clear()`，把别的鱼钩的去重一起重置。已改为挂在鱼钩上的 `ModAttachments.CONDUCTIVE_LINE_STRUCK`（松钩即复位）。唯一行为差异：两条鱼钩同时勾住同一生物时旧版只劈 1 次、新版各劈 1 次。侧判断仍未加（客户端仍会放本地闪电），见 P2 |
| P1-5 | 重复 effect 类 | `RandomBeneficialMobEffect` / `RandomHarmfulMobEffect` 已合并为 `RandomMobEffectEffect(chance, pool)`（注册名 `random_mob_effect`，JSON 多一个 `"pool": "beneficial"|"harmful"`，黑名单只留在 harmful 池里）。**另两项核实后不合并**：`IgniteAreaEffect` 是把命中点周围 3×3×3 的**空气方块**点燃（fire_arrows 的 `fill ... fire keep`），`AreaIgniteEffect` 是点燃**生物**，二者不同功能，清单原描述有误；`SummonItemEffect` 是"战斗类附魔基建"留下的模板类，**没有任何附魔在用**，`GiveItemEffect`（检索，箭入背包）与它语义不同（原地掉落 / 等级化数量），保持独立 |
| P1-6 | `enchantment/effect/AreaMobEffectEffect.java:39-51` | `target=others` 对范围内**所有** `LivingEntity` 生效（含其他玩家），联机时无差别下负面。已新增 `AreaTarget.OTHERS_NON_PLAYER`（JSON `"others_non_player"`，枚举后来提到顶层，见 P2），中毒/缓慢/虚弱/凋零/寄生五个负面光环改用它；发光的 `others` 与增益类的 `all` 维持原样。原文建议的"加 requirement"走不通——`location_changed` 的 `requirements` 只在触发前对穿戴者求值一次，无法逐个筛目标（见 `../reference/enchantment-components.md`） |

**待办**

| # | 位置 | 问题 |
|---|---|---|
| P1-7 | `event/armor_head/ArmorHeadTickEvents.java:75-76` | `adaptive`（矿工）在 Y≥0 时用 `removeEffect(NIGHT_VISION)` 无差别摘夜视，也会洗掉夜视药水（`../reference/enchantments.md:356` 已记此行为）。当前决定：**先不动** |
| P1-8 | `event/sword/SwordLivingDamageEvents.java:195-210` | `execution`（处决）是"把伤害设成目标当前血量"的绝对值语义，**不是真 kill**：①门槛用**受伤前**血量且严格 <25%，所以要两刀（先打到 25% 以下，下一刀才处决）；②`LivingDamageEvent.Pre` 的结算顺序是 护甲 → 保护/抗性 → **Pre** → **吸收** → 扣血（`LivingEntity.java:1787-1794`，`DamageContainer.setReduction` 里 `newDamage -= amount`），所以**吸收会按 `min(吸收量, 伤害)` 抵扣掉这次伤害，带吸收的目标秒不掉**；③走的是普通扣血流，不死图腾照常救（`:1260-1261` 的 `checkTotemDeathProtection`）；④排在 dispatcher 最后做绝对覆盖，会丢弃赌徒/伏击/背刺/均衡器的连乘。已知改法：最小改是 `setNewDamage(getHealth() + getAbsorptionAmount())`（残余伤害正好等于血量）；更彻底的是照 `Apotheosis/.../ExecutingAffix.java:69-89` 改成 POST_ATTACK + 自定义 DamageType + `die()` 的单刀处决（会改变玩家感知，且改动涉及是否绕过护甲/图腾）。当前决定：**维持现状，不动** |

### P2 — 观察项

**已处理（2026-09-23 第二批）**

- **范围索敌**：三个类（`ChainBindEffect` / `RicochetEffect` / `SnowballBurstEffect`）各自的"找最近的
  N 个非玩家生物"已收进 `util/TargetingUtil`。原文把第三个记成 `ChainArrowsEffect`——那只是朝 6 个
  固定方向射箭、根本不索敌，实际第三个成员是 `SnowballBurstEffect`。
- **`Block.getDrops(...)` 弃用**：`ToolBlockBreakEvents` 里其实有**四处**（原文只记了三处，漏了区域挖掘的
  `:245`），已改用 `BlockState#getDrops(LootParams.Builder)`，收在私有 helper `blockDrops` 里。
- **`aura_burning` 点火波及玩家**：`AreaMobEffectEffect.Target` 提成顶层枚举 `AreaTarget`，
  `AreaIgniteEffect` 也带上 `target` 字段（默认 `others`，保持既有行为），燃烧光环声明
  `others_non_player`——原 mcfunction 的 `data merge entity {Fire:...}` 对玩家本来就静默失败，
  等于回到原行为。
- **导电鱼线补侧判断**：客户端不再放一道本地闪电。

**待观察**

- `enchantment/effect/**` 目前 26 个 `EnchantmentEntityEffect` + 4 个 location-based（共 30 个类），
  除索敌外没有别的明显可合并族。
- 21.1.219 里另外三处弃用 API 仍未处理（`-Xlint:deprecation` 实测）：`EntityType.builtInRegistryHolder()`
  （`data/provider/RangedEnchantments.java:577,:595`）、`Item.byBlock(Block)` 与
  `Item.builtInRegistryHolder()`（`event/tool/ToolBlockBreakEvents.java:165`）。

## 5. 与其他文档的分工

本文只写**怎么办 + 待办**。「API 长什么样」写进 `../reference/enchantment-components.md`，
「这次改了什么」写进 `../reference/enchantment-migrations.md`，「217 个附魔各是什么」由
`scripts/gen_ench_doc.py` 生成到 `../reference/enchantments.md`。三处不重复叙述同一事实，只互相链接。
