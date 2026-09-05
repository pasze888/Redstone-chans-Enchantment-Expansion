# Redstone 附魔扩展 · 附魔全表

> **自动生成**：由 `scripts/gen_ench_doc.py` 从 `src/generated/resources/data/redstone_enchants/enchantment/*.json`
> 与 `zh_cn.json` 提取。共 **217** 个附魔。
>
> **数值公式约定**：效果量 = 首级基础值 + 每级增量 × (Lv − 1)，形如 `0.5 + 0.5*(Lv-1)`
> 表示 Lv1 为 0.5，此后每提升 1 级再加 0.5（即 Lv1~5 = 0.5/1.0/1.5/2.0/2.5）。
> 对应 JSON 的线性 LevelBasedValue（`base` + `per_level_above_first`）。
> 时长单位为 tick（20 tick = 1 秒）。"条件触发"表示带 entity_requirements 谓词，详见 JSON。
> 标记型附魔（如自动熔炼）无参数，行为由事件代码实现，见「机制备注」。


## 远程武器（弓/弩）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 失明蔓延（`splash_blindness`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: blindness（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加失明效果并生成区域效果云 |
| 延迟爆破（`splash_delayed_explosion`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: blasting（条件触发）；命中方块: [kill_self] ；攻击后: [all_of] （条件触发） | 箭矢命中生物后让其延迟爆炸 |
| 发光蔓延（`splash_glowing`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: glowing（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加发光效果并生成区域效果云 |
| 饥饿蔓延（`splash_hunger`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: hunger（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加饥饿效果并生成区域效果云 |
| 寄生蔓延（`splash_infested`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: infested（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加寄生效果并生成区域效果云 |
| 渗浆蔓延（`splash_oozing`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: oozing（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加渗浆效果并生成区域效果云 |
| 中毒蔓延（`splash_poison`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: poison（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加中毒效果并生成区域效果云 |
| 治疗蔓延（`splash_regeneration`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: regeneration（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加生命恢复效果并生成区域效果云 |
| 缓降蔓延（`splash_slow_falling`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: slow_falling（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加缓降效果并生成区域效果云 |
| 缓慢蔓延（`splash_slowness`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: slowness（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加缓慢效果并生成区域效果云 |
| 迅捷蔓延（`splash_speed`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: speed（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加速度效果并生成区域效果云 |
| 盘丝蔓延（`splash_weaving`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: weaving（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加盘丝效果并生成区域效果云 |
| 蓄风蔓延（`splash_wind_charged`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: wind_charged（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加蓄风效果并生成区域效果云 |
| 凋零蔓延（`splash_wither`）〔蔓延〕 | 3 | 命中方块: [splash_cloud] 效果: wither（条件触发）；攻击后: [all_of] （条件触发） | 命中目标施加凋零效果并生成区域效果云 |
| 精准射击（`accuracy_shot`） | 1 | 射出弹射物: [hovering_arrow]  | 箭矢无视重力直飞目标，未命中会消失 |
| 空中抛掷（`air_toss`） | 2 | 攻击后: [air_toss] （条件触发）；攻击后: [air_toss] （条件触发） | 攻击时将被击中的生物抛向空中 |
| 爆裂箭矢（`blast_arrows`） | 3 | 命中方块: [explode] 半径 = 1.5 + 0.75*(Lv-1)格（条件触发）；命中方块: [kill_self] ；攻击后: [explode] 半径 = 1.5 + 0.75*(Lv-1)格（条件触发） | 命中产生爆炸伤害 |
| 爆炸箭（`bomb_arrows`） | 3 | 伤害: [add] 值 = 2；命中方块: [all_of] （条件触发）；命中方块: [kill_self] ；攻击后: [explode] 半径 = 0.5 + 0.5*(Lv-1)格（条件触发） | 命中产生强力爆炸和击退 |
| 子弹时间（`bullet_time`） | 3 | 换格触发: [apply_mob_effect] 效果: slow_falling，时长 1.3tick，时长上限 1.3tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 移动时获得缓降效果 |
| 连锁反应（`chain_reaction`） | 3 | 攻击后: [chain_arrows] spectral=True（条件触发）；攻击后: [chain_arrows] （条件触发） | 受伤的生物有几率使它们向附近的生物发射箭矢 |
| 绝命箭矢（`fatal_arrow`） | 1 | 攻击后: [all_of] （条件触发）；攻击后: [kill_self] （条件触发）；攻击后: [all_of] （条件触发） | 一击必杀非Boss生物(无掉落战利品，使用后给予DeBuff) |
| 火焰箭（`fire_arrows`） | 1 | 命中方块: [ignite_area] （条件触发）；命中方块: [kill_self] （条件触发）；攻击后: [ignite_area] （条件触发）；攻击后: [kill_self] （条件触发） | 命中区域产生火焰效果 |
| 焦点（`focus`） | 3 | projectile_spread: [add] 值 = -5 - 5*(Lv-1) | 减少每支箭的散布 |
| 赌徒（`gambler`） | 1 | gambler_data: 引用 bonus_multiplier；gambler_data: 引用 odds；gambler_data: 引用 penalty_multiplier | 50%概率伤害+40%，50%概率伤害-20% |
| 玻璃破坏者（`glass_breaker`） | 1 | 命中方块: [replace_block] （条件触发）；命中方块: [spawn_particles] speed=0.3 | 命中区域破坏玻璃 |
| 冰霜箭（`ice_arrows`） | 1 | 命中方块: [freeze_water] （条件触发）；命中方块: [kill_self] （条件触发）；攻击后: [all_of] （条件触发）；攻击后: [kill_self] （条件触发） | 命中区域产生冻结效果，有水则冻结成冰 |
| 石锥雨（`rain_dripstone`） | 1 | 命中方块: [all_of] （条件触发）；命中方块: [kill_self] ；攻击后: [all_of] （条件触发） | 命中区域生成滴水石锥攻击 |
| 铁砧雨（`rain_forge`） | 1 | 命中方块: [all_of] （条件触发）；命中方块: [kill_self] ；攻击后: [all_of] （条件触发） | 命中区域生成铁砧攻击 |
| 速射（`rapid`） | 4 | 属性: 数值 = 0.3 + 0.15*(Lv-1)，属性 draw_speed（乘总量） | 增加拉弓速度 |
| 跳弹（`ricochet`） | 3 | 攻击后: [ricochet] （条件触发）；projectile_piercing: [add] 值 = 1 + 1*(Lv-1) | 击中一个生物会让箭从生物身上反弹并击中附近的另一个生物 |
| 分散（`scatter`） | 3 | projectile_spread: [add] 值 = 10 + 10*(Lv-1) | 增加每支箭的散布 |
| 霰弹枪（`shotgun`） | 6 | crossbow_charge_time: 引用 type；crossbow_charge_time: 引用 value；耐久: [multiply] （条件触发）；projectile_count: [add] 值 = 0 + 1*(Lv-1)；projectile_spread: [add] 值 = 0 + 1*(Lv-1) | 一次射出多支箭矢 |
| 狙击（`snipe`） | 4 | snipe_bonus: [add] 值 = 0.15 + 0.15*(Lv-1) | 与目标距离每增加10格，造成的伤害提高 |
| 伏特（`volt`） | 4 | volt_bonus: [add] 值 = 0.25 + 0.25*(Lv-1) | 雷雨天射出的箭矢伤害增加 |

