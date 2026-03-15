package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 事件监听器
 * 监听玩家事件并更新统计数据
 */
public class EventListener implements Listener {
    private final Playerstatsboard plugin;
    private final DataManager dataManager;
    private final PlayerScoreboardManager scoreboardManager;
    private final SkillManager skillManager;
    
    public EventListener(Playerstatsboard plugin, DataManager dataManager, PlayerScoreboardManager scoreboardManager, SkillManager skillManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.scoreboardManager = scoreboardManager;
        this.skillManager = skillManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 如果配置了自动显示计分板，则创建计分板
        if (plugin.getConfig().getBoolean("scoreboard.auto-show", true)) {
            scoreboardManager.createScoreboard(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 移除玩家的计分板和数据
        scoreboardManager.removeScoreboard(player);
        dataManager.removePlayerData(player);
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData data = dataManager.getPlayerData(player);
        
        if (data != null) {
            // 增加死亡次数
            data.addDeath();
            
            // 增加惩罚值
            int punishmentOnDeath = plugin.getConfig().getInt("stats.deaths.punishment-on-death", 1);
            int incrementOnDeath = plugin.getConfig().getInt("stats.punishment.increment-on-death", 1);
            data.addPunishment(punishmentOnDeath + incrementOnDeath);
            
            // 检查最大惩罚值限制
            int maxPunishment = plugin.getConfig().getInt("stats.punishment.max-punishment", 100);
            if (maxPunishment > 0 && data.getPunishment() > maxPunishment) {
                data.setPunishment(maxPunishment);
            }
            
            // 更新计分板
            scoreboardManager.updateScoreboard(player);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // 检查是否是技能物品
        SkillData skill = skillManager.getSkillByItem(item);
        if (skill == null) {
            // 不是技能物品，使用旧的触发逻辑（兼容性）
            List<String> triggerItems = plugin.getConfig().getStringList("stats.skills.trigger-items");
            Material itemType = item.getType();
            
            for (String triggerItem : triggerItems) {
                try {
                    Material triggerMaterial = Material.valueOf(triggerItem.toUpperCase());
                    if (itemType == triggerMaterial) {
                        // 右键使用物品，增加技能使用次数
                        PlayerData data = dataManager.getPlayerData(player);
                        if (data != null) {
                            data.addSkill();
                            scoreboardManager.updateScoreboard(player);
                        }
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    // 物品ID无效，跳过
                    plugin.getLogger().warning("无效的物品ID: " + triggerItem);
                }
            }
            return;
        }
        
        // 检查技能系统是否启用
        if (!plugin.getConfig().getBoolean("skills.enabled", true)) {
            return;
        }
        
        // 检查冷却
        if (skillManager.isOnCooldown(player)) {
            int remaining = skillManager.getRemainingCooldown(player);
            String message = plugin.getConfig().getString("messages.skill-cooldown", "&c技能冷却中，剩余%time%秒")
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            return;
        }
        
        // 使用技能
        PlayerData data = dataManager.getPlayerData(player);
        if (data != null) {
            // 增加技能使用次数
            data.addSkill();
            
            // 设置冷却
            if (skill.getCooldown() > 0) {
                skillManager.setSkillCooldown(player, skill);
            }
            
            // 发送技能使用消息
            int skillNumber = skill.getSkillNumber();
            String message = plugin.getConfig().getString("messages.skill-used", "&a你使用了技能%skill_id%")
                    .replace("%skill_id%", String.valueOf(skillNumber));
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            
            // 更新计分板
            scoreboardManager.updateScoreboard(player);
            
            // 触发技能效果（预留接口）
            triggerSkillEffect(player, skill);
        }
    }
    
    /**
     * 触发技能效果（预留接口）
     * 你可以在后续告诉我每个技能的具体效果
     */
    private void triggerSkillEffect(Player player, SkillData skill) {
        // 这里预留技能效果接口
        // 你可以后续告诉我每个技能的效果，我会在这里实现
        
        String effect = skill.getEffect();
        int skillNumber = skill.getSkillNumber();
        
        // 根据技能编号触发不同效果
        switch (skillNumber) {
            case 1:
                // 技能1效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能1效果触发");
                break;
            case 2:
                // 技能2效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能2效果触发");
                break;
            case 3:
                // 技能3效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能3效果触发");
                break;
            case 4:
                // 技能4效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能4效果触发");
                break;
            case 5:
                // 技能5效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能5效果触发");
                break;
            case 6:
                // 技能6效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能6效果触发");
                break;
            case 7:
                // 技能7效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能7效果触发");
                break;
            case 8:
                // 技能8效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能8效果触发");
                break;
            case 9:
                // 技能9效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能9效果触发");
                break;
            case 10:
                // 技能10效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能10效果触发");
                break;
            case 11:
                // 技能11效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能11效果触发");
                break;
            case 12:
                // 技能12效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能12效果触发");
                break;
            case 13:
                // 技能13效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能13效果触发");
                break;
            case 14:
                // 技能14效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能14效果触发");
                break;
            case 15:
                // 技能15效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能15效果触发");
                break;
            case 16:
                // 技能16效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能16效果触发");
                break;
            case 17:
                // 技能17效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能17效果触发");
                break;
            case 18:
                // 技能18效果
                player.sendMessage(org.bukkit.ChatColor.GREEN + "技能18效果触发");
                break;
            default:
                // 其他技能
                player.sendMessage(org.bukkit.ChatColor.YELLOW + "技能" + skillNumber + "效果待实现");
                break;
        }
        
        // 如果配置文件中有自定义效果配置，可以在这里处理
        if (!"none".equalsIgnoreCase(effect)) {
            // 处理自定义效果
            plugin.getLogger().info("玩家 " + player.getName() + " 使用了技能" + skillNumber + "，效果: " + effect);
        }
    }
}