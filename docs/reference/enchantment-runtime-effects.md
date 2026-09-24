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
  amplifier/target，编码 `redstone_enchants:area_mob_effect`）与
  `AreaIgniteEffect`（radius/fire_ticks/target，`redstone_enchants:area_ignite`），
  注册在 `Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE`（NeoForge
  DeferredRegister 即可，见 `ModEnchantmentLocationBasedEffects`）。
  `target` 取值见顶层枚举 `enchantment/effect/AreaTarget`
  （`all` / `others` / `others_non_player` / `self`）：`others_non_player` 是 2026-09-23 为
  "负面效果不碰玩家"加的，`AreaMobEffectEffect` 的中毒/缓慢/虚弱/凋零/寄生与
  `AreaIgniteEffect` 的燃烧光环都用它；`AreaIgniteEffect` 默认 `others`（点火会波及玩家），
  `AreaMobEffectEffect` 默认 `all`。
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

## mcfunction → Java 全面迁移（A/B/C 三批 2026-08-31，D 批 2026-09-24 收尾）

- 68 个 mcfunction 于 A/B/C 三批迁完，最后 5 个（`eternal_frost` + `freeze_pic` 动画库）
  于 D 批迁为 `EternalFrostAnimationEffect`（注册名 `eternal_frost_animation`），
  **本仓库已无 `.mcfunction`**。当时判"Java 化为纯数据搬运、收益低"的理由（13 组手调
  block_display 变换矩阵）依然成立——D 批的收益是去掉 `run_function` 这条数据包依赖，
  动画数据本身仍按行照搬在 Java 里（见文末「D 批」条目）。
- **`schedule function` 的 Java 等价要自己实现，`TickTask` 不是定时器**：`tell(new TickTask(
  server.getTickCount()+delay, callback))` 的 tick 只在服务器落后于计划时才是闸门
  （`MinecraftServer#shouldRun` = `tick + 3 < tickCount || haveTime()`，`MinecraftServer.java:852-854`），
  健康服务器每 tick 末走 `waitUntilNextTick()` → `runAllTasks()`（同文件 `:718`、`:833-836`）会把队列一次
  抽干，**排到未来刻的任务当 tick 即执行**，`delay` 形同虚设。本仓库现有实现统一走 `util/DelayedTasks`
  （`ServerTickEvent.Post` + 按 `Level#getGameTime()` 到期）；精准射击因为要跨存档守恒，另走持久化附件（见下）。
  回调照旧先判实体是否还在（`isRemoved()`、是否仍在同一 level）。
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
- **精准射击的清理判据修正（2026-09-24，同日二次修正）**：Java 迁移时写的"每 100 tick 检查，5 格内无玩家则 kill"
  判据是错的——箭初速约 3 格/tick，5 秒后早已远离所有玩家，于是**射向远处或仍在飞行的箭会被直接清掉**。
  ~~改为：NoGravity 保留，删掉玩家距离轮询，只在发射后 1200 tick 做一次兜底 `discard()`。~~
  **这个"改成 1200 tick 延时"同样没生效**：`tell(new TickTask(now + 1200, …))` 在健康服务器上当 tick 就执行
  （见上面的 schedule 条目），玩家看到的是"箭一射出来就没了"——两次修正错在同一处：把 `TickTask` 当定时器。
  落地实现改为**持久化附件 + 每 tick 比对**：`ModAttachments#HOVERING_ARROW_DEADLINE`（`Codec.LONG`，
  值 = `level.getGameTime() + 1200`）+ `event/projectile/HoveringArrowTickEvents`（`EntityTickEvent.Post` 里
  `level.getGameTime() >= deadline` 就 `discard()`）。**附件必须持久化**：箭是会存档的实体
  （`Entity#shouldBeSaved()` 默认 true，`Entity.java:3670-3676`），区块卸载/重载后 `Entity#tickCount`
  从 0 重来（它不写 NBT，全仓库只有 `AreaEffectCloud` 把 Age 存进存档），用 `tickCount` 计数等于
  "每卸载/重载一次就多给 60 秒"；只有绝对的存档时钟 `gameTime` 能让 60 秒守恒。
  窗口取 1200 的依据是原版自己就管落地箭：`AbstractArrow#tickDespawn()` 在 `life >= 1200`（落地 60 秒）时 `discard()`，
  且 `life` 随 NBT 存档——所以自定义清理只需覆盖"NoGravity 导致永不落地"的箭，窗口取同一个 1200。
  另两条同批核实的 API 事实（均来自 api-sources 镜像）：`inGround`/`life` 是 protected/private，外部读不到
"落地了没"，要逐实例判"不持久"得 mixin `Entity#shouldBeSaved()`（`PersistentEntitySectionManager.java:196`）；
  `Entity#kill()` 会额外发 `GameEvent.ENTITY_DIE`（`Entity.java:333-336`），纯清理应当用 `discard()`。

## D 批（2026-09-24）：`Display` 的 transformation 写入

`EternalFrostAnimationEffect`（注册名 `eternal_frost_animation`）替代最后 5 个 mcfunction，
本批验证的运行期事实：

