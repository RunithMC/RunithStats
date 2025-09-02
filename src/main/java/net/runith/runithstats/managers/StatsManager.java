package net.runith.runithstats.managers;

import net.runith.runithstats.RunithStats;
import net.runith.runithstats.database.DatabaseManager;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.database.repository.PlayerStatsRepository;
import net.runith.runithstats.database.repository.impl.MongoPlayerStatsRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {

    private final RunithStats plugin;
    private final DatabaseManager databaseManager;
    private final PlayerStatsRepository repository;
    private final Map<UUID, PlayerStats> cachedStats = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playtimeSessions = new ConcurrentHashMap<>();

    public StatsManager(RunithStats plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.repository = new MongoPlayerStatsRepository(databaseManager);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllStats, 6000, 6000);
    }

    public void loadPlayerStats(Player player) {
        UUID uuid = player.getUniqueId();
        repository.findByIdAsync(uuid).thenAcceptAsync(optionalStats -> {
            PlayerStats stats = optionalStats.orElse(new PlayerStats(uuid, player.getName()));
            stats.setName(player.getName());
            cachedStats.put(uuid, stats);
            playtimeSessions.put(uuid, System.currentTimeMillis());
        });
    }

    public void savePlayerStats(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = cachedStats.get(uuid);
        if (stats != null) {
            Long sessionStart = playtimeSessions.remove(uuid);
            if (sessionStart != null) {
                stats.addPlaytime(System.currentTimeMillis() - sessionStart);
            }
            repository.saveAsync(stats);
            cachedStats.remove(uuid);
        }
    }

    public void saveAllStats() {
        long currentTime = System.currentTimeMillis();
        cachedStats.forEach((uuid, stats) -> {
            Long sessionStart = playtimeSessions.get(uuid);
            if (sessionStart != null) {
                stats.addPlaytime(currentTime - sessionStart);
                playtimeSessions.put(uuid, currentTime);
            }
            repository.saveAsync(stats);
        });
    }

    public PlayerStats getPlayerStats(UUID uuid) {
        return cachedStats.get(uuid);
    }

    public void addKill(UUID uuid) {
        PlayerStats stats = cachedStats.get(uuid);
        if (stats != null) {
            stats.addKills(1);
        }
    }

    public void addDeath(UUID uuid) {
        PlayerStats stats = cachedStats.get(uuid);
        if (stats != null) {
            stats.addDeaths(1);
        }
    }

    public void setKills(UUID uuid, int kills) {
        PlayerStats stats = cachedStats.get(uuid);
        if (stats != null) {
            stats.setKills(kills);
        }
    }

    public void setDeaths(UUID uuid, int deaths) {
        PlayerStats stats = cachedStats.get(uuid);
        if (stats != null) {
            stats.setDeaths(deaths);
        }
    }

    public CompletableFuture<Optional<PlayerStats>> getOfflinePlayerStats(String playerName) {
        Optional<PlayerStats> cached = cachedStats.values().stream()
                .filter(stats -> stats.getName().equalsIgnoreCase(playerName))
                .findFirst();

        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture.supplyAsync(() -> {
            for (PlayerStats stats : cachedStats.values()) {
                if (stats.getName().equalsIgnoreCase(playerName)) {
                    return Optional.of(stats);
                }
            }
            return Optional.empty();
        });
    }
}