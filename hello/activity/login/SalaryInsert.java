package com.helloants.mm.helloants1.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.data.SMS.SMSReader;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;

public class SalaryInsert extends AppCompatActivity {
    private Button mNextBtn;
    private NumberPicker mSalarydateEdit;
    private BackPressCloseHandler backPressCloseHandler;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_insert);

        try {
            layout = (RelativeLayout) findViewById(R.id.rlay_root_view_salary_activity);
            mSalarydateEdit = (NumberPicker) findViewById(R.id.npik_salarydate_salaryinsert);
            mSalarydateEdit.setMinValue(1);
            mSalarydateEdit.setMaxValue(31);
            mSalarydateEdit.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            //기존문자 넣기
            Thread threadSMS = new Thread() {
                @Override
                public void run() {
                    SMSReader.INSTANCE.SMSList(SalaryInsert.this);
                }
            };
            threadSMS.start();
            try {
                threadSMS.join();
            } catch (InterruptedException e) {
            }

            mNextBtn = (Button) findViewById(R.id.btn_next_salaryinsert);
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MemberDB.INSTANCE.init(SalaryInsert.this);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            BasicDBObject user = new BasicDBObject("salaryDate", mSalarydateEdit.getValue());

                            String email = "";
                            try {
                                email = Cryptogram.INSTANCE.Decrypt(LoginData.mEmail);
                            } catch (Exception e) {
                            }

                            MemberDB.INSTANCE.update(new BasicDBObject("email", email),
                                    new BasicDBObject("$set", user));
                        }
                    };

                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                    }

                    Intent intent = new Intent(SalaryInsert.this, CardOffsetDayInsert.class);
                    startActivity(intent);
                    SalaryInsert.this.finish();
                }
            });

            backPressCloseHandler = new BackPressCloseHandler(this);
        } catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
        try {
            backPressCloseHandler.onBackPressed();
        } catch (NullPointerException e) {
            super.onBackPressed();
        }
    }

    private class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
            }
        }

        private void showGuide() {
            Snackbar.make(layout, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
        }
    }
}