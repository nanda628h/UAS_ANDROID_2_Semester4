package com.example.remainderjadwal;

import java.io.Serializable;

public class Badge implements Serializable {
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean unlocked;
    private long unlockedAt;

    public Badge(String id, String name, String description, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.unlocked = false;
        this.unlockedAt = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public boolean isUnlocked() { return unlocked; }
    public long getUnlockedAt() { return unlockedAt; }

    public void unlock() {
        this.unlocked = true;
        this.unlockedAt = System.currentTimeMillis();
    }
}