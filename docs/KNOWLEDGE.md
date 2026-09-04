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
- `Enchantment.definition(supported, weight, maxLevel, minCost, maxCost, anvilCost, slots...)` 无 primary 重载 →
  `primaryItems=Optional.empty`；1.21.1 中 `isPrimaryItem` 对 empty 回退为"全部 supported 均视为 primary"，
  与原手写 JSON `primary_items == supported_items` 等价。
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
