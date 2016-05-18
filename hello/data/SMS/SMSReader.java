package com.helloants.mm.helloants1.data.SMS;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.helloants.mm.helloants1.data.type.SMSType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kingherb on 2016-02-15.
 */
public enum SMSReader {

    INSTANCE;

    private Date mDate;
    private String mMsgWhere;
    private String mPrice;
    private String mPhoneNum;
    private String mCardCompanyName;
    private boolean mCheck;
    private Cursor mSmsCursor;
    public HashMap<String, String> mSmsMap;
    public ArrayList<String> mOrderList;
    public ArrayList<SMSType> mSmsList;

    public void init(Context context) {
        Uri allMessage = Uri.parse("content://sms/inbox");
        mSmsCursor = context.getContentResolver().query(allMessage, null, null, null, null);
    }

    public void SMSList(Context context){
        Uri allMessage = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(allMessage, null, null, null, null);
        Date dbDate = BsDB.INSTANCE.alreadyIsFind();
        String value = "";
        Set setName = new HashSet();
        while(cursor.moveToNext()){
            //배열안에 우리나라 모든 카드사 이름을 넣고 그카드사의 이름이 문자내용에 있으면 실행
            String[] cardCompany = {"국민", "농협", "우리", "하나","현대","신한","스탠다드","외환","새마을" };//일단 국민, 농협만 나중에 카드사 이름 전부 넣어야함
            String msgBody = cursor.getString(cursor.getColumnIndex("body")).replace("\n", " ").replace("\r", " ");//카드 메세지 받아온거;
            for (int i = 0; i < cardCompany.length; i++) {//카드컴퍼니배열안에 있는 카드사 만큼 for문돌림
                //메세지바디안에 카드컴퍼니배열에 있는 카드사 이름을 포함하는 지 확인
                //ex)농협앞에서 5000원씩 가지고 모이자같은 문자 걸러내기(카드문자는 시간 날짜 표시를 위해 :랑 /가 꼭있음.
                if (msgBody.contains(cardCompany[i]) && msgBody.contains(":") && msgBody.contains("/")) {
                    //문자받은 번호
                    mPhoneNum = cursor.getString(cursor.getColumnIndex("address"));
                    //문자받은 날짜 시간
                    mDate = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex("date"))));
                    //카드사 이름
                    mCardCompanyName = cardCompany[i];

                    //메세지 바디 안에서 금액만 빼오기
                    String temp = msgBody.replaceAll("[0-9]", "t");
                    int wonIndex = temp.indexOf("t원");
                    String temp2 = msgBody.substring(0, wonIndex + 2);
                    String[] splitMsgPrice = temp2.split(" ");
                    mCheck = true;
                    mPrice = "";
                    for (int j = 0; j < splitMsgPrice.length; j++) {
                        if (splitMsgPrice[j].contains("원")) {
                            mPrice += splitMsgPrice[j].replaceAll("[^0-9]", "");
                            mCheck = false;
                        }
                    }
                    if(mCheck){//가격 못 뽑아내면 브레이크
                        break;
                    }
                    //메세지 바디 안에서 어디서 썼는지 빼오기
                    String[] splitMsgWhere = msgBody.split(" ");
                    mMsgWhere = "";
                    for (int k = 0; k < splitMsgWhere.length; k++) {
                        switch (mCardCompanyName) {
                            case "현대":
                                if (!(splitMsgWhere[k].contains("님") || splitMsgWhere[k].contains("/") || splitMsgWhere[k].contains(":") || splitMsgWhere[k].contains("승인") || splitMsgWhere[k].contains("[") || splitMsgWhere[k].contains(mCardCompanyName) || splitMsgWhere[k].contains("사용") || splitMsgWhere[k].contains("["))) {
                                    String temp3 = "";
                                    temp3 += splitMsgWhere[k];
                                    int IndexHyundai = temp3.indexOf(")");
                                    mMsgWhere = temp3.substring(IndexHyundai + 1);
                                }
                                break;
                            case "우리":
                                if (!(splitMsgWhere[k].contains("님") || splitMsgWhere[k].contains("/") || splitMsgWhere[k].contains(":") || splitMsgWhere[k].contains("승인") || splitMsgWhere[k].contains("[") || splitMsgWhere[k].contains(mCardCompanyName) || splitMsgWhere[k].contains("사용") || splitMsgWhere[k].contains("[")|| splitMsgWhere[k].contains("원")|| splitMsgWhere[k].contains("*")|| splitMsgWhere[k].contains("POINT")|| splitMsgWhere[k].contains("적립"))) {
                                    mMsgWhere += splitMsgWhere[k];
                                }
                                break;
                            default://국민,농협,신한
                                if (!(splitMsgWhere[k].contains("님") || splitMsgWhere[k].contains("/") || splitMsgWhere[k].contains(":") || splitMsgWhere[k].contains("승인") || splitMsgWhere[k].contains("[") || splitMsgWhere[k].contains(mCardCompanyName) || splitMsgWhere[k].contains("사용") || splitMsgWhere[k].contains("[")|| splitMsgWhere[k].contains("원")|| splitMsgWhere[k].contains("*"))) {
                                    mMsgWhere += splitMsgWhere[k];
                                }
                                break;
                        }
                    }
                    //이미 있는 문자 빼고 집어넣기
                    if(dbDate == null){
                        if (msgBody.contains("체크")) {//체크카드일 경우 통장 현금 잔고 마이너스
                            value += "("+mDate + ")" + mPhoneNum + "~" + mCardCompanyName + "~" + mPrice + "~" + mMsgWhere +"~check"+"~지출+"+"~"+mCardCompanyName+"은행 계좌-/";
                            setName.add(mCardCompanyName + "~check");
                            Log.v("문자 check : ", msgBody);
                        } else {//신용카드의 경우 부채로 잡아놨다가 카드정산일에 통장현금 마이너스
                            value += "("+mDate + ")" + mPhoneNum + "~" + mCardCompanyName + "~" + mPrice + "~" + mMsgWhere +"~credit"+"~지출+"+"~"+mCardCompanyName+"카드+/";
                            setName.add(mCardCompanyName + "~credit");
                            Log.v("문자 credit : ",msgBody);
                        }
                    }else if(mDate.after(dbDate)){//날짜로 이미있는 날짜 보다 후에것만 집어 넣는다
                        if (msgBody.contains("체크")) {//체크카드일 경우 통장 현금 잔고 마이너스
                            value += "("+mDate + ")" + mPhoneNum + "~" + mCardCompanyName + "~" + mPrice + "~" + mMsgWhere +"~check"+"~지출+"+"~"+mCardCompanyName+"은행 계좌-/";
                            setName.add(mCardCompanyName + "~check");
                        } else {//신용카드의 경우 부채로 잡아놨다가 카드정산일에 통장현금 마이너스
                            value += "("+mDate + ")" + mPhoneNum + "~" + mCardCompanyName + "~" + mPrice + "~" + mMsgWhere +"~credit"+"~지출+"+"~"+mCardCompanyName+"카드+/";
                            setName.add(mCardCompanyName + "~credit");
                        }
                    }
                }
            }
        }

        //마이카드(set)들 멤버디비에 등록
        String email = "";
        try {
            email = Cryptogram.Decrypt(LoginData.mEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!setName.isEmpty()){
            MemberDB.INSTANCE.update(new BasicDBObject("email", email),
                    new BasicDBObject("$set", new BasicDBObject("myCard",setName)));
        }


        //현재까지 모든 거래내역 비에스디비에 등록
        if (value.equals("")) {
            return;
        }else {
            BsDB.INSTANCE.costInsertReader(value);
        }
    }

    public void settingSMSMap() {
        if (mSmsCursor == null) {
            return;
        }

        mSmsMap = new HashMap<String, String>();
        mOrderList = new ArrayList<String>();
        mSmsCursor.moveToFirst();
        do {
            Set<String> key = mSmsMap.keySet();
            String str = mSmsCursor.getString(mSmsCursor.getColumnIndex("address"));

            if (str == null) {
                str = "발신정보없음";
            } else if (str.equals("unknown sender")) {
                str = "발신정보없음";
            }

            if (key.contains(str)) {
                continue;
            } else {
                mOrderList.add(str);
                mSmsMap.put(str, mSmsCursor.getString(mSmsCursor.getColumnIndex("body")));
            }
        } while (mSmsCursor.moveToNext());
    }

    public void settingSMSList(String number) {
        if (mSmsCursor == null) {
            return;
        }

        mSmsList = new ArrayList<SMSType>();
        mSmsCursor.moveToFirst();
        do {
            String phone = mSmsCursor.getString(mSmsCursor.getColumnIndex("address"));
            if (number.equals(phone)) {
                mSmsList.add(new SMSType(phone,
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("body")),
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("date"))));
            } else if (number.equals("발신정보없음") && phone == null) {
                phone = "발신정보없음";

                mSmsList.add(new SMSType(phone,
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("body")),
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("date"))));
            } else if (number.equals("발신정보없음") && phone.equals("unknown sender")) {
                phone = "발신정보없음";

                mSmsList.add(new SMSType(phone,
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("body")),
                        mSmsCursor.getString(mSmsCursor.getColumnIndex("date"))));
            }
        } while (mSmsCursor.moveToNext());
    }
}
