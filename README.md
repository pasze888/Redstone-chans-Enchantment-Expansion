
Installation information
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/

附魔说明
==========

狼群领袖（pack_leader）
----------

给**驯服的狼**加攻击伤害的狼铠附魔，同伴越多加成越高。

- 装备槽：狼铠（BODY 槽，`#redstone_enchants:wolf_armor`）
- 最高等级：5 级（weight 3，费用 12-6 / 24-12，铁砧成本 8）
- 触发条件（须同时满足）：
  1. 伤害的直接来源（`getDirectEntity()`）是狼；
  2. 狼为驯服状态（`isTame`）且主人是玩家；
  3. 狼铠上带有本附魔；
  4. 16 格范围内存在**其它狼**（范围 = 狼自身碰撞箱向外扩 16 格，
     统计所有 `Wolf` 实体并排除自己；不区分是否存活、是否同一主人）。

伤害公式：

```
最终伤害 = 原伤害 × (1 + 同伴狼数 × 0.5 × 等级)
```

即每只范围内的同伴狼提供 **50% × 等级** 的乘法加成。举例：
1 级 + 2 只同伴狼 = ×2 倍；5 级 + 2 只同伴狼 = ×6 倍。

> **注意：代码内注释与实际数值不符。** 旧版遗留注释写的是"每级每只狼 +5%"，
> 但实现常量是 `0.5`（=50%），实际行为以 **50%** 为准（本文档为准）。
> 按用户决定，代码与注释保持原样不做修改；若未来要改为注释意图的 5%，
> 只需把数值组件 `pack_leader_damage_bonus` 从 `perLevel(0.5)` 改为 `perLevel(0.05)` 后重跑 runData。

另见：`docs/KNOWLEDGE.md`（迁移与修复批次的完整备忘）。

