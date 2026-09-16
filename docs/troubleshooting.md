# 环境 / 构建 / 运行坑 — Redstone-chan's Enchantment Expansion

## 构建与数据生成

- 构建：`JAVA_HOME=C:/Users/lzp/scoop/apps/dragonwell21-jdk/current` 后 `./gradlew build` / `runData`
  （build.gradle 已配好 `--output src/generated/resources --existing src/main/resources`）。
- **坑：`./gradlew runData build` 并行执行时 build 可能先于 runData 完成**，
  jar 会打进旧 JSON；runData 后单独再跑一次 build 确认。
- 自定义 effect 忘注册会在 datagen 报 "Unregistered holder"。
