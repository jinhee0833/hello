package com.helloants.mm.helloants1.db.mypage;

import com.helloants.mm.helloants1.data.type.ContentType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by park on 2016-02-04.
 */
public enum ScrapDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    public ArrayList<ContentType> mScrapList;
    private String mFilePath;
    private boolean mIsScrap;
    private boolean mIsDuplicate;

    private ScrapDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mFilePath = "http://d2exf4rydl6bqi.cloudfront.net/img/";
    }

    public void onlyCall() {}
    public void setScrapList() {
        try {
            if (LoginData.mEmail.equals("")) {} else {
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mScrapList = new ArrayList<ContentType>();
                mColc = mDB.getCollection("scrap");
                final String str = email;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        DBCursor cursor = mColc.find(new BasicDBObject("email", str));
                        int i = 0;
                        while (cursor.hasNext()) {
                            BasicDBObject obj = (BasicDBObject) cursor.next();
                            mScrapList.add(new ContentType());
                            mScrapList.get(i).mID = obj.getInt("link");
                            mScrapList.get(i).mSubTitle = obj.getString("name");
                            mScrapList.get(i++).mFilePath = mFilePath + obj.getString("filePath");
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
        } catch(NullPointerException e) {}
    }

    public boolean isScrap(final int ID) {
        try {
            if (LoginData.mEmail.equals("")) {
                return false;
            } else {
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String str = email;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        mColc = mDB.getCollection("scrap");
                        DBCursor cursor = mColc.find(new BasicDBObject("email", str)
                                .append("link", ID));
                        if (cursor.hasNext()) {
                            cursor.close();
                            mIsScrap = true;
                        } else {
                            cursor.close();
                            mIsScrap = false;
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return mIsScrap;
            }
        } catch(NullPointerException e) { return false; }
    }

    public boolean isDuplicate(final String name) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mColc = mDB.getCollection("scrap");
                DBCursor cursor = mColc.find(new BasicDBObject("email", email)
                        .append("name", name));
                if (cursor.hasNext()) {
                    cursor.close();
                    mIsDuplicate = true;
                } else {
                    cursor.close();
                    mIsDuplicate = false;
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {}

        return mIsDuplicate;
    }

    public void insert(final int ID, final String NAME) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {}

        final String EMAIL = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    mColc = mDB.getCollection("content");
                    BasicDBObject file = ((BasicDBObject) mColc.find(new BasicDBObject("_id", ID)).next());
                    String filePath = file.getString("filePath");

                    mColc = mDB.getCollection("scrap");
                    BasicDBObject scrap = new BasicDBObject("email", EMAIL)
                            .append("storageName", "All")
                            .append("name", NAME)
                            .append("link", ID)
                            .append("filePath", filePath);
                    mColc.insert(scrap);

                    mScrapList.add(new ContentType());
                    int size = mScrapList.size() - 1;
                    mScrapList.get(size).mID = ID;
                    mScrapList.get(size).mSubTitle = NAME;
                    mScrapList.get(size).mFilePath = mFilePath + filePath;
                } catch (NoSuchElementException e) {}
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {}
    }

    public void remove(final int id) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {}

        final String str = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("scrap");
                BasicDBObject scrap = new BasicDBObject("email", str)
                        .append("link", id);
                mColc.remove(scrap);

                Iterator iter = mScrapList.iterator();
                while(iter.hasNext()) {
                    ContentType ct = (ContentType) iter.next();
                    if(ct.mID == id) {
                        iter.remove();
                        break;
                    }
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {}
    }
}
