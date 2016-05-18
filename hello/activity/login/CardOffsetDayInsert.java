package com.helloants.mm.helloants1.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CardOffsetDayInsert extends AppCompatActivity {
    private ArrayList<NumberPicker> pikerList;
    private String[] cardN;
    private ArrayList<String> cardNameList;
    private BackPressCloseHandler backPressCloseHandler;
    private ScrollView layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //디비서 카드가져오기

        try {
            final Set myCardSet = MemberDB.INSTANCE.myCardFind();

            if (isEmptySet(myCardSet)) {
                Intent intent = new Intent(CardOffsetDayInsert.this, BSInsertChoice.class);
                startActivity(intent);
                CardOffsetDayInsert.this.finish();
            } else {
                setContentView(R.layout.activity_card_offset_day_insert);
                layout = (ScrollView) findViewById(R.id.sv_root_card_offset_insert);
                LinearLayout root = (LinearLayout) findViewById(R.id.linear_cardoffset);

                //카드 정산일 집어넣을 셋
                final Set cardOffDateSet = new HashSet();

                cardNameList = new ArrayList<>();
                pikerList = new ArrayList<>();
                for (Object card : myCardSet) {
                    String cardName = String.valueOf(card);
                    cardN = cardName.split("~");
                    if (cardN[1].equals("credit")) {
                        LinearLayout layout = new LinearLayout(this);

                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.card_offset_insert, layout);

                        //카드이름만 추출해서 넣기
                        TextView tvxCardName = (TextView) v.findViewById(R.id.txv_card_name);
                        tvxCardName.setText(cardN[0]);
                        cardNameList.add(cardN[0]);

                        NumberPicker datePicker = (NumberPicker) v.findViewById(R.id.npik_carddate_cardoffset);
                        datePicker.setMinValue(1);
                        datePicker.setMaxValue(31);
                        datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        pikerList.add(datePicker);
                        root.addView(layout);
                    }
                }

                //다음 버튼 누르면 디비에 입력
                Button nextBtn = (Button) findViewById(R.id.btn_next_cardoffset);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < pikerList.size(); i++) {
                            cardOffDateSet.add(cardNameList.get(i) + "~" + pikerList.get(i).getValue());
                        }

                        new Thread() {
                            @Override
                            public void run() {
                                //데이트 픽커에서 가지고온 날짜
                                BasicDBObject user = new BasicDBObject("cardOffsetDay", cardOffDateSet);

                                String email = "";
                                try {
                                    email = Cryptogram.INSTANCE.Decrypt(LoginData.mEmail);
                                } catch (Exception e) {
                                }

                                MemberDB.INSTANCE.update(new BasicDBObject("email", email),
                                        new BasicDBObject("$set", user));
                            }
                        }.start();

                        Intent intent = new Intent(CardOffsetDayInsert.this, BSInsertChoice.class);
                        startActivity(intent);
                        CardOffsetDayInsert.this.finish();
                    }
                });

                backPressCloseHandler = new BackPressCloseHandler(this);
            }
        } catch (Exception e) {}
    }

    private boolean isEmptySet(Set set) {
        for (Object obj : set) {
            String str = String.valueOf(obj);
            if(str.contains("credit")) return false;
        }

        return true;
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