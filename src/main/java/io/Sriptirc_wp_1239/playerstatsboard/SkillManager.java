package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 技能管理器
 * 管理所有技能配置和冷却
 */
public class SkillManager {
    private final Playerstatsboard plugin;
    private final Map<Material, SkillData> skillByItem;
    private final Map<String, SkillData> skillById;
    private final Map<String, Long> playerCooldowns; // 玩家UUID -> 冷却结束时间戳
    
    public SkillManager(Playerstatsboard plugin) {
        this.plugin = plugin;
        this.skillByItem = new HashMap<>();
        this.skillById = new HashMap<>();
        this.playerCooldowns = new HashMap<>();
        loadSkills();
    }
    
    /**
     * 从配置文件加载技能
     */
    public void loadSkills() {
        skillByItem.clear();
        skillById.clear();
        
        if (!plugin.getConfig().getBoolean("skills.enabled", true)) {
            plugin.getLogger().info("技能系统已禁用");
            return;
        }
        
        ConfigurationSection skillsSection = plugin.getConfig().getConfigurationSection("skills.list");
        if (skillsSection == null) {
            plugin.getLogger().warning("未找到技能配置");
            return;
        }
        
        for (String skillId : skillsSection.getKeys(false)) {
            ConfigurationSection skillConfig = skillsSection.getConfigurationSection(skillId);
            if (skillConfig == null) continue;
            
            String itemName = skillConfig.getString("item", "BOOK");
            String name = skillConfig.getString("name", skillId);
            int cooldown = skillConfig.getInt("cooldown", 5);
            String effect = skillConfig.getString("effect", "none");
            
            try {
                Material item = Material.valueOf(itemName.toUpperCase());
                SkillData skillData = new SkillData(skillId, name, item, cooldown, effect);
                
                skillByItem.put(item, skillData);
                skillById.put(skillId, skillData);
                
                plugin.getLogger().info("加载技能: " + skillId + " -> " + name + " (物品: " + item + ")");
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("无效的物品ID: " + itemName + " (技能: " + skillId + ")");
            }
        }
        
        plugin.getLogger().info("已加载 " + skillByItem.size() + " 个技能");
    }
    
    /**
     * 根据物品获取技能
     */
    public SkillData getSkillByItem(ItemStack item) {
        if (item == null) return null;
        return skillByItem.get(item.getType());
    }
    
    /**
     * 根据物品获取技能
     */
    public SkillData getSkillByItem(Material material) {
        return skillByItem.get(material);
    }
    
    /**
     * 根据ID获取技能
     */
    public SkillData getSkillById(String skillId) {
        return skillById.get(skillId);
    }
    
    /**
     * 获取所有技能
     */
    public Map<Material, SkillData> getAllSkills() {
        return new HashMap<>(skillByItem);
    }
    
    /**
     * 检查玩家是否在冷却中
     */
    public boolean isOnCooldown(Player player) {
        String playerKey = player.getUniqueId().toString();
        if (!playerCooldowns.containsKey(playerKey)) {
            return false;
        }
        
        long cooldownEnd = playerCooldowns.get(playerKey);
        return System.currentTimeMillis() < cooldownEnd;
    }
    
    /**
     * 获取剩余冷却时间（秒）
     */
    public int getRemainingCooldown(Player player) {
        String playerKey = player.getUniqueId().toString();
        if (!playerCooldowns.containsKey(playerKey)) {
            return 0;
        }
        
        long cooldownEnd = playerCooldowns.get(playerKey);
        long remaining = cooldownEnd - System.currentTimeMillis();
        
        if (remaining <= 0) {
            playerCooldowns.remove(playerKey);
            return 0;
        }
        
        return (int) Math.ceil(remaining / 1000.0);
    }
    
    /**
     * 设置玩家冷却
     */
    public void setCooldown(Player player, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return;
        }
        
        String playerKey = player.getUniqueId().toString();
        long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        playerCooldowns.put(playerKey, cooldownEnd);
    }
    
    /**
     * 设置玩家技能冷却（使用技能默认冷却）
     */
    public void setSkillCooldown(Player player, SkillData skill) {
        setCooldown(player, skill.getCooldown());
    }
    
    /**
     * 清除玩家冷却
     */
    public void clearCooldown(Player player) {
        String playerKey = player.getUniqueId().toString();
        playerCooldowns.remove(playerKey);
    }
    
    /**
     * 检查物品是否是技能物品
     */
    public boolean isSkillItem(ItemStack item) {
        return item != null && skillByItem.containsKey(item.getType());
    }
    
    /**
     * 获取技能数量
     */
    public int getSkillCount() {
        return skillByItem.size();
    }
    
    /**
     * 重新加载技能配置
     */
    public void reload() {
        loadSkills();
        playerCooldowns.clear();
    }
}