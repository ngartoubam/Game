package com.umbrella.stfctracker.Database.Entities;

import com.umbrella.stfctracker.DataTypes.ResourceMaterial;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Tier implements Serializable {
    private int tier;
    private int buildTime;
    private int repairTime;
    private LinkedList<Level> levels;
    private List<Component> components;

    public Tier(int tier, int buildTime, int repairTime, LinkedList<Level> levels, List<Component> components) {
        this.tier = tier;
        this.buildTime = buildTime;
        this.repairTime = repairTime;
        this.levels = levels;
        this.components = components;
    }

    public int getTier() {
        return tier;
    }
    public int getBuildTime() {
        return buildTime;
    }
    public int getRepairTime() {
        return repairTime;
    }
    public LinkedList<Level> getLevels() {
        return levels;
    }
    public List<Component> getComponents() {
        return components;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }
    public void setRepairTime(int repairTime) {
        this.repairTime = repairTime;
    }
    public void setLevels(LinkedList<Level> levels) {
        this.levels = levels;
    }
    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public static class Level implements Serializable {
        private int level;
        private int requiredShipXP;
        private int shipAbilityBonus;
        private Scrap scrapInfo;

        public Level(int level, int requiredShipXP, int shipAbilityBonus, Scrap scrapInfo) {
            this.level = level;
            this.requiredShipXP = requiredShipXP;
            this.shipAbilityBonus = shipAbilityBonus;
            this.scrapInfo = scrapInfo;
        }

        public Level(int level, int requiredShipXP, int shipAbilityBonus) {
            this.level = level;
            this.requiredShipXP = requiredShipXP;
            this.shipAbilityBonus = shipAbilityBonus;
            this.scrapInfo = null;
        }

        public int getLevel() {
            return level;
        }
        public int getRequiredShipXP() {
            return requiredShipXP;
        }
        public int getShipAbilityBonus() {
            return shipAbilityBonus;
        }
        public Scrap getScrapInfo() {
            return scrapInfo;
        }

        public void setLevel(int level) {
            this.level = level;
        }
        public void setRequiredShipXP(int requiredShipXP) {
            this.requiredShipXP = requiredShipXP;
        }
        public void setShipAbilityBonus(int shipAbilityBonus) {
            this.shipAbilityBonus = shipAbilityBonus;
        }
        public void setScrapInfo(Scrap scrapInfo) {
            this.scrapInfo = scrapInfo;
        }

        public static class Scrap {
            private int scrapTime;
            private List<ResourceMaterial> rewards;

            public Scrap(int scrapTime, List<ResourceMaterial> rewards) {
                this.scrapTime = scrapTime;
                this.rewards = rewards;
            }

            public int getScrapTime() {
                return scrapTime;
            }
            public List<ResourceMaterial> getRewards() {
                return rewards;
            }

            public void setScrapTime(int scrapTime) {
                this.scrapTime = scrapTime;
            }
            public void setRewards(List<ResourceMaterial> rewards) {
                this.rewards = rewards;
            }
        }
    }

    public static class Component {
        private ComponentName name;
        private String image;
        private boolean locked;
        private LinkedList<ResourceMaterial> repairCosts;
        private LinkedList<ResourceMaterial> resources;
        private LinkedList<ResourceMaterial> materials;

        public Component(ComponentName name, String image, boolean locked, LinkedList<ResourceMaterial> repairCosts, LinkedList<ResourceMaterial> resources, LinkedList<ResourceMaterial> materials) {
            this.name = name;
            this.image = image;
            this.locked = locked;
            this.repairCosts = repairCosts;
            this.resources = resources;
            this.materials = materials;
        }

        public Component(ComponentName name, String image, LinkedList<ResourceMaterial> repairCosts, LinkedList<ResourceMaterial> resources, LinkedList<ResourceMaterial> materials) {
            this.name = name;
            this.image = image;
            this.locked = true;
            this.repairCosts = repairCosts;
            this.resources = resources;
            this.materials = materials;
        }

        public ComponentName getName() {
            return name;
        }
        public String getImage() {
            return image;
        }
        public boolean isLocked() {
            return locked;
        }
        public LinkedList<ResourceMaterial> getRepairCosts() {
            return repairCosts;
        }
        public LinkedList<ResourceMaterial> getResources() {
            return resources;
        }
        public LinkedList<ResourceMaterial> getMaterials() {
            return materials;
        }

        public void setName(ComponentName name) {
            this.name = name;
        }
        public void setImage(String image) {
            this.image = image;
        }
        public void setLocked(boolean locked) {
            this.locked = locked;
        }
        public void setRepairCosts(LinkedList<ResourceMaterial> repairCosts) {
            this.repairCosts = repairCosts;
        }
        public void setResources(LinkedList<ResourceMaterial> resources) {
            this.resources = resources;
        }
        public void setMaterials(LinkedList<ResourceMaterial> materials) {
            this.materials = materials;
        }

        public enum ComponentName {
            BUILD_SHIP_TOTAL,
            WARP_ENGINES,
            CARGO_BAY,
            SHIELD,
            TRITANIUM_ARMOR,
            IMPULSE_ENGINE,
            PHASER_CANNON,
            PHOTON_TORPEDOES,
            MINING_LASER
        }
    }
}
