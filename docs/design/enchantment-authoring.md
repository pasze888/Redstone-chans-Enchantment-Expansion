# 附魔编写规范与审查清单

> 本文是**约定**：新增或修改本模组附魔时，按这里的分层决策树定位落点，按这里的流程收尾。
> API 事实与初版验证批次见 `../reference/enchantment-components.md`；迁移批次见
> `../reference/enchantment-migrations.md`；JSON 迁 datagen 见 `../reference/enchantment-datagen.md`；
> 全表与行为备注见 `../reference/enchantments.md`。
>
> **本文中的 file:line 于 2026-09-23 逐份读源码核对过**（含 P0/P1 清单）。行号会随提交漂移，
> 动工前重新读一遍对应片段再改。

## 1. 先分层：这个行为该落在哪一层

从上往下第一条命中的就算数，不要跳层。

| 层 | 适用条件 | 产出物 | 例子 |
|---|---|---|---|
| L0 纯声明 | 原版组件能完整表达：`attributes`、`tick`、`post_attack`、`hit_block`、`location_changed`、`damage_protection`、`item_damage`、`projectile_*` 等，必要时加 `entity_requirements` | 只改 `data/provider/*.java` | `vitality`、`fortress_stance`、`retrieval` |
| L1 数值/标记组件 + 现有分发器 | 只是"条件成立时给个按等级变化的数"或"有无此附魔"，现有 `event/**` 分发器已挂好钩子 | provider + `ModEnchantmentEffectComponents` 的一类组件；分发器读值 | `magnet`、`snipe`、`life_steal` |
| L2 参数化自定义 effect | 需要新"行为"，但同族会出现第二个成员（看不见也按出现算） | 一个 `record + MapCodec` 的 `EnchantmentEntityEffect` / `EnchantmentLocationBasedEffect` / `LevelBasedValue`，在 `ModEnchantment*Effects` 注册 codec | `AreaMobEffectEffect`（撑起 15 个 `aura_*`）、`SplashCloudEffect`（14 个 `splash_*`） |
| L3 事件代码 | 需要跨 tick 状态、连带破坏多个方块、取消并重跑破坏掉落流程、改附魔等级本身 | `event/**` 分发器；必要时 Mixin | `timber`、`auto_smelt`、`preservation`（Mixin） |

**家族优先，禁止一魔一类。** L2 的范式是 `AreaMobEffectEffect`：15 个光环附魔共用它，
只有 provider 里传的 `radius / effect / target` 不同（`data/provider/BootsEnchantments.java:178-274`，
类定义在 `enchantment/effect/AreaMobEffectEffect.java:28`）。给同族第三个成员写新类之前，
先回头把前两个合并成一个带参数的类。已知可合并项见 §4 P1。

**已有的硬性禁令**（继承 `../reference/enchantment-components.md:45-49`）：不再写
"一个附魔一个 `@EventBusSubscriber` + ResourceLocation 字符串 + `getEnchantments().getLevel()`"。
`ModEnchantments` 里的 key 常量只用于三件事：注册
（`ModEnchantments.java:231` 的 `key()`）、datagen 引用、**需要改附魔等级本身**（如 `revive_ward`
触发后扣一级，`event/all_armor/ArmorDamageEvents.java:117`）。

## 2. 新增 / 修改附魔的标准流程

1. **定层**：用 §1 的表判定。判定不下先用 L1 试，不要直接写 L3。
2. **落 provider**：按 `supported_items` 主标签进 `data/provider/` 对应的分类类（共 13 个；
   17 个诅咒按名称家族聚合在 `CurseEnchantments`）。数值照抄原 JSON，不得"顺手调整"。
3. **数值全进 JSON**：Java 里不留伤害系数、概率、半径这类常量。确有全局平衡规则需要留常量的
   （如 `TIMBER_CHAIN_LIMIT`），必须在 `../reference/enchantments.md` 的「机制备注」表登记，
   否则全表会失真——最近几次返工都是描述与 JSON 不一致。
4. **读值走 util**：组件求值统一走 `util/EnchantmentUtil`（`itemValue:27` / `specialValue:39` /
   `levelOn:51` / `holder:55`）。缺 helper 就补 helper，不要在事件类里手写
   `EnchantmentHelper.has` + `holder()` + `levelOn()` 三连（现状见 §4 P1-1）。
5. **tick 钩子过四件套**：见 §3。
6. **收尾三步**：`./gradlew runData` → 跑 `scripts/gen_ench_doc.py` 刷新
   `../reference/enchantments.md` → 同步 `zh_cn` 与 `en_us`。**改了行为常量就补机制备注表。**
7. **fork 专项（必做）**：这是 `WitherRedstone/Redstone-chans-Enchantment-Expansion` 的 fork，
   动手前先看上游是否已前进同类改动，避免白改。

## 3. tick 钩子纪律（四件套）

凡是挂在 `EntityTickEvent` / `PlayerTickEvent` 上的分发器，四条都要满足：

1. **侧判断**：入口第一行判 ServerLevel 或 ServerPlayer（范式：`event/tool/ToolPlayerTickEvents.java:29`、
   `event/curse/CurseTickEvents.java:44`）。分别写在各分段里也要补。
2. **节流**：明确周期，别每 tick 做重活（参考 `event/armor_horse/ArmorHorseTickEvents.java:69` 的
   `tickCount % 20`、`CurseTickEvents.java:62` 的 `PERIOD_TICKS`）。
