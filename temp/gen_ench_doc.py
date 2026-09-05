# -*- coding: utf-8 -*-
"""从生成的附魔 JSON + 语言文件生成分类附魔文档 docs/ENCHANTMENTS.md"""
import json, os, re

BASE = 'src/generated/resources/data/redstone_enchants/enchantment'
lang = json.load(open('src/main/resources/assets/redstone_enchants/lang/zh_cn.json', encoding='utf-8'))

def fmt_num(x):
    if isinstance(x, dict):
        return fmt_value(x)
    if isinstance(x, float) and x == int(x):
        return str(int(x))
    return f"{x:g}"

def fmt_value(v):
    """线性/常量/查表/夹取 LevelBasedValue → 可读字符串"""
    if isinstance(v, dict):
        t = v.get('type', '')
        if 'lookup' in t:
            vals = ", ".join(fmt_num(x) for x in v.get('values', []))
            fb = v.get('fallback', {})
            fb_s = f"（超出后 {fmt_value(fb)}）" if fb else ""
            return f"查表 [{vals}]{fb_s}"
        if 'clamped' in t:
            return f"clamp({fmt_value(v.get('value'))}，{fmt_num(v.get('min'))}~{fmt_num(v.get('max'))})"
        if 'fraction' in t:
            return f"{fmt_num(v.get('numerator'))}/{fmt_num(v.get('denominator'))}"
        base = v.get('base')
        pl = v.get('per_level_above_first', 0)
        if base is None:
            return json.dumps(v, ensure_ascii=False)
        s = fmt_num(base)
        if pl:
            sign = "-" if pl < 0 else "+"
            s += f" {sign} {fmt_num(abs(pl))}*(Lv-1)"
        if 'exponential' in t:
            s += "（指数）"
        return s
    return fmt_num(v)

def strip_ns(s):
    if isinstance(s, list):
        return "/".join(strip_ns(x) for x in s)
    s = str(s)
    return s.split(':')[-1] if ':' in s else s

def fmt_effect(e):
    """单个效果 dict → 简洁描述"""
    t = strip_ns(e.get('type', ''))
    prefix = f"[{t}] " if t and t != '?' else ""
    parts = []
    if t == 'add' or t == 'set' or 'value' in e and t in ('add', 'set'):
        parts.append(f"值 = {fmt_value(e.get('value'))}")
    if 'amount' in e and t != 'set':
        parts.append(f"数值 = {fmt_value(e['amount'])}")
    if 'chance' in e:
        parts.append(f"概率 = {fmt_value(e['chance'])}")
    if 'radius' in e:
        parts.append(f"半径 = {fmt_num(e['radius'])}格")
    if 'effect' in e and isinstance(e['effect'], str):
        parts.append(f"效果: {strip_ns(e['effect'])}")
    if 'to_apply' in e:
        parts.append(f"效果: {strip_ns(e['to_apply'])}")
    if 'attribute' in e:
        op = {'add_value': '加值', 'add_multiplied_base': '乘基数', 'add_multiplied_total': '乘总量'}.get(e.get('operation'), e.get('operation'))
        parts.append(f"属性 {strip_ns(e['attribute'])}（{op}）")
    for dk, label in [('min_duration', '时长'), ('max_duration', '时长上限')]:
        if dk in e:
            parts.append(f"{label} {fmt_value(e[dk])}tick")
    if 'duration_ticks' in e:
        parts.append(f"时长 {fmt_value(e['duration_ticks'])}tick")
    if 'min_amplifier' in e or 'max_amplifier' in e:
        lo = e.get('min_amplifier'); hi = e.get('max_amplifier')
        parts.append(f"等级 {fmt_value(lo)}~{fmt_value(hi)}")
    if 'amplifier' in e and isinstance(e['amplifier'], (int, float)):
        parts.append(f"等级+{fmt_num(e['amplifier'])}")
    elif 'amplifier' in e:
        parts.append(f"等级 {fmt_value(e['amplifier'])}")
    if 'count' in e:
        parts.append(f"数量 = {fmt_value(e['count'])}")
    if not parts:
        # 把剩余的数值字段都带上
        for k, v in e.items():
            if k in ('type', 'id') or isinstance(v, (dict, list)):
                continue
            parts.append(f"{k}={v}")
    return prefix + "，".join(parts)

