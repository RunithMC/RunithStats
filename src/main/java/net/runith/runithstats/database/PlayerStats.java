package net.runith.runithstats.database;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class PlayerStats {
    private UUID uuid;
    private String name;
    private int kills;
    private int deaths;
    private long playtime;

    public PlayerStats(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.kills = 0;
        this.deaths = 0;
        this.playtime = 0;
    }

    public void addKills(int amount) { this.kills += amount; }

    public void addDeaths(int amount) { this.deaths += amount; }

    public void addPlaytime(long milliseconds) { this.playtime += milliseconds; }

    public double getKDR() {
        if (deaths == 0) return kills;
        return (double) Math.round((double) kills / deaths * 100) / 100;
    }

    public long getPlaytimeHours() {
        return playtime / 3600000;
    }
}