## 远程武器（弓/三叉戟）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 永恒冰霜（`eternal_frost`） | 3 | 命中方块: [replace_disk] 半径 = 3 + 2*(Lv-1)格（条件触发）；命中方块: [run_function] function=redstone_enchants:enchantment/eternal_frost（条件触发）；命中方块: [kill_self] （条件触发）；攻击后: [run_function] function=redstone_enchants:enchantment/eternal_frost；攻击后: [apply_mob_effect] 效果: slowness，时长 1 + 1*(Lv-1)tick，时长上限 3 + 1*(Lv-1)tick，等级 0 + 1*(Lv-1)~1 + 1*(Lv-1) | 命中后冻结目标及周围水域并将水转为霜冰 |
| 传送（`teleport`） | 1 | （纯标记型，行为见代码） | 命中后与目标交换位置 |

## 远程武器（弓/弩/三叉戟）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 水生克星（`bane_water`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: slowness，时长 1.5tick，时长上限 1.5 + 0.5*(Lv-1)tick，等级 3~3（条件触发） | 对海洋生物造成额外伤害 |
| 海风（`sea_breeze`） | 3 | sea_breeze_damage: [set] 值 = 4 + 2*(Lv-1) | 命中时产生推进风弹 |
| 投掷强化（`throwing_enhancement`） | 5 | 伤害: [add] 值 = 3 + 1.5*(Lv-1)（条件触发） | 提升投掷物伤害 |
| 雷鸣（`thundering`） | 1 | 命中方块: [all_of] （条件触发） | 雨天命中避雷针时引发雷暴 |
| 水下爆破（`underwater_blasting`） | 1 | 攻击后: [summon_entity] entity=minecraft:tnt（条件触发） | 水中攻击时召唤点燃的TNT |

## 近战武器（剑/斧）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 微小克星（`bane_bady`）〔克星〕 | 5 | 伤害: [multiply] （条件触发） | 对小型生物造成多倍伤害 |
| Boss克星（`bane_boss`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: slowness，时长 1.5tick，时长上限 1.5 + 0.5*(Lv-1)tick，等级 3~3（条件触发） | 对Boss生物造成额外伤害 |
| 末地克星（`bane_end`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: slowness，时长 1.5tick，时长上限 1.5 + 0.5*(Lv-1)tick，等级 3~3（条件触发） | 对末地生物造成额外伤害(Boss除外) |
| 灾厄克星（`bane_illager`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: slowness，时长 1.5tick，时长上限 1.5 + 0.5*(Lv-1)tick，等级 3~3（条件触发） | 对袭击生物造成额外伤害 |
| 下界克星（`bane_nether`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: slowness，时长 1.5tick，时长上限 1.5 + 0.5*(Lv-1)tick，等级 3~3（条件触发） | 对下界生物造成额外伤害(Boss除外) |
| 幻翼克星（`bane_phantom`）〔克星〕 | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发） | 对幻翼造成额外伤害 |
| 伏击（`ambush`） | 5 | ambush_bonus: [add] 值 = 0.2 + 0.2*(Lv-1) | 潜行时首次攻击增加伤害 |
| 背刺（`backstab`） | 5 | backstab_behind_bonus: [add] 值 = 0.3 + 0.3*(Lv-1)；backstab_front_penalty: [add] 值 = 0.15 + 0.15*(Lv-1) | 从背后攻击时造成额外伤害，正面攻击则减少伤害 |
| 福佑（`boons`） | 5 | 攻击后: [random_beneficial_mob_effect] 概率 = 0.05 + 0.05*(Lv-1) | 攻击时有概率获得随机一个正面效果 |
| 屠夫（`butcher`） | 5 | butcher_extra_drop: [add] 值 = 0.5 + 0.5*(Lv-1) | 击杀动物时增加其掉落物 |
| 灾厄（`calamity`） | 5 | 攻击后: [random_harmful_mob_effect] 概率 = 0.05 + 0.05*(Lv-1) | 攻击时有概率让目标获得随机一个负面效果 |
| 锁链（`chains`） | 4 | 攻击后: [chain_bind] （条件触发） | 击中生物会导致自身和附近的多个生物在短时间内被眩晕 |
| 斩首（`decapitation`） | 5 | decapitation_chance: [add] 值 = 0.2 + 0.2*(Lv-1) | 增加生物头颅的掉落概率 |
| 吞噬（`devouring`） | 1 | 攻击后: [devouring] （条件触发） | 攻击生物时恢复饥饿值，满饥饿时转为生命恢复 |
| 发电机（`dynamo`） | 4 | 伤害: [add] 值 = 1 + 1*(Lv-1)（条件触发）；knockback: [add] 值 = 0.5 + 0.5*(Lv-1)（条件触发） | 冲刺时增加攻击力 |
| 均衡器（`equalizer`） | 5 | equalizer_bonus: [add] 值 = 0.2 + 0.2*(Lv-1) | 伤害根据目标血量百分比变化；目标血量越高，伤害越高 |
| 处决（`execution`） | 1 | （纯标记型，行为见代码） | 对生命值低于25%的目标直接秒杀 |
| 生命吸取（`life_steal`） | 5 | life_steal_ratio: [set] 值 = 0.1 | 攻击时恢复造成伤害10%的生命值 |
| 消解（`nullify`） | 5 | 攻击后: [remove_random_beneficial] 概率 = 0.05 + 0.05*(Lv-1) | 攻击时有概率移除目标身上的一个正面效果 |
| 抗击之盾（`resilience_shield`） | 4 | 属性: 数值 = 0.2 + 0.1*(Lv-1)，属性 generic.knockback_resistance（乘基数） | 提升击退抗性 |
| 影刺（`shadow_pierce`） | 3 | 伤害: [multiply] （条件触发） | 攻击处于隐身或黑暗效果下的目标时，会造成多倍伤害 |
| 迅捷斩影（`swift_shadowcutter`） | 3 | 属性: 数值 = 0.05 + 0.15*(Lv-1)，属性 generic.attack_speed（乘基数） | 提升攻击速度 |
| 伐木（`timber`） | 1 | （纯标记型，行为见代码） | 砍伐树木时，同时破坏其上方所有同种原木 |
| 流血之触（`touch_bleeding`） | 3 | 攻击后: [all_of] （条件触发） | 攻击时使目标流血 |
| 毒触之刃（`touch_poison`） | 3 | 攻击后: [all_of] （条件触发） | 攻击时使目标中毒 |
| 凋零之触（`touch_wither`） | 3 | 攻击后: [all_of] （条件触发） | 攻击时使目标凋零 |
| 越战越勇（`under_pressure`） | 3 | 攻击后: [all_of] （条件触发） | 攻击时获得急迫、力量和迅捷效果 |
| 暗流涌动（`undercurrent`） | 3 | 属性: 数值 = 2 + 1.5*(Lv-1)，属性 cold_damage（加值）；攻击后: [all_of] （条件触发） | 攻击时造成冰霜伤害并附加霜冻、凋零和虚弱效果 |
| 激发潜能（`unleash_potential`） | 1 | 换格触发: [unleash_potential]  | 饥饿值低于3鸡腿时获得急迫、速度、力量(随饥饿值降低而增强) |
| 水瓶投射（`water_bottle_projection`） | 2 | 攻击后: [throw_water_bottle] （条件触发）；攻击后: [throw_water_bottle] （条件触发） | 攻击时向目标投掷水瓶 |
| 沉重（`weighted`） | 5 | 属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 generic.attack_damage（乘总量）；属性: 数值 = -0.05 - 0.05*(Lv-1)，属性 generic.attack_speed（乘总量） | 减慢攻击速度同时增加攻击伤害 |
| 经验收割者（`xp_reaper_mobs`） | 3 | mob_experience: [multiply]  | 击杀生物时获得额外经验(只支持直接击杀) |

