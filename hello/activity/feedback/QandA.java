package com.helloants.mm.helloants1.activity.feedback;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.QandAViewHolder;
import com.helloants.mm.helloants1.data.constant.Icon;
import com.helloants.mm.helloants1.data.type.QandAType;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.db.mypage.QandADB;
import com.helloants.mm.helloants1.loading.WaitDlg;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;

import java.util.ArrayList;

/**
 * Created by park on 2016-02-09.
 */
public class QandA extends AppCompatActivity implements AbsListView.OnScrollListener {
    private static ArrayList<QandAType> mQandAList;
    private static int mQandASize;
    private static QandAAdapter mAdapter;
    Typeface mFontFamily;
    private WaitDlg mWaitDlg;
    private int mNumber;
    Toolbar toolbar;

    public static void setQandA() {
        mQandAList = QandADB.INSTANCE.mQandAList;
        mQandASize = QandADB.INSTANCE.mQandAList.size();
    }

    public static void dataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qanda);
        setQandA();

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView)findViewById(R.id.txv_title_qanda);
        txvTitle.setText("Q & A");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_qanda);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QandA.this.onBackPressed();
            }
        });


        if(mQandASize != 0) {
            ((TextView) findViewById(R.id.txv_empty_text_request)).setVisibility(View.GONE);
        }

        mFontFamily = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome.ttf");
        mNumber = 1;
        mAdapter = new QandAAdapter();

        TextView qandaInsert = (TextView) findViewById(R.id.qanda_insert);
        qandaInsert.setTypeface(mFontFamily);
        qandaInsert.setText(Icon.QUESTION);

        ListView lv = (ListView) findViewById(R.id.qanda_listview);
        lv.setOnScrollListener(this);
        lv.setAdapter(mAdapter);

        TextView insert = (TextView) findViewById(R.id.qanda_insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "";

                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {}

                Dialog dialog = new Dialog(QandA.this);
                dialog.setContentView(R.layout.alert_qanda_insert);
                dialog.setTitle("Q & A");

                final EditText TITLE = (EditText) dialog.findViewById(R.id.alert_qanda_title);
                final EditText CONTENT = (EditText) dialog.findViewById(R.id.alert_qanda_content);
                Button btnInsert = (Button) dialog.findViewById(R.id.alert_qanda_insert);
                Button btnCalcel = (Button) dialog.findViewById(R.id.alert_qanda_cancel);

                final Dialog DIALOG = dialog;
                btnCalcel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DIALOG.dismiss();
                    }
                });
                btnInsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strTitle = TITLE.getText().toString();
                        String strContent = CONTENT.getText().toString();

                        if (strTitle.equals("") || strContent.equals("")) {
                            Snackbar.make(v, "내용을 입력해 주세요.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            mWaitDlg = new WaitDlg(DIALOG.getContext(), "Please Wait", "Loading...");
                            mWaitDlg.start();
                            QandADB.INSTANCE.insert(strTitle, strContent);
                            QandADB.INSTANCE.list(1);
                            QandA.setQandA();
                            QandA.dataChanged();

                            if(mQandASize == 1) ((TextView) findViewById(R.id.txv_empty_text_request)).setVisibility(View.GONE);
                            WaitDlg.stop(mWaitDlg);
                            DIALOG.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && !mAdapter.endReached()) {
            mAdapter.showMore();
        }
    }

    private class QandAAdapter extends BaseAdapter {
        private int mCount = 0;
        private QandAViewHolder viewHolder;

        public boolean endReached() {
            return mCount == mQandASize;
        }

        public boolean showMore() {
            if (mCount == mQandASize) {
                return true;
            } else {
                mWaitDlg = new WaitDlg(QandA.this, "Please Wait", "Loading...");
                mWaitDlg.start();
                mCount = Math.min(mCount + MongoQuery.INSTANCE.QANDA_PAGE_SIZE, mQandASize);
                QandADB.INSTANCE.list(++mNumber);
                setQandA();
                notifyDataSetChanged();
                WaitDlg.stop(mWaitDlg);
                return endReached();
            }
        }

        @Override
        public int getCount() {
            return mQandASize;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_qanda_list, parent, false);

                viewHolder = new QandAViewHolder();

                viewHolder.mReqTitle = (TextView) convertView.findViewById(R.id.qanda_title);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.qanda_date);

                convertView.setTag(viewHolder);
            } else viewHolder = (QandAViewHolder) convertView.getTag();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd. HH:mm");

            viewHolder.mReqTitle.setText(mQandAList.get(position).mTitle);
            viewHolder.mDate.setText(format.format(mQandAList.get(position).mDate));

            int hit = mQandAList.get(position).mHit;
            int complete = mQandAList.get(position).mComplete;
            TextView answer = (TextView) convertView.findViewById(R.id.qanda_answer);
            if (hit > 0 && complete > 0) {
                answer.setEnabled(true);
                final int POS = position;
                answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(QandA.this)
                                .setTitle(mQandAList.get(POS).mATitle)
                                .setMessage(mQandAList.get(POS).mAContent)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                    }
                });
            } else {
                answer.setVisibility(View.INVISIBLE);
            }

            final int POS = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(QandA.this)
                            .setTitle(mQandAList.get(POS).mTitle)
                            .setMessage(mQandAList.get(POS).mContent)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            });

            return convertView;
        }
    }
}
