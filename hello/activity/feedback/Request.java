package com.helloants.mm.helloants1.activity.feedback;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.RequestViewHolder;
import com.helloants.mm.helloants1.data.constant.Icon;
import com.helloants.mm.helloants1.data.type.RequestType;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.db.mypage.RequestDB;
import com.helloants.mm.helloants1.loading.WaitDlg;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;

import java.util.ArrayList;


/**
 * Created by park on 2016-02-09.
 */
public class Request extends AppCompatActivity implements AbsListView.OnScrollListener {
    private static ArrayList<RequestType> mRequestList;
    private static int mRequestSize;
    private static RequestAdapter mAdapter;
    Typeface mFontFamily;
    private WaitDlg mWaitDlg;
    private int mNumber;

    public static void setRequest() {
        mRequestList = RequestDB.INSTANCE.mRequestList;
        mRequestSize = RequestDB.INSTANCE.mRequestList.size();
    }

    public static void dataChanged() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView)findViewById(R.id.txv_title_request);
        txvTitle.setText("자료 요청");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_request);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request.this.onBackPressed();
            }
        });

        setRequest();

        if(mRequestSize != 0) {
            ((TextView) findViewById(R.id.txv_empty_text_request)).setVisibility(View.GONE);
        }

        mFontFamily = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome.ttf");
        mNumber = 1;
        mAdapter = new RequestAdapter();

        TextView qandaInsert = (TextView) findViewById(R.id.request_insert);
        qandaInsert.setTypeface(mFontFamily);
        qandaInsert.setText(Icon.QUESTION);

        ListView lv = (ListView) findViewById(R.id.request_listview);
        lv.setOnScrollListener(this);
        lv.setAdapter(mAdapter);

        TextView insert = (TextView) findViewById(R.id.request_insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "";

                try {
                    email = Cryptogram.Decrypt(LoginData.mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Dialog dialog = new Dialog(Request.this);
                dialog.setContentView(R.layout.alert_request_insert);
                dialog.setTitle("자료 요청");

                final EditText TITLE = (EditText) dialog.findViewById(R.id.alert_request_title);
                final EditText CONTENT = (EditText) dialog.findViewById(R.id.alert_request_content);
                Button btnInsert = (Button) dialog.findViewById(R.id.alert_request_insert);
                Button btnCalcel = (Button) dialog.findViewById(R.id.alert_request_cancel);

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
                            Toast.makeText(DIALOG.getContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            mWaitDlg = new WaitDlg(DIALOG.getContext(), "Please Wait", "Loading...");
                            mWaitDlg.start();
                            RequestDB.INSTANCE.insert(strTitle, strContent);
                            RequestDB.INSTANCE.list(1);
                            Request.setRequest();
                            Request.dataChanged();

                            if(mRequestSize == 1) ((TextView) findViewById(R.id.txv_empty_text_request)).setVisibility(View.GONE);
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

    private class RequestAdapter extends BaseAdapter {
        private int mCount = 0;
        private RequestViewHolder viewHolder;

        public boolean endReached() {
            return mCount == mRequestSize;
        }

        public boolean showMore() {
            if (mCount == mRequestSize) {
                return true;
            } else {
                mWaitDlg = new WaitDlg(Request.this, "Please Wait", "Loading...");
                mWaitDlg.start();
                mCount = Math.min(mCount + MongoQuery.INSTANCE.REQUEST_PAGE_SIZE, mRequestSize);
                RequestDB.INSTANCE.list(++mNumber);
                setRequest();
                notifyDataSetChanged();
                WaitDlg.stop(mWaitDlg);
                return endReached();
            }
        }

        @Override
        public int getCount() {
            return mRequestSize;
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
                        .inflate(R.layout.item_request_list, parent, false);

                viewHolder = new RequestViewHolder();

                viewHolder.mReqTitle = (TextView) convertView.findViewById(R.id.request_title);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.request_date);
                viewHolder.mState = (TextView) convertView.findViewById(R.id.request_state);

                convertView.setTag(viewHolder);
            } else viewHolder = (RequestViewHolder) convertView.getTag();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd. HH:mm");

            viewHolder.mReqTitle.setText(mRequestList.get(position).mTitle);
            viewHolder.mDate.setText(format.format(mRequestList.get(position).mDate));

            int hit = mRequestList.get(position).mHit;
            int complete = mRequestList.get(position).mComplete;
            if (hit > 0 && complete > 0) {
                viewHolder.mState.setText("제작 완료");
            } else if (hit > 0) {
                viewHolder.mState.setText("제작중");
            } else {
                viewHolder.mState.setText("읽지 않음");
            }

            final int POS = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(Request.this)
                            .setTitle(mRequestList.get(POS).mTitle)
                            .setMessage(mRequestList.get(POS).mContent)
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
