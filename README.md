# PlayerStatsBoard - 玩家统计计分板插件

一个为玩家显示死亡次数、技能使用次数和惩罚值的计分板插件。

## 功能特性

- **实时统计显示**：在侧边栏显示玩家的三项统计数据
- **自动追踪**：
  - 死亡次数：玩家死亡时自动增加
  - 技能使用：右键使用特定物品时自动增加
  - 惩罚值：死亡时自动增加
- **技能系统**：
  - 18个可配置技能（可扩展）
  - 每个技能使用不同物品触发
  - 技能冷却系统
  - 预留技能效果接口
- **可配置性**：通过配置文件自定义所有显示内容和行为
- **命令控制**：玩家可以随时显示/隐藏计分板、查看技能列表
- **管理员功能**：重置玩家数据、重载配置

## 安装方法

1. 将编译后的 `.jar` 文件放入服务器的 `plugins` 文件夹
2. 重启服务器
3. 插件会自动生成配置文件

## 配置文件说明

配置文件位于 `plugins/PlayerStatsBoard/config.yml`

### 主要配置项

```yaml
# 计分板设置
scoreboard:
  title: "&6&l玩家统计"  # 计分板标题
  update-interval: 2     # 更新间隔（秒）
  auto-show: true        # 是否自动为玩家显示计分板

# 统计项目设置
stats:
  deaths:
    display-name: "&c死亡次数"      # 显示名称
    punishment-on-death: 1          # 死亡时增加的惩罚值
  
  skills:
    display-name: "&a技能使用"      # 显示名称
    trigger-items:                  # 触发技能使用的物品列表
      - "WOODEN_SWORD"
      - "STONE_SWORD"
      - "IRON_SWORD"
      - "DIAMOND_SWORD"
      - "NETHERITE_SWORD"
    increment-on-use: 1             # 每次使用增加的技能次数
  
  punishment:
    display-name: "&4惩罚值"        # 显示名称
    increment-on-death: 1           # 死亡时增加的惩罚值
    max-punishment: 100             # 最大惩罚值（0表示无限制）

# 技能系统设置
skills:
  enabled: true                     # 是否启用技能系统
  list:
    skill1:
      item: "BOOK"                  # 技能物品
      name: "技能1"                 # 技能名称
      cooldown: 5                   # 冷却时间（秒）
      effect: "none"                # 技能效果（预留接口）
    # 更多技能...
    skill18:
      item: "FIRE_CHARGE"
      name: "技能18"
      cooldown: 10
      effect: "none"
```

## 命令使用

### 玩家命令
- `/scoreboard show` - 显示计分板
- `/scoreboard hide` - 隐藏计分板
- `/scoreboard skills` - 查看技能列表
- `/scoreboard cooldown` - 查看技能冷却
- `/scoreboard` - 显示帮助信息

**别名**: `/sb`, `/stats`

### 管理员命令
- `/scoreboard reset [玩家]` - 重置指定玩家的数据（不指定玩家则重置所有）
- `/scoreboard reload` - 重载配置文件

## 权限节点

- `playerstatsboard.use` - 使用计分板命令（显示/隐藏）
- `playerstatsboard.admin` - 管理员权限（重置数据、重载配置）

## 事件追踪

### 死亡次数
- 玩家死亡时自动增加

### 技能使用次数
- 右键使用配置文件中指定的物品时增加
- 默认追踪所有剑类物品
- 新技能系统：使用特定物品触发不同技能

### 技能系统
- 18个可配置技能，每个技能使用不同物品
- 技能冷却系统，防止滥用
- 使用技能后发送技能编号给玩家
- 预留技能效果接口，可后续添加具体效果

### 惩罚值
- 玩家死亡时自动增加
- 可配置最大惩罚值限制

## 数据存储

- 插件使用内存存储数据
- 服务器重启后数据会重置
- 玩家退出游戏时数据会从内存中移除

## 自定义配置

### 修改计分板标题
```yaml
scoreboard:
  title: "&e&l我的服务器统计"
```

### 添加更多触发物品
```yaml
stats:
  skills:
    trigger-items:
      - "WOODEN_SWORD"
      - "BOW"
      - "TRIDENT"
      - "CROSSBOW"
```

### 调整惩罚值规则
```yaml
stats:
  punishment:
    increment-on-death: 2    # 每次死亡增加2点惩罚
    max-punishment: 50       # 最大惩罚值50
```

## 开发信息

- **Minecraft 版本**: 1.21.x
- **插件版本**: 1.0.0
- **作者**: ScriptIrc Engine

## 注意事项

1. 计分板更新间隔不宜设置过小，建议2-5秒
2. 技能触发物品必须是有效的 Minecraft 物品ID
3. 最大惩罚值设为0表示无限制
4. 插件不会持久化存储数据，重启服务器后数据会丢失

## 问题反馈

如果遇到任何问题，请检查：
1. 服务器控制台是否有错误日志
2. 配置文件格式是否正确
3. 玩家是否有相应的权限

---

**享受游戏，统计你的冒险历程！**