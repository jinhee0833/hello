package com.helloants.mm.helloants1.db.sms;

import com.helloants.mm.helloants1.data.SMS.SMSReader;
import com.helloants.mm.helloants1.data.type.SMSType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by park on 2016-03-18.
 */
public enum SMSDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;

    private SMSDB() {
        mDB = ConnectDB.INSTANCE.mDB;
    }

    public void insert() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mColc = mDB.getCollection("sms");

                Iterator<SMSType> iter = SMSReader.INSTANCE.mSmsList.iterator();
                while (iter.hasNext()) {
                    SMSType type = iter.next();
                    if (type.mIsSelected) {
                        BasicDBObject sms = new BasicDBObject("email", email)
                                .append("content", type.mBody)
                                .append("phoneNum", type.mPhoneNumber)
                                .append("date", new Date());
                        mColc.insert(sms);
                    }
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
