# 光环与 mcfunction 迁移的运行期事实 — Redstone-chan's Enchantment Expansion

光环（aura_*）附魔与 run_function 的排查结论，以及 mcfunction → Java 全面迁移中已验证的运行期 API 事实。
组件体系见 `enchantment-components.md`；构建坑见 `../troubleshooting.md`。

## 光环（aura_*）附魔与 run_function（2026-08-31 排查）

- **aura 13 个附魔曾整体无效**：JSON/datagen 引用
  `redstone_enchants:enchantment/aura/<effect>` 函数，但
  `function/enchantment/aura/` 目录从未存在（上游 1.0.0 jar 起就缺失）。
  已补齐 13 个 mcfunction（增益 4 格含自身、减益 4 格排除自身用
  `tag rcee_aura_source` 临时标记、burning 2 格点火 + 自身火焰抗性；
  时长 3s、amp 0、隐藏粒子）。
- **`RunFunction.apply`（1.21.1 反编译）**：找不到函数只
  `LOGGER.error("Enchantment run_function effect failed for non-existent function ...")`
  静默失败，不崩溃不进聊天栏——排查"附魔无效"先 grep 这条日志。
  命令源为 `withEntity(entity)` + `withPosition(origin)` + 玩家朝向，
  即函数内 `@s` = 触发实体、原点在实体位置。
- **`EnchantmentEffectComponents.LOCATION_CHANGED`**：实体换格/移动时触发
  （`LivingEntity` 里每 tick 调 `EnchantmentHelper.runLocationChangedEffects`），
  停止移动不再触发——光环类需要每 tick 刷新的效果用较短时长（3s）保证
  停下后渐退。`run_function` 同时注册在 LOCATION_BASED 与 ENTITY 两类
  effect type 注册表，可直接用于 location_changed。
- **点燃实体**：vanilla 无点燃命令，函数里用
  `execute as @e[...] at @s run data merge entity @s {Fire:80s}`；
  对玩家会静默失败（data merge 不允许作用于玩家）。

### aura 改为 Java 实现（2026-08-31，替代上一节的 mcfunction 方案）

- 已删除 13 个 `function/enchantment/aura/*.mcfunction`，改用两个自定义
  location-based effect：`AreaMobEffectEffect`（radius/effect/duration_ticks/
  amplifier/target=all|others|others_non_player|self，编码 `redstone_enchants:area_mob_effect`）与
  `AreaIgniteEffect`（radius/fire_ticks，`redstone_enchants:area_ignite`），
  注册在 `Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE`（NeoForge
  DeferredRegister 即可，见 `ModEnchantmentLocationBasedEffects`）。
  `others_non_player` 是 2026-09-23 为"负面光环不碰玩家"加的（见
  `enchantment-migrations.md` 同批记录）；`AreaIgniteEffect` 仍只排除自己，点火会波及玩家。
- 关键接口事实：`location_changed` 组件反序列化走
  `EnchantmentLocationBasedEffect` 注册表，自定义类直接实现
  `onChangedBlock(level, enchLevel, item, entity, pos, applyTransientEffects)`；
  `EnchantmentEntityEffect.apply` 只是通过 default 方法桥接到 onChangedBlock。
  `RunFunction` 同时注册在两个注册表所以两边都能用，自定义类只注册
  location-based 也能用于 post_attack？——不能：post_attack 走 ENTITY 注册表，
  需要两边都注册或实现 EnchantmentEntityEffect。
- 效果施加：`target.addEffect(new MobEffectInstance(holder, dur, amp, true/*ambient*/, false/*隐藏粒子*/), source)`；
  点燃用 `livingEntity.igniteForTicks(ticks)`（对玩家也生效，优于
  函数方案里 `data merge entity {Fire:...}` 的玩家限制）。
- MobEffects 字段名（1.21.1）：`MOVEMENT_SPEED/DIG_SPEED/DAMAGE_BOOST/JUMP/
  REGENERATION/DAMAGE_RESISTANCE/MOVEMENT_SLOWDOWN/WEAKNESS/POISON/WITHER/
  GLOWING/INFESTED/FIRE_RESISTANCE`。

## mcfunction → Java 全面迁移（A/B/C 三批，2026-08-31）

- 68 个 mcfunction 迁移后仅剩 5 个保留：`eternal_frost` + `freeze_pic` 动画库
  （13 组手调 block_display 变换矩阵 + interpolation 时长的多步动画，
  Java 化为纯数据搬运、收益低）。
- **schedule 的 Java 等价**：`level.getServer().tell(new TickTask(
  server.getTickCount()+delay, callback))`（TickTask 的 tick 是绝对服务器刻）；
  回调里先检查 `entity.isRemoved()`。
- **瞬态属性修正**（1.21.1）：`AttributeInstance.addTransientModifier(
  new AttributeModifier(ResourceLocation id, amount, Operation))`，
  `removeModifier(id)`；transient 不入 NBT，死亡/卸载自动清理。
  Record 构造，id 用 fromNamespaceAndPath（withDefaultNamespace 不接受带冒号串）。
- **FallingBlockEntity**：公开构造只有 (EntityType, Level)；带 NBT 的生成走
  `entity.load(CompoundTag)`（与 /summon 同路径，BlockState 用
  NbtUtils.writeBlockState）；`setHurtsEntities(float, int)` 设落伤。
- `/kill` 与 `Entity.kill()` 都不走死亡掉落流程（remove KILLED），
  因此绝命箭矢原函数的 DeathLootTable/隐形/clear 在同 tick kill 下本就无效。
- 常见坑：`SoundSource.PLAYERS`（复数）；RecordCodecBuilder 在
  `com.mojang.serialization.codecs`。
- 附魔效果实体指向：POST_ATTACK 的 affected 目标决定 effect 的 entity
  （ATTACKER=伤害来源实体、VICTIM=受击者、DAMAGING_ENTITY=直击实体如箭）；
  箭矢清理类效果挂 affected=DAMAGING_ENTITY。
- 顺带修复的上游 bug：冰霜箭减速永不解除（scheduled 函数从未被调度）、
  雪球弹回自己（选择器未排除自身）、精准射击时间窗恒真、
  第一印象 reset_mark 死代码。