3. **值变了才写属性**：禁止每 tick `removeModifier` + `addPermanentModifier`。属性写脏会触发重算
   并向客户端刷同步包（反例见 §4 P0-1）。
4. **per-player 状态不用静态容器**：要用 `ModAttachments`，见 §4 P0-2/3/4。

另外：随机数统一用 `RandomSource`（范式 `enchantment/effect/RandomBeneficialMobEffect.java:53`），
不要用 `java.util.Random` 静态实例（反例 `event/armor_foot/ArmorFootTickEvents.java:38`）。

## 4. 审查待办清单

### P0 — 正确性 / 资源（建议尽快处理）

| # | 位置 | 问题 |
|---|---|---|
| P0-1 | `event/all_armor/ArmorEntityTickEvents.java:36-91` | 每 tick 无条件 `removeModifier` + `addPermanentModifier`（`:68,:80,:88`）；且**无侧判断**（`:37` 只判了 `Player`），客户端也整套跑一遍。`daynight_cycle` 是常量加成，应改成"上次值变了才动"或改挂装备变更 + 节流 tick |
| P0-2 | `event/unbreaking/UnbreakingPlayerEvents.java:27-45` | `LAST_DAMAGE_MAP` 静态 `HashMap`，key = `UUID + identityHashCode(stack)`，**无删除路径**，随服务器时长无界增长；`:30` 也无侧判断 |
| P0-3 | `event/armor_foot/ArmorFootTickEvents.java:44` | `LAST_SNEAKING` 是 `ConcurrentHashMap<Player, Boolean>`，**强引用 Player 且从不清理**（类注释已自认），退出服务器的玩家对象无法 GC |
| P0-4 | `event/curse/CurseTickEvents.java:28-29` | `LAST_DAMAGE_TIME` / `LAST_EFFECT_TIME` 两个静态 `Map<UUID, Long>` 无清理，玩家退出后条目永久驻留 |

> P0-2/3/4 的统一改法：状态随实体生命周期走 `ModAttachments`，或在 `EntityLeaveLevel` /
> `PlayerEvent.PlayerLoggedOutEvent` 上清理。静态 Map 只允许存容量天然有界的全局量。

### P1 — 一致性与设计

| # | 位置 | 问题 |
|---|---|---|
| P1-1 | `event/**` 7 处 | `EnchantmentUtil.holder(...)` + `levelOn(...)` 手写两连（`ToolPlayerTickEvents.java:54`、`ToolBlockBreakEvents.java:186`、`TeleportSwapEvents.java:52`、`ShearBlockInteractEvents.java:74`、`ArmorFootTickEvents.java:82`、`ShieldIncomingDamageEvents.java:58`、`ArmorDamageEvents.java:117`）。应在 `EnchantmentUtil` 补一个 `levelOf(level, stack, key)` 之类的 helper 统一收纳。 |
| P1-2 | 节流写法散落 | `tickCount % 20`（`ArmorHorseTickEvents.java:69`）与 `PERIOD_TICKS`（`CurseTickEvents.java:62`）各自为政，应抽成统一常量 + helper |
| P1-3 | 随机数 | `ArmorFootTickEvents.java:38` 用 `java.util.Random`，与 `RandomBeneficialMobEffect.java:53` 的 `RandomSource` 不一致 |
| P1-4 | `event/all_fishing/FishingHookTickEvents.java:28,77` | `STRUCK_ENTITIES` 只在"本次 tick 没勾住"时整体 `clear()`；鱼钩在勾住状态下消失则条目残留。去重 Set 应挂在鱼钩实体上或用 attachment |
| P1-5 | 重复 effect 类 | `RandomBeneficialMobEffect` / `RandomHarmfulMobEffect`（`:46` 的 `isBeneficial()` 是唯一实质差异）应合并为带 filter 参数的一类；`IgniteAreaEffect.java:17`（写死 3×3×3 范围）的功能是 `AreaIgniteEffect`（radius / fireTicks 参数化）的特例，优先统一到后者；`GiveItemEffect` 与 `SummonItemEffect` 功能接近（待核实两者的调用方差异后再决定合并）。均属"触碰时顺手做"，不单独立项。 |
| P1-6 | `enchantment/effect/AreaMobEffectEffect.java:66-71` | `target=others` 对范围内**所有** `LivingEntity` 生效，含其他玩家。`aura_poison/wither/slowness/weakness` 在联机 / PvP 语境是无差别下负面，需加 requirement（如仅对玩家以外的生物生效）。 |
| P1-7 | `event/armor_head/ArmorHeadTickEvents.java:74-75` | `adaptive`（矿工）在 Y≥0 时用 `removeEffect(NIGHT_VISION)` 无差别摘夜视，也会洗掉夜视药水（`../reference/enchantments.md:356` 已记此行为）。若要修，应判断效果来源再决定 |

### P2 — 观察项

- `enchantment/effect/**` 目前 26 个 `EnchantmentEntityEffect` + 4 个 location-based，其中 3 个
  （`ChainBindEffect` / `RicochetEffect` / `ChainArrowsEffect`）都做"范围索敌"，是否可以共用取目标部分的 helper，留待下次触碰时判断。

## 5. 与其他文档的分工

本文只写**怎么办 + 待办**。「API 长什么样」写进 `../reference/enchantment-components.md`，
「这次改了什么」写进 `../reference/enchantment-migrations.md`，「217 个附魔各是什么」由
`scripts/gen_ench_doc.py` 生成到 `../reference/enchantments.md`。三处不重复叙述同一事实，只互相链接。
