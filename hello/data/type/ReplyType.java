package com.helloants.mm.helloants1.data.type;

import java.util.Date;

/**
 * Created by park on 2016-01-25.
 */
public class ReplyType {
    public int mID;
    public String mWriter;
    public String mContent;
    public int mLike;
    public Date mDate;

    public ReplyType() {
        mID = 0;
        mWriter = "";
        mContent = "";
        mLike = 0;
        mDate = null;
    }
}
