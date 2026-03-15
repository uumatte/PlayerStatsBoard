package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.entity.Player;

/**
 * 玩家数据类
 * 存储玩家的死亡次数、技能使用次数和惩罚值
 */
public class PlayerData {
    private final Player player;
    private int deaths;
    private int skills;
    private int punishment;
    
    public PlayerData(Player player) {
        this.player = player;
        this.deaths = 0;
        this.skills = 0;
        this.punishment = 0;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public int getDeaths() {
        return deaths;
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public void addDeath() {
        this.deaths++;
    }
    
    public int getSkills() {
        return skills;
    }
    
    public void setSkills(int skills) {
        this.skills = skills;
    }
    
    public void addSkill() {
        this.skills++;
    }
    
    public int getPunishment() {
        return punishment;
    }
    
    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }
    
    public void addPunishment(int amount) {
        this.punishment += amount;
    }
    
    public void reset() {
        this.deaths = 0;
        this.skills = 0;
        this.punishment = 0;
    }
    
    @Override
    public String toString() {
        return "PlayerData{" +
                "player=" + player.getName() +
                ", deaths=" + deaths +
                ", skills=" + skills +
                ", punishment=" + punishment +
                '}';
    }
}