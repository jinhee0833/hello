package com.helloants.mm.helloants1.data.type;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by park on 2016-01-29.
 */
public class NoticeType {
    public int mID;
    public int mContentID;
    public String mFilePath;
    public Date mDate;
    public Bitmap mBitmap;

    public NoticeType() {
        mID = 0;
        mContentID = 0;
        mFilePath = "";
        mDate = null;
        mBitmap = null;
    }
}