def fmt_entry_effects(effects):
    lines = []
    for comp, entries in effects.items():
        comp_short = strip_ns(comp)
        if comp == 'minecraft:attributes':
            comp_short = '属性'
        elif comp == 'minecraft:tick':
            comp_short = '每tick'
        elif comp == 'minecraft:location_changed':
            comp_short = '换格触发'
        elif comp == 'minecraft:post_attack':
            comp_short = '攻击后'
        elif comp == 'minecraft:item_damage':
            comp_short = '耐久'
        elif comp == 'minecraft:damage':
            comp_short = '伤害'
        elif comp == 'minecraft:hit_block':
            comp_short = '命中方块'
        elif comp == 'minecraft:projectile_spawned':
            comp_short = '射出弹射物'
        for ent in entries:
            eff = ent['effect'] if isinstance(ent, dict) and 'effect' in ent else ent
            if isinstance(eff, str):
                line = f"引用 {strip_ns(eff)}"
            elif isinstance(eff, dict):
                line = fmt_effect(eff)
            else:
                continue
            if isinstance(ent, dict) and 'requirements' in ent:
                line += "（条件触发）"
            lines.append(f"{comp_short}: {line}")
    return "；".join(lines)

# ---- 分类规则 ----
def category(d):
    sup = d['supported_items']
    table = [
        ('all_bow', '远程武器（弓/弩）'), ('swords_and_bow', '远程武器（弓/弩）'),
        ('trident_and_bow', '远程武器（弓/三叉戟）'), ('minecraft:enchantable/trident', '远程武器（弓/弩/三叉戟）'),
        ('swords', '近战武器（剑/斧）'), ('swords_and_axes', '近战武器（剑/斧）'),
        ('weapon', '近战武器（剑/斧/重锤）'), ('last_hope_weapons', '近战武器（剑/斧/重锤）'),
        ('minecraft:axes', '近战武器（剑/斧）'),
        ('c:tools/mace', '重锤'),
        ('redstone_enchants:tools', '工具（镐/斧/锹/锄）'), ('all_tools', '工具（镐/斧/锹/锄）'),
        ('minecraft:pickaxes', '工具（镐）'), ('minecraft:hoes', '工具（锄）'),
        ('all_flint_and_steel', '工具（打火石）'),
        ('all_fishing', '钓鱼竿'),
        ('all_shear', '剪刀'),
        ('c:tools/shield', '盾牌（副手）'),
        ('armors_head', '头盔'),
        ('armors_chest', '胸甲'), ('minecraft:chest_armor', '胸甲'),
        ('armors_leg', '护腿'),
        ('armors_foot', '靴子'),
        ('armors', '全身护甲'), ('c:enchantables', '通用（所有可附魔物品）'),
        ('wolf_armor', '狼铠'),
        ('horse_armor', '马铠'), ('horse_animal_armor', '马铠/鞍甲'),
        ('elytra', '鞘翅'),
    ]
    for key, cat in table:
        if sup.endswith(key):
            return cat
    return '其他'

FAMILY = {
    'aura': '光环', 'curse_of': '诅咒', 'splash': '蔓延', 'trail': '轨迹',
    'walker': '行者', 'bane': '克星', 'protection': '防护',
}

def family(eid):
    for pre, name in FAMILY.items():
        if eid.startswith(pre):
            return name
    return ''

enchs = []
for f in sorted(os.listdir(BASE)):
    if not f.endswith('.json'):
        continue
    eid = f[:-5]
    d = json.load(open(os.path.join(BASE, f), encoding='utf-8'))
    key = d['description'].get('translate', '')
    name = lang.get(key, f"`{key}`")
    desc = lang.get(key + '.desc', '')
    enchs.append({
        'id': eid,
        'name': name,
        'desc': desc.rstrip('。'),
        'max_level': d.get('max_level', 1),
        'min_cost': d.get('min_cost', {}),
        'max_cost': d.get('max_cost', {}),
        'anvil_cost': d.get('anvil_cost'),
        'cat': category(d),
        'fam': family(eid),
        'formula': fmt_entry_effects(d.get('effects', {})) or '（纯标记型，行为见代码）',
    })

