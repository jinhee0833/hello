package com.helloants.mm.helloants1.db.mypage;

import com.helloants.mm.helloants1.data.type.RequestType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.db.NumberCountDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoTimeoutException;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by park on 2016-02-04.
 */
public enum RequestDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    public ArrayList<RequestType> mRequestList;

    private RequestDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mRequestList = new ArrayList<RequestType>();
    }

    public void onlyCall() {
    }

    public void insert(final String TITLE, final String CONTENT) {
        int id = NumberCountDB.INSTANCE.request();
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int ID = id;
        final String EMAIL = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("request");
                mColc.insert(new BasicDBObject("_id", ID)
                        .append("email", EMAIL)
                        .append("rTitle", TITLE)
                        .append("rContent", CONTENT)
                        .append("date", new Date())
                        .append("hit", 0)
                        .append("complete", 0));
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void list(final int CURRENTPAGE) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String EMAIL = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    BasicDBObject sort = new BasicDBObject("date", -1);
                    mColc = mDB.getCollection("request");
                    DBCursor cursor = mColc.find(MongoQuery.INSTANCE.email(EMAIL))
                            .sort(sort)
                            .limit(MongoQuery.INSTANCE.REQUEST_PAGE_SIZE * CURRENTPAGE);
                    int i = 0;
                    mRequestList = new ArrayList<RequestType>();
                    while (cursor.hasNext()) {
                        BasicDBObject obj = (BasicDBObject) cursor.next();
                        mRequestList.add(new RequestType());
                        mRequestList.get(i).mID = obj.getInt("_id");
                        mRequestList.get(i).mEmail = EMAIL;
                        mRequestList.get(i).mTitle = obj.getString("rTitle");
                        mRequestList.get(i).mContent = obj.getString("rContent");
                        mRequestList.get(i).mDate = obj.getDate("date");
                        mRequestList.get(i).mHit = obj.getInt("hit");
                        mRequestList.get(i++).mComplete = obj.getInt("complete");
                    }
                    cursor.close();
                } catch (MongoTimeoutException e) {}
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void delete(final int ID) {
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                mColc = mDB.getCollection("request");
//                mColc.remove(MongoQuery.INSTANCE.id(ID));
//            }
//        };
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