## 近战武器（剑/斧/重锤）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 承诺（`committed`） | 3 | 伤害: [multiply] （条件触发）；攻击后: [all_of] （条件触发） | 击中已经受伤的生物会造成更高的伤害 |
| 驱魔（`exorcism`） | 5 | 伤害: [add] 值 = 2.5 + 2.5*(Lv-1)（条件触发）；攻击后: [apply_mob_effect] 效果: weakness/slowness，时长 1.5tick，时长上限 3 + 0.5*(Lv-1)tick，等级 1~1（条件触发） | 对悦灵/恼鬼/烈焰人/旋风人/女巫/唤魔者造成高额伤害 |
| 第一印象（`first_impression`） | 5 | 伤害: [add] 值 = 5 + 2.5*(Lv-1)（条件触发）；攻击后: [add_tag] tag=redstone_enchants.first_impression | 对未攻击过的目标(包括横扫击中)造成额外伤害 |
| 最后的希望（`last_hope`） | 1 | 伤害: [add] 值 = 2147483600（条件触发）；攻击后: [clear_main_hand] （条件触发）；攻击后: [particle_burst] 数量 = 50 | 消耗武器对目标造成一击必杀效果(使用后武器消失) |
| 影袭（`shadow_assault`） | 1 | 伤害: [multiply] factor=2.0（条件触发）；伤害: [multiply] factor=2.0（条件触发） | 攻击时有概率造成双倍伤害 |
| 经验利刃（`xp_blade`） | 4 | 伤害: [add] 值 = 1 + 1.25*(Lv-1)（条件触发）；攻击后: [add_experience] points=-15（条件触发） | 消耗15点经验值提升攻击伤害(需10级启动) |

## 重锤

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 闪电使者（`boltbringer`） | 1 | （纯标记型，行为见代码） | 从高处(≥8格)攻击时召唤闪电 |
| 俯冲炸弹（`dive_bomb`） | 3 | 属性: 数值 = 0.2 + 0.2*(Lv-1)，属性 generic.jump_strength（加值）；攻击后: [explode] 半径 = 2 + 1*(Lv-1)格（条件触发） | 提供跳跃加成，从高处(≥4格)攻击时引发爆炸 |
| 轻重两仪（`lightness-heavy_dualism`） | 5 | 属性: 数值 = -0.12 - 0.02*(Lv-1)，属性 generic.gravity（乘基数）；属性: 数值 = 0.8 + 0.05*(Lv-1)，属性 player.entity_interaction_range（加值） | 增加攻击范围并降低重力 |
| 势能转化（`potential_conversion`） | 4 | potential_conversion_armor_factor: [set] 值 = 0.015；potential_conversion_fall_bonus: [add] 值 = 0.008 + 0.008*(Lv-1) | 下落攻击时，每格高度都增加伤害，并对有护甲的目标造成额外伤害 |
| 重击手（`slugger`） | 3 | 攻击后: [all_of] （条件触发） | 攻击有几率对生物造成强力减速 |
| 风力推进（`wind_propulsion`） | 3 | 命中方块: [explode] 半径 = 3.5格（条件触发） | 攻击地面可将自己弹向空中 |

## 工具（镐/斧/锹/锄）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 死亡诅咒（`curse_of_death`）〔诅咒〕 | 1 | 攻击后: [kill_self] （条件触发） | 攻击时有几率立即死亡 |
| 双刃剑诅咒（`curse_of_double_edge`）〔诅咒〕 | 1 | 攻击后: [damage_entity] damage_type=minecraft:magic，max_damage=4.0，min_damage=2.0（条件触发） | 使用时有几率伤害自己 |
| 隐形诅咒（`curse_of_hiding`）〔诅咒〕 | 1 | 攻击后: [apply_mob_effect] 效果: invisibility，时长 2 + 3*(Lv-1)tick，时长上限 5 + 5*(Lv-1)tick，等级 1 + 1*(Lv-1)~1 + 1*(Lv-1)（条件触发） | 攻击会使目标生物隐形 |
| 短手诅咒（`curse_of_reach`）〔诅咒〕 | 3 | 属性: 数值 = -1 - 1*(Lv-1)，属性 player.block_interaction_range（加值）；属性: 数值 = -1 - 1*(Lv-1)，属性 player.entity_interaction_range（加值） | 减少交互和攻击距离 |
| 自动熔炼（`auto_smelt`） | 1 | （纯标记型，行为见代码） | 挖掘的方块自动熔炼成对应成品 |
| 连锁效率（`chain_haste`） | 3 | chain_haste_bonus: [add] 值 = 0.01 + 0.01*(Lv-1) | 挖掘方块时挖掘周围同类型方块 |
| 挖掘机（`excavator`） | 3 | area_break_radius: [add] 值 = 1 + 1*(Lv-1) | 可以挖掘3x3/5x5/7x7区域的方块 |
| 延伸（`extend`） | 3 | 属性: 数值 = 1 + 1*(Lv-1)，属性 player.block_interaction_range（加值）；属性: 数值 = 1 + 1*(Lv-1)，属性 player.entity_interaction_range（加值） | 增加攻击/交互距离 |
| 急速（`haste`） | 3 | 属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 player.block_break_speed（乘总量） | 手持时获得急迫效果 |
| 轻便（`lightweight`） | 4 | 属性: 数值 = 0.05 + 0.05*(Lv-1)，属性 generic.movement_speed（乘总量） | 手持时增加移动速度 |
| 精通采集（`master_gatherer`） | 3 | ore_double_drop_chance: [add] 值 = 0.2 + 0.2*(Lv-1) | 挖掘矿石时有概率触发双倍掉落 |
| 经验源泉（`xp_spring_block`） | 3 | block_experience: [multiply]  | 挖掘时获得额外经验 |

