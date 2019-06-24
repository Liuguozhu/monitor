//package com.coder.monitor.service;
//
//
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Service("mongoService")
//public class MongoService {
//    public static Map<String, String> map = new HashMap<>();
//
//    public static void monitorOne(String host, String dbName) {
//        MongoClient mongo = new MongoClient(host);
//        MongoDatabase db = mongo.getDatabase(dbName);
//        Document d = new Document();
//        d.append("_id", "testmongoislive");
//        db.getCollection("test").insertOne(d);
//        db.getCollection("test").deleteOne(d);
//        mongo.close();
//    }
//}
