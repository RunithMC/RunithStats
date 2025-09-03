package net.runith.runithstats.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.concurrent.ExecutorService;

public record MongoDatabase(
    ExecutorService executor,
    MongoClient mongoClient,
    MongoCollection<Document> collection
) {

    public void close() {
        mongoClient.close();
        executor.shutdown();
    }
}