## 工具（镐）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 基岩破坏者（`bedrock_breaker`） | 1 | 命中方块: [play_sound] pitch=2.0，sound=minecraft:entity.generic.explode，volume=1.0；命中方块: [all_of] （条件触发） | 允许破坏基岩(不产生掉落，消耗1.5K耐久) |
| 地质学（`geology`） | 5 | stone_to_ore_chance: [add] 值 = 0.05 + 0.05*(Lv-1) | 挖掘石头有概率掉落矿石 |
| 点石成金（`goldfinger`） | 4 | stone_to_gold_chance: [add] 值 = 0.025 + 0.025*(Lv-1) | 挖掘石头类方块有概率掉落金粒 |
| 磁力（`magnet`） | 4 | magnet_range: [add] 值 = 4 + 4*(Lv-1) | 吸引附近的掉落物到身边 |
| 幻岩转化（`rock_illusion`） | 1 | 命中方块: [damage_item] 数值 = 1000（条件触发）；命中方块: [replace_block] trigger_game_event=minecraft:block_destroy；命中方块: [spawn_particles] speed=0.5（条件触发） | 将基岩转化为强化深板岩(消耗1K耐久) |

## 工具（锄）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 湿润（`moist`） | 1 | （纯标记型，行为见代码） | 犁过的地会湿润一会 |

## 工具（打火石）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 灼烧（`searing`） | 3 | searing_damage: [set] 值 = 1 + 1*(Lv-1)；searing_fire_ticks: [set] 值 = 40 + 40*(Lv-1) | 右键目标时使其燃烧 |

## 钓鱼竿

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 渔夫（`angler`） | 5 | angler_double_chance: [add] 值 = 0.1 + 0.1*(Lv-1) | 钓鱼后收获鱼的数量有概率翻倍 |
| 导电鱼线（`conductive_line`） | 1 | （纯标记型，行为见代码） | 雷雨天时，勾住生物产生闪电 |
| 潮汐感应（`tide_sense`） | 3 | fishing_time_reduction: [add] 值 = 10 + 10*(Lv-1)；tide_sense_fish_chance: [set] 值 = 0.8 + 0.1*(Lv-1) | 雷雨天时，钓鱼等待时间大幅缩短且鱼的几率大幅提高 |

## 剪刀

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 绵延不绝（`endless_wool`） | 4 | endless_wool_regrow_chance: [add] 值 = 0.1 + 0.1*(Lv-1) | 剪羊毛时，有概率让羊立刻重新长出羊毛 |
| 经验修剪（`experience_shear`） | 3 | experience_shear_exp_per_level: [set] 值 = 3 + 3*(Lv-1) | 剪下的羊毛改为掉落随机的经验球 |
| 收获回响（`harvest_echo`） | 1 | （纯标记型，行为见代码） | 剪刀右键使用时，获得生命恢复 |
| 牧羊人（`shepherd`） | 4 | shepherd_extra_chance: [add] 值 = 0.2 + 0.2*(Lv-1) | 修剪羊毛或采集蜜脾时有概率获得多个 |

## 盾牌（副手）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 吸收护盾（`absorbent_shield`） | 2 | 每tick: [apply_mob_effect] 效果: absorption，时长 11tick，时长上限 11tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1)（条件触发） | 获得10秒伤害吸收效果(冷却10秒) |
| 战斗回响（`echoes_battle`） | 3 | （纯标记型，行为见代码） | 盾牌格挡后，提升下一次攻击的伤害 |
| 盾甲（`shield_armor`） | 5 | 属性: 数值 = 2.5 + 1.5*(Lv-1)，属性 generic.armor（加值） | 副手持盾时增加护甲值 |
| 抗退盾牌（`stable_shield`） | 2 | 属性: 数值 = 1，属性 generic.knockback_resistance（加值） | 副手持盾时免疫击退 |
| 力量之盾（`strength_shield`） | 3 | 攻击后: [apply_mob_effect] 效果: strength，时长 2 + 1*(Lv-1)tick，时长上限 4 + 3*(Lv-1)tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1)（条件触发） | 被击中时有几率获得力量效果 |

