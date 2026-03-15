package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 命令处理器
 * 处理插件的所有命令
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private final Playerstatsboard plugin;
    private final DataManager dataManager;
    private final PlayerScoreboardManager scoreboardManager;
    private final SkillManager skillManager;
    
    public CommandHandler(Playerstatsboard plugin, DataManager dataManager, PlayerScoreboardManager scoreboardManager, SkillManager skillManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.scoreboardManager = scoreboardManager;
        this.skillManager = skillManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "show":
                handleShow(sender);
                break;
            case "hide":
                handleHide(sender);
                break;
            case "reset":
                handleReset(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "skills":
                handleSkills(sender);
                break;
            case "cooldown":
                handleCooldown(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    /**
     * 处理显示计分板命令
     */
    private void handleShow(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令！");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("playerstatsboard.use")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        if (scoreboardManager.hasScoreboard(player)) {
            player.sendMessage(ChatColor.YELLOW + "你的计分板已经在显示了！");
            return;
        }
        
        scoreboardManager.createScoreboard(player);
        String message = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.scoreboard-show", "&a计分板已显示"));
        player.sendMessage(message);
    }
    
    /**
     * 处理隐藏计分板命令
     */
    private void handleHide(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令！");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("playerstatsboard.use")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        if (!scoreboardManager.hasScoreboard(player)) {
            player.sendMessage(ChatColor.YELLOW + "你的计分板已经隐藏了！");
            return;
        }
        
        scoreboardManager.removeScoreboard(player);
        String message = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.scoreboard-hide", "&c计分板已隐藏"));
        player.sendMessage(message);
    }
    
    /**
     * 处理重置数据命令
     */
    private void handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("playerstatsboard.admin")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        if (args.length == 1) {
            // 重置所有玩家数据
            dataManager.resetAllPlayerData();
            scoreboardManager.updateAllScoreboards();
            
            String message = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.reset-all-success", "&a已重置所有玩家的统计数据"));
            sender.sendMessage(message);
        } else if (args.length == 2) {
            // 重置指定玩家数据
            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);
            
            if (target == null) {
                String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.player-not-found", "&c找不到玩家 %player%"))
                    .replace("%player%", playerName);
                sender.sendMessage(message);
                return;
            }
            
            dataManager.resetPlayerData(target);
            scoreboardManager.updateScoreboard(target);
            
            String message = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.reset-success", "&a已重置玩家 %player% 的统计数据"))
                .replace("%player%", target.getName());
            sender.sendMessage(message);
        } else {
            sender.sendMessage(ChatColor.RED + "用法: /scoreboard reset [玩家]");
        }
    }
    
    /**
     * 处理重载配置命令
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("playerstatsboard.admin")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        plugin.reloadConfig();
        scoreboardManager.loadConfig();
        skillManager.reload();
        scoreboardManager.updateAllScoreboards();
        
        sender.sendMessage(ChatColor.GREEN + "配置已重载！");
    }
    
    /**
     * 处理技能列表命令
     */
    private void handleSkills(CommandSender sender) {
        if (!sender.hasPermission("playerstatsboard.use")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== 技能列表 ===");
        sender.sendMessage(ChatColor.YELLOW + "已加载 " + skillManager.getSkillCount() + " 个技能");
        
        skillManager.getAllSkills().forEach((material, skill) -> {
            int skillNumber = skill.getSkillNumber();
            String itemName = material.toString();
            String skillName = skill.getName();
            int cooldown = skill.getCooldown();
            
            String line = ChatColor.GREEN + "技能" + skillNumber + ": " + 
                         ChatColor.AQUA + skillName + 
                         ChatColor.GRAY + " (物品: " + itemName + 
                         ChatColor.GRAY + ", 冷却: " + cooldown + "秒)";
            sender.sendMessage(line);
        });
        
        sender.sendMessage(ChatColor.GRAY + "使用对应物品右键即可使用技能");
    }
    
    /**
     * 处理冷却查询命令
     */
    private void handleCooldown(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令！");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("playerstatsboard.use")) {
            sendNoPermissionMessage(sender);
            return;
        }
        
        if (skillManager.isOnCooldown(player)) {
            int remaining = skillManager.getRemainingCooldown(player);
            sender.sendMessage(ChatColor.RED + "技能冷却中，剩余 " + remaining + " 秒");
        } else {
            sender.sendMessage(ChatColor.GREEN + "没有技能冷却");
        }
    }
    
    /**
     * 发送帮助信息
     */
    private void sendHelp(CommandSender sender) {
        String header = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.help-header", "&6&lPlayerStatsBoard 命令帮助"));
        sender.sendMessage(header);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.help-show", "&e/scoreboard show &7- 显示计分板")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.help-hide", "&e/scoreboard hide &7- 隐藏计分板")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.help-reset", "&e/scoreboard reset [玩家] &7- 重置玩家数据")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.help-reload", "&e/scoreboard reload &7- 重载配置")));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            "&e/scoreboard skills &7- 查看技能列表"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            "&e/scoreboard cooldown &7- 查看技能冷却"));
    }
    
    /**
     * 发送无权限消息
     */
    private void sendNoPermissionMessage(CommandSender sender) {
        String message = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("messages.no-permission", "&c你没有权限执行此命令"));
        sender.sendMessage(message);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 主命令补全
            List<String> subCommands = Arrays.asList("show", "hide", "reset", "reload", "skills", "cooldown");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            // reset 命令的玩家名补全
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}