package com.helloants.mm.helloants1.db.mypage;

import com.helloants.mm.helloants1.data.type.UserType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * Created by park on 2016-02-04.
 */
public enum ProfileDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private UserType mUser;

    private ProfileDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mUser = new UserType();
    }

    public void onlyCall() {}
    public UserType getUserDate() {
        return mUser;
    }
    public void settingData() {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String EMAIL = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL));
                BasicDBObject user = ((BasicDBObject) mColc.find(new BasicDBObject("email", EMAIL)).next());

                mUser.mEmail = EMAIL;
                mUser.mName = user.getString("name");
                mUser.mGender = user.getString("gender");
                mUser.mBirth = user.getString("birth");
                mUser.mJoinDate = user.getDate("joinDate");
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