## 头盔

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 恐高症（`curse_of_acrophobia`）〔诅咒〕 | 1 | 每tick: [apply_mob_effect] 效果: nausea/slowness，时长 11tick，时长上限 11tick，等级 2~2（条件触发） | 当高度超过180时获得反胃、缓慢和霜冻效果 |
| 低洼恐惧症（`curse_of_basiphobia`）〔诅咒〕 | 1 | 每tick: [apply_mob_effect] 效果: mining_fatigue/darkness，时长 11tick，时长上限 11tick，等级 2~2（条件触发） | 当高度低于-36时获得挖掘疲劳和黑暗效果 |
| 失明诅咒（`curse_of_blindness`）〔诅咒〕 | 3 | 每tick: [apply_mob_effect] 效果: blindness，时长 3tick，时长上限 3tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 使穿戴者持续获得失明效果 |
| 饥饿诅咒（`curse_of_hunger`）〔诅咒〕 | 4 | 每tick: [apply_mob_effect] 效果: hunger，时长 0.2tick，时长上限 0.2tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 使穿戴者持续获得饥饿效果 |
| 倒霉诅咒（`curse_of_unlucky`）〔诅咒〕 | 4 | 每tick: [apply_mob_effect] 效果: unluck，时长 0.2tick，时长上限 0.2tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 你的运气会变差 |
| [移动轨迹]樱花（`trail_cherry_leaves`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下樱花花瓣粒子效果 |
| [移动轨迹]龙息（`trail_dragon_breath`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下龙息粒子效果 |
| [移动轨迹]烟花火箭（`trail_firework`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下烟花火箭尾迹粒子效果 |
| [移动轨迹]荧光（`trail_glow`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下荧光粒子效果 |
| [移动轨迹]幽匿（`trail_sculk_soul`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下幽匿粒子效果 |
| [移动轨迹]雪花（`trail_snowflake`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下雪花粒子效果 |
| [移动轨迹]试炼之兆（`trail_trial_omen`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下试炼之兆粒子效果 |
| [移动轨迹]白色星星（`trail_wax_off`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下白色星星粒子效果 |
| [移动轨迹]橙色星星（`trail_wax_on`）〔轨迹〕 | 1 | 换格触发: [trail_particle]  | 移动时留下橙色星星粒子效果 |
| 矿工（`adaptive`） | 1 | （纯标记型，行为见代码） | 在低于Y0时获得夜视效果 |
| 以寡敌众（`against_all_odds`） | 5 | against_all_odds_bonus_per_enemy: [set] 值 = 0.02 + 0.02*(Lv-1) | 根据周围敌人数目，每多一个敌人伤害/护甲+2% |
| 反伪装（`anti_camouflage`） | 1 | anti_camouflage_duration_bonus: [add] 值 = 10 + 10*(Lv-1) | 潜行时显示周围的敌对生物 |
| 绝境逆袭（`desperate_counter`） | 5 | desperate_counter_damage: [set] 值 = 0.25 + 0.25*(Lv-1) | 自身拥有失明或黑暗时增加攻击伤害 |
| 幸运增幅（`lucky_boost`） | 5 | 属性: 数值 = 2 + 2.5*(Lv-1)，属性 generic.luck（加值） | 提升幸运值 |
| 幸运之光（`lucky_light`） | 5 | 每tick: [apply_mob_effect] 效果: luck，时长 0.2tick，时长上限 0.2tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 获得幸运效果 |
| 巨大化（`maximization`） | 4 | 属性: 数值 = 查表 [0.15, 0.25, 0.35, 0.5]（超出后 0.25 + 0.25*(Lv-1)），属性 generic.scale（乘基数）；属性: 数值 = 0.25 + 0.25*(Lv-1)，属性 generic.step_height（加值）；属性: 数值 = 1 + 0.5*(Lv-1)，属性 generic.attack_damage（加值）；属性: 数值 = 2.5 + 2.5*(Lv-1)，属性 generic.max_health（加值）；属性: 数值 = 0.15 + 0.1*(Lv-1)，属性 player.entity_interaction_range（乘总量）；属性: 数值 = 0.15 + 0.1*(Lv-1)，属性 player.block_interaction_range（乘总量）；属性: 数值 = -0.015 - 0.01*(Lv-1)，属性 generic.movement_speed（加值）；属性: 数值 = -0.04 - 0.02*(Lv-1)，属性 generic.jump_strength（加值） | 增大玩家体型 |
| 迷你化（`minify`） | 4 | 属性: 数值 = 查表 [-0.15, -0.25, -0.35, -0.5]（超出后 -0.15 - 0.15*(Lv-1)），属性 generic.scale（乘基数）；属性: 数值 = -0.25 - 0.25*(Lv-1)，属性 generic.step_height（加值）；属性: 数值 = -1 - 0.5*(Lv-1)，属性 generic.attack_damage（加值）；属性: 数值 = -2.5 - 2.5*(Lv-1)，属性 generic.max_health（加值）；属性: 数值 = -0.15 - 0.1*(Lv-1)，属性 player.entity_interaction_range（乘总量）；属性: 数值 = -0.15 - 0.1*(Lv-1)，属性 player.block_interaction_range（乘总量）；属性: 数值 = 0.015 + 0.01*(Lv-1)，属性 generic.movement_speed（加值）；属性: 数值 = 0.04 + 0.02*(Lv-1)，属性 generic.jump_strength（加值） | 缩小玩家体型 |

## 胸甲

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 狂战士（`berserk`） | 5 | berserk_damage_bonus: [set] 值 = 0.03 + 0.03*(Lv-1) | 根据损失的生命值百分比增加近战伤害 |
| 防弹（`bulletproof`） | 3 | bulletproof_immunity_chance: [set] 值 = 0.5 + 0.25*(Lv-1) | 有概率免疫潜影子弹伤害 |
| 双重暴击（`dual_critical_boost`） | 5 | 属性: 数值 = 0.233 + 0.126*(Lv-1)，属性 crit_chance（加值）；属性: 数值 = 0.582 + 0.286*(Lv-1)，属性 crit_damage（加值） | 提升暴击率和暴击伤害 |
| 冰霜荆棘（`frost_thorn`） | 5 | 攻击后: [all_of] ；攻击后: [all_of] ；攻击后: [apply_mob_effect] 效果: slowness，时长 1 + 1*(Lv-1)tick，时长上限 5 + 5*(Lv-1)tick，等级 0 + 0.25*(Lv-1)~1 + 0.5*(Lv-1)；每tick: [spawn_particles] speed=0.25（条件触发） | 被攻击时使攻击者获得缓慢/虚弱效果 |
| 坚毅壁垒（`resilient_bastion`） | 3 | 每tick: [apply_mob_effect] 效果: resistance，时长 0.2tick，时长上限 0.2tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1) | 提升抗性效果 |
| 检索（`retrieval`） | 4 | 攻击后: [give_item] item=minecraft:spectral_arrow（条件触发）；攻击后: [give_item] item=minecraft:arrow（条件触发） | 将射向你的箭收入背包 |
| 法术防护（`spell_magic_resist`） | 5 | 属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 ice_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 blood_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 holy_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 nature_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 eldritch_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 lightning_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 evocation_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 ender_magic_resist（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 fire_magic_resist（加值） | 增强[铁魔法]的法术抗性 |
| 法术增幅（`spell_power`） | 5 | 属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 ice_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 blood_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 holy_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 nature_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 eldritch_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 lightning_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 evocation_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 ender_spell_power（加值）；属性: 数值 = 0.1 + 0.1*(Lv-1)，属性 fire_spell_power（加值） | 增强[铁魔法]的法术强度 |
| 活力（`vitality`） | 4 | 属性: 数值 = 0.25 + 0.25*(Lv-1)，属性 generic.max_health（乘基数） | 提升最大生命值 |

