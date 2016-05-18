package com.helloants.mm.helloants1.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.AssetAdapter;
import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.loading.WaitDlg;

import java.util.ArrayList;
import java.util.Set;

public class BSInsertChoice extends AppCompatActivity {
    private Button mSkipBtn;
    private Button mInsertBtn;
    private static ArrayList<BSType> mList;
    private static ArrayList<BSType> mList2;
    private BackPressCloseHandler backPressCloseHandler;
    private RelativeLayout layout;
    private WaitDlg mWaitDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsinsert_choice);

        try {
            layout = (RelativeLayout) findViewById(R.id.rlay_root_bs_insert_choice_activity);

            //건너뛰면서 기본값(0)넣기 클릭시
            mSkipBtn = (Button) findViewById(R.id.btn_skip_bsinsertchoice);
            mSkipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //자산기본값
                    mWaitDlg = new WaitDlg(BSInsertChoice.this, null, "Loading...");
                    mWaitDlg.start();

                    AssetInsertThread at = new AssetInsertThread();
                    DebtInsertThread dt = new DebtInsertThread();

                    try {
                        at.start();
                        at.join();
                        dt.start();
                        dt.join();
                    } catch (Exception e) {
                    }

                    Intent MainActivity = new Intent(BSInsertChoice.this, com.helloants.mm.helloants1.activity.MainActivity.class);
                    startActivity(MainActivity);
                    WaitDlg.stop(mWaitDlg);
                    BSInsertChoice.this.finish();
                }


            });

            //입력하기버튼 클릭시
            mInsertBtn = (Button) findViewById(R.id.btn_insert_bsinsertchoice);
            mInsertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent AssetInsert = new Intent(BSInsertChoice.this, AssetInsert.class);
                    startActivity(AssetInsert);
                    BSInsertChoice.this.finish();
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

    public static class AssetInsertThread extends Thread {
        @Override
        public void run() {
            mList = new ArrayList();
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

            BsDB.INSTANCE.assetInsert(mList, "asset");
            BsItem.INSTANCE.insertAsset(mList);
        }
    }

    public static class DebtInsertThread extends Thread {
        @Override
        public void run() {
            //부채기본값
            mList2 = new ArrayList<BSType>();
            mList2.add(new BSType("부동산 대출+", "loan"));
            mList2.add(new BSType("신용 대출+", "loan"));
            mList2.add(new BSType("학자금 대출+", "loan"));

            Set myCardSet = MemberDB.INSTANCE.myCardFind();
            for (Object card : myCardSet) {
                String cardName = String.valueOf(card);
                String[] cardN = cardName.split("~");
                if (cardName.contains("credit")) {
                    mList2.add(new BSType(cardN[0] + "카드+", "loan"));
                }
            }

            BsDB.INSTANCE.assetInsert(mList2, "debt");
            BsItem.INSTANCE.insertDebt(mList2);
            BsItem.INSTANCE.insert();
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
                defaultAsset();
                activity.finish();
            }
        }

        private void showGuide() {
            Snackbar.make(layout, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
        }

        private void defaultAsset() {
            AssetInsertThread at = new AssetInsertThread();
            DebtInsertThread dt = new DebtInsertThread();

            try {
                at.start();
                at.join();
                dt.start();
                dt.join();
            } catch (Exception e) {
            }
        }
    }
}