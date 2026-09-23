# AI 协作坑 — Redstone-chan's Enchantment Expansion

跨会话知识沉淀的约定、纠错记录与验证方法。技术结论本身按落点表归位（`../reference/`、`../troubleshooting.md`），
本文件只记录"怎么记错的、下次怎么查"。

- **只沉淀已验证结论**：写入 `../reference/` 的 API 事实必须来自编译通过 / runData 通过 / 源码核对三选一，
  并注明验证批次与验证方式（时间、批次名），否则下次会话无法判断可信度。
- **对"某 API 不存在"的记载一律复核**：本仓库出现过错误记载——`Enchantment.definition` 曾记为"无 primary 重载"
  （见 `../reference/enchantment-components.md` 该条）。凭印象下"不存在"的结论最危险，必须查 api-sources 或字节码。
- **迁移可行性预判常被实际落地推翻**：swords 迁移前判断"绝大多数无法零代码声明化、每个附魔都要写自定义 effect 类"，
  2026-09 实际落地为单值/标记/数值组件 + 事件分发器（见 `../reference/enchantment-components.md` 的 swords 补充）。
  下结论前先确认组件形态。
- **沉淀内容会被后续批次纠正，纠正时要留痕**：revive_ward 判死时点曾误记为 `getNewDamage`
  （见 `../reference/enchantment-migrations.md` 怪癖清单），修正时保留删除线标注而不是直接抹掉。
- **迁移前先判"这句到底有没有生效"**：原实现里的语句不都是活代码，照搬死代码会把它固化成"行为"。
  2026-09-24 迁 `freeze_pic` 时发现 `first_step.mcfunction` 的
  `execute at @e[...] run playsound … @s` 从来是死代码（`at` 不换执行实体，而 `schedule function`
  的命令源无实体，`@s` 不指向任何实体），`start` 的三声只发给 `@s`＝受击者/弹射物、实际也几乎不响。
  做法：逐条问"这条在原环境下作用于谁、有没有输出"，把判定结果**交给用户决定修还是留**
  （本次用户选修）。判据见 `../reference/enchantment-runtime-effects.md` 的 D 批条目。
- **新增/修改附魔先读 `../design/enchantment-authoring.md`**：那里有分层决策树（L0 纯声明 → L1 组件+分发器
  → L2 参数化 effect → L3 事件代码）与收尾流程，并附带 file:line 的 P0/P1 审查清单。
  该清单的行号会随提交漂移，动工前重新读一遍代码再改，不要照抄；**条目的组成也可能写错**——
  2026-09-23 修 P1-1 时发现"7 处手写两连"里有两处与实际不符、还漏记一处，
  另外"15 个 aura_*"实际是 13 个。逐处 grep 核对比照抄清单快。

## 查证 API 的坑

- **api-sources 是更高版本镜像（1.21.2+），与项目真实 21.1.219 有 API 偏差**
  （如 `critereon.BlockPredicate.Builder.block()` 在 1.21.1 实为内部类路径同名的另一套、
  `DamageTypeTags.ARROWS` 在 1.21.1 无常量、`EntityPredicate.Builder` 无 `level()`）。
  查证以 gradle 缓存 jar `javap` 为准：
  `$jar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\neoformruntime\intermediate_results" -Filter "sourcesAndCompiledWithNeoForge*output.jar" | Sort LastWriteTime -Desc | Select -First 1; & $jdk -cp $jar.FullName <FQCN>`。
- **AT（access transformer）的成员描述符同样只能拿真实 jar 核**：AT 写错**不报编译错**，只在应用 AT
  时失败。2026-09-24 为本仓库引入 `META-INF/accesstransformer.cfg`（放开 `Display` 的 4 个私有
  setter）时逐条核对过；嵌套类在 AT 里写 `$`（`Display$BlockDisplay`）。那 4 个成员在 `api-sources`
  与真实 jar 中都是 `private`——但**一致是核出来的，不是默认的**。

## 迁移对比法（手写 JSON vs 生成 JSON）

- 手写 JSON 从删除提交的父提交取（`git log --diff-filter=D` 定位），
  `temp/compare-enchant-json.ps1 -Names ...` 逐字段对比；颜色 DIFF 大多是抄错，以手写为准。
- 报 DIFF 不等于要改：裸 id vs 命名空间 id、单条件数组、省略 default 值、float 精度等属编码等价，
  按 `../reference/enchantment-datagen.md` 的约定归类接受。
