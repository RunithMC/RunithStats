package net.runith.runithstats.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import net.runith.runithstats.database.PlayerStats;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public record PlayerStatsRepository(MongoCollection<Document> collection) implements Repository<PlayerStats> {

    public static final String
        ID = "_id", // MongoDB id optimized
        NAME = "name",
        KILLS = "kills",
        DEATHS = "deaths",
        PLAYTIME = "playtime";

    public static final ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);
    public static final long MINIMUM_PLAY_TIME = TimeUnit.SECONDS.toMillis(10);

    @Override
    public Optional<PlayerStats> findById(final @NotNull UUID uuid) {
        final Document doc = collection.find(Filters.eq(ID, uuid)).first();
        if (doc == null) {
            return Optional.empty();
        }

        return Optional.of(createPlayerStat(uuid, doc));
    }

    private static @NotNull PlayerStats createPlayerStat(@NotNull UUID uuid, Document doc) {
        final Long playTime = doc.getLong(PLAYTIME);
        return new PlayerStats(
            uuid,
            doc.getString(NAME),
            doc.getInteger(KILLS, 0),
            doc.getInteger(DEATHS, 0),
            playTime == null ? 0 : playTime
        );
    }

    @Override
    public List<PlayerStats> findAll() {
        final List<Document> documents = collection.find().into(new ArrayList<>());
        final List<PlayerStats> stats = new ArrayList<>();
        for (final Document document : documents) {
            stats.add(createPlayerStat((UUID) document.get(ID), document));
        }
        return stats;
    }

    @Override
    public void save(final @NotNull PlayerStats playerStats) {
        final Document document = transformStatsToDocument(playerStats);
        if (document == null) {
            return;
        }

        collection.replaceOne(
            Filters.eq(ID, playerStats.getUuid()),
            document,
            REPLACE_OPTIONS
        );
    }

    @Override
    public void saveAll(@NotNull final Collection<PlayerStats> playerStats) {
        final var writes = new ArrayList<WriteModel<Document>>(playerStats.size());

        for (final PlayerStats stat : playerStats) {
            final Document document = transformStatsToDocument(stat);
            if (document != null) {
                writes.add(new ReplaceOneModel<>(
                    Filters.eq(ID, stat.getUuid()),
                    document,
                    REPLACE_OPTIONS
                ));
            }
        }

        if (!writes.isEmpty()) {
            collection.bulkWrite(writes);
        }
    }

    private static @Nullable Document transformStatsToDocument(@NotNull PlayerStats playerStats) {
        final Document document = new Document();

        if (playerStats.getKills() > 0) {
            document.append(KILLS, playerStats.getKills());
        }
        if (playerStats.getDeaths() > 0) {
            document.append(DEATHS, playerStats.getDeaths());
        }

        final long playTime = playerStats.getPlayTime();
        if (playTime >= MINIMUM_PLAY_TIME) {
            document.append(PLAYTIME, playTime);
        }

        if (document.isEmpty()) { // Don't save in database if isn't necessary
            return null;
        }

        document.append(ID, playerStats.getUuid());
        document.append(NAME, playerStats.getName());
        return document;
    }

    @Override
    public void delete(@NotNull UUID uuid) {
        collection.deleteOne(Filters.eq(ID, uuid));
    }
}