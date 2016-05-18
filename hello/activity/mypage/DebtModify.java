package com.helloants.mm.helloants1.activity.mypage;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.activity.login.DebtInsert;
import com.helloants.mm.helloants1.adapters.AssetAdapter;
import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;
import com.helloants.mm.helloants1.loading.WaitDlg;

import java.util.ArrayList;


public class DebtModify extends AppCompatActivity {
    private Button mAdddebtBtn;
    private Button mAdjustBtn;
    private ArrayList<BSType> mList;
    private boolean mIsBs;
    private Button mAssetInsertBtn;
    private WaitDlg mWaitDlg;
    private ArrayList<BSType> beforeList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread ethread = new Thread() {
            @Override
            public void run() {
                mIsBs = BsItem.INSTANCE.checkDebt();
            }
        };

        ethread.start();
        try {
            ethread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mIsBs) {
            // 가계부 입력을 한 사람
            setContentView(R.layout.activity_debt_modify);
            //툴바 타이틀 텍스트뷰
            TextView txvTitle = (TextView)findViewById(R.id.txv_title_dif);
            txvTitle.setText("부채 초기값 입력");

            //툴바 이미지 백 버튼
            ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_dif);
            btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DebtModify.this.onBackPressed();
                }
            });
            final ListView list = (ListView) findViewById(R.id.list_debt_debtinsertfrag);
            final AssetAdapter assetAdapter = new AssetAdapter();

            //DB에 있는 부채항목 가져와서 뿌리기
            mList = new ArrayList<BSType>();
            beforeList = new ArrayList<BSType>();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    BsDB.INSTANCE.firstDebtFind(mList, beforeList);
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final int SIZE = mList.size();
            assetAdapter.setList(mList);
            assetAdapter.setTag("debt");
            list.setAdapter(assetAdapter);

            //부채항목 추가 버튼 눌렀을때 얼럿다이얼로그
            mAdddebtBtn = (Button)findViewById(R.id.btn_adddebt_debtinsertfrag);
            mAdddebtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText DebtName = new EditText(DebtModify.this);
                    new AlertDialog.Builder(DebtModify.this)
                            .setTitle("부채항목 추가")
                            .setMessage("새로운 부채항목을 추가해 주세요.")
                            .setView(DebtName)
                            .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //추가 버튼 눌렀을때
                                    String name = DebtName.getText().toString();
                                    mList.add(new BSType(name + "+" ,"loan"));
                                    assetAdapter.setList(mList);
                                    list.setAdapter(assetAdapter);
                                }
                            }).show();
                }
            });
            //수정완료 버튼 눌렀을때
            mAdjustBtn = (Button)findViewById(R.id.btn_adjust_debtinsertfrag);
            mAdjustBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //프로그레스 다이얼 로그
                    mWaitDlg = new WaitDlg(DebtModify.this, null, "Loading...");
                    mWaitDlg.start();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            BsDB.INSTANCE.debtModify(beforeList, mList, SIZE);
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    WaitDlg.stop(mWaitDlg);
                    Snackbar.make(v, "수정하였습니다.", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(DebtModify.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // 가계부 입력이 안 된 사람
            setContentView(R.layout.activity_debt_modify2);
            //툴바 타이틀 텍스트뷰
            TextView txvTitle = (TextView)findViewById(R.id.txv_title_dif2);
            txvTitle.setText("부채 초기값 입력");

            //툴바 이미지 백 버튼
            ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_dif2);
            btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DebtModify.this.onBackPressed();
                }
            });
            mAssetInsertBtn = (Button)findViewById(R.id.btn_insertdebt_debtinsertfrag2);
            mAssetInsertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(DebtModify.this, DebtInsert.class);
                    startActivity(a);
                }
            });

        }
    }

}