package com.helloants.mm.helloants1.data;

import android.graphics.Bitmap;

/**
 * Created by JJ on 2016-01-15.
 */
public class ContentImage {
    public int mImage;
    public Bitmap mBitmap;
    public String mFilePath;
    public String mSubTitle;
    public int mId;

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public String getmSubTitle() {
        return mSubTitle;
    }

    public ContentImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }
    public ContentImage( String subTitle, Bitmap bitmap,int id,String filePath) {
        mSubTitle = subTitle;
        mBitmap = bitmap;
        mId = id;
        mFilePath = filePath;
    }
    public ContentImage( String subTitle, Bitmap bitmap) {
        mSubTitle = subTitle;
        mBitmap = bitmap;

    }
}
