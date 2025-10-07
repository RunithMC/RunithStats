package net.runith.runithstats.leaderboard;

import lombok.RequiredArgsConstructor;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.database.repository.PlayerStatsRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class LeaderboardLoader {
    private final PlayerStatsRepository statsRepository;
    private final Leaderboards leaderboards;
    private final ExecutorService executorService;
    private final Logger logger;

    public void load() {
        executorService.execute(() -> {
            try {
                loadSync();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error on load leaderboards", e);
            }
        });
    }

    private void loadSync() {
        final List<PlayerStats> players = statsRepository.findAll();

        final Leaderboard
            kills = new Leaderboard(),
            deaths = new Leaderboard();
        final LongLeaderboard playTime = new LongLeaderboard();

        for (final PlayerStats stats : players) {
            final String name = stats.getName();
            kills.setScore(name, stats.getKills());
            deaths.setScore(name, stats.getDeaths());
            playTime.setScore(name, stats.getPlayTime());
        }

        leaderboards.setKills(kills);
        leaderboards.setDeaths(deaths);
        leaderboards.setPlayTime(playTime);
    }
}
