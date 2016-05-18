package com.helloants.mm.helloants1.db.content;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.helloants.mm.helloants1.data.constant.ReplyLikeResult;
import com.helloants.mm.helloants1.data.type.ReplyType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by park on 2016-01-22.
 */
public enum ReplyDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private Context mContext;
    private Fragment mFragment;
    private BaseAdapter mBaseAdapter;

    public int mID;
    public int mCurrentPage;
    public int mSize;
    public ArrayList<ReplyType> mReplyList;
    public ArrayList<ReplyType> mBestReplyList;
    public BasicDBObject mReply;
    public List<String> mReplyLikeList;

    private ReplyDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mReplyList = new ArrayList<ReplyType>();
        setReplyLike();
    }

    public void init(Fragment fragment) {
        mFragment = fragment;
    }
    public void init(Context context) {
        mContext = context;
    }
    public void init(BaseAdapter adapter) {
        mBaseAdapter = adapter;
    }

    private void setReplyLike() {
        try {
            if (LoginData.mEmail.equals("")) {
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
                        mColc = mDB.getCollection("member");
                        DBCursor cursor = mColc.find(new BasicDBObject("email", str));
                        mReplyLikeList = (List<String>) cursor.next().get("replyLike");
                    }
                };
                thread.start();
            }
        } catch(NullPointerException e) {}
    }

    public void setCurrentPage(int currentPage, int tag) {
        mCurrentPage = currentPage;
        setttingReplyList(tag);
    }

    private void setttingReplyList(int tag) {
        setReplySize();
        setBestList();
        setReplyList(tag);
    }

    private void setReplySize() {
        ReplySizeTask rst = new ReplySizeTask();
        rst.execute();
    }

    private void setReplyList(int tag) {
        ReplyTask rt = new ReplyTask(tag);
        rt.execute();
    }

    private void setBestList() {
        BestReplyTask brt = new BestReplyTask();
        brt.execute();
    }

    public void clickLike(int replyID) {
        ReplyLikeTask rlt = new ReplyLikeTask(replyID);
        rlt.execute();
    }

    public boolean isLike(String id) {
        if (mReplyLikeList != null) {
            if (mReplyLikeList.contains(id)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void replyLikeUp(int replyID) {
        mColc = mDB.getCollection("reply");
        mColc.findAndModify(MongoQuery.INSTANCE.id(replyID),
                new BasicDBObject("$inc",
                        new BasicDBObject("like", 1)));
    }

    private void replyLikeDown(int replyID) {
        mColc = mDB.getCollection("reply");
        mColc.findAndModify(MongoQuery.INSTANCE.id(replyID),
                new BasicDBObject("$inc",
                        new BasicDBObject("like", -1)));
    }

    private void replyLikeUpdate(String[] str) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mColc = mDB.getCollection("member");
        mColc.findAndModify(MongoQuery.INSTANCE.email(email),
                new BasicDBObject("$set",
                        new BasicDBObject("replyLike", str)));
    }

    public void insert(String content) {
        new ReplyInsertTask(content).execute();
    }

    public void delete(int id) {
        mColc = mDB.getCollection("reply");
        BulkWriteOperation builder = mColc.initializeOrderedBulkOperation();
        builder.find(MongoQuery.INSTANCE.id(id)).removeOne();
        builder.execute();
    }

    private class ReplyTask extends AsyncTask<Void, Void, Void> {
        private int mTag;

        public ReplyTask(int tag) {
            mTag = tag;
        }

        @Override
        protected Void doInBackground(Void... params) {
            BasicDBObject sort = new BasicDBObject("date", -1);
            mColc = mDB.getCollection("reply");
            DBCursor cursor = mColc.find(MongoQuery.INSTANCE.replyList(mID))
                    .sort(sort)
                    .limit(MongoQuery.INSTANCE.REPLY_PAGE_LIMIT * (mCurrentPage-1));

            int i = 0;
            mReplyList = new ArrayList<ReplyType>();
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                mReplyList.add(new ReplyType());
                mReplyList.get(i).mID = obj.getInt("_id");
                mReplyList.get(i).mWriter = obj.getString("writer");
                mReplyList.get(i).mContent = obj.getString("content");
                mReplyList.get(i).mLike = obj.getInt("like");
                mReplyList.get(i++).mDate = obj.getDate("date");
            }
            cursor.close();

            switch (mTag) {
                case ReplyLikeResult.CONTEXT:
                    synchronized (mContext) {
                        mContext.notify();
                    }
                    break;
                case ReplyLikeResult.FRAGMENT:
                    synchronized (mFragment) {
                        mFragment.notify();
                    }
                    break;
                case ReplyLikeResult.ADAPTER:
                    synchronized (mBaseAdapter) {
                        mBaseAdapter.notify();
                    }
                    break;
            }

            return null;
        }
    }

    private class BestReplyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            BasicDBObject sort = new BasicDBObject("like", -1);
            mColc = mDB.getCollection("reply");
            DBCursor cursor = mColc.find(MongoQuery.INSTANCE.replyList(mID)
                    .append("like", new BasicDBObject("$gte", MongoQuery.INSTANCE.REPLY_BEST_LIMIT)))
                    .sort(sort)
                    .limit(3);

            int i = 0;
            mBestReplyList = new ArrayList<ReplyType>();
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                mBestReplyList.add(new ReplyType());
                mBestReplyList.get(i).mID = obj.getInt("_id");
                mBestReplyList.get(i).mWriter = obj.getString("writer");
                mBestReplyList.get(i).mContent = obj.getString("content");
                mBestReplyList.get(i).mLike = obj.getInt("like");
                mBestReplyList.get(i++).mDate = obj.getDate("date");
            }
            cursor.close();
            return null;
        }
    }

    private class ReplyLikeTask extends AsyncTask<Void, Void, Integer> {
        private int mReplyID;

        public ReplyLikeTask(int replyID) {
            mReplyID = replyID;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String email = "";

            try {
                email = Cryptogram.Decrypt(LoginData.mEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mColc = mDB.getCollection("member");
            DBCursor cursor = mColc.find(new BasicDBObject("email", email));

            mReplyLikeList = (List<String>) cursor.next().get("replyLike");
            String[] str = new String[0];
            String id = String.valueOf(mReplyID);

            if (mReplyLikeList.contains(id)) {
                mReplyLikeList.remove(id);
                replyLikeDown(mReplyID);
                str = mReplyLikeList.toArray(str);
                replyLikeUpdate(str);

                synchronized (mBaseAdapter) {
                    mBaseAdapter.notify();
                }

                return ReplyLikeResult.REMOVE;
            } else {
                mReplyLikeList.add(id);
                replyLikeUp(mReplyID);
                str = mReplyLikeList.toArray(str);
                replyLikeUpdate(str);

                synchronized (mBaseAdapter) {
                    mBaseAdapter.notify();
                }

                return ReplyLikeResult.ADD;
            }
        }
    }

    private class ReplyInsertTask extends AsyncTask<Void, Void, Void> {
        private String mContent;

        public ReplyInsertTask(String content) {
            mContent = content;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mColc = mDB.getCollection("content");
            mColc.findAndModify(new BasicDBObject("_id", mID),
                    new BasicDBObject("$inc",
                            new BasicDBObject("replySeq", 1)));

            mColc = mDB.getCollection("numberCount");
            mColc.findAndModify(new BasicDBObject("_id", "userid"),
                    new BasicDBObject("$inc",
                            new BasicDBObject("replySeq", 1)));
            DBObject temp = mColc.findOne(new BasicDBObject("_id", "userid"));

            mColc = mDB.getCollection("reply");
            String name = "";
            String email = "";

            try {
                name = Cryptogram.INSTANCE.Decrypt(LoginData.mName);
                email = Cryptogram.INSTANCE.Decrypt(LoginData.mEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mReply = new BasicDBObject("_id", temp.get("replySeq"))
                    .append("contentId", mID)
                    .append("writer", name)
                    .append("email", email)
                    .append("content", mContent)
                    .append("date", new Date())
                    .append("like", 0);

            BulkWriteOperation builder = mColc.initializeOrderedBulkOperation();
            builder.insert(mReply);
            builder.execute();

            synchronized (mFragment) {
                mFragment.notify();
            }

            return null;
        }
    }

    private class ReplySizeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mColc = mDB.getCollection("reply");
            mSize = mColc.find(MongoQuery.INSTANCE.replyList(mID)).size();
            return null;
        }
    }
}