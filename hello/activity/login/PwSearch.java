package com.helloants.mm.helloants1.activity.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.mongodb.BasicDBObject;

public class PwSearch extends AppCompatActivity {
    private Button mSendBtn;
    private EditText mEmailEdit;
    private Toast mEmailToast;
    private boolean mIsDuplicated;
    private String mPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_search);

        try {
            mIsDuplicated = false;
            mSendBtn = (Button) findViewById(R.id.btn_send_pwsearch);
            mEmailEdit = (EditText) findViewById(R.id.edit_email_pwsearch);
            mEmailToast = Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT);

            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEmailEdit.getText().toString().equals("")) {
                        mEmailToast.show();
                    } else if(!mEmailEdit.getText().toString().contains("@")
                            || !mEmailEdit.getText().toString().contains(".")) {
                        Toast.makeText(PwSearch.this, "알맞은 형식의 이메일 주소가 아닙니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                BasicDBObject user = new BasicDBObject("email", mEmailEdit.getText().toString());
                                if(MemberDB.INSTANCE.isDuplicate(mEmailEdit.getText().toString())) {
                                    mPW = MemberDB.INSTANCE.pwSearch(user);
                                    mIsDuplicated = true;
                                } else {
                                    mIsDuplicated = false;
                                }
                            }
                        };
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                        }

                        if(mIsDuplicated) {
                            if(mPW == null
                                    || mPW.equals("")) {
                                Toast.makeText(PwSearch.this, "네트워크 문제가 발생했습니다\n연결 확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            } else {



                                new AlertDialog.Builder(PwSearch.this)
                                        .setTitle("알림")
                                        .setMessage("새로운 비밀번호가 이메일로 전송되었습니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent LoginActivity = new Intent(PwSearch.this, LoginActivity.class);
                                                startActivity(LoginActivity);
                                                PwSearch.this.finish();
                                            }
                                        }).show();
                            }
                        } else {
                            Toast.makeText(PwSearch.this, "알맞지 않은 이메일입니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (Exception e) {}
    }
}