package com.helloants.mm.helloants1.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * Created by park on 2016-02-09.
 */
public enum NumberCountDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private int mID;

    private NumberCountDB() {
        mDB = ConnectDB.INSTANCE.mDB;
    }

    public int member() {
        return get("seq");
    }

    public int request() {
        return get("rSeq");
    }

    public int reply() {
        return get("replySeq");
    }

    public int qanda() {
        return get("qandaSeq");
    }

    public int content() {
        return get("cSeq");
    }

    private int get(final String tag) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("numberCount");
                DBCursor cursor = mColc.find(MongoQuery.INSTANCE.numberCount());
                mID = 0;
                if(cursor.hasNext()) {
                    BasicDBObject member = (BasicDBObject) cursor.next();
                    mID = member.getInt(tag) + 1;
                    mColc.findAndModify(MongoQuery.INSTANCE.numberCount(),
                            new BasicDBObject("$inc",
                                    new BasicDBObject(tag, 1)));
                } else {}

                cursor.close();
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mID;
    }
}