## 护腿

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 快速游泳（`fast_swim`） | 1 | 每tick: [apply_mob_effect] 效果: dolphins_grace，时长 4tick，时长上限 8tick，等级 0~0（条件触发） | 大幅提高水中移动速度 |
| 潜行庇佑（`fortress_stance`） | 4 | 每tick: [apply_mob_effect] 效果: slowness/resistance，时长 0.8tick，时长上限 0.8tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1)（条件触发） | 蹲伏时获得抗性提升(伴随缓慢效果) |
| 隐身斗篷（`invisibility_cloak`） | 1 | （纯标记型，行为见代码） | 蹲伏时获得隐身效果 |
| 抗性守护（`resilience_sentinel`） | 4 | 攻击后: [all_of] （条件触发）；攻击后: [all_of] （条件触发） | 被攻击时概率获得抗性提升/使攻击者虚弱 |
| 安全坠落（`safe_fall`） | 4 | 属性: 数值 = 2 + 1*(Lv-1)，属性 generic.safe_fall_distance（加值） | 增加安全坠落高度 |
| 跨步（`striding`） | 3 | 属性: 数值 = 0.5 + 0.25*(Lv-1)，属性 generic.step_height（加值）；属性: 数值 = 0 + 0.05*(Lv-1)，属性 generic.movement_speed（乘基数） | 提升行走高度和基础移动速度 |
| 战术护膝（`tactical_knee`） | 1 | （纯标记型，行为见代码） | 落地时潜行可抵消掉落伤害 |

## 靴子

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 燃烧光环（`aura_burning`）〔光环〕 | 1 | 换格触发: [area_ignite] 半径 = 2格；换格触发: [area_mob_effect] 半径 = 2格，效果: fire_resistance | 移动过程中，自身的2格范围内产生火焰(同时获得火焰抗性) |
| 发光光环（`aura_glowing`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: glowing | 移动过程中，自身的4格范围内产生发光效果 |
| 急迫光环（`aura_haste`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: haste | 移动过程中，自身的4格范围内产生急迫效果 |
| 寄生光环（`aura_infested`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: infested | 移动过程中，自身的4格范围内产生寄生效果 |
| 跳跃光环（`aura_jump_boost`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: jump_boost | 移动过程中，自身的4格范围内产生跳跃提升效果 |
| 中毒光环（`aura_poison`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: poison | 移动过程中，自身的4格范围内产生中毒效果 |
| 生命光环（`aura_regeneration`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: regeneration | 移动过程中，自身的4格范围内产生生命恢复效果 |
| 抗性光环（`aura_resistance`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: resistance | 移动过程中，自身的4格范围内产生抗性提升效果 |
| 缓慢光环（`aura_slowness`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: slowness | 移动过程中，自身的4格范围内产生缓慢效果 |
| 迅捷光环（`aura_speed`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: speed | 移动过程中，自身的4格范围内产生迅捷效果 |
| 力量光环（`aura_strength`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: strength | 移动过程中，自身的4格范围内产生力量效果 |
| 虚弱光环（`aura_weakness`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: weakness | 移动过程中，自身的4格范围内产生虚弱效果 |
| 凋零光环（`aura_wither`）〔光环〕 | 1 | 换格触发: [area_mob_effect] 半径 = 4格，效果: wither | 移动过程中，自身的4格范围内产生凋零效果 |
| 重力诅咒（`curse_of_gravity`）〔诅咒〕 | 3 | 属性: 数值 = 0.02 + 0.06*(Lv-1)，属性 generic.gravity（加值） | 增加坠落速度和伤害 |
| 草皮行者（`walker_grass`）〔行者〕 | 2 | damage_immunity: （条件触发）；换格触发: [replace_disk] 半径 = clamp(3 + 1*(Lv-1)，0~16)格（条件触发）；换格触发: [spawn_particles] speed=1.0 | 行走时将泥土变为草方块 |
| 熔岩行者（`walker_magma`）〔行者〕 | 2 | damage_immunity: （条件触发）；换格触发: [replace_disk] 半径 = clamp(3 + 1*(Lv-1)，0~16)格（条件触发） | 行走时生成将熔岩变为玄武岩 |
| 融雪行者（`walker_snowmelt`）〔行者〕 | 2 | damage_immunity: （条件触发）；换格触发: [replace_disk] 半径 = clamp(3 + 1*(Lv-1)，0~16)格（条件触发）；换格触发: [spawn_particles] speed=1.0 | 行走时将雪融化 |
| 庄稼舞（`crop_dance`） | 1 | crop_dance_growth_chance: [set] 值 = 0.2 + 0.1*(Lv-1) | 蹲下时走过农田会加速周围作物生长 |
| 踏焰者（`flame_walker`） | 1 | （纯标记型，行为见代码） | 可在岩浆上行走，潜行时下潜到岩浆下 |
| 疾驰（`gallop`） | 3 | 属性: 数值 = 0.2 + 0.2*(Lv-1)，属性 generic.movement_speed（乘基数） | 提升奔跑速度 |
| 末路疾行（`gallop_end`） | 3 | 换格触发: [attribute] 数值 = 0.0405 + 0.0105*(Lv-1)，属性 generic.movement_speed（加值）（条件触发）；换格触发: [attribute] 数值 = 1，属性 generic.movement_efficiency（加值）（条件触发）；换格触发: [damage_item] 数值 = 1（条件触发）；每tick: [spawn_particles] speed=1.0（条件触发）；每tick: [play_sound] sound=minecraft:particle.soul_escape，volume=0.6（条件触发） | 末地维度获得速度加成 |
| 沙地疾行（`gallop_sand`） | 3 | 换格触发: [attribute] 数值 = 0.0405 + 0.0105*(Lv-1)，属性 generic.movement_speed（加值）（条件触发）；换格触发: [attribute] 数值 = 1，属性 generic.movement_efficiency（加值）（条件触发）；换格触发: [damage_item] 数值 = 1（条件触发）；每tick: [spawn_particles] speed=1.0（条件触发） | 提升沙地移动速度 |
| 弹跳强化（`jump_amplifier`） | 3 | 属性: 数值 = 0.15 + 0.25*(Lv-1)，属性 generic.jump_strength（乘总量）；属性: 数值 = 0.15 + 0.25*(Lv-1)，属性 generic.safe_fall_distance（乘总量） | 增加跳跃高度 |
| 飞马座（`pegasus`） | 1 | （纯标记型，行为见代码） | 骑乘时获得缓降效果 |
| 踏浪者（`wave_walker`） | 1 | （纯标记型，行为见代码） | 可在水面行走，潜行时下潜到水下 |