- **`Display` 没有公开写入 API，只能自备 AT**：1.21.1 里
  `Display#setTransformation(Transformation)`、`setTransformationInterpolationDuration(int)`、
  `setTransformationInterpolationDelay(int)`、`Display.BlockDisplay#setBlockState(BlockState)`
  全是 `private`；NeoForge 没有 Display 扩展接口（`ITransformationExtension` 只加
  `isIdentity` / `transformPosition` / `blockCenterToCorner` / `applyOrigin` 这类矩阵数学），
  NeoForge 自己的 AT 清单（`api-sources/META-INF/accesstransformer.cfg`）也没有覆盖 Display。
  两条独立出处一致：`api-sources/net/minecraft/world/entity/Display.java:273,353,361,581` 与真实编译
  classpath `compiledWithNeoForge_50f69430…jar`（`javap -p`）。故本仓库自备
  `src/main/resources/META-INF/accesstransformer.cfg`（4 条），并在 `build.gradle` 用
  `accessTransformers = project.files(...)` 显式声明（不依赖 ModDevGradle 的自动检测）。
- **AT 的 descriptor 必须对准真实版本**：AT 写错**不报编译错**，只在应用 AT 时失败。嵌套类在
  AT 里写 `$`，形如 `net.minecraft.world.entity.Display$BlockDisplay setBlockState(...)V`。
- **`data merge` 的 Java 等价就是这三个 setter**，逐字段对应 `transformation` /
  `interpolation_duration` / `start_interpolation`（`Display.readAdditionalSaveData`，
  `Display.java:210-225`）。**`Entity#load(CompoundTag)` 不是 `data merge`**：它无条件读
  `Pos`/`Motion`/`Rotation`（缺失即归零），`BlockDisplay.readAdditionalSaveData` 还无条件读
  `block_state`（缺失 → 空气）；`load()` 只适合"新建实体"（`RainBlocksEffect` 那种），
  不能用来增量改活实体。
- **插值要先让客户端建立起初态**：客户端 `interpolationDuration` 非 0 且 `renderState != null`
  时才做插值（`Display.java:151-166`），所以原实现 `schedule … 0.1s`（2 tick）后才改
  `interpolation_duration`，Java 侧照搬这个间隔，不要同 tick 内完成 spawn + 改插值
  （这个 2 tick 间隔一度被 `TickTask` 吞掉，2026-09-24 换 `DelayedTasks` 后才真正生效）。
- **`execute at` 不换执行实体**：`ExecuteCommand.java:163-178` 的 `at` 只做
  `withLevel/withPosition/withRotation`。**`schedule function` 的命令源无实体**：
  `FunctionCallback.java:20` → `ServerFunctionManager.getGameLoopSender()`
  （`ServerFunctionManager.java:88-89` = `withPermission(2).withSuppressedOutput()`）。
  于是 `schedule` 出来的函数里 `execute at @e[...] run playsound … @s` 的 `@s` 不指向实体，
  该音效静默失效（错误被 `withSuppressedOutput` 吞掉）——判原 mcfunction 有无死代码先看这条。
- **`@s` 的实体由 `affected` 决定**：`RunFunction` 用 `withEntity(entity)`
  （`RunFunction.java:31-37`），entity 取自 `TargetedConditionalEffect.affected()`
  （`Enchantment.java:324-328`）。本附魔声明 `affected=VICTIM` → POST_ATTACK 路径 `@s` 是受击者、
  `origin` 是受击者位置；HIT_BLOCK 路径 entity 是弹射物、`origin` 是
  `hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation())`
  （`AbstractArrow.java:484-496`）。两条路径的 `@s` 都**不是**攻击者。
- **`schedule function` 会持久化，本仓库的 `DelayedTasks` 不会**：`ScheduleCommand` 把待执行函数写进
  `overworldData().getScheduledEvents()`（`ScheduleCommand.java:101-113`），随存档保存、重启续跑；
  `DelayedTasks` 只是内存队列（`TickTask` 同样是内存队列，但它连"延后"都不成立，见上面的 schedule 条目）。
  断链留下的霜冰由下一条的清扫兜住（见 `enchantment-migrations.md`）。
- **`EntityJoinLevelEvent#loadedFromDisk()` 是"从存档恢复"的可靠判据**：该标志为 true 只有两条
  路径——区块实体反序列化（`PersistentEntitySectionManager.processPendingLoads`，
  `PersistentEntitySectionManager.java:247`）与旧格式区块实体（同文件 `:114`）；自己
  `addFreshEntity` 的走 `addNewEntity`（同文件 `:70-71`），标志为 false；客户端恒为 false。
  该事件在实体**进入 `PersistentEntitySectionManager` 之前**触发且可取消
  （`EntityJoinLevelEvent.java:30` 实现 `ICancellableEvent`），所以"取消加入"比事后 `discard()`
  干净，也避开了它 javadoc 里"不要在此做世界交互，会与区块加载死锁"的警告——本仓库的霜冰残留
  清扫（`event/freeze/FreezeShardCleanupEvents`）就是这么做的，无需任何静态状态。
- 本批核对过签名的 API：`new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, Level)` 公开；
  `Transformation(Vector3f, Quaternionf, Vector3f, Quaternionf)`；
  `Quaternionf(float,float,float,float)`；
  `ServerLevel#sendParticles(T,double,double,double,int,double,double,double,double)`
  （对应 `particle … <delta> <speed> <count>`）；
  `Level#playSound(Player,double,double,double,SoundEvent,SoundSource,float,float)`
  （首参 `null` = 不排除任何玩家）；`SoundEvents.AMETHYST_BLOCK_STEP/BREAK/FALL`；
  `SoundSource.MASTER`；`FrostedIceBlock.AGE`（= `BlockStateProperties.AGE_3`）；
  `Entity#addTag(String)` / `kill()`。
