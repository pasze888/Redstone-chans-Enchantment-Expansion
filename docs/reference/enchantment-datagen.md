# 附魔 JSON 迁 datagen（EnchantmentProvider，2026-08）

手写附魔 JSON 迁入 datagen 后，已验证（runData / build 通过、生成 JSON 与手写逐字段核对）的 API 事实与编码等价约定。
迁移对比法、API 查证坑见 `../ai/gotchas.md`；组件体系见 `enchantment-components.md`。

- **Enchantment.definition 双 tag 形状**：手写有 `primary_items` 时必须用
  `definition(supported.getOrThrow(TAG), primary.getOrThrow(TAG), weight, ...)` 重载，
  单 tag 版不输出 `primary_items`。
- **slots 是 1.20.x 残留数组**（["mainhand","offhand"]/["chest"]/["any"]…）：
  1.21.1 codec 只接受单个 EquipmentSlotGroup 名。映射：
  mainhand+offhand→`HAND`、body→`BODY`、chest→`CHEST`、any→`ANY`；
  生成输出 "hand"/"body" 等单值与手写数组的差异属修复而非漂移。
- **DFU optionalFieldOf(name, default) 编码省略等于 default 的值**：
  offset ZERO、scale 1、粒子 speed 0（`optionalFieldOf("speed", ConstantFloat.ZERO)`）、
  velocity base/movement_scale 0、block_state_property 空 properties 等生成 JSON 不出现该字段，
  与手写显式 0 语义等价。
- **MovementPredicate 未用字段必须传 `MinMaxBounds.Doubles.ANY`，不能传 null**
  （null 会在 DFU encode Optional.of(null) 时 NPE）；ANY 编码为空对象 `{}`，decode 等价。
- **裸 id vs 命名空间 id**（"play_sound" vs "minecraft:play_sound"、"linear" vs "minecraft:linear"、
  粒子 position/velocity 省略 0 项）：编码形状差异，语义等价；对比脚本报 DIFF 时按此归类接受。
- **单条件手写数组 `[c]`** 生成端等价于裸对象 `c` 或 `all_of{c}`；air_toss 手写 conditions
  是条件数组 → 生成用 `AllOfCondition.allOf(...)` 包装，等价。
- **音效**：`SoundEvents.*` 常量分两种形态——`Holder<SoundEvent>`（GENERIC_EXPLODE/SOUL_ESCAPE/
  TRIDENT_THUNDER 直接用）与裸 `SoundEvent`（如 BREEZE_DEATH，需
  `BuiltInRegistries.SOUND_EVENT.wrapAsHolder(...)`）；无常量时
  `BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(ResourceKey.create(Registries.SOUND_EVENT, id))`。
- **NBT 谓词**：`TagParser.parseTag(...)` 抛受检 CommandSyntaxException，
  datagen 里用小 helper 集中 try/catch（见 provider 的 `nbtPredicate`）。
  SNBT 字符串单引号/双引号等价（手写 `{Tags:['x']}` 生成 `{Tags:["x"]}`）。
- **xp_blade 玩家经验等级**：1.21.1 用
  `EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().setLevel(MinMaxBounds.Ints.atLeast(n)).build())`
  （1.20.x 手写 `type_specific` 形状在新版由 type_specific dispatch 保留，编码一致）。
- **Foreign mod 引用**（irons_spellbooks 属性/补间、异 mod 音效等）：
  `Holder` 用 `BuiltInRegistries.X.getHolderOrThrow(ResourceKey.create(...))`（provider 里的
  `foreignHolder` helper）；`HolderSet` 用 `HolderSet.direct(...)` 包上述 Holder；
  EntityType 需 `EntityType.X.builtInRegistryHolder()`。异 mod 内容须在
  ModDataGenerator.conditionsBuilder 挂 `ModLoadedCondition`。
- **int 最大值伤害（last_hope）**：LevelBasedValue 全 float，2147483647 无法精确编码
  （float 化为 2.14748365E9），属可接受近似。
- **`ValueCheckCondition` 等裸条件对象作条件参数时需 `() -> new ValueCheckCondition(...)`**
  （条件参数类型是 `LootCondition`）。
