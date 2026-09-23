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
   否则全表会失真——最近几次返工都是描述与 JSON 不一致。
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

**待办**

P0 已全部处理完。新增状态照 §3 第 4 条走 `ModAttachments`；静态 Map 只允许存容量天然有界的全局量。

### P1 — 一致性与设计

**已修**

| # | 位置 | 当时的描述与处置 |
|---|---|---|
| P1-1 | `event/**` | `holder(...)` + `levelOn(...)` 两连已收成 `EnchantmentUtil.levelOf`。原文记的 7 处组成有误：`TeleportSwapEvents` 用的是原版 `EnchantmentHelper.getItemEnchantmentLevel`、`ArmorDamageEvents` 只有 `holder` 没有 `levelOn`（它要 Holder 本身做 `removeIf` 比对，保持不动），又漏了 `ToolBlockBreakEvents` 的 `fortuneLevel`。实际改动的 7 处：`ToolPlayerTickEvents:52`、`ToolBlockBreakEvents:184` 与 `:362`、`TeleportSwapEvents:51`、`ShearBlockInteractEvents:73`、`ArmorFootTickEvents:78`、`ShieldIncomingDamageEvents:57` |
| P1-2 | 节流写法散落 | `tickCount % 20` 与自建 `PERIOD_TICKS` 已统一到 `util/TickUtil`（见 §3 第 2 条） |
| P1-3 | `event/armor_foot/ArmorFootTickEvents.java` | `java.util.Random` 静态实例已删，逐格催熟判定改用 `serverLevel.random` |
| P1-6 | `enchantment/effect/AreaMobEffectEffect.java:69-76` | `target=others` 对范围内**所有** `LivingEntity` 生效（含其他玩家），联机时无差别下负面。已新增 `Target.OTHERS_NON_PLAYER`（JSON `"others_non_player"`），中毒/缓慢/虚弱/凋零/寄生五个负面光环改用它；发光的 `others` 与增益类的 `all` 维持原样。原文建议的"加 requirement"走不通——`location_changed` 的 `requirements` 只在触发前对穿戴者求值一次，无法逐个筛目标（见 `../reference/enchantment-components.md`） |

**待办**

| # | 位置 | 问题 |
|---|---|---|
| P1-4 | `event/all_fishing/FishingHookTickEvents.java:28,77` | `STRUCK_ENTITIES` 只在"本次 tick 没勾住"时整体 `clear()`；鱼钩在勾住状态下消失则条目残留。去重 Set 应挂在鱼钩实体上或用 attachment |
| P1-5 | 重复 effect 类 | `RandomBeneficialMobEffect` / `RandomHarmfulMobEffect`（`:46` 的 `isBeneficial()` 是唯一实质差异）应合并为带 filter 参数的一类；`IgniteAreaEffect.java:17`（写死 3×3×3 范围）的功能是 `AreaIgniteEffect`（radius / fireTicks 参数化）的特例，优先统一到后者；`GiveItemEffect` 与 `SummonItemEffect` 功能接近（待核实两者的调用方差异后再决定合并）。均属"触碰时顺手做"，不单独立项。 |
| P1-7 | `event/armor_head/ArmorHeadTickEvents.java:75-76` | `adaptive`（矿工）在 Y≥0 时用 `removeEffect(NIGHT_VISION)` 无差别摘夜视，也会洗掉夜视药水（`../reference/enchantments.md:356` 已记此行为）。若要修，应判断效果来源再决定 |

### P2 — 观察项

- `enchantment/effect/**` 目前 25 个 `EnchantmentEntityEffect` + 4 个 location-based（共 29 个类），
  其中 3 个（`ChainBindEffect` / `RicochetEffect` / `ChainArrowsEffect`）都做"范围索敌"，
  是否可以共用取目标部分的 helper，留待下次触碰时判断。
- `Block.getDrops(BlockState, ServerLevel, BlockPos, BlockEntity, Entity, ItemStack)` 在 21.1.219
  已被标记弃用（`event/tool/ToolBlockBreakEvents.java:100,:168,:191` 三处，对应自动熔炼/精通采集/伐木）；
  建议改成 `state.getDrops(...)` 形态，本次未动。
- `aura_burning` 的 `AreaIgniteEffect(2.0F, 80)` 同样会对范围内其他玩家点火。P1-6 只处理了
  `AreaMobEffectEffect`，若点火也要 PvP 安全，需要在 `AreaIgniteEffect` 上加同样的目标参数
  （正好和 P1-5 的"统一到 `AreaIgniteEffect`"一起做）。

## 5. 与其他文档的分工

本文只写**怎么办 + 待办**。「API 长什么样」写进 `../reference/enchantment-components.md`，
「这次改了什么」写进 `../reference/enchantment-migrations.md`，「217 个附魔各是什么」由
`scripts/gen_ench_doc.py` 生成到 `../reference/enchantments.md`。三处不重复叙述同一事实，只互相链接。
