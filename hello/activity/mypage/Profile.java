package com.helloants.mm.helloants1.activity.mypage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.HeartImageView;
import com.github.siyamed.shapeimageview.PentagonImageView;
import com.github.siyamed.shapeimageview.StarImageView;
import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.login.LoginActivity;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.constant.Icon;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.db.mypage.ProfileDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;


public class Profile extends AppCompatActivity {
    private EditText mPresentPW;
    private EditText mExchangePW;
    private EditText mExchangePWConfirm;
    public static Activity mMainActivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView)findViewById(R.id.txv_title_pro);
        txvTitle.setText("프로필");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_pro);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.this.onBackPressed();
            }
        });

        Typeface fontFamily = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/fontawesome.ttf");
        TextView name = (TextView) findViewById(R.id.profile_name);
        TextView email = (TextView) findViewById(R.id.profile_email);
        TextView gender = (TextView) findViewById(R.id.profile_gender);
        TextView birth = (TextView) findViewById(R.id.profile_birth);
        TextView logout = (TextView) findViewById(R.id.txv_logout_profile_activity);
        TextView nameIcon = (TextView) findViewById(R.id.profile_name_icon);
        TextView emailIcon = (TextView) findViewById(R.id.profile_email_icon);
        TextView genderIcon = (TextView)findViewById(R.id.profile_gender_icon);
        TextView birthIcon = (TextView) findViewById(R.id.profile_birth_icon);
        TextView nameAngleIcon = (TextView) findViewById(R.id.profile_name_icon_angle);
        TextView emailAngleIcon = (TextView) findViewById(R.id.profile_email_icon_angle);
        TextView genderAngleIcon = (TextView) findViewById(R.id.profile_gender_icon_angle);
        TextView birthAngleIcon = (TextView) findViewById(R.id.profile_birth_icon_angle);
        TextView logoutIcon = (TextView) findViewById(R.id.txv_logout_icon_profile_activity);
        com.github.siyamed.shapeimageview.CircularImageView circle = (CircularImageView) findViewById(R.id.circle_image);

        nameIcon.setTypeface(fontFamily);
        emailIcon.setTypeface(fontFamily);
        genderIcon.setTypeface(fontFamily);
        birthIcon.setTypeface(fontFamily);
        nameAngleIcon.setTypeface(fontFamily);
        emailAngleIcon.setTypeface(fontFamily);
        genderAngleIcon.setTypeface(fontFamily);
        birthAngleIcon.setTypeface(fontFamily);
        logoutIcon.setTypeface(fontFamily);

        nameIcon.setText(Icon.BARCODE);
        emailIcon.setText(Icon.ENVELOPE_O);
        genderIcon.setText(Icon.VENUS_MARS);
        birthIcon.setText(Icon.BIRTHDAY_CAKE);
        nameAngleIcon.setText(Icon.ANGLE_DOUBLE_RIGHT);
        emailAngleIcon.setText(Icon.ANGLE_DOUBLE_RIGHT);
        genderAngleIcon.setText(Icon.ANGLE_DOUBLE_RIGHT);
        birthAngleIcon.setText(Icon.ANGLE_DOUBLE_RIGHT);
        logoutIcon.setText(Icon.LOGOUT);

        ProfileDB.INSTANCE.settingData();
        name.setText(" " + ProfileDB.INSTANCE.getUserDate().mName);
        email.setText(" " + ProfileDB.INSTANCE.getUserDate().mEmail);
        gender.setText(" " + ProfileDB.INSTANCE.getUserDate().mGender);
        birth.setText(" " + ProfileDB.INSTANCE.getUserDate().mBirth);

        circle.setMinimumHeight(DeviceSize.mWidth / 3);
        circle.setMinimumWidth(DeviceSize.mWidth / 3);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Profile.this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String joinPath = "";
                                try {
                                    joinPath = Cryptogram.Decrypt(LoginData.mJoinPath);
                                } catch (Exception e) {
                                }

                                final String JOINPATH = joinPath;
                                if (JOINPATH.equals("naver")) MemberDB.INSTANCE.naverLogout();
                                else if (JOINPATH.equals("facebook")) MemberDB.INSTANCE.fbLogout();
                                else MemberDB.INSTANCE.logout();

                                startActivity(new Intent(Profile.this, LoginActivity.class));
                                mMainActivity.finish();
                                Profile.this.finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .show();
            }
        });
    }
}