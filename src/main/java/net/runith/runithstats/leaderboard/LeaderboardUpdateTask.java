package net.runith.runithstats.leaderboard;

import lombok.RequiredArgsConstructor;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.storage.PlayersStatsStorage;

import java.util.Collection;

@RequiredArgsConstructor
public final class LeaderboardUpdateTask implements Runnable {
    private final PlayersStatsStorage storage;
    private final Leaderboards leaderboards;

    @Override
    public void run() {
        final Collection<PlayerStats> players = storage.getStats();
        final Leaderboard
            kills = leaderboards.getKills(),
            deaths = leaderboards.getDeaths();

        final LongLeaderboard playTime = leaderboards.getPlayTime();
        for (final PlayerStats stats : players) {
            final String name = stats.getName();
            kills.setScore(name, stats.getKills());
            deaths.setScore(name, stats.getDeaths());
            playTime.setScore(name, stats.getPlayTime());
        }
    }
}
