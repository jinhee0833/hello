package com.helloants.mm.helloants1.data.type;

/**
 * Created by park on 2016-01-21.
 */
public class ContentType {
    public int mID;
    public String mFilePath;
    public String mSubTitle;
    public String mImageDescription;
    public String mLink;
    public int image;
    public ContentType() {
        int mID = 0;
        mFilePath = "";
        mSubTitle = "";
        mImageDescription = "";
    }

    public ContentType( String mSubTitle,int image) {
        this.image = image;
        this.mSubTitle = mSubTitle;
    }

    public String getmSubTitle() {
        return mSubTitle;
    }

    public int getImage() {
        return image;
    }
}
