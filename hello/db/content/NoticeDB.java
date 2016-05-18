package com.helloants.mm.helloants1.db.content;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.type.NoticeType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by park on 2016-01-21.
 */
public enum NoticeDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private String mCloudFront;
    public ArrayList<NoticeType> mNoticeList;

    private NoticeDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mNoticeList = new ArrayList<NoticeType>();
        mCloudFront = "http://d2exf4rydl6bqi.cloudfront.net/img/";
    }

    public void settingImg() {
        mColc = mDB.getCollection("notice");
        findNoticePath();
        settingSlideList();
    }

    private void settingSlideList() {
        SlideTask st = new SlideTask();
        st.execute();
    }

    private void findNoticePath() {
        Thread mThread = new Thread() {
            @Override
            public void run() {
                BasicDBObject oderby = new BasicDBObject("date", -1);
                DBCursor cursor = mColc.find();
                int i = 0;
                while (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    mNoticeList.add(new NoticeType());
                    mNoticeList.get(i).mID = obj.getInt("_id");
                    mNoticeList.get(i).mContentID = obj.getInt("contentId");
                    mNoticeList.get(i).mFilePath = mCloudFront + obj.getString("filePath");
                    mNoticeList.get(i++).mDate = obj.getDate("date");
                }
                cursor.close();
            }
        };
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
        }
    }

    private class SlideTask extends AsyncTask<Void, Void, Void> {
        private URL mUrl;
        private HttpURLConnection mHUC;
        private InputStream mIpStream;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < mNoticeList.size(); ++i) {
                    mUrl = new URL(mNoticeList.get(i).mFilePath);
                    mHUC = (HttpURLConnection) mUrl.openConnection();
                    mHUC.connect();

                    mIpStream = mHUC.getInputStream();
                    Bitmap bit = BitmapFactory.decodeStream(mIpStream);
                    bit = Bitmap.createScaledBitmap(bit,
                            DeviceSize.mWidth,
                            DeviceSize.mWidth / 2,
                            true);
                    mNoticeList.get(i).mBitmap = bit;
                }

            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }

            return null;
        }
    }
}