## 全身护甲

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 自爆（`curse_of_blast`）〔诅咒〕 | 1 | 攻击后: [all_of] （条件触发） | 受到伤害时有几率引发爆炸 |
| 静止诅咒（`curse_of_stillness`）〔诅咒〕 | 1 | 每tick: [damage_entity] damage_type=minecraft:dry_out，max_damage=0.5，min_damage=0.0（条件触发） | 当玩家静止不动时会受到伤害 |
| 脆弱诅咒（`curse_of_vulnerability`）〔诅咒〕 | 3 | 属性: 数值 = -0.2，属性 generic.armor（乘总量）；属性: 数值 = 0.1，属性 generic.movement_speed（乘总量） | 减少护甲同时增加移动速度 |
| 水源诅咒（`curse_of_water_source`）〔诅咒〕 | 1 | （纯标记型，行为见代码） | 接触水会给予中毒效果 |
| 日光防护（`protection_day`）〔防护〕 | 4 | damage_protection: [add] 值 = 2 + 2*(Lv-1)（条件触发） | 白天保护效果×2(生效时间6:00~18:00) |
| 末地防护（`protection_end`）〔防护〕 | 4 | damage_protection: [add] 值 = 2 + 2*(Lv-1)（条件触发） | 末地维度保护效果×2 |
| 下界防护（`protection_nether`）〔防护〕 | 4 | damage_protection: [add] 值 = 2 + 2*(Lv-1)（条件触发） | 下界维度保护效果×2 |
| 夜间防护（`protection_night`）〔防护〕 | 4 | damage_protection: [add] 值 = 2 + 2*(Lv-1)（条件触发） | 夜晚保护效果×2(生效时间18:00~6:00) |
| 锋芒之险（`dangerous_edge`） | 3 | 属性: 数值 = -2.5 - 1.5*(Lv-1)，属性 generic.armor（加值）；属性: 数值 = 2.5 + 1.5*(Lv-1)，属性 generic.attack_damage（加值） | 提升攻击力但降低防御 |
| 昼夜流转（`daynight_cycle`） | 1 | （纯标记型，行为见代码） | 白天增加伤害，夜晚增加移动速度 |
| 外骨骼（`exoskeleton`） | 4 | 属性: 数值 = 4 + 2*(Lv-1)，属性 generic.armor_toughness（加值） | 增加盔甲韧性 |
| 消防（`fire_protection`） | 4 | 属性: 数值 = -0.25 - 0.25*(Lv-1)，属性 generic.burning_time（乘基数）；damage_protection: [add] 值 = 2 + 2*(Lv-1)（条件触发） | 减少火焰持续时间 |
| 坚定（`fortitude`） | 4 | 属性: 数值 = 0.14 + 0.12*(Lv-1)，属性 generic.armor（乘基数） | 提升护甲值 |
| 狂怒（`fury`） | 4 | armor_effectiveness: [add] 值 = -0.045 - 0.035*(Lv-1)；属性: 数值 = 0.2 + 0.2*(Lv-1)，属性 generic.attack_damage（乘总量）；属性: 数值 = -0.1 - 0.2*(Lv-1)，属性 generic.armor（乘总量） | 提升攻击力和穿透但降低防御 |
| 重生护盾（`revive_ward`） | 1 | （纯标记型，行为见代码） | 受到致命伤害时阻止死亡，并给予生命恢复/抗性提升和抗火，触发后该附魔消失1个等级 |
| 雪球（`snowball`） | 3 | 攻击后: [snowball_burst] （条件触发） | 佩戴时受伤会概率向攻击者扔雪球 |
| 脆弱护甲（`weak_armor`） | 4 | 属性: 数值 = -1 - 1*(Lv-1)，属性 generic.armor（加值）；属性: 数值 = 0.05 + 0.05*(Lv-1)，属性 generic.movement_speed（乘基数）；每tick: [apply_mob_effect] 效果: resistance，时长 0.2tick，时长上限 0.2tick，等级 1~1（条件触发） | 降低防御但提升速度并在潜行且在地面时给予抗性提升 |

## 通用（所有可附魔物品）

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 破损诅咒（`curse_of_breaking`）〔诅咒〕 | 4 | 耐久: [add] 值 = 1 + 1*(Lv-1)（条件触发） | 每次使用都会消耗额外耐久 |
| 笨拙诅咒（`curse_of_clumsiness`）〔诅咒〕 | 1 | 命中方块: [drop_held_item] （条件触发）；攻击后: [drop_held_item] （条件触发） | 使用时有几率掉落物品 |
| 腐蚀（`curse_of_rust`）〔诅咒〕 | 3 | curse_of_rust_durability: [set] 值 = 1 + 1*(Lv-1) | 在水中或雨中时每秒减少耐久 |
| 高级耐久（`advanced_unbreaking`） | 1 | 耐久: [remove_binomial] 概率 = 4/5 | 应用附魔到物品时，有概率不消耗耐久 |
| 坚不可摧（`indestructible`） | 1 | （纯标记型，行为见代码） | 物品不再消耗耐久 |
| 保全（`preservation`） | 1 | （纯标记型，行为见代码） | 装备不会因无耐久而损坏消失 |
| 牺牲（`sacrifice`） | 5 | sacrifice_repair: [set] 值 = 1 + 0.5*(Lv-1) | 受到攻击时自动修复手持物品 |
| 坚固（`sturdy`） | 1 | （纯标记型，行为见代码） | 装备免疫爆炸、闪电和岩浆 |

## 狼铠

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 尸体回收（`carrion_eater`） | 5 | carrion_eater_heal: [set] 值 = 0.25 + 0.25*(Lv-1) | 狼击杀生物后回复最大生命值 |
| 狼群领袖（`pack_leader`） | 5 | pack_leader_damage_bonus: [set] 值 = 0.5 + 0.5*(Lv-1) | 附近每多一只狼，增加你狼的伤害 |
| 追踪者（`tracker`） | 1 | tracker_glow_duration_bonus: [add] 值 = 20 + 20*(Lv-1) | 狼攻击时，标记它攻击过的敌人 |
| 狼魂守护（`wolf_spirit_shield`） | 5 | 属性: 数值 = 5 + 5*(Lv-1)，属性 generic.attack_damage（加值）；属性: 数值 = 5 + 5*(Lv-1)，属性 generic.armor（加值）；属性: 数值 = 10 + 10*(Lv-1)，属性 generic.max_health（加值） | 提升狼铠所有属性 |

