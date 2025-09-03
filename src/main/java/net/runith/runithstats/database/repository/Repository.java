package net.runith.runithstats.database.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    Optional<T> findById(@NotNull UUID uuid);
    void save(@NotNull T data);
    void saveAll(@NotNull Collection<T> data);
    void delete(@NotNull UUID uuid);
}