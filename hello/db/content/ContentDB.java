package com.helloants.mm.helloants1.db.content;


import com.helloants.mm.helloants1.data.constant.AreaType;
import com.helloants.mm.helloants1.data.type.ContentType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by park on 2016-01-21.
 */
public enum ContentDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private String mFilePath;

    public int mCurrentNum;
    public ArrayList<ContentType> mContentList;
    public ArrayList<ContentType> mNewsList;
    public ArrayList<ContentType> mPcmtList;
    public ArrayList<ContentType> mTipList;
    public ArrayList<ContentType> mAllList;

    private List<String> mContentLike;

    private ContentDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mColc = mDB.getCollection("content");
        mContentList = new ArrayList<ContentType>();
        mFilePath = "http://d2exf4rydl6bqi.cloudfront.net/img/";

        contentMain(MongoQuery.INSTANCE.NewsContent());
        mNewsList = mContentList;
        contentMain(MongoQuery.INSTANCE.PcmtContent());
        mPcmtList = mContentList;
        contentMain(MongoQuery.INSTANCE.TipContent());
        mTipList = mContentList;
        contentMain(MongoQuery.INSTANCE.Content());
        mAllList = mContentList;
    }

    public void onlyCall() {}
    public String getSubTitle(int id) {
        for(ContentType ct : mAllList) {
            if(ct.mID == id) return ct.mSubTitle;
        }

        return null;
    }
    public void getContent(int location, int area) {
        switch (location) {
            case AreaType.CONTENT_MAIN:
                switch (area) {
                    case AreaType.NEWS:
                        contentMain(MongoQuery.INSTANCE.NewsContent());
                        break;
                    case AreaType.PCMT:
                        contentMain(MongoQuery.INSTANCE.PcmtContent());
                        break;
                    case AreaType.TIP:
                        contentMain(MongoQuery.INSTANCE.TipContent());
                        break;
                    case AreaType.ALL:
                        contentMain(MongoQuery.INSTANCE.Content());
                        break;
                }
                break;
            case AreaType.CONTENT_DETAIL:
                contentDetail(MongoQuery.INSTANCE.ContentDetail(area));
                break;
        }
    }

    public void plusReplyCount(int id) {
        mColc.findAndModify(new BasicDBObject("_id", id),
                new BasicDBObject("$inc",
                        new BasicDBObject("replyCount", 1)));
    }

    private void contentDetail(BasicDBObject query) {
        BasicDBObject sort = new BasicDBObject("fileName", 1);
        mColc = mDB.getCollection("file");
        DBCursor cursor = mColc.find(query).sort(sort);
        int i = 0;
        mContentList = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            mContentList.add(new ContentType());
            mContentList.get(i).mImageDescription = obj.getString("ImgContent");
            mContentList.get(i).mLink = obj.getString("link");
            mContentList.get(i++).mFilePath = mFilePath + obj.getString("filePath");
        }
        cursor.close();
    }

    private void contentMain(BasicDBObject query) {
        BasicDBObject sort = new BasicDBObject("_id", -1);
        mColc = mDB.getCollection("content");
        DBCursor cursor = mColc.find(query).sort(sort);
        int i = 0;
        mContentList = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            mContentList.add(new ContentType());
            mContentList.get(i).mID = obj.getInt("_id");
            mContentList.get(i).mSubTitle = obj.getString("subTitle");
            mContentList.get(i++).mFilePath = mFilePath + obj.getString("filePath");
        }
        cursor.close();
    }

    public boolean isLike(int id) {
        try {
            if (LoginData.mEmail.equals("")) { return false; } else {
                String email = "";

                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mColc = mDB.getCollection("member");

                final String EMAIL = email;
                final int ID = id;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        DBCursor cursor = mColc.find(MongoQuery.INSTANCE.email(EMAIL));
                        mContentLike = (List<String>) cursor.next().get("contentLike");
                        String str = String.valueOf(ID);
                    }
                };

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String s = String.valueOf(id);
                if (mContentLike != null) {
                    if (mContentLike.contains(s)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        } catch(NullPointerException e) { return false; }
    }

    public void clickLike(int id) {
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mColc = mDB.getCollection("member");

        final String EMAIL = email;
        final int ID = id;
        Thread thread = new Thread() {
            @Override
            public void run() {
                DBCursor cursor = mColc.find(MongoQuery.INSTANCE.email(EMAIL));
                List<String> contentLike = (List<String>) cursor.next().get("contentLike");
                String[] arr = new String[0];
                String str = String.valueOf(ID);

                if (contentLike.contains(str)) {
                    contentLike.remove(str);
                    contentLikeDown(ID);
                } else {
                    contentLike.add(str);
                    contentLikeUp(ID);
                }
                arr = contentLike.toArray(arr);
                contentLikeUpdate(arr);
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void contentLikeDown(int id) {
        mColc = mDB.getCollection("content");
        mColc.findAndModify(MongoQuery.INSTANCE.id(id),
                new BasicDBObject("$inc",
                        new BasicDBObject("like", -1)));
    }

    private void contentLikeUp(int id) {
        mColc = mDB.getCollection("content");
        mColc.findAndModify(MongoQuery.INSTANCE.id(id),
                new BasicDBObject("$inc",
                        new BasicDBObject("like", 1)));
    }

    private void contentLikeUpdate(String[] arr) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mColc = mDB.getCollection("member");
        mColc.findAndModify(MongoQuery.INSTANCE.email(email),
                new BasicDBObject("$set",
                        new BasicDBObject("contentLike", arr)));
    }

    public void contentViewUp(final int id) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("content");
                mColc.findAndModify(MongoQuery.INSTANCE.id(id),
                        new BasicDBObject("$inc",
                                new BasicDBObject("hit", 1)));
            }
        };
        thread.start();
    }
}
