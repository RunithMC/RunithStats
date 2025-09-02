package net.runith.runithstats.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;

import java.util.concurrent.ExecutorService;

@Getter
public class DatabaseManager {
    private final ExecutorService executor;
    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;

    public DatabaseManager(ExecutorService executor, MongoClient mongoClient, MongoCollection<Document> collection) {
        this.executor = executor;
        this.mongoClient = mongoClient;
        this.collection = collection;
    }

    public void close() {
        mongoClient.close();
        executor.shutdown();
    }
}