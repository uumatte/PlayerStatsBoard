package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 数据管理器
 * 管理所有玩家的数据
 */
public class DataManager {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    
    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
    }
    
    /**
     * 获取玩家数据
     */
    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData(player));
    }
    
    /**
     * 获取玩家数据（通过UUID）
     */
    public PlayerData getPlayerData(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null) {
            return getPlayerData(player);
        }
        return null;
    }
    
    /**
     * 重置玩家数据
     */
    public void resetPlayerData(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null) {
            data.reset();
        }
    }
    
    /**
     * 重置所有玩家数据
     */
    public void resetAllPlayerData() {
        for (PlayerData data : playerDataMap.values()) {
            data.reset();
        }
    }
    
    /**
     * 移除玩家数据（玩家退出时调用）
     */
    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }
    
    /**
     * 获取所有玩家数据
     */
    public Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataMap);
    }
    
    /**
     * 获取在线玩家数量
     */
    public int getOnlinePlayerCount() {
        return playerDataMap.size();
    }
}