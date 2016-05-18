package com.helloants.mm.helloants1.db;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * Created by park on 2016-01-12.
 */
public enum ConnectDB {
    INSTANCE;

    public MongoClient mMC;
    public DB mDB;
    public DBCollection mColc;

    private ConnectDB() {
        connect();
    }

    public void connect() {

        try {
            if( mMC == null) {

//                String user = "helloants8686888990";
//                String dbname = "helloants";
//                char[] pass = "z3jhh2ki".toCharArray();
//
//                List<MongoCredential> creds = new ArrayList<>();
//                creds.add(MongoCredential.createScramSha1Credential(user, dbname, pass));
//
//                mMC = new MongoClient(new ServerAddress("52.79.151.254", 27017), creds);
//                mDB = mMC.getDB("helloants");
//
//                mMC.setWriteConcern(WriteConcern.SAFE);

                mMC = new MongoClient("52.69.20.130", 27017);
                mDB = mMC.getDB("helloants");

                mMC.setWriteConcern(WriteConcern.JOURNALED);
            }
        } catch (NoClassDefFoundError e) {}
    }
}