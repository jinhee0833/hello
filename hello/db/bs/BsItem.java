package com.helloants.mm.helloants1.db.bs;

import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingherb on 2016-02-02.
 */
public enum BsItem {
    INSTANCE;

    private DBCollection mColc;
    private DB mDB;
    private String[] mAssetArray;
    private String[] mDebtArray;

    private BsItem() {
        mDB = ConnectDB.INSTANCE.mDB;
        mColc = mDB.getCollection("bsItem");

        mAssetArray = new String[0];
        mDebtArray = new String[0];
    }

    public String[] getAssetArrayy() {

        return mAssetArray;
    }

    public String[] getDebtArray() {

        return mDebtArray;
    }

    public void setArray() {
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mColc = mDB.getCollection("bsItem");
        DBCursor cursor = mColc.find(new BasicDBObject("email", email));
        if (cursor.hasNext()) {
            // 가계부를 작성 했다
            BasicDBObject obj = (BasicDBObject) cursor.next();
            List<String> assetList = (List<String>) obj.get("assetList");
            List<String> debtList = (List<String>) obj.get("debtList");



            mAssetArray = assetList.toArray(mAssetArray);
            mDebtArray = debtList.toArray(mDebtArray);

        } else {
            // 가계부를 쓰지 않았다
            mAssetArray = new String[0];
            mDebtArray = new String[0];
        }
        cursor.close();
    }

    public void assetModify(List<BSType> list) {

        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < list.size(); ++i) {
            l.add(list.get(i).getName());
        }
        mAssetArray = l.toArray(mAssetArray);

        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;

        final String str = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bsItem");
                mColc.findAndModify(new BasicDBObject("email", str),
                        new BasicDBObject("$set",
                                new BasicDBObject("assetList", mAssetArray)));
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void debtModify(List<BSType> list) {
        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < list.size(); ++i) {
            l.add(list.get(i).getName());
        }
        mDebtArray = l.toArray(mDebtArray);
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        };

        final String str = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc = mDB.getCollection("bsItem");
                mColc.findAndModify(new BasicDBObject("email", str),
                        new BasicDBObject("$set",
                                new BasicDBObject("debtList", mDebtArray)));
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }

    }

    public void insertAsset(List<BSType> arr) {
        mAssetArray = new String[arr.size()];
        for (int i = 0; i < arr.size(); ++i) {
            mAssetArray[i] = arr.get(i).getName();
        }
    }

    public void insertDebt(List<BSType> arr) {
        mDebtArray = new String[arr.size()];
        for (int i = 0; i < arr.size(); ++i) {
            mDebtArray[i] = arr.get(i).getName();
        }
    }

    public void insert() {
        mColc = mDB.getCollection("bsItem");

        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
        }

        BasicDBObject user = new BasicDBObject("email", email)
                .append("assetList", mAssetArray)
                .append("debtList", mDebtArray);
        mColc.insert(user);
    }

    public boolean checkAsset() {
        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
        }

        mColc = mDB.getCollection("bsItem");
        DBCursor cursor = mColc.find(new BasicDBObject("email", email));
        if (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            Object temp = obj.get("assetList");
            if (temp == null) {
                return false;
            } else {
                List<String> list = (List<String>) temp;
                mAssetArray = list.toArray(mAssetArray);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean checkDebt() {
        mColc = mDB.getCollection("bsItem");

        String email = "";

        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBCursor cursor = mColc.find(new BasicDBObject("email", email));
        if (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            Object temp = obj.get("debtList");
            if (temp == null) {
                return false;
            } else {
                List<String> list = (List<String>) temp;
                mDebtArray = list.toArray(mDebtArray);
                return true;
            }
        } else {
            return false;
        }
    }

    public void insertOne(String item, int i) {
        mColc = mDB.getCollection("bsItem");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BasicDBObject user = null;
        final String EMAIL = email;
        if (i == 0) {
            user = new BasicDBObject("assetList", item);
        } else if (i == 1) {
            user = new BasicDBObject("debtList", item);
        }
        final BasicDBObject finalUser = user;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc.update(new BasicDBObject("email", EMAIL), new BasicDBObject("$push", finalUser));
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public ArrayList[] BsItemFind() {
        mColc = mDB.getCollection("bsItem");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ArrayList[] list = new ArrayList[2];
        final String finalEmail = email;
        Thread thread = new Thread() {
            @Override
            public void run() {
                DBCursor cursor = mColc.find(new BasicDBObject("email", finalEmail));
                if (cursor.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cursor.next();
                    list[0] = (ArrayList<String>) obj.get("assetList");
                    list[1] = (ArrayList<String>) obj.get("debtList");
                }
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

    public void removeOne(String item, int i) {
        mColc = mDB.getCollection("bsItem");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BasicDBObject user = null;
        final String EMAIL = email;
        if (i == 0) {
            user = new BasicDBObject("assetList", item);
        } else if (i == 1) {
            user = new BasicDBObject("debtList", item);
        }
        final BasicDBObject finalUser = user;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc.update(new BasicDBObject("email", EMAIL), new BasicDBObject("$pull", finalUser));
                removeArray();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void removeArray() {
        String[] temp = new String[mAssetArray.length-1];
        for(int i = 0; i < temp.length; ++i) {
            temp[i] = mAssetArray[i];
        }
        mAssetArray = temp;


        temp = new String[mDebtArray.length-1];
        for(int i = 0; i < temp.length; ++i) {
            temp[i] = mDebtArray[i];
        }
        mDebtArray = temp;
    }

    public void modifyOne(String before, String after, int i) {
        mColc = mDB.getCollection("bsItem");
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BasicDBObject user = null;
        BasicDBObject user2 = null;
        final String EMAIL = email;
        if (i == 0) {
            user = new BasicDBObject("assetList", before);
        } else if (i == 1) {
            user = new BasicDBObject("debtList", before);
        }
        if (i == 0) {
            user2 = new BasicDBObject("assetList", after);
        } else if (i == 1) {
            user2 = new BasicDBObject("debtList", after);
        }
        final BasicDBObject finalUser = user;
        final BasicDBObject finalUser2 = user2;
        Thread thread = new Thread() {
            @Override
            public void run() {
                mColc.update(new BasicDBObject("email", EMAIL), new BasicDBObject("$pull", finalUser));
                mColc.update(new BasicDBObject("email", EMAIL), new BasicDBObject("$push", finalUser2));
                removeArray();
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