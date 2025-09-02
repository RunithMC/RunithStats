package net.runith.runithstats.database.repository;

import net.runith.runithstats.database.PlayerStats;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerStatsRepository {
    CompletableFuture<Optional<PlayerStats>> findByIdAsync(UUID uuid);
    void saveAsync(PlayerStats playerStats);
    Optional<PlayerStats> findById(UUID uuid);
    void save(PlayerStats playerStats);
    void delete(UUID uuid);
}