## 马铠

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 寒霜蹄（`frost_hooves`） | 4 | damage_immunity: （条件触发）；换格触发: [replace_disk] 半径 = clamp(3 + 1*(Lv-1)，0~16)格（条件触发） | 马匹移动时冻结周围的水为霜冰 |
| 我的小马驹（`my_little_pony`） | 1 | 属性: 数值 = -0.8 - 0.2*(Lv-1)，属性 generic.scale（乘基数）；属性: 数值 = 2 + 1*(Lv-1)，属性 generic.jump_strength（乘基数）；属性: 数值 = 10 + 10*(Lv-1)，属性 generic.safe_fall_distance（乘基数）；属性: 数值 = 0.01 + 0.01*(Lv-1)，属性 generic.fall_damage_multiplier（乘基数） | 缩小马匹体型并提高跳跃能力 |
| 牧场（`pasture`） | 1 | pasture_heal: [set] 值 = 0.5 + 0.5*(Lv-1) | 马在草方块上时会自然恢复生命 |
| 精神（`spirit`） | 5 | spirit_speed_bonus: [set] 值 = 0.25 + 0.25*(Lv-1) | 马在夜晚时增加速度 |

## 马铠/鞍甲

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 强化铠甲（`enhanced_armor`） | 4 | 属性: 数值 = 2 + 2*(Lv-1)，属性 generic.armor（加值）；属性: 数值 = -0.05 - 0.05*(Lv-1)，属性 generic.jump_strength（乘基数） | 提升防御力但降低跳跃高度 |
| 信使（`messenger`） | 4 | 属性: 数值 = 0.25 + 0.125*(Lv-1)，属性 generic.movement_speed（乘基数）；属性: 数值 = 1 + 0.5*(Lv-1)，属性 generic.movement_efficiency（加值）；属性: 数值 = 0.6 + 0.1*(Lv-1)，属性 generic.water_movement_efficiency（加值）；属性: 数值 = 0.2 + 0.1*(Lv-1)，属性 generic.jump_strength（乘基数） | 提升坐骑移动灵活性 |

## 鞘翅

| 附魔 | 最大等级 | 数值/公式 | 描述 |
|---|---|---|---|
| 末影之心（`ender_heart`） | 2 | 属性: 数值 = 0 + 2*(Lv-1)，属性 generic.max_health（加值）；每tick: [apply_mob_effect] 效果: regeneration，时长 3tick，时长上限 3tick，等级 0 + 1*(Lv-1)~0 + 1*(Lv-1)（条件触发） | 飞行时获得生命恢复(2级时增加最大生命值) |
| 月球漫步（`moonwalk`） | 3 | 属性: 数值 = -0.03 - 0.015*(Lv-1)，属性 generic.gravity（加值） | 降低飞行时的重力影响 |
| 安全着陆（`safe_landing`） | 5 | 属性: 数值 = 2 + 2.5*(Lv-1)，属性 generic.safe_fall_distance（加值） | 提升安全坠落高度(最高+12格) |

## 机制备注（行为由事件代码驱动，JSON 中无参数的标记型附魔）

以下行为经源码核对（`event/`、`enchantment/effect/`），列出关键规则：

| 附魔 | 机制要点 |
|---|---|
| 自动熔炼（`auto_smelt`） | 监听 `BreakEvent`：取消原版破坏 → `Block.getDrops` 重算掉落（时运等照常生效）→ 每个掉落物查熔炼配方表替换成品 → 重新生成掉落物，额外扣 1 耐久。**创造模式不生效**（守卫已加）。有此附魔时跳过同分发器上的其它挖掘效果 |
| 伐木（`timber`） | BFS 搜索相邻同种原木（6 向），上限 `ToolBlockBreakEvents.TIMBER_CHAIN_LIMIT`（代码常量 512，原配置项已移除），逐个破坏并掉落，每方块扣 1 耐久 |
| 挖掘机（`excavator`） | 按玩家朝向（含俯仰角定上下）取 (2r+1)² 区域，要求 `tool.isCorrectToolForDrops` 且非不可破坏方块；创造模式跳过 |
| 精通采集（`master_gatherer`） | 掉落物属 `#c:ores` 时按概率把**全部掉落**复制一份（时运加成过的也会翻倍），`setPickUpDelay(0)`；概率 `min(chance, 1.0)` 封顶 |
| 地质学 / 点石成金 | 额外掉落独立于原版掉落，走时运计数加成（`rand(时运+2)-1`，最小 1 倍） |
| 矿工（`adaptive`） | 每 tick 检查：Y<0 时施加 12 秒夜视（隐藏效果，无粒子），每 tick 重置所以无闪烁；Y≥0 时**无差别移除夜视**（会洗掉夜视药水）；摘头盔不立即移除 |
| 反伪装（`anti_camouflage`） | 服务端 tick：潜行中给 16 格内所有 `Monster`（接口级，含 mod 生物）上发光 2.5 秒（40+10×级 tick），无粒子；中立怪（猪灵/狼/铁傀儡非 `Monster`）不点亮；停止潜行后残留≤2.5 秒 |
| 光环系（`aura_*`） | 挂 `location_changed`，**跨方块格触发**；时长默认 60 tick（3 秒）持续重刷；效果隐藏（ambient+无粒子），但 HUD 图标仍显示；15 个全部互斥（`exclusive_set/aura`），一件装备只能一个 |
| 斩首（`decapitation`） | `LivingDropsEvent` 上按实体 ID 猜头颅物品（`<type>_head/_skull/head_/skull_`，先原版后全注册表）；**找不到头颅物品则完全不掷骰**；主手武器判定，远程击杀也有效 |
| 绝境逆袭 / 以寡敌众 | 属性修饰符（transient/permanent）按 tick 重算，`ADD_MULTIPLIED_BASE` 乘区 |
| 高级耐久（`advanced_unbreaking`） | 二项分布概率减免耐久（4/5 概率免耗），应用在 `item_damage` 组件，非原版 Unbreaking 机制 |
| 基岩破坏者 / 幻岩转化 | 数据包函数 `run_function` + `replace_block` 实现，分别消耗 1.5K / 1K 耐久，均无掉落 |

**联动附魔**（带 `neoforge:mod_loaded` 条件，缺 mod 时不注册）：法术增幅/法术防护（`irons_spellbooks`）、
双重暴击/速射/流血之触（`apothic_attributes`）、延迟爆破（`ars_nouveau`）、暗流涌动（`apothic_attributes`+`twilightforest` 双条件）；
潮汐感知软引用 `tide:fish` 标签。

