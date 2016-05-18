package com.helloants.mm.helloants1.activity.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.AssetAdapter;
import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.db.ConnectDB;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;
import com.helloants.mm.helloants1.db.member.MemberDB;

import java.util.ArrayList;
import java.util.Set;

public class AssetInsert extends AppCompatActivity {
    Button mAddassetBtn;
    Button mNextBtn;
    ArrayList<BSType> mList;
    private BackPressCloseHandler backPressCloseHandler;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_insert);

        try {
            final ListView list = (ListView) findViewById(R.id.list_asset_assetinsertactivity);

            final AssetAdapter assetAdapter = new AssetAdapter();
            mList = new ArrayList<BSType>();
            mList.add(new BSType("집값 혹은 보증금+", "house"));
            mList.add(new BSType("예·적금+", "save"));
            mList.add(new BSType("현금+", "income"));
            mList.add(new BSType("보험+", "save"));
            mList.add(new BSType("펀드+", "save"));
            mList.add(new BSType("주식+", "save"));
            mList.add(new BSType("자동차+", "car"));

            Set myCardSet = MemberDB.INSTANCE.myCardFind();
            for (Object card : myCardSet) {
                String cardName = String.valueOf(card);
                String[] cardN = cardName.split("~");
                if (cardName.contains("check")) {
                    mList.add(new BSType(cardN[0] + "은행 계좌+", "save"));
                }
            }
            assetAdapter.setList(mList);
            list.setAdapter(assetAdapter);

            //자산항목 추가 버튼 눌렀을때 얼럿다이얼로그
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.asset_modify_dialog, null);
            mAddassetBtn = (Button) findViewById(R.id.btn_addasset_assetinsertactivity);
            mAddassetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AssetInsert.this)
                            .setTitle("자산항목 추가")
                            .setView(dialogView)
                            .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //추가 버튼 눌렀을때
                                    EditText editText = (EditText) dialogView.findViewById(R.id.dialog_edit);
                                    String name = editText.getText().toString();

                                    RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.dialog_rg);
                                    int checkedId = radioGroup.getCheckedRadioButtonId();
                                    String type = "";
                                    switch (checkedId) {
                                        case R.id.dialog_rb_save:
                                            type = "save";
                                            break;
                                        case R.id.dialog_rb_house:
                                            type = "house";
                                            break;
                                        case R.id.dialog_rb_income:
                                            type = "income";
                                            break;
                                        case R.id.dialog_rb_car:
                                            type = "car";
                                            break;
                                        case R.id.dialog_rb_etc:
                                            type = "etc";
                                            break;
                                    }

                                    mList.add(new BSType(name + "+", type));
                                    assetAdapter.setList(mList);
                                    list.setAdapter(assetAdapter);
                                    editText.setText("");
                                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                                }
                            });
                    alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                        }
                    });
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });

            //다음버튼 눌렀을때
            mNextBtn = (Button) findViewById(R.id.btn_send_assetinsertactivity);
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            BsDB.INSTANCE.assetInsert(mList, "asset");
                            BsItem.INSTANCE.insertAsset(mList);
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //부채입력으로 넘어가기
                    Intent DebtInsert = new Intent(AssetInsert.this, DebtInsert.class);
                    startActivity(DebtInsert);
                    AssetInsert.this.finish();
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
                defaultBS();
                activity.finish();
            }
        }

        private void showGuide() {
            Snackbar.make(layout, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
        }

        private void defaultBS() {
            BSInsertChoice.AssetInsertThread at = new BSInsertChoice.AssetInsertThread();
            BSInsertChoice.DebtInsertThread dt = new BSInsertChoice.DebtInsertThread();

            try {
                at.start();
                at.join();
                dt.start();
                dt.join();
            } catch (Exception e) {}
        }
    }
}