package com.helloants.mm.helloants1.db.bs;


import android.content.Context;
import android.util.Log;

import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.data.type.ISType;
import com.helloants.mm.helloants1.data.type.messageType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kingherb on 2016-01-29.
 */
public enum BsDB {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private Date a;
    private Date b;
    private Date firstDate;
    public int mInsertID;
    public ArrayList<ISType> mList;
    public Map<String, ArrayList<ISType>> mDateMap;
    boolean mIsReceiver;
    boolean mIsReader;

    private BsDB() {
        mDB = ConnectDB.INSTANCE.mDB;
        mColc = mDB.getCollection("bs");
        mList = new ArrayList<ISType>();
        mDateMap = new HashMap();
    }

    public void onlyCall() {
    }

    public void plusID(final long I) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bsCount");
                mColc.findAndModify(new BasicDBObject("_id", "bsid"),
                        new BasicDBObject("$inc",
                                new BasicDBObject("seq", I)));
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void firstAssetFind(ArrayList<BSType> l1, ArrayList<BSType> l2) {
        mColc = mDB.getCollection("bs");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBCursor cursor = mColc.find(new BasicDBObject("email", email).append("part", "first").append("firstPlot", "asset")).sort(new BasicDBObject("_id", 1));

        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            String name = obj.getString("left");
            Long value = Long.valueOf(obj.getString("price"));
            String type = obj.getString("type");

            BSType bsType = new BSType();
            bsType.setName(name);
            bsType.setValue(value);
            bsType.setType(type);
            l1.add(bsType);

            BSType bsType2 = new BSType();
            bsType2.setName(name);
            bsType2.setValue(value);
            bsType2.setType(type);
            l2.add(bsType2);
        }
    }

    public void firstDebtFind(ArrayList<BSType> l1, ArrayList<BSType> l2) {
        mColc = mDB.getCollection("bs");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBCursor cursor = mColc.find(new BasicDBObject("email", email).append("part", "first").append("firstPlot", "debt")).sort(new BasicDBObject("_id", 1));

        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            String name = obj.getString("right");
            Long value = Long.valueOf(obj.getString("price"));
            String type = obj.getString("type");

            BSType bsType = new BSType();
            bsType.setName(name);
            bsType.setValue(value);
            bsType.setType(type);
            l1.add(bsType);

            BSType bsType2 = new BSType();
            bsType2.setName(name);
            bsType2.setValue(value);
            bsType2.setType(type);
            l2.add(bsType2);
        }
    }

    public void assetInsert(ArrayList<BSType> list, String tag) {
        plusID(1);
        mColc = mDB.getCollection("bsCount");
        BasicDBObject temp = (BasicDBObject) mColc.findOne(new BasicDBObject("_id", "bsid"));

        mColc = mDB.getCollection("bs");
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int num = temp.getInt("seq");
        for (int i = 0; i < list.size(); ++i) {
            BasicDBObject user = new BasicDBObject("_id", num++)
                    .append("email", email)
                    .append("date", new Date((new Date().getTime() / 1000) * 1000))
                    .append("price", list.get(i).getValue())
                    .append("type", list.get(i).getType())
                    .append("part", "first")
                    .append("where", "초기값 입력")
                    .append("firstPlot", tag);
            if (tag.equals("asset")) {
                user.append("left", list.get(i).getName());
                user.append("right", "기초금액+");
            } else if (tag.equals("debt")) {
                user.append("left", "기초금액+");
                user.append("right", list.get(i).getName());
            }
            final BasicDBObject d = user;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    mColc.insert(d);
                }
            };

            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        plusID((list.size() - 1));
    }

    public void assetModify(ArrayList<BSType> beforeList, final ArrayList<BSType> LIST, final int SIZE) {

        for (int i = 0 ; i < beforeList.size() ; i++){
            Log.v("체크 비포",beforeList.get(i).getName());
        }
        for (int i = 0 ; i < LIST.size() ; i++){
            Log.v("체크 에프터", LIST.get(i).getName());
        }
        mColc = mDB.getCollection("bs");
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //초기값 입력한 날짜
        Date FirstDate = firstDate();

        ArrayList part = new ArrayList();
        part.add("receiver");
        part.add("self");
        //초기값 입력한 날 이후의 거래정보들 불러오기(리시버,셀프)
        DBCursor cursorSelfReceiver = mColc.find(new BasicDBObject("email", email).append("date",new BasicDBObject("$gt",FirstDate)).append("part",new BasicDBObject("$in",part)));

        //초기값 입력한 날 이후의 거래정보들 불러오기(리더)
        DBCursor cursorReader = mColc.find(new BasicDBObject("email", email).append("date", new BasicDBObject("$gt", FirstDate)).append("part", "reader"));

        for (int i = 0; i < beforeList.size(); i++) {
            if (!beforeList.get(i).getName().equals(LIST.get(i).getName())) {
                //퍼스트, 리시버
                while (cursorSelfReceiver.hasNext()) {
                    BasicDBObject obj1 = (BasicDBObject) cursorSelfReceiver.next();
                    String aR = obj1.getString("right");
                    String aL = obj1.getString("left");
                    String afterStrRight = aR.substring(0, aR.length() - 1);
                    String afterStrLeft = aL.substring(0, aL.length() - 1);
                    String tempName = beforeList.get(i).getName();
                    String name = tempName.substring(0, tempName.length() - 1);
                    String temp = LIST.get(i).getName();
                    String name2 = temp.substring(0,temp.length() - 1);
                    if (name.equals(afterStrRight)) {
                        String plusMinusRight = aR.substring(aR.length()-1);
                        int id = obj1.getInt("_id");
                        mColc.findAndModify(new BasicDBObject("_id", id),
                                new BasicDBObject("$set",
                                        new BasicDBObject("right", name2+plusMinusRight)));
                    } else if (name.equals(afterStrLeft)) {
                        String plusMunusLeft = aL.substring(aL.length()-1);
                        int id = obj1.getInt("_id");
                        mColc.findAndModify(new BasicDBObject("_id", id),
                                new BasicDBObject("$set",
                                        new BasicDBObject("left", name2+plusMunusLeft)));
                    }
                }
                //리더
                while (cursorReader.hasNext()) {
                    BasicDBObject obj2 = (BasicDBObject) cursorReader.next();
                    Map map2 = obj2.toMap();
                    Set<String> keySet2 = map2.keySet();
                    Iterator<String> iter3 = keySet2.iterator();
                    iter3.next();
                    iter3.next();
                    iter3.next();
                    while (iter3.hasNext()) {
                        String key = iter3.next();
                        Object str = map2.get(key);
                        //스플릿!
                        String[] arr = str.toString().split("~");
                        String aR = arr[6];
                        String aL = arr[5];
                        String afterStrRight = aR.substring(0, aR.length() - 1);
                        String afterStrLeft = aL.substring(0, aL.length() - 1);

                        String tempName = beforeList.get(i).getName();
                        String name = tempName.substring(0, tempName.length() - 1);
                        String temp = LIST.get(i).getName();
                        String name2 = temp.substring(0,temp.length() - 1);
                        if (name.equals(afterStrRight)) {//right
                            String plusMinusRight = aR.substring(aR.length()-1);
                            String data = arr[0] + "~" + arr[1] + "~" + arr[2] + "~" + arr[3] + "~" + arr[4] + "~" +arr[5] + "~" + name2+plusMinusRight;
                            mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                    new BasicDBObject("$set",
                                            new BasicDBObject(String.valueOf(key), data)));
                        } else if (name.equals(afterStrLeft)) {//left
                            String plusMinusLeft = aL.substring(aL.length()-1);
                            String data = arr[0] + "~" + arr[1] + "~" + arr[2] + "~" + arr[3] + "~" + arr[4] + "~" + name2+plusMinusLeft + "~" + arr[6];
                            mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                    new BasicDBObject("$set",
                                            new BasicDBObject(String.valueOf(key), data)));
                        }
                    }
                }
            }
        }


        DBCursor cursor = mColc.find(new BasicDBObject("email", email)
                .append("part", "first")
                .append("firstPlot", "asset"));

        int i = 0;
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            int id = obj.getInt("_id");
            mColc.findAndModify(new BasicDBObject("_id", id),
                    new BasicDBObject("$set",
                            new BasicDBObject("price", String.valueOf(LIST.get(i).getValue()))
                                    .append("left", LIST.get(i).getName())));
            i++;
        }

        final int COUNT = LIST.size();

        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bsCount");
                DBCursor cur = mColc.find(new BasicDBObject("_id", "bsid"));
                if (cur.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cur.next();
                    mInsertID = obj.getInt("seq");
                    mColc.update(new BasicDBObject("_id", "bsid"),
                            new BasicDBObject("$set", new BasicDBObject("seq", (mInsertID + (COUNT - SIZE)))));
                } else {
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final String EMAIL = email;
        Thread threadInsert = new Thread() {
            @Override
            public void run() {
                for (int j = SIZE; j < COUNT; ++j) {
                    mColc = mDB.getCollection("bs");
                    mColc.insert(new BasicDBObject("_id", ++mInsertID)
                            .append("email", EMAIL)
                            .append("date", new Date((new Date().getTime() / 1000) * 1000))
                            .append("price", String.valueOf(LIST.get(j).getValue()))
                            .append("firstPlot", "asset")
                            .append("type", LIST.get(j).getType())
                            .append("part", "first")
                            .append("where", "초기값 입력")
                            .append("left", LIST.get(j).getName())
                            .append("right", "기초금액+"));
                }
            }
        };

        threadInsert.start();
        try {
            threadInsert.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BsItem.INSTANCE.assetModify(LIST);
    }

    public void debtModify(ArrayList<BSType> beforeList, final ArrayList<BSType> LIST, final int SIZE) {
        mColc = mDB.getCollection("bs");
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //초기값 입력한 날짜
        Date FirstDate = firstDate();

        ArrayList part = new ArrayList();
        part.add("receiver");
        part.add("self");
        //초기값 입력한 날 이후의 거래정보들 불러오기(리시버,셀프)
        DBCursor cursorSelfReceiver = mColc.find(new BasicDBObject("email", email).append("date",new BasicDBObject("$gt",FirstDate)).append("part",new BasicDBObject("$in",part)));

        //초기값 입력한 날 이후의 거래정보들 불러오기(리더)
        DBCursor cursorReader = mColc.find(new BasicDBObject("email", email).append("date",new BasicDBObject("$gt",FirstDate)).append("part", "reader"));


        for (int i = 0; i < beforeList.size(); i++) {
            if (!beforeList.get(i).getName().equals(LIST.get(i).getName())) {
                //퍼스트, 리시버
                while (cursorSelfReceiver.hasNext()) {
                    BasicDBObject obj1 = (BasicDBObject) cursorSelfReceiver.next();
                    String aR = obj1.getString("right");
                    String aL = obj1.getString("left");
                    String afterStrRight = aR.substring(0, aR.length() - 1);
                    String afterStrLeft = aL.substring(0, aL.length() - 1);
                    String tempName = beforeList.get(i).getName();
                    String name = tempName.substring(0, tempName.length() - 1);

                    String temp = LIST.get(i).getName();
                    String name2 = temp.substring(0,temp.length() - 1);
                    if (name.equals(afterStrRight)) {
                        String plusMinusRight = aR.substring(aR.length()-1);
                        int id = obj1.getInt("_id");
                        mColc.findAndModify(new BasicDBObject("_id", id),
                                new BasicDBObject("$set",
                                        new BasicDBObject("right", name2+plusMinusRight)));
                    } else if (name.equals(afterStrLeft)) {
                        String plusMunusLeft = aL.substring(aL.length()-1);
                        int id = obj1.getInt("_id");
                        mColc.findAndModify(new BasicDBObject("_id", id),
                                new BasicDBObject("$set",
                                        new BasicDBObject("left", name2+plusMunusLeft)));
                    }
                }
                //리더
                while (cursorReader.hasNext()) {
                    BasicDBObject obj2 = (BasicDBObject) cursorReader.next();
                    Map map2 = obj2.toMap();
                    Set<String> keySet2 = map2.keySet();
                    Iterator<String> iter3 = keySet2.iterator();
                    iter3.next();
                    iter3.next();
                    iter3.next();
                    while (iter3.hasNext()) {
                        String key = iter3.next();
                        Object str = map2.get(key);
                        //스플릿!
                        String[] arr = str.toString().split("~");
                        String aR = arr[6];
                        String aL = arr[5];
                        String afterStrRight = aR.substring(0, aR.length() - 1);
                        String afterStrLeft = aL.substring(0, aL.length() - 1);

                        String tempName = beforeList.get(i).getName();
                        String name = tempName.substring(0, tempName.length() - 1);
                        String temp = LIST.get(i).getName();
                        String name2 = temp.substring(0,temp.length() - 1);
                        if (name.equals(afterStrRight)) {//right
                            String plusMinusRight = aR.substring(aR.length()-1);
                            String data = arr[0] + "~" + arr[1] + "~" + arr[2] + "~" + arr[3] + "~" + arr[4] + "~" +arr[5] + "~" + name2+plusMinusRight;
                            mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                    new BasicDBObject("$set",
                                            new BasicDBObject(String.valueOf(key), data)));
                        } else if (name.equals(afterStrLeft)) {//left
                            String plusMinusLeft = aL.substring(aL.length()-1);
                            String data = arr[0] + "~" + arr[1] + "~" + arr[2] + "~" + arr[3] + "~" + arr[4] + "~" + name2+plusMinusLeft + "~" + arr[6];
                            mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                    new BasicDBObject("$set",
                                            new BasicDBObject(String.valueOf(key), data)));
                        }
                    }
                }
            }
        }

        DBCursor cursor = mColc.find(new BasicDBObject("email", email)
                .append("part", "first")
                .append("firstPlot", "debt"));

        int i = 0;
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            int id = obj.getInt("_id");
            mColc.findAndModify(new BasicDBObject("_id", id),
                    new BasicDBObject("$set",
                            new BasicDBObject("price", String.valueOf(LIST.get(i).getValue()))
                                    .append("right", LIST.get(i).getName())));
            i++;
        }

        final int COUNT = LIST.size();

        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bsCount");
                DBCursor cur = mColc.find(new BasicDBObject("_id", "bsid"));
                if (cur.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cur.next();
                    mInsertID = obj.getInt("seq");
                    mColc.update(new BasicDBObject("_id", "bsid"),
                            new BasicDBObject("$set", new BasicDBObject("seq", (mInsertID + (COUNT - SIZE)))));
                } else {
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final String EMAIL = email;
        Thread threadInsert = new Thread() {
            @Override
            public void run() {
                for (int j = SIZE; j < COUNT; ++j) {
                    mColc = mDB.getCollection("bs");
                    mColc.insert(new BasicDBObject("_id", ++mInsertID)
                            .append("email", EMAIL)
                            .append("date", new Date((new Date().getTime() / 1000) * 1000))
                            .append("price", String.valueOf(LIST.get(j).getValue()))
                            .append("firstPlot", "debt")
                            .append("type", LIST.get(j).getType())
                            .append("part", "first")
                            .append("where", "초기값 입력")
                            .append("left", "기초금액+")
                            .append("right", LIST.get(j).getName()));
                }
            }
        };
        threadInsert.start();
        try {
            threadInsert.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BsItem.INSTANCE.debtModify(LIST);
    }


    public void costInsertReceiver(final messageType mt) {
        Log.v("mt나와2", mt.mDate + " " + mt.mCard + " " + mt.mWhere + " " + mt.mPrice + " " + mt.mRight + " ");
        plusID(1);
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bsCount");
                BasicDBObject temp = (BasicDBObject) mColc.findOne(new BasicDBObject("_id", "bsid"));
                mColc = mDB.getCollection("bs");

                String email = "";
                //아이디 값 받아오기
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int num = temp.getInt("seq");
                BasicDBObject user = new BasicDBObject("_id", num++)
                        .append("email", email)
                        .append("date", new Date((mt.mDate.getTime() / 1000) * 1000))
                        .append("phoneNum",mt.mPhoneNum)
                        .append("price", mt.mPrice)
                        .append("where", mt.mWhere)
                        .append("cardName", mt.mCard)
                        .append("type", mt.mCheck)
                        .append("left", mt.mLeft)
                        .append("right", mt.mRight)
                        .append("installment",mt.mMonth)
                        .append("part", "receiver");

                final BasicDBObject d = user;
                mColc.insert(d);
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void costInsertReader(final String value) {
        plusID(1);
        final String[] tempSplit = value.split("/");
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bsCount");
                BasicDBObject temp = (BasicDBObject) mColc.findOne(new BasicDBObject("_id", "bsid"));
                mColc = mDB.getCollection("bs");

                String email = "";
                //아이디 값 받아오기
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int num = temp.getInt("seq");
                BasicDBObject user = new BasicDBObject("_id", num++)
                        .append("part", "reader")
                        .append("email", email);
                //쪼갠 밸류값에서 날짜와 내용 분리해서 키 밸류로 나눠 넣기
                for (int i = 0; i < tempSplit.length; i++) {
                    int end = tempSplit[i].indexOf(")");
                    String date = tempSplit[i].substring(1, end);
                    String data = tempSplit[i].substring(end + 1);
                    user.append(date, data);
                }
                final BasicDBObject d = user;
                mColc.insert(d);
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Date alreadyIsFind() {//DB에 들어가있는 문자 날짜중 가장 최근 날짜 리턴
        a = null;
        b = null;
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
                mColc = mDB.getCollection("bs");
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL).append("part", "receiver")).sort(new BasicDBObject("_id", -1));

                if (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    a = obj.getDate("date");
                }
                //맵으로 키밸류 받아오기
                DBCursor cursorReader = mColc.find(new BasicDBObject("email", EMAIL).append("part", "reader")).sort(new BasicDBObject("_id", -1));
                if (cursorReader.hasNext()) {
                    BasicDBObject obj2 = (BasicDBObject) cursorReader.next();
                    Map map = obj2.toMap();
                    Set<String> keySet = map.keySet();
                    Iterator<String> iter = keySet.iterator();
                    iter.next();
                    iter.next();
                    iter.next();
                    b = new Date(iter.next());
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (a == null && b == null) {
            return null;
        } else if (a == null && b != null) {
            return b;
        } else if (a != null && b == null) {
            return a;
        } else {
            if (a.after(b)) {
                return a;
            } else if (b.after(a)) {
                return b;
            } else {
                return a;
            }
        }
    }


    public ArrayList<ISType> initData() {
        final ArrayList list = new ArrayList<>();

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
                mColc = mDB.getCollection("bs");
                BasicDBObject clause1 = new BasicDBObject("part", "self");
                BasicDBObject clause2 = new BasicDBObject("part", "receiver");
                BasicDBObject clause3 = new BasicDBObject("part", "first");
                BasicDBList or = new BasicDBList();
                or.add(clause1);
                or.add(clause2);
                or.add(clause3);
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL).append("$or", or));
                while (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    ISType type = new ISType();
                    type.mEmail = obj.getString("email");
                    type.mDate = obj.getDate("date");
                    type.mPhoneNum = obj.getString("phoneNum");
                    type.mPrice = obj.getString("price");
                    type.mWhere = obj.getString("where");
                    type.mCardName = obj.getString("cardName");
                    type.mType = obj.getString("type");
                    type.mPart = obj.getString("part");
                    type.mFirstPlot = "firstPlot";
                    type.mLeft = obj.getString("left");
                    type.mRight = obj.getString("right");
                    list.add(type);
                }
                DBCursor crs = mColc.find(new BasicDBObject("email", EMAIL).append("part", "reader"));
                while (crs.hasNext()) {
                    BasicDBObject obj3 = (BasicDBObject) crs.next();
                    Map map3 = obj3.toMap();
                    Set<String> keySet3 = map3.keySet();
                    Iterator<String> iter3 = keySet3.iterator();
                    iter3.next();
                    String email = map3.get(iter3.next()).toString();
                    iter3.next();
                    while (iter3.hasNext()) {
                        String key = iter3.next();
                        Object str = map3.get(key);
                        //스플릿!
                        String[] arr = str.toString().split("~");
                        ISType type = new ISType();
                        type.mEmail = email;
                        type.mDate = new Date(key);
                        type.mPhoneNum = arr[0];
                        type.mPrice = arr[2];
                        type.mWhere = arr[3];
                        type.mCardName = arr[1];
                        type.mType = arr[4];
                        type.mLeft = arr[5];
                        type.mRight = arr[6];
                        type.mPart = "reader";
                        list.add(type);
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

        //합쳐진 리스트를 날짜순으로 소트해서 리턴
        final Comparator<ISType> comparator = new Comparator<ISType>() {
            @Override
            public int compare(ISType lhs, ISType rhs) {
                Date d1;
                Date d2;

                d1 = lhs.mDate;
                d2 = rhs.mDate;

                //return (d1.getTime() > d2.getTime() ? -1 : 1);     //descending
                return (d1.getTime() > d2.getTime() ? 1 : -1);     //ascending
            }
        };
        Collections.sort(list, comparator);

        return list;
    }


    public void newIsInsert(final String mRprice, final String mRwhere, final Date mRdate, final String mRtype, final String mRpart, final String mLeft, final String mRight) {
        plusID(1);
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bsCount");
                BasicDBObject temp = (BasicDBObject) mColc.findOne(new BasicDBObject("_id", "bsid"));
                mColc = mDB.getCollection("bs");

                String email = "";
                //아이디 값 받아오기
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int num = temp.getInt("seq");
                BasicDBObject user = new BasicDBObject("_id", num++)
                        .append("email", email)
                        .append("date", mRdate)
                        .append("price", mRprice)
                        .append("where", mRwhere)
                        .append("type", mRtype)
                        .append("part", mRpart)
                        .append("left", mLeft)
                        .append("right", mRight);

                final BasicDBObject d = user;
                mColc.insert(d);
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Date firstDate() {
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bs");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DBCursor cursor = mColc.find(new BasicDBObject("email", email).append("part", "first")).sort(new BasicDBObject("_id", 1));
                if (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    firstDate = obj.getDate("date");
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return firstDate;
    }

    public void firstRemove(final String name, final int i) {
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bs");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i == 0) {
                    mColc.remove(new BasicDBObject("email", email).append("part", "first").append("firstPlot", "asset").append("left", name));
                } else if (i == 1) {
                    mColc.remove(new BasicDBObject("email", email).append("part", "first").append("firstPlot", "debt").append("right", name));
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

    public void isRemove(final Date mDate, final String mLeft, final String mRight, final String mtype, final String mPart) {
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bs");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mColc.remove(new BasicDBObject("email", email)
                        .append("part", mPart)
                        .append("type", mtype)
                        .append("date", mDate)
                        .append("right", mRight)
                        .append("left", mLeft));
            }
        };

        switch (mPart) {//part : first는 마이페이지에서
            case "receiver":
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "reader"://복잡 특별함
                Thread thread2 = new Thread() {
                    public void run() {
                        mColc = mDB.getCollection("bs");
                        String email = "";
                        try {
                            email = Cryptogram.Decrypt(LoginData.mEmail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                new BasicDBObject("$unset",
                                        new BasicDBObject(String.valueOf(mDate), 1)));
                    }
                };
                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "self":
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void isModify(final ArrayList<ISType> before, final ArrayList<ISType> after) {
        Thread thread = new Thread() {
            public void run() {
                mColc = mDB.getCollection("bs");
                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mColc.update(new BasicDBObject("email", email)
                                .append("part", before.get(0).mPart)
                                .append("type", before.get(0).mType)
                                .append("date", before.get(0).mDate)
                                .append("right", before.get(0).mRight)
                                .append("left", before.get(0).mLeft),
                        new BasicDBObject("$set",
                                new BasicDBObject("part", after.get(0).mPart)
                                        .append("type", after.get(0).mType)
                                        .append("date", after.get(0).mDate)
                                        .append("right", after.get(0).mRight)
                                        .append("left", after.get(0).mLeft)
                                        .append("cardName", after.get(0).mCardName)
                                        .append("phoneNum", after.get(0).mPhoneNum)
                                        .append("price", after.get(0).mPrice)
                                        .append("where", after.get(0).mWhere)));
            }
        };

        switch (before.get(0).mPart) {
            case "receiver":
            case "self":
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "reader":
                final String data = after.get(0).mPhoneNum + "~" + after.get(0).mCardName + "~" + after.get(0).mPrice + "~" + after.get(0).mWhere + "~" + after.get(0).mType + "~" +after.get(0).mLeft + "~" + after.get(0).mRight;
                Thread thread2 = new Thread() {
                    public void run() {
                        mColc = mDB.getCollection("bs");
                        String email = "";
                        try {
                            email = Cryptogram.Decrypt(LoginData.mEmail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mColc.update(new BasicDBObject("email", email).append("part", "reader"),
                                new BasicDBObject("$set",
                                        new BasicDBObject(String.valueOf(before.get(0).mDate), data)));
                    }
                };
                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public Map<String, ArrayList<ISType>> monthDataFind(final Context context) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String EMAIL = email;

        mIsReader = false;
        mIsReceiver = false;

        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bs");
                BasicDBObject clause1 = new BasicDBObject("part", "self");
                BasicDBObject clause2 = new BasicDBObject("part", "receiver");
                BasicDBObject clause3 = new BasicDBObject("part", "first");
                BasicDBList or = new BasicDBList();
                or.add(clause1);
                or.add(clause2);
                or.add(clause3);

                Calendar cal = Calendar.getInstance();
                int year = 0;
                int month = 0;
                int preYear;
                int preMonth;
                DBCursor cursor = mColc.find(new BasicDBObject("email", EMAIL).append("$or", or));
                while (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    ISType type = new ISType();
                    type.mDate = obj.getDate("date");
                    type.mPhoneNum = obj.getString("phoneNum");
                    type.mPrice = obj.getString("price");
                    type.mWhere = obj.getString("where");
                    type.mCardName = obj.getString("cardName");
                    type.mType = obj.getString("type");
                    type.mPart = obj.getString("part");
                    type.mFirstPlot = "firstPlot";
                    type.mLeft = obj.getString("left");
                    type.mRight = obj.getString("right");

                    cal.setTime(type.mDate);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH) + 1;

                    if (mList.isEmpty()) {
                        preYear = year;
                        preMonth = month;
                    } else {
                        cal.setTime(mList.get(mList.size() - 1).mDate);
                        preYear = cal.get(Calendar.YEAR);
                        preMonth = cal.get(Calendar.MONTH) + 1;
                    }

                    if (year != preYear || month != preMonth) {
                        mDateMap.put(String.valueOf(year + "년 " + preMonth + "월"), mList);
                        mList = new ArrayList();
                        mList.add(type);
                    } else {
                        mList.add(type);
                    }
                    mDateMap.put(String.valueOf(year + "년 " + month + "월"), mList);
                }
                mIsReader = true;
                if(mIsReceiver && mIsReceiver) {
                    synchronized (context) {
                        context.notify();
                    }
                }
            }
        };

        Thread th_reader = new Thread() {
            @Override
            public void run() {
                mList = new ArrayList();
                DBCursor crs = mColc.find(new BasicDBObject("email", EMAIL).append("part", "reader"));
                while (crs.hasNext()) {
                    BasicDBObject obj3 = (BasicDBObject) crs.next();
                    Map map3 = obj3.toMap();
                    List<String> list = bsSetToList(map3.keySet());
                    Iterator<String> iter3 = list.iterator();
                    try {
                        Set keySet = mDateMap.keySet();
                        while (iter3.hasNext()) {
                            String key = iter3.next();
                            if(key.equals("email") || key.equals("_id") || key.equals("part")) continue;
                            Object str = map3.get(key);

                            String[] arr = str.toString().split("~");
                            ISType type = new ISType();
                            type.mDate = new Date(key);
                            type.mPhoneNum = arr[0];
                            type.mCardName = arr[1];
                            type.mPrice = arr[2];
                            type.mWhere  = arr[3];
                            type.mType = arr[4];
                            type.mLeft = arr[5];
                            type.mRight = arr[6];
                            type.mPart = "reader";

                            Calendar cal = Calendar.getInstance();
                            int year = 0;
                            int month = 0;
                            int preYear;
                            int preMonth;

                            cal.setTime(type.mDate);
                            year = cal.get(Calendar.YEAR);
                            month = cal.get(Calendar.MONTH) + 1;

                            String keyStr = String.valueOf(year + "년 " + month + "월");
                            if (keySet.contains(keyStr)) {
                                mDateMap.get(keyStr).add(type);
                            } else {
                                mDateMap.put(keyStr, new ArrayList<ISType>());
                                mDateMap.get(keyStr).add(type);
                            }
                        }
                    } catch (IllegalArgumentException e) {}
                }
                mIsReceiver = true;
                if(mIsReceiver && mIsReceiver) {
                    synchronized (context) {
                        context.notify();
                    }
                }
            }
        };

        try {
            thread.start();
            th_reader.start();
        } catch (Exception e) {
            return null;
        }

        try {
            synchronized (context) {
                context.wait();
            }
        } catch (InterruptedException e) {}

        return mDateMap;
    }

    public ArrayList cardOffsetPrice(final String cardName) {
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String finalEmail = email;
        final ArrayList list = new ArrayList();
        Thread thread = new Thread(){
            @Override
            public void run(){
                mColc = mDB.getCollection("bs");
                BasicDBObject clause1 = new BasicDBObject("left", cardName+"카드+");
                BasicDBObject clause2 = new BasicDBObject("left", cardName+"카드-");
                BasicDBObject clause3 = new BasicDBObject("right", cardName+"카드+");
                BasicDBObject clause4 = new BasicDBObject("right", cardName+"카드-");
                BasicDBList or = new BasicDBList();
                or.add(clause1);
                or.add(clause2);
                or.add(clause3);
                or.add(clause4);

                int Left = 0;
                int Right = 0;

                Calendar cal = Calendar.getInstance ( );
                cal.add(cal.MONTH, -1);
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 40);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date month;
                Date before1Month = new Date(cal.getTimeInMillis());
                Date firstDate = firstDate();
                if (before1Month.after(firstDate)){
                    month = before1Month;
                }else {
                    month = firstDate;
                }

                list.add(before1Month);
                DBCursor cursor = mColc.find(new BasicDBObject("email", finalEmail).append("$or",or).append("date", new BasicDBObject("$gt", month)));
                while (cursor.hasNext()){
                    BasicDBObject object = (BasicDBObject) cursor.next();
                    String aR = object.getString("right");
                    String aL = object.getString("left");
                    String afterStrRight = aR.substring(0, aR.length() - 1);
                    String afterStrLeft = aL.substring(0, aL.length() - 1);

                    if (afterStrLeft.equals(cardName+"카드")){
                        Left += Integer.parseInt(object.getString("price"));
                    } else if (afterStrRight.equals(cardName+ "카드")) {
                        Right += Integer.parseInt(object.getString("price"));
                    }
                }
                int Sum = Right - Left;
                list.add(Sum);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List bsSetToList(Set<String> set) {
        List<String> list = new ArrayList();
        Iterator<String> iter = set.iterator();

        while(iter.hasNext()) {
            String key = iter.next();
            if(key.equals("email") || key.equals("_id") || key.equals("part")) continue;
            list.add(key);
        }

        return list;
    }
}
