package net.runith.runithstats.database.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.runith.runithstats.database.DatabaseManager;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.database.repository.PlayerStatsRepository;
import org.bson.Document;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MongoPlayerStatsRepository implements PlayerStatsRepository {

    private final ExecutorService executor;
    private final MongoCollection<Document> collection;

    public MongoPlayerStatsRepository(DatabaseManager databaseManager) {
        this.executor = databaseManager.getExecutor();
        this.collection = databaseManager.getCollection();
    }

    @Override
    public CompletableFuture<Optional<PlayerStats>> findByIdAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findById(uuid), executor);
    }

    @Override
    public void saveAsync(PlayerStats playerStats) {
        CompletableFuture.runAsync(() -> save(playerStats), executor);
    }

    @Override
    public Optional<PlayerStats> findById(UUID uuid) {
        Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
        if (doc != null) {
            PlayerStats stats = new PlayerStats(uuid, doc.getString("name"));
            stats.setKills(doc.getInteger("kills", 0));
            stats.setDeaths(doc.getInteger("deaths", 0));
            stats.setPlaytime(doc.getLong("playtime"));
            return Optional.of(stats);
        }
        return Optional.empty();
    }

    @Override
    public void save(PlayerStats playerStats) {
        Document doc = new Document()
                .append("uuid", playerStats.getUuid().toString())
                .append("name", playerStats.getName())
                .append("kills", playerStats.getKills())
                .append("deaths", playerStats.getDeaths())
                .append("playtime", playerStats.getPlaytime());

        collection.replaceOne(
                Filters.eq("uuid", playerStats.getUuid().toString()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public void delete(UUID uuid) {
        collection.deleteOne(Filters.eq("uuid", uuid.toString()));
    }
}