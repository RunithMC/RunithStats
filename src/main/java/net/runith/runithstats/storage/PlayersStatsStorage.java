package net.runith.runithstats.storage;

import lombok.Getter;
import net.runith.runithstats.database.MongoDatabase;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.database.repository.PlayerStatsRepository;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public final class PlayersStatsStorage {

    private final ExecutorService executorService;
    private final Map<UUID, PlayerStats> cachedStats = new ConcurrentHashMap<>();

    @Getter
    private final PlayerStatsRepository playerStatsRepository;

    public PlayersStatsStorage(MongoDatabase mongoDatabase) {
        this.executorService = mongoDatabase.executor();
        this.playerStatsRepository = new PlayerStatsRepository(mongoDatabase.collection());
    }

    public PlayerStats getPlayerStats(UUID uuid) {
        return cachedStats.get(uuid);
    }

    public Collection<PlayerStats> getStats() {
        return cachedStats.values();
    }

    public void loadPlayerStats(final Player player) {
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();

        executorService.execute(() -> cachedStats.put(uuid, playerStatsRepository.findById(uuid)
            .orElse(new PlayerStats(uuid, name))));
    }

    public void savePlayerStats(final Player player) {
        final UUID uuid = player.getUniqueId();

        final PlayerStats stats = cachedStats.remove(uuid);
        if (stats != null) {
            executorService.execute(() -> playerStatsRepository.save(stats));
        }
    }

    public void saveAllStats() {
        playerStatsRepository.saveAll(cachedStats.values());
    }

    public void saveAllStatsAsync() {
        executorService.execute(this::saveAllStats);
    }

    public Optional<PlayerStats> getCachedPlayerStats(final String playerName) {
        final Optional<PlayerStats> cached = cachedStats.values().stream()
            .filter(stats -> stats.getName().equalsIgnoreCase(playerName))
            .findFirst();

        if (cached.isPresent()) {
            return cached;
        }

        for (final PlayerStats stats : cachedStats.values()) {
            if (stats.getName().equalsIgnoreCase(playerName)) {
                return Optional.of(stats);
            }
        }
        return Optional.empty();
    }
}