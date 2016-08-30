package com.couchbase.lite;

import com.couchbase.lite.replicator.Replication;

import java.net.URL;

public class DBService {
    public static final String DATABASE = "db";

    private static final String DB_DIRECTORY = "data";

    private Manager manager;
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

    private DBService() {
        try {
            manager = new Manager(new JavaContext(DB_DIRECTORY), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DATABASE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class Holder {
        private static DBService INSTANCE = new DBService();
    }

    public static DBService getInstance() { return Holder.INSTANCE; }

    public Database getDatabase() {
        return database;
    }

    public void startReplication(URL gateway, boolean continuous) {
        pushReplication = database.createPushReplication(gateway);
        pullReplication = database.createPullReplication(gateway);
        pushReplication.setContinuous(continuous);
        pullReplication.setContinuous(continuous);
        pushReplication.start();
        pullReplication.start();
    }

    public void stopReplication() {
        pushReplication.stop();
        pullReplication.stop();
    }
}
