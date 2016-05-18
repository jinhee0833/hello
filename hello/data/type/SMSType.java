package com.helloants.mm.helloants1.data.type;

/**
 * Created by park on 2016-04-05.
 */
public class SMSType {
    public String mPhoneNumber;
    public String mBody;
    public String mDate;
    public boolean mIsSelected;

    public SMSType() {
        mPhoneNumber = "";
        mBody = "";
        mDate = "";
        mIsSelected = false;
    }

    public SMSType(String number, String body, String date) {
        mPhoneNumber = number;
        mBody = body;
        mDate = date;
        mIsSelected = false;
    }
}
