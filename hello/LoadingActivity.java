package com.helloants.mm.helloants1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.activity.login.CardOffsetDayInsert;
import com.helloants.mm.helloants1.activity.login.LoginActivity;
import com.helloants.mm.helloants1.activity.login.SalaryInsert;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.SMS.SMSReader;
import com.helloants.mm.helloants1.data.network.GetNetState;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.content.ContentDB;
import com.helloants.mm.helloants1.db.content.NoticeDB;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.db.mypage.RequestDB;
import com.helloants.mm.helloants1.db.mypage.ScrapDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;
import com.nhn.android.naverlogin.OAuthLogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            init();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        ConnectDB.INSTANCE.connect();
                        SMSReader.INSTANCE.init(LoadingActivity.this);
                        ContentDB.INSTANCE.onlyCall();
                        NoticeDB.INSTANCE.settingImg();
                        ScrapDB.INSTANCE.onlyCall();
                        RequestDB.INSTANCE.onlyCall();
                    } catch (ExceptionInInitializerError e) {
                    }
                }
            };
            thread.start();

            try {
                GetNetState.INSTANCE.checkNetwork(LoadingActivity.this);
            } catch (NullPointerException e) {
                new AlertDialog.Builder(LoadingActivity.this)
                        .setTitle("인터넷 연결 오류")
                        .setMessage("인터넷 연결 중 문제가 발생했습니다.\n" +
                                "연결을 확인하고 다시 실행해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoadingActivity.this.finish();
                            }
                        })
                        .show();
            }

            if (GetNetState.INSTANCE.mWifi) {
                Handler hd = new Handler();
                hd.postDelayed(new splashhandler(), 2000);
            } else if (GetNetState.INSTANCE.mMobile) {
                Handler hd = new Handler();
                hd.postDelayed(new splashhandler(), 2000);
            } else {
                new AlertDialog.Builder(LoadingActivity.this)
                        .setTitle("인터넷 연결 오류")
                        .setMessage("인터넷이 연결되어 있지 않습니다.\n" +
                                "연결을 확인하고 다시 실행해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoadingActivity.this.finish();
                            }
                        }).show();
            }
        } catch (Exception e) {}
    }

    private void init() {
        setContentView(R.layout.activity_loading);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        DeviceSize.init(LoadingActivity.this);
        OAuthLogin.getInstance().init(
                LoadingActivity.this,
                "_obagyJoXIu0wGtf9HeV",
                "fIGURdt5La",
                "헬로앤츠");
        MemberDB.INSTANCE.init(LoadingActivity.this);
    }

    private class splashhandler implements Runnable {
        public void run() {
            String email = "";

            try {
                email = Cryptogram.Decrypt(LoginData.mEmail);
            } catch (Exception e) {
            }

            final String EMAIL = email;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    if (EMAIL.equals("")) {
                        startActivity(new Intent(getApplication(), LoginActivity.class));
                        LoadingActivity.this.finish();
                    } else if (MemberDB.INSTANCE.find(new BasicDBObject("email", EMAIL)).next().get("salaryDate") == null) {
                        startActivity(new Intent(getApplication(), SalaryInsert.class));
                        LoadingActivity.this.finish();
                    } else if (MemberDB.INSTANCE.isCardOff()) {
                        startActivity(new Intent(getApplication(), CardOffsetDayInsert.class));
                        LoadingActivity.this.finish();
                    } else {
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        LoadingActivity.this.finish();
                    }
                }
            };

            thread.start();
        }
    }
}