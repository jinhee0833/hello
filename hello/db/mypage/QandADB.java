package com.helloants.mm.helloants1.db.mypage;

import com.helloants.mm.helloants1.data.type.QandAType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.db.NumberCountDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by park on 2016-02-04.
 */
public enum QandADB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    public ArrayList<QandAType> mQandAList;

    private QandADB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mQandAList = new ArrayList<QandAType>();
    }

    public void insert(final String TITLE, final String CONTENT) {
        int id = NumberCountDB.INSTANCE.qanda();
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
                mColc = mDB.getCollection("qanda");
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
                BasicDBObject sort = new BasicDBObject("date", -1);
                mColc = mDB.getCollection("qanda");
                DBCursor cursor = mColc.find(MongoQuery.INSTANCE.email(EMAIL))
                        .sort(sort)
                        .limit(MongoQuery.INSTANCE.QANDA_PAGE_SIZE * CURRENTPAGE); // 10

                int i = 0;
                mQandAList = new ArrayList<QandAType>();
                while(cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    mQandAList.add(new QandAType());
                    mQandAList.get(i).mID = obj.getInt("_id");
                    mQandAList.get(i).mEmail = EMAIL;
                    mQandAList.get(i).mTitle = obj.getString("rTitle");
                    mQandAList.get(i).mContent = obj.getString("rContent");
                    mQandAList.get(i).mDate = obj.getDate("date");
                    mQandAList.get(i).mHit = obj.getInt("hit");
                    mQandAList.get(i).mATitle = obj.getString("aTitle");
                    mQandAList.get(i).mAContent = obj.getString("aContent");
                    mQandAList.get(i++).mComplete = obj.getInt("complete");
                }
                cursor.close();
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