# 排序：类别 → 家族 → 名称
order = ['远程武器（弓/弩）', '远程武器（弓/三叉戟）', '远程武器（弓/弩/三叉戟）',
         '近战武器（剑/斧）', '近战武器（剑/斧/重锤）', '重锤',
         '工具（镐/斧/锹/锄）', '工具（镐）', '工具（锄）', '工具（打火石）',
         '钓鱼竿', '剪刀', '盾牌（副手）',
         '头盔', '胸甲', '护腿', '靴子', '全身护甲', '通用（所有可附魔物品）',
         '狼铠', '马铠', '马铠/鞍甲', '鞘翅', '其他']
fam_order = {'光环': 0, '诅咒': 1, '克星': 2, '蔓延': 3, '轨迹': 4, '行者': 5, '防护': 6, '': 7}

def sort_key(e):
    ci = order.index(e['cat']) if e['cat'] in order else 999
    return (ci, fam_order.get(e['fam'], 9), e['id'])

enchs.sort(key=sort_key)

def cost_str(e):
    mn, mx = e['min_cost'], e['max_cost']
    s = f"附魔台消耗 {fmt_num(mn.get('base', 0))}"
    if mn.get('per_level_above_first'):
        s += f"+{fmt_num(mn['per_level_above_first'])}/级"
    s += f" ~ {fmt_num(mx.get('base', 0))}"
    if mx.get('per_level_above_first'):
        s += f"+{fmt_num(mx['per_level_above_first'])}/级"
    return s

lines = []
cur_cat = None
cur_fam = None
for e in enchs:
    if e['cat'] != cur_cat:
        cur_cat = e['cat']
        cur_fam = None
        lines.append(f"\n## {cur_cat}\n")
        lines.append("| 附魔 | 最大等级 | 数值/公式 | 描述 |")
        lines.append("|---|---|---|---|")
    fam_tag = f"〔{e['fam']}〕" if e['fam'] else ''
    if e['fam']:
        cur_fam = e['fam']
    lines.append(f"| {e['name']}（`{e['id']}`）{fam_tag} | {e['max_level']} | {e['formula']} | {e['desc']} |")

header = """# Redstone 附魔扩展 · 附魔全表

> **自动生成**：由 `temp/gen_ench_doc.py` 从 `src/generated/resources/data/redstone_enchants/enchantment/*.json`
> 与 `zh_cn.json` 提取。共 **{n}** 个附魔。
>
> **数值公式约定**：效果量 = 首级基础值 + 每级增量 × (Lv − 1)，形如 `0.5 + 0.5*(Lv-1)`
> 表示 Lv1 为 0.5，此后每提升 1 级再加 0.5（即 Lv1~5 = 0.5/1.0/1.5/2.0/2.5）。
> 对应 JSON 的线性 LevelBasedValue（`base` + `per_level_above_first`）。
> 时长单位为 tick（20 tick = 1 秒）。"条件触发"表示带 entity_requirements 谓词，详见 JSON。
> 标记型附魔（如自动熔炼）无参数，行为由事件代码实现，见「机制备注」。

""".format(n=len(enchs))

APPENDIX = """

## 机制备注（行为由事件代码驱动，JSON 中无参数的标记型附魔）

以下行为经源码核对（`event/`、`enchantment/effect/`），列出关键规则：

| 附魔 | 机制要点 |
|---|---|
| 自动熔炼（`auto_smelt`） | 监听 `BreakEvent`：取消原版破坏 → `Block.getDrops` 重算掉落（时运等照常生效）→ 每个掉落物查熔炼配方表替换成品 → 重新生成掉落物，额外扣 1 耐久。**创造模式不生效**（守卫已加）。有此附魔时跳过同分发器上的其它挖掘效果 |
| 伐木（`timber`） | BFS 搜索相邻同种原木（6 向），上限 `ModConfigData.TIMBER_CHAIN_LIMIT`（可配置），逐个破坏并掉落，每方块扣 1 耐久 |
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
潮汐感知软引用 `tide:fish` 标签。法术防护的翻译键是 `enchantment.redstone_enchants.magic_resist`（与文件名不一致，但语言文件齐全）。
"""

out = header + "\n".join(lines) + APPENDIX
open('docs/ENCHANTMENTS.md', 'w', encoding='utf-8').write(out + '\n')
print(f"written docs/ENCHANTMENTS.md, {len(enchs)} enchantments, {len(out)} chars")
