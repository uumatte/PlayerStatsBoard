package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PlayerStatsBoard 主类
 * 玩家统计计分板插件
 */
public final class Playerstatsboard extends JavaPlugin {
    
    private DataManager dataManager;
    private PlayerScoreboardManager scoreboardManager;
    private SkillManager skillManager;
    private EventListener eventListener;
    private CommandHandler commandHandler;
    private int updateTaskId;
    
    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        
        // 初始化管理器
        dataManager = new DataManager(this);
        scoreboardManager = new PlayerScoreboardManager(this, dataManager);
        skillManager = new SkillManager(this);
        eventListener = new EventListener(this, dataManager, scoreboardManager, skillManager);
        commandHandler = new CommandHandler(this, dataManager, scoreboardManager, skillManager);
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(eventListener, this);
        
        // 注册命令
        getCommand("scoreboard").setExecutor(commandHandler);
        getCommand("scoreboard").setTabCompleter(commandHandler);
        
        // 启动计分板更新任务
        startUpdateTask();
        
        // 为所有在线玩家创建计分板（如果配置了自动显示）
        if (getConfig().getBoolean("scoreboard.auto-show", true)) {
            scoreboardManager.createAllScoreboards();
        }
        
        getLogger().info("PlayerStatsBoard 插件已启用！");
        getLogger().info("版本: " + getDescription().getVersion());
        getLogger().info("作者: " + getDescription().getAuthors());
    }
    
    @Override
    public void onDisable() {
        // 取消更新任务
        if (updateTaskId != 0) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
        }
        
        // 移除所有玩家的计分板
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            scoreboardManager.removeScoreboard(player);
        }
        
        getLogger().info("PlayerStatsBoard 插件已禁用！");
    }
    
    /**
     * 启动计分板更新任务
     */
    private void startUpdateTask() {
        int updateInterval = getConfig().getInt("scoreboard.update-interval", 2);
        
        if (updateInterval <= 0) {
            getLogger().warning("更新间隔必须大于0，使用默认值2秒");
            updateInterval = 2;
        }
        
        updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            scoreboardManager.updateAllScoreboards();
        }, 0L, updateInterval * 20L);
        
        getLogger().info("计分板更新任务已启动，间隔: " + updateInterval + "秒");
    }
    
    /**
     * 获取数据管理器
     */
    public DataManager getDataManager() {
        return dataManager;
    }
    
    /**
     * 获取计分板管理器
     */
    public PlayerScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    
    /**
     * 获取技能管理器
     */
    public SkillManager getSkillManager() {
        return skillManager;
    }
}
