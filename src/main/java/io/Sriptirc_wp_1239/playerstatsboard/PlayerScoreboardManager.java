package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 计分板管理器
 * 管理玩家的计分板显示
 */
public class PlayerScoreboardManager {
    private final Playerstatsboard plugin;
    private final DataManager dataManager;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final Map<UUID, Objective> playerObjectives;
    
    private String scoreboardTitle;
    private String deathsDisplayName;
    private String skillsDisplayName;
    private String punishmentDisplayName;
    
    public PlayerScoreboardManager(Playerstatsboard plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.playerScoreboards = new HashMap<>();
        this.playerObjectives = new HashMap<>();
        
        loadConfig();
    }
    
    /**
     * 加载配置
     */
    public void loadConfig() {
        scoreboardTitle = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("scoreboard.title", "&6&l玩家统计"));
        deathsDisplayName = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("stats.deaths.display-name", "&c死亡次数"));
        skillsDisplayName = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("stats.skills.display-name", "&a技能使用"));
        punishmentDisplayName = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("stats.punishment.display-name", "&4惩罚值"));
    }
    
    /**
     * 为玩家创建计分板
     */
    public void createScoreboard(Player player) {
        ScoreboardManager bukkitScoreboardManager = Bukkit.getScoreboardManager();
        if (bukkitScoreboardManager == null) return;
        
        Scoreboard scoreboard = bukkitScoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("playerstats", "dummy", scoreboardTitle);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        playerScoreboards.put(player.getUniqueId(), scoreboard);
        playerObjectives.put(player.getUniqueId(), objective);
        
        updateScoreboard(player);
        player.setScoreboard(scoreboard);
    }
    
    /**
     * 更新玩家的计分板
     */
    public void updateScoreboard(Player player) {
        PlayerData data = dataManager.getPlayerData(player);
        Objective objective = playerObjectives.get(player.getUniqueId());
        
        if (objective == null || data == null) return;
        
        // 清空之前的分数
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }
        
        // 设置新的分数
        objective.getScore(deathsDisplayName + ": " + data.getDeaths()).setScore(3);
        objective.getScore(skillsDisplayName + ": " + data.getSkills()).setScore(2);
        objective.getScore(punishmentDisplayName + ": " + data.getPunishment()).setScore(1);
    }
    
    /**
     * 移除玩家的计分板
     */
    public void removeScoreboard(Player player) {
        playerScoreboards.remove(player.getUniqueId());
        playerObjectives.remove(player.getUniqueId());
        
        // 恢复默认计分板
        ScoreboardManager bukkitScoreboardManager = Bukkit.getScoreboardManager();
        if (bukkitScoreboardManager != null) {
            player.setScoreboard(bukkitScoreboardManager.getMainScoreboard());
        }
    }
    
    /**
     * 为所有在线玩家更新计分板
     */
    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }
    
    /**
     * 为所有在线玩家创建计分板
     */
    public void createAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            createScoreboard(player);
        }
    }
    
    /**
     * 检查玩家是否有计分板
     */
    public boolean hasScoreboard(Player player) {
        return playerScoreboards.containsKey(player.getUniqueId());
    }
    
    /**
     * 获取玩家的计分板
     */
    public Scoreboard getPlayerScoreboard(Player player) {
        return playerScoreboards.get(player.getUniqueId());
    }
}