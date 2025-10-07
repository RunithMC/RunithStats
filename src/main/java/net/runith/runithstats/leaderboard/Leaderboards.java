package net.runith.runithstats.leaderboard;

import lombok.Data;

@Data
public final class Leaderboards {
    private Leaderboard
        kills = new Leaderboard(),
        deaths = new Leaderboard();
    private LongLeaderboard playTime = new LongLeaderboard();

    public void remove(final String name) {
        kills.remove(name);
        deaths.remove(name);
        playTime.remove(name);
    }
}
