package com.helloants.mm.helloants1.data.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.helloants.mm.helloants1.data.notification.NotificationFormat;
import com.helloants.mm.helloants1.data.type.messageType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by kingherb on 2016-02-10.
 */
public class SMSReceiver extends BroadcastReceiver {
    public String msgBody;
    String excahngeQueryUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDKRW%22)&env=store://datatables.org/alltableswithkeys";

    private Double excahneRate() {
        final String[] rate = {""};
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(excahngeQueryUrl);
                    InputStream is = url.openStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is, "UTF-8"));
                    String tag;

                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.END_DOCUMENT:
                                break;
                            case XmlPullParser.START_TAG:
                                tag = xpp.getName();    //테그 이름 얻어오기
                                if (tag.equals("Rate")) {
                                    eventType = xpp.next();
                                    Log.v("환율 text", xpp.getText());
                                    rate[0] = xpp.getText();
                                    Log.v("환율 rate", rate[0]);
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                break;
                            case XmlPullParser.TEXT:
                                break;
                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    Log.v("환율 에러메시지", e.toString());
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Double ExcahgeRate = Double.parseDouble(rate[0].substring(0, rate[0].length() - 2));
        return ExcahgeRate;
    }

    private String whereSplit(String str) {
        StringBuilder sb = new StringBuilder();
        str = str.replace("　", " ");
        String[] temp = str.split(" ");

        try {
            if (temp.length == 1) {
                return str;
            } else {
                for (int i = 0; i < temp.length - 1; ++i) {
                    if (temp.equals("")) continue;
                    sb.append(temp[i] + " ");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return "알수없음";
        }

        return sb.toString();
    }

    private messageType caseMsg(String msg) {
        messageType mt = new messageType();
        String[] splitBody;
        String[] arr;
        Log.v("체크", "최초");
        Log.v("체크 조건", msg);
        try {
            mt.mLeft = "지출+";
            Log.v("체크 조건 2", msg);
            switch (msg) {
                case "[KB]":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "국민";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[0].substring(4));
                    mt.mWhere = whereSplit(splitBody[2]);
                    mt.mCheck = splitBody[3].contains("체크") ? "check" : "credit";
                    if (splitBody[4].contains("\\(") && splitBody[4].contains("\\)") && splitBody[4].contains("월")) {
                        mt.mPrice = splitBody[4].split("원")[0].replaceAll("[^0-9]", "");
                        mt.mMonth = Integer.parseInt(splitBody[4].split("원")[1].substring(1, 2));
                    } else {
                        mt.mPrice = splitBody[4].replaceAll("[^0-9]", "");
                        mt.mMonth = 1;
                    }

                    break;
                case "KB국민카드":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "국민";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mWhere = whereSplit(splitBody[4]);
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    if (splitBody[4].contains("\\(") && splitBody[4].contains("\\)") && splitBody[4].contains("월")) {
                        mt.mPrice = splitBody[3].split("원")[0].replaceAll("[^0-9]", "");
                        mt.mMonth = Integer.parseInt(splitBody[4].split("원")[1].substring(1, 2));
                    } else {
                        mt.mPrice = splitBody[3].split("원")[0].replaceAll("[^0-9]", "");
                        mt.mMonth = 1;
                    }
                    break;
                case "KB국민체크":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "국민";
                    mt.mCheck = "credit";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mPrice = splitBody[3].replaceAll("[^0-9]", "");
                    mt.mWhere = whereSplit(splitBody[4]);
                    mt.mMonth = 1;
                    break;
                case "삼성가족카드":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "삼성";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[1]);
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mWhere = whereSplit(splitBody[4]);
                    mt.mPrice = splitBody[2].replaceAll("[^0-9]", "");
                    mt.mMonth = splitBody[3].contains("일시불") ? 1 :
                            Integer.parseInt(splitBody[3].split("개월")[0]);
                    break;
                case "삼성카드":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "삼성";
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    if (msgBody.contains("삼성카드가족")) {
                        arr = splitBody[1].split(" ");
                        mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + arr[0] + " " + arr[1]);
                        mt.mWhere = arr[2];
                        mt.mPrice = splitBody[2].split(" ")[0].replaceAll("[^0-9]", "");
                        mt.mMonth = msgBody.contains("체크") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(msgBody.substring(msgBody.indexOf("개월") - 2, msgBody.indexOf("개월")));
                    } else if (splitBody.length == 4) {
                        arr = splitBody[1].split(" ");
                        mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + arr[0] + " " + arr[1]);
                        mt.mWhere = arr[2];
                        mt.mPrice = splitBody[2].split("원")[0].replaceAll("[^0-9]", "");
                        mt.mMonth = msgBody.contains("체크") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(msgBody.substring(msgBody.indexOf("개월") - 2, msgBody.indexOf("개월")));
                    } else {
                        mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[1]);
                        mt.mWhere = whereSplit(splitBody[2]);
                        mt.mPrice = splitBody[3].replaceAll("[^0-9]", "");
                        mt.mMonth = splitBody[4].contains("일시불") ? 1 :
                                Integer.parseInt(splitBody[4].split("개월")[0]);
                    }
                    break;
                case "삼성법인":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "삼성";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mWhere = whereSplit(splitBody[3]);
                    mt.mPrice = splitBody[4].replaceAll("[^0-9]", "");
                    mt.mMonth = splitBody[5].contains("일시불") ? 1 :
                            Integer.parseInt(splitBody[5].split("개월")[0]);
                    break;
                case "우리카드":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "우리";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mWhere = splitBody[4].contains("누적") || splitBody[4].contains("점 사용") || splitBody[4].contains("지급가능액") ?
                            whereSplit(splitBody[5]) : whereSplit(splitBody[4]);
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mMonth = (splitBody[0].contains("일시불") ||
                            splitBody[0].contains("체크") ||
                            splitBody[0].contains("해외승인")) ? 1 :
                            Integer.parseInt(splitBody[0].split("개월")[0].substring(1));
                    break;
                case "우리(":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "우리";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mWhere = splitBody[4].contains("점 사용") || splitBody[4].contains("지급가능액") ?
                            whereSplit(splitBody[5]) : whereSplit(splitBody[4]);
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mMonth = (splitBody[0].contains("일시불") ||
                            splitBody[0].contains("체크") ||
                            splitBody[0].contains("해외승인")) ? 1 :
                            Integer.parseInt(splitBody[0].split("개월")[0].substring(1));
                    break;
                case "[현대카드]":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "현대";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mCheck = "credit";
                    if (splitBody[0].contains("해외")) {
                        mt.mWhere = splitBody[4];
                        mt.mPrice = splitBody[3].split(" ")[1].substring(0, splitBody[3].split(" ")[1].length() - 3).replaceAll("[^0-9]", "");
                        mt.mMonth = 1;
                    } else if (splitBody[4].contains("해외")) {
                        mt.mWhere = splitBody[5];
                        mt.mMonth = 1;
                        if (splitBody[3].contains("KRW")) {
                            mt.mPrice = splitBody[3].split(" ")[1].substring(0, splitBody[3].split(" ")[1].length() - 3).replaceAll("[^0-9]", "");
                        } else if (splitBody[3].contains("USD")) {
                            Double rate = excahneRate();
                            mt.mPrice = String.valueOf(Math.round((Double.parseDouble(splitBody[3].split("[$]")[1]) * rate)));
                        }
                    }
                    break;
                case "현대카드M":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "현대";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mCheck = "credit";
                    if (splitBody.length == 5) {
                        mt.mWhere = splitBody[4];
                        arr = splitBody[3].split(" ");
                        mt.mPrice = arr[0].replaceAll("[^0-9]", "");
                        mt.mMonth = splitBody[3].contains("일시불") ? 1 :
                                Integer.parseInt(arr[1].split("개월")[0]);
                    } else if (splitBody.length == 4) {
                        arr = splitBody[3].split("\\(일시불\\)");
                        mt.mPrice = arr[0];
                        mt.mWhere = arr[1];
                        mt.mMonth = 1;
                    }
                    break;
                case "기업BC":
                    splitBody = msgBody.split("\n");
                    mt.mCard = "기업BC";
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mCheck = "credit";
                    mt.mWhere = splitBody[5];
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    arr = splitBody[0].split(" ");
                    mt.mMonth = splitBody[0].contains("일시불") ? 1 :
                            Integer.parseInt(arr[0].split("개월")[0]);
                    break;
                case "NH농협카드":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[4]);
                    mt.mWhere = splitBody[5];
                    mt.mCard = "NH농협";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : splitBody[0].contains("일시불") ? 1 : Integer.parseInt(splitBody[0].split("개월")[0]);
                    break;
                case "농협BC":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = splitBody[0].contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mWhere = splitBody[4];
                    mt.mCard = "농협BC";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : splitBody[0].contains("일시불") ? 1 : Integer.parseInt(splitBody[0].split("개월")[0]);
                    break;
                case "하나카드":
                    splitBody = msgBody.split(" ");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[3].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[5] + " " + splitBody[6].split("/")[0]);
                    mt.mWhere = splitBody[4];
                    mt.mCard = "하나";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : splitBody[2].contains("일시불") ? 1 : Integer.parseInt(splitBody[2].split("개월")[0]);
                    break;
                case "신한카드":
                    splitBody = msgBody.split(" ");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[4].split("\\)")[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2] + " " + splitBody[3]);
                    mt.mWhere = splitBody[5];
                    mt.mCard = "신한";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : splitBody[4].contains("일시불") ? 1 : Integer.parseInt(splitBody[4].split("개월")[0].substring(1));
                    break;
                case "신한체크":
                    splitBody = msgBody.split(" ");
                    mt.mCheck = "check";
                    mt.mPrice = splitBody[4].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2] + " " + splitBody[3]);
                    mt.mWhere = splitBody[5];
                    mt.mCard = "신한";
                    mt.mMonth = 1;
                    break;
                case "롯데카드":
                    splitBody = msgBody.split(" ");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[2].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[4] + " " + splitBody[5]);
                    mt.mWhere = splitBody.length == 8 ? splitBody[6] + splitBody[7] : splitBody[6];
                    mt.mCard = "롯데";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(splitBody[3].split("개월")[0]);
                    break;
                case "씨티카드":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[3].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2]);
                    mt.mWhere = splitBody[5];
                    mt.mCard = "씨티";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(splitBody[4].split("개월")[0]);
                    break;
                case "SC은행BC":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mWhere = splitBody[5];
                    mt.mCard = "SC은행BC";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(splitBody[0].split("개월")[0]);
                    break;
                case "씨티BC":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[3]);
                    mt.mWhere = splitBody[4];
                    mt.mCard = "씨티BC";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(splitBody[0].split("개월")[0]);
                    break;
                case "KEB하나":
                    splitBody = msgBody.split("\n");
                    mt.mCheck = msgBody.contains("체크") ? "check" : "credit";
                    mt.mPrice = splitBody[1].replaceAll("[^0-9]", "");
                    mt.mDate = new Date(Calendar.getInstance().get(Calendar.YEAR) + "/" + splitBody[2].split(" ")[1] + " " + splitBody[2].split(" ")[2]);
                    mt.mWhere = splitBody[2].split(" ")[0];
                    mt.mCard = "KEB하나";
                    mt.mMonth = mt.mCheck.equals("check") ? 1 : msgBody.contains("일시불") ? 1 : Integer.parseInt(splitBody[1].split("개월")[0]);
                    break;
            }
            if (mt.mCheck.equals("check")) {
                mt.mRight = mt.mCard + "은행 계좌-";
            } else if (mt.mCheck.equals("credit")) {
                mt.mRight = mt.mCard + "카드+";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (NumberFormatException e) {
        } catch (IllegalArgumentException e) {
        }

        return mt;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // SMS를 받았을 경우에만 반응하도록 if문을 삽입
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                if (ConnectDB.INSTANCE.mDB == null) ConnectDB.INSTANCE.connect();
                if (LoginData.mEmail.equals("")) {
                    MemberDB.INSTANCE.init(context);
                    MemberDB.INSTANCE.setLoginData();
                }

                String email = "";
                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                }

                if (email.equals("")) {
                    Log.v("체크", "로그인이 안 됐다");
                    Log.v("체크", "푸쉬 알람을 보내줘야됨");
                } else {
                    Bundle bundle = intent.getExtras();// Bundle객체에 문자를 받아온다
                    Log.v("체크", "문자왔다");
                    if (bundle != null) {
                        Object[] pdusObj = (Object[]) bundle.get("pdus");
                        SmsMessage[] messages = new SmsMessage[pdusObj.length];

                        for (int i = 0; i < pdusObj.length; i++) {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        }

                        //멤버디비에서 마이카드 불러오기기
                        final Set setName;
                        setName = MemberDB.INSTANCE.myCardFind();

                        for (SmsMessage smsMessage : messages) {
                            msgBody = smsMessage.getMessageBody().replace("[Web발신]\n", "");

                            boolean isContain = msgBody.contains("/") && msgBody.contains(":");
                            messageType mt = new messageType();
                            mt.mPhoneNum = smsMessage.getOriginatingAddress();
                            if (msgBody.contains("[KB]") && isContain) mt = caseMsg("[KB]");
                            else if (msgBody.contains("KB국민카드") && isContain)
                                mt = caseMsg("KB국민카드");
                            else if (msgBody.contains("KB국민체크") && isContain)
                                mt = caseMsg("KB국민체크");
                            else if (msgBody.contains("[현대카드]") && isContain)
                                mt = caseMsg("[현대카드]");
                            else if (msgBody.contains("삼성카드") && isContain) mt = caseMsg("삼성카드");
                            else if (msgBody.contains("삼성카드가족") && isContain)
                                mt = caseMsg("삼성카드가족");
                            else if (msgBody.contains("삼성가족카드") && isContain)
                                mt = caseMsg("삼성가족카드");
                            else if (msgBody.contains("삼성법인") && isContain) mt = caseMsg("삼성법인");
                            else if (msgBody.contains("우리카드") && isContain) mt = caseMsg("우리카드");
                            else if (msgBody.contains("우리(") && isContain) mt = caseMsg("우리(");
                            else if (msgBody.contains("[현대카드]")) mt = caseMsg("[현대카드]");
                            else if (msgBody.contains("현대카드M") && isContain) mt = caseMsg("현대카드M");
                            else if (msgBody.contains("기업BC") && isContain) mt = caseMsg("기업BC");
                            else if (msgBody.contains("NH농협카드") && isContain)
                                mt = caseMsg("NH농협카드");
                            else if (msgBody.contains("농협BC") && isContain) mt = caseMsg("농협BC");
                            else if (msgBody.contains("하나카드") && isContain) mt = caseMsg("하나카드");
                            else if (msgBody.contains("신한카드") && isContain) mt = caseMsg("신한카드");
                            else if (msgBody.contains("신한체크") && isContain) mt = caseMsg("신한체크");
                            else if (msgBody.contains("롯데카드") && isContain) mt = caseMsg("롯데카드");
                            else if (msgBody.contains("씨티카드") && isContain) mt = caseMsg("씨티카드");
                            else if (msgBody.contains("SC은행BC") && isContain)
                                mt = caseMsg("SC은행BC");
                            else if (msgBody.contains("씨티BC") && isContain) mt = caseMsg("씨티BC");
                            else if (msgBody.contains("KEB하나") && isContain) mt = caseMsg("KEB하나");

                            if(mt.mDate == null
                                    || mt.mCard == null
                                    || mt.mWhere == null
                                    || mt.mPrice == null
                                    || mt.mRight == null)
                                return;

                            Log.v("mt나와", mt.mDate + " " + mt.mCard + " " + mt.mWhere + " " + mt.mPrice + " " + mt.mRight + " ");

                            SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
                            Boolean push = pref.getBoolean("push", true);
                            if (push) {
                                String message = mt.mWhere + "에서 소비하신 " + mt.mPrice + "원이 정상 처리 되었습니다.";
                                NotificationFormat.NotificationPush(context, "헬로앤츠", message, message);
                            }
                            //결과값 디비에 집어넣기
                            BsDB.INSTANCE.costInsertReceiver(mt);

                            //마이카드(set)들 멤버디비에 등록
                            if (!setName.contains(mt.mCard + "~" + mt.mCheck)) {//처음이랑 달라진 경우
                                setName.add(mt.mCard + "~" + mt.mCheck);
                                final String finalEmail = email;
                                Thread thread = new Thread() {
                                    public void run() {
                                        MemberDB.INSTANCE.update(new BasicDBObject("email", finalEmail),
                                                new BasicDBObject("$set", new BasicDBObject("myCard", setName)));
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
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}