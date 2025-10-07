package net.runith.runithstats.database;

import com.mongodb.*;
import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import net.runith.runithstats.RunithStatsPlugin;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class MongoDBInitializer {

    public static MongoDatabase initialize(final ConfigurationSection config) throws MongoException {
        final String user = Assertions.notNull("username", config.getString("user"));
        final String pass = Assertions.notNull("password", config.getString("password"));
        final String host = Assertions.notNull("host", config.getString("host"));
        final String database = Assertions.notNull("database", config.getString("database"));
        final String collection = Assertions.notNull("collection", config.getString("collection"));

        final int port = config.getInt("port");
        final int connectionTimeOut = Math.max(1, config.getInt("connect-timeout-seconds"));
        final int readTimeOut = Math.max(1, config.getInt("read-timeout-seconds"));

        if (port < 1024 || port > 49151) {
            throw new MongoConfigurationException("Mongodb port need be 1024 - 49151. See iana registered ports");
        }
        String uri;
        if (config.getBoolean("uri-enable")) {
            uri = config.getString("uri");
        } else {
            uri = "mongodb://" + user + ':' + pass + '@' + host + ':' + port + "/?authSource=" + database;
        }

        final ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        final MongoClientSettings settings = MongoClientSettings.builder()
                .serverApi(serverApi)
                .applyConnectionString(new ConnectionString(uri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                        .readTimeout(readTimeOut, TimeUnit.SECONDS)
                )
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(5000, TimeUnit.MILLISECONDS)
                )
                .build();

        try {
            final MongoClient client = MongoClients.create(settings);
            final com.mongodb.client.MongoDatabase mongoDatabase = client.getDatabase(database);

            mongoDatabase.runCommand(new Document("ping", 1));

            final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);

            return new MongoDatabase(Executors.newVirtualThreadPerTaskExecutor(), client, mongoCollection);

        } catch (Exception e) {
            throw new MongoException("Failed to initialize MongoDB: " + e.getMessage(), e);
        }
    }
}