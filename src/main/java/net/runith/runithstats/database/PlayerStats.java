package net.runith.runithstats.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public final class PlayerStats {
    private final UUID uuid;
    private final String name;
    private final long sessionJoinDate = System.currentTimeMillis();

    private int kills;
    private int deaths;
    private long totalPlayTime;

    public void addKills(int amount) { this.kills += amount; }

    public void addDeaths(int amount) { this.deaths += amount; }

    public double getKDR() {
        if (deaths == 0) return kills;
        return (double) Math.round((double) kills / deaths * 100) / 100;
    }

    public long getPlayTime() {
        return (System.currentTimeMillis() - sessionJoinDate) + totalPlayTime;
    }
}