package io.Sriptirc_wp_1239.playerstatsboard;

import org.bukkit.Material;

/**
 * 技能数据类
 * 存储技能配置信息
 */
public class SkillData {
    private final String skillId;
    private final String name;
    private final Material item;
    private final int cooldown;
    private final String effect;
    
    public SkillData(String skillId, String name, Material item, int cooldown, String effect) {
        this.skillId = skillId;
        this.name = name;
        this.item = item;
        this.cooldown = cooldown;
        this.effect = effect;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getName() {
        return name;
    }
    
    public Material getItem() {
        return item;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public String getEffect() {
        return effect;
    }
    
    /**
     * 获取技能编号（从skill1中提取数字）
     */
    public int getSkillNumber() {
        try {
            if (skillId.startsWith("skill")) {
                return Integer.parseInt(skillId.substring(5));
            }
        } catch (NumberFormatException e) {
            // 如果解析失败，返回0
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "SkillData{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", item=" + item +
                ", cooldown=" + cooldown +
                ", effect='" + effect + '\'' +
                '}';
    }
}