package com.helloants.mm.helloants1.db.member;

/**
 * Created by park on 2016-01-21.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.helloants.mm.helloants1.data.type.FBType;
import com.helloants.mm.helloants1.data.type.NaverType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.nhn.android.naverlogin.OAuthLogin;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by park on 2016-01-13.
 */
public enum MemberDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private String mDBName;
    private Context mContext;
    private SharedPreferences mPref;
    private BasicDBObject mUser;
    private boolean mIsEqual;
    private boolean mIsFbJoin;
    private boolean mIsDuplicated;
    private boolean mIsNaverJoin;
    private String mEnc;

    private MemberDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mDBName = "member";
    }

    public void init(Context context) {
        mContext = context;
        mPref = mContext.getSharedPreferences("helloants", mContext.MODE_PRIVATE);
        setLoginData();
    }

    public void join(BasicDBObject insert) {
        String[] strs = new String[0];
        mColc = mDB.getCollection("numberCount");
        mColc.findAndModify(new BasicDBObject("_id", "userid"),
                new BasicDBObject("$inc",
                        new BasicDBObject("seq", 1)));
        DBObject temp = mColc.findOne(new BasicDBObject("_id", "userid"));

        mColc = mDB.getCollection("member");
        BasicDBObject doc = new BasicDBObject("_id", temp.get("seq"))
                .append("email", insert.get("email"))
                .append("pw", insert.get("pw"))
                .append("name", insert.get("name"))
                .append("gender", insert.get("gender"))
                .append("birth", insert.get("birth"))
                .append("joinPath", insert.get("joinPath"))
                .append("joinDate", new Date())
                .append("contentLike", strs)
                .append("replyLike", strs)
                .append("boxLists", strs)
                .append("dcsReplyList", strs);
        new SendPwTask().execute(doc);

        encryptLoginData(doc);
    }

    public void deviceToken(BasicDBObject query, BasicDBObject update) {
        mColc = mDB.getCollection("member");
        BulkWriteOperation builder = mColc.initializeOrderedBulkOperation();
        builder.find(query).updateOne(new BasicDBObject("$set", update));
        builder.execute();
    }

    public void insert(BasicDBObject user) {
        mColc = mDB.getCollection("member");
        mColc.insert(user);
    }

    public boolean confirmPW(final String pw) {
        ConfirmPW cp = new ConfirmPW(pw);
        cp.start();
        try {
            cp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mIsEqual;
    }

    public void modifyPW(final String pw) {
        ModifyPW mp = new ModifyPW(pw);
        mp.start();
        try {
            mp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean checkLogin(BasicDBObject user, boolean isAutoLogin) {
        mColc = mDB.getCollection(mDBName);
        mUser = user;
        Thread pw = new OnlyGetPW();
        pw.start();
        try {
            pw.join();
        } catch (InterruptedException e) {
        }

        Log.v("체크", String.valueOf(mUser.getString("email")));
        Log.v("체크", String.valueOf(mUser.getString("pw")));

        DBCursor cursor = mColc.find(mUser);

        if (cursor.hasNext()) {
            encryptLoginData(cursor.next());
            if (isAutoLogin) autoLogin();
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private void encryptLoginData(DBObject user) {
        String email = user.get("email").toString();
        String name = user.get("name").toString();
        String joinPath = user.get("joinPath").toString();
        try {
            email = Cryptogram.INSTANCE.Encrypt(email);
            name = Cryptogram.INSTANCE.Encrypt(name);
            joinPath = Cryptogram.INSTANCE.Encrypt(joinPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginData.setLoginData(email, name, joinPath);
        autoLogin();
    }

    private void encryptLoginData(FBType user) {
        String email = user.mEmail;
        String name = user.mName;
        String joinPath = user.mJoinPath;

        try {
            email = Cryptogram.INSTANCE.Encrypt(email);
            name = Cryptogram.INSTANCE.Encrypt(name);
            joinPath = Cryptogram.INSTANCE.Encrypt(joinPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginData.setLoginData(email, name, joinPath);
        autoLogin();
    }

    private void encryptLoginData(NaverType user) {
        String email = user.mEmail;
        String name = user.mName;
        String joinPath = user.mJoinPath;

        try {
            email = Cryptogram.INSTANCE.Encrypt(email);
            name = Cryptogram.INSTANCE.Encrypt(name);
            joinPath = Cryptogram.INSTANCE.Encrypt(joinPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginData.setLoginData(email, name, joinPath);
        autoLogin();
    }

    public void setLoginData() {
        String email = mPref.getString("email", "");
        String name = mPref.getString("name", "");
        String joinPath = mPref.getString("joinPath", "");

        LoginData.setLoginData(email, name, joinPath);
    }

    public void autoLogin() {
        SharedPreferences.Editor edit = mPref.edit();

        edit.putString("email", LoginData.mEmail);
        edit.putString("name", LoginData.mName);
        edit.putString("joinPath", LoginData.mJoinPath);

        edit.commit();
    }

    public boolean isFbJoin(final String EMAIL) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL));

                if (cursor.hasNext()) mIsFbJoin = true;
                else mIsFbJoin = false;
                cursor.close();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mIsFbJoin;
    }

    public void fbLogin(FBType user) {
        encryptLoginData(user);
        autoLogin();
    }

    public boolean isNaverJoin(final String EMAIL) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL));

                if (cursor.hasNext()) mIsNaverJoin = true;
                else mIsNaverJoin = false;

                cursor.close();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mIsNaverJoin;
    }

    public void naverLogin(NaverType user) {
        encryptLoginData(user);
        autoLogin();
    }

    public void logout() {
        LoginData.clear();

        SharedPreferences.Editor edit = mPref.edit();

        edit.putString("email", "");
        edit.putString("name", "");
        edit.putString("joinPath", "");

        edit.commit();
    }

    public void fbLogout() {
        LoginManager.getInstance().logOut();
        logout();
    }

    public void naverLogout() {
        OAuthLogin.getInstance().logout(mContext);
        logout();
    }

    public long getCount() {
        return mDB.getCollection(mDBName).count();
    }

    public DBCursor find() {
        BasicDBObject oderby = new BasicDBObject("date", -1);
        mColc = mDB.getCollection(mDBName);
        return mColc.find().sort(oderby);
    }

    public DBCursor find(DBObject query) {
        BasicDBObject oderby = new BasicDBObject("date", -1);
        mColc = mDB.getCollection(mDBName);
        return mColc.find(query).sort(oderby);
    }

    public void update(final DBObject query, final DBObject update) {
        mColc = mDB.getCollection(mDBName);
        mColc.update(query, update);
    }

    public void delete(DBObject query) {
        mColc = mDB.getCollection(mDBName);
        BulkWriteOperation builder = mColc.initializeOrderedBulkOperation();
        builder.find(query).removeOne();
        builder.execute();
    }

    public String emailSearch(BasicDBObject query) {
        mColc = mDB.getCollection(mDBName);
        DBCursor cursor = mColc.find(query);
        StringBuilder sb = new StringBuilder();
        while (cursor.hasNext()) {
            sb.append(cursor.next().get("email").toString() + "\n ");
        }
        cursor.close();
        return sb.toString();
    }

    public String pwSearch(final BasicDBObject USER) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                String pw = Math.floor(Math.random() * 100000000) + "ants";
                try {
                    mEnc = Cryptogram.Encrypt(pw);
                } catch (Exception e) {
                }

                // 멤버 비밀번호 변경
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            return null;
        }

        return mEnc;
    }

    public boolean isDuplicate(final String EMAIL) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL));
                if (cursor.hasNext()) mIsDuplicated = true;
                else mIsDuplicated = false;
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
        return mIsDuplicated;
    }

    private class SendPwTask extends AsyncTask<BasicDBObject, Void, Object> {
        private HttpClient mHttpClient = new DefaultHttpClient();
        private String mUrl = "http://www.helloants.com/getAndSendPW";

        @Override
        protected Object doInBackground(BasicDBObject... params) {
            executeClient(params[0]);
            return null;
        }

        public void executeClient(BasicDBObject user) {
            HttpPost httpPost = new HttpPost(mUrl + "/" + user.getString("pw"));
            HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");

            try {
                HttpResponse response = mHttpClient.execute(httpPost);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "utf-8")
                );

                String line = null;
                String result = "";

                while ((line = bufReader.readLine()) != null) {
                    result += line;
                }
                user.removeField("pw");
                user.append("pw", result);
                INSTANCE.insert(user);
                synchronized (mContext) {
                    INSTANCE.mContext.notify();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class OnlyGetPW extends Thread {
        private HttpClient mHttpClient = new DefaultHttpClient();
        private String mUrl = "http://www.helloants.com/getAndSendPW";

        @Override
        public void run() {
            HttpPost httpPost = new HttpPost(mUrl + "/" + mUser.getString("pw"));
            HttpParams hparams = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(hparams, 3000);
            HttpConnectionParams.setSoTimeout(hparams, 3000);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            try {
                HttpResponse response = mHttpClient.execute(httpPost);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "utf-8")
                );
                String line = null;
                String result = "";
                while ((line = bufReader.readLine()) != null) {
                    result += line;
                }
                mUser.removeField("pw");
                mUser.append("pw", result);
            } catch (IOException e) {
            }
        }
    }

    private class ModifyPW extends Thread {
        private HttpClient mHttpClient = new DefaultHttpClient();
        private String mUrl = "http://www.helloants.com/getAndSendPW";
        private String mPW;

        public ModifyPW(String pw) {
            mPW = pw;
        }

        @Override
        public void run() {
            HttpPost httpPost = new HttpPost(mUrl + "/" + mPW);
            HttpParams hparams = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(hparams, 3000);
            HttpConnectionParams.setSoTimeout(hparams, 3000);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            try {
                HttpResponse response = mHttpClient.execute(httpPost);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "utf-8")
                );
                String line = null;
                String result = "";
                while ((line = bufReader.readLine()) != null) {
                    result += line;
                }

                String email = "";

                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                }
                mColc = mDB.getCollection("member");
                mColc.findAndModify(new BasicDBObject("email", email),
                        new BasicDBObject("$set",
                                new BasicDBObject("pw", result)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConfirmPW extends Thread {
        private HttpClient mHttpClient = new DefaultHttpClient();
        private String mUrl = "http://www.helloants.com/getAndSendPW";
        private String mPW;

        public ConfirmPW(String pw) {
            mPW = pw;
        }

        @Override
        public void run() {
            HttpPost httpPost = new HttpPost(mUrl + "/" + mPW);
            HttpParams hparams = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(hparams, 3000);
            HttpConnectionParams.setSoTimeout(hparams, 3000);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            try {
                HttpResponse response = mHttpClient.execute(httpPost);
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "utf-8")
                );
                String line = null;
                String result = "";
                while ((line = bufReader.readLine()) != null) {
                    result += line;
                }

                String email = "";

                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mColc = mDB.getCollection("member");
                try {
                    String test = ((BasicDBObject) mColc.find(new BasicDBObject("email", email)).next()).getString("pw");

                if (test.equals(result)) mIsEqual = true;
                else mIsEqual = false;
                } catch (NoSuchElementException e) {
                    mIsEqual = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set myCardFind() {
        final Set set = new HashSet();
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("member");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    DBCursor cursor = mColc.find(new BasicDBObject("email", email));
                    if (cursor.hasNext()) {
                        BasicDBObject obj = (BasicDBObject) cursor.next();
                        List list = (List) obj.get("myCard");

                        if(list == null) return;

                        Iterator iter = list.iterator();
                        while (iter.hasNext()) {
                            set.add(iter.next());
                        }
                    }
                } catch (IllegalStateException e) {
                } catch (NullPointerException e) {

                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return set;
    }

    public Set myCardOffsetFind() {
        final Set set = new HashSet();
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("member");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                }

                try {
                    DBCursor cursor = mColc.find(new BasicDBObject("email", email));
                    if (cursor.hasNext()) {
                        BasicDBObject obj = (BasicDBObject) cursor.next();
                        List list = (List) obj.get("cardOffsetDay");
                        Iterator iter = list.iterator();
                        while (iter.hasNext()) {
                            set.add(iter.next());
                        }
                    }
                } catch (Exception e) {
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }

        return set;
    }

    public int salaryDayFind() {
        final int[] salaryDay = {0};

        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("member");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DBCursor cursor = mColc.find(new BasicDBObject("email", email));
                if (cursor.hasNext()) {
                    BasicDBObject object = (BasicDBObject) cursor.next();
                    salaryDay[0] = (int) object.get("salaryDate");
                }

            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return salaryDay[0];
    }

    public boolean isCardOff() {
        Set set = myCardFind();
        if (set == null) return false;
        else if (set.isEmpty()) return false;

        set = myCardOffsetFind();
        if (set == null) return true;
        else if (set.isEmpty()) return true;
        for (Object obj : set) {
            String str = String.valueOf(obj);
            if (str.contains("credit")) return true;
        }

        return false;
    }
}