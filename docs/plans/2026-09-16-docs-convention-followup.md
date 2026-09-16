# 2026-09-16 文档落点规范后续整理

按工作区根 `AGENTS.md` §7.2 落点表，把本仓库散落的两份文档拆到各归其位，并修正全仓库引用。
本次只做文档结构迁移与链接修正，不改任何代码行为。

## 迁移结果

| 旧位置 | 新位置 | 处理 |
|---|---|---|
| `docs/ENCHANTMENTS.md` | `docs/reference/enchantments.md` | `git mv` 整体搬；`scripts/gen_ench_doc.py` 的 docstring 与输出路径同步 |
| `docs/KNOWLEDGE.md` | `docs/reference/enchantment-components.md` | `git mv` 主目标（组件体系、重构架构、批次 1 行为备忘、战斗类基建） |
| ↑ 拆出 | `docs/reference/enchantment-migrations.md` | swords / unbreaking / 旧式 handler 35 个 / 怪癖修复 各批次的 API 事实与行为变更 |
| ↑ 拆出 | `docs/reference/enchantment-datagen.md` | 手写 JSON 迁 datagen 的 API 事实与编码等价约定 |
| ↑ 拆出 | `docs/reference/enchantment-runtime-effects.md` | 光环与 `run_function` 排查、mcfunction → Java 迁移事实 |
| ↑ 拆出 | `docs/ai/gotchas.md` | 跨会话沉淀约定与纠错记录、API 查证坑（api-sources 版本偏差）、迁移对比法 |
| ↑ 拆出 | `docs/troubleshooting.md` | 构建命令、`runData build` 并行顺序坑、datagen "Unregistered holder" |

正文逐字保留，只调整标题层级、拆出跨主题段落并加过渡说明；行为变更 / 纠错内容留在各自批次小节内不做二次拆分。

引用修正：`README.md`（原指向 `docs/KNOWLEDGE.md` 的一行改为新 reference 文件 + ai/troubleshooting）、
`scripts/gen_ench_doc.py`（2 处路径）。`.github/`、源码、其它 docs 经 `git grep` 确认无旧路径引用。

## 剩余待办

1. `README.md` 第 27-58 行「附魔说明」是单个附魔的机制长文（触发条件、伤害公式、数值矛盾说明），
   超出 README 承载范围（简介 / 安装 / 快速开始 / 配置 / 常用命令 / 文档链接）；
   应按落点表迁到 `docs/reference/`（数值事实）或 `docs/design/`（设计取舍）后再精简 README。
   本次遵守"README 正文不重写"，未搬动正文。
2. `README.md` 前半仍是 MDK 模板样板（Installation information / Mapping Names / Additional Resources），
   未替换为本项目简介与安装说明；本仓库没有 `README.zh-CN.md`，故顶部语言切换行（English ↔ 简体中文）未添加。
   是否新建中文 README 需用户决定。
3. 本轮未重跑 `scripts/gen_ench_doc.py`：它依赖 `runData` 产物，直接跑会覆盖已提交的附魔全表。
   若后续跑 `runData`，脚本会把全表写回 `docs/reference/enchantments.md`（路径已改对）。
