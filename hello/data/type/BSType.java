package com.helloants.mm.helloants1.data.type;

/**
 * Created by kingherb on 2016-01-30.
 */
public class BSType {
    private String mType;
    private String mName;
    private long mValue;



    private static int mCheck;
    private static boolean mCheck2;

    public static boolean ismCheck2() {
        return mCheck2;
    }

    public static void setmCheck2(boolean mCheck2) {
        BSType.mCheck2 = mCheck2;
    }

    public static int getmCheck() {
        return mCheck;
    }

    public static void setmCheck(int mCheck) {
        BSType.mCheck = mCheck;
    }

    public BSType() {
        mName = "";
        mValue = 0;
    }

    public BSType(String name) {
        mName = name;
        mValue = 0;
    }

    public BSType(String name, long value) {
        mName = name;
        mValue = value;
    }

    public BSType(String name, String type) {
        mName = name;
        mType = type;
    }


    public void setName(String name) {
        mName = name;
    }

    public void setValue(long value) {
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public long getValue() {
        return mValue;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }
}