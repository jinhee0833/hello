package com.helloants.mm.helloants1.activity.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.mongodb.BasicDBObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EditSalary extends AppCompatActivity {
    private String[] splitData;
    private ArrayList<String> cardNameList;
    private ArrayList<NumberPicker> pikerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_salary);
        LinearLayout root = (LinearLayout) findViewById(R.id.linear_cardoffset_editsalary);

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView) findViewById(R.id.txv_title_editsalary);
        txvTitle.setText("월급날, 카드정산일 수정");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton) findViewById(R.id.img_btn_editsalary);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditSalary.this.onBackPressed();
            }
        });

        //월급날 입력
        int salaryDay = MemberDB.INSTANCE.salaryDayFind();
        final NumberPicker datePicker1 = (NumberPicker) findViewById(R.id.npik_salarydate_editsalary);
        datePicker1.setMinValue(1);
        datePicker1.setMaxValue(31);
        datePicker1.setValue(salaryDay);
        datePicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //카드 정산일 수정
        //이미 입력된 거 가져오기
        Set beforeCardOffset = MemberDB.INSTANCE.myCardOffsetFind();
        //새로 입력할 데이터 담을 셋
        final Set afterCardOffset = new HashSet();

        cardNameList = new ArrayList<>();
        pikerList = new ArrayList<>();

        for (Object set : beforeCardOffset) {
            String data = String.valueOf(set);
            splitData = data.split("~");

            LinearLayout layout = new LinearLayout(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.card_offset_insert, layout);

            TextView tvxCardName = (TextView) v.findViewById(R.id.txv_card_name);
            tvxCardName.setText(splitData[0] + "카드");
            cardNameList.add(splitData[0]);

            NumberPicker datePicker = (NumberPicker) v.findViewById(R.id.npik_carddate_cardoffset);
            datePicker.setMinValue(1);
            datePicker.setMaxValue(31);
            datePicker.setValue(Integer.parseInt(splitData[1]));
            datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            pikerList.add(datePicker);

            root.addView(layout);
        }

        //다음 버튼 누르면 디비에 입력
        Button modifyBtn = (Button) findViewById(R.id.btn_next_editsalary);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < pikerList.size(); i++) {
                    afterCardOffset.add(cardNameList.get(i) + "~" + pikerList.get(i).getValue());
                }

                MemberDB.INSTANCE.init(EditSalary.this);
                new Thread() {
                    @Override
                    public void run() {
                        //데이트 픽커에서 가지고온 날짜
                        BasicDBObject user = new BasicDBObject("cardOffsetDay", afterCardOffset).append("salaryDate", datePicker1.getValue());

                        String email = "";
                        try {
                            email = Cryptogram.INSTANCE.Decrypt(LoginData.mEmail);
                        } catch (Exception e) {}

                        MemberDB.INSTANCE.update(new BasicDBObject("email", email),
                                new BasicDBObject("$set", user));
                    }
                }.start();

                Intent intent = new Intent(EditSalary.this, MainActivity.class);
                startActivity(intent);
                EditSalary.this.finish();
            }
        });
    }
}