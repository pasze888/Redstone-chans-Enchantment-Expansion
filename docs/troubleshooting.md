# 环境 / 构建 / 运行坑 — Redstone-chan's Enchantment Expansion

## 构建与数据生成

- 构建：`JAVA_HOME=C:/Users/lzp/scoop/apps/dragonwell21-jdk/current` 后 `./gradlew build` / `runData`
  （build.gradle 已配好 `--output src/generated/resources --existing src/main/resources`）。
- **坑：`./gradlew runData build` 并行执行时 build 可能先于 runData 完成**，
  jar 会打进旧 JSON；runData 后单独再跑一次 build 确认。
- 自定义 effect 忘注册会在 datagen 报 "Unregistered holder"。

## 定位弃用 API

`compileJava` 默认只给一句 `Note: Some input files use or override a deprecated API.`，不报位置。
用 Gradle 初始化脚本临时加上 `-Xlint:deprecation`（不必改 `build.gradle`）：

```bash
# 在工作区根 temp/ 放 deprecation-lint.gradle：
# allprojects { tasks.withType(JavaCompile).configureEach { options.compilerArgs.add('-Xlint:deprecation') } }
./gradlew compileJava --rerun-tasks --console=plain -I ../temp/deprecation-lint.gradle
```

实测（2026-09-23）1.21.1 + NeoForge 21.1.219 下命中过：
`EnchantmentHelper.getItemEnchantmentLevel`（改用 `ItemStack#getEnchantmentLevel`）、
`Block.getDrops(state, level, pos, blockEntity, entity, tool)`（改用 `BlockState#getDrops(LootParams.Builder)`）、
`Holder#is(Holder)`（注册表单例可直接 `a.value() == b.value()`）、
`EntityType/Item.builtInRegistryHolder()`、`Item.byBlock(Block)`。
