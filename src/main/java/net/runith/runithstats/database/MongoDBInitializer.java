package net.runith.runithstats.database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.runith.runithstats.RunithStats;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class MongoDBInitializer {

    public static DatabaseManager initialize(final RunithStats plugin) throws MongoException {
        FileConfiguration config = plugin.getConfig();

        final String user = config.getString("database.user", "admin");
        final String pass = config.getString("database.password", "password");
        final String host = config.getString("database.host", "localhost");
        final String databaseName = config.getString("database.database", "runithstats");
        final String collection = config.getString("database.collection", "player_stats");

        final int threadsPool = config.getInt("database.thread-pool", 4);
        final int port = config.getInt("database.port", 27017);
        final int connectionTimeOut = config.getInt("database.connect-timeout-seconds", 3);
        final int readTimeOut = config.getInt("database.read-timeout-seconds", 2);

        final String uri = "mongodb://" + user + ":" + pass + "@" + host + ":" + port + "/?authSource=" + databaseName;

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
            final MongoDatabase mongoDatabase = client.getDatabase(databaseName);

            mongoDatabase.runCommand(new Document("ping", 1));

            final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);

            final ExecutorService executor = Executors.newFixedThreadPool(threadsPool);

            return new DatabaseManager(executor, client, mongoCollection);

        } catch (Exception e) {
            throw new MongoException("Failed to initialize MongoDB: " + e.getMessage(), e);
        }
    }
}