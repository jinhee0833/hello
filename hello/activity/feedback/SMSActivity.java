package com.helloants.mm.helloants1.activity.feedback;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.SMSViewHolder;
import com.helloants.mm.helloants1.data.SMS.SMSReader;
import com.helloants.mm.helloants1.fragment.sms.SlidingSmsFragment;


/**
 * Created by kingherb on 2016-04-13.
 */
public class SMSActivity extends AppCompatActivity {
    private ListView mListView;
    private SMSAdapter mAdapter;
    private int mSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView)findViewById(R.id.txv_title_sms);
        txvTitle.setText("미인식 문자 신고");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton)findViewById(R.id.img_btn_sms);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSActivity.this.onBackPressed();
            }
        });

        SMSReader.INSTANCE.settingSMSMap();
        mSize = SMSReader.INSTANCE.mSmsMap.size();

        mListView = (ListView) findViewById(R.id.listview_sms_fragment);
        mAdapter = new SMSAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (SlidingSmsFragment.mIsVisible) {
            SlidingSmsFragment.mBack.performClick();
            SlidingSmsFragment.mIsVisible = false;
        } else {
            super.onBackPressed();
        }
    }

    private class SMSAdapter extends BaseAdapter {
        private SMSViewHolder viewHolder;
        private Typeface fontFamily;

        @Override
        public int getCount() {
            return mSize;
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
                        .inflate(R.layout.item_sms_list, parent, false);

                viewHolder = new SMSViewHolder();
                viewHolder.mPhoneNum = (TextView) convertView.findViewById(R.id.txv_phone_number_sms);
                viewHolder.mBody = (TextView) convertView.findViewById(R.id.txv_body_sms);

                convertView.setTag(viewHolder);
            } else viewHolder = (SMSViewHolder) convertView.getTag();

            fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
            TextView txv = (TextView) convertView.findViewById(R.id.txv_phone_icon_sms);
            txv.setTypeface(fontFamily);
            txv.setText("\uF003");

            String str = "";
            try {
                str = SMSReader.INSTANCE.mOrderList.get(position);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }

            if (str.equals("")) {
                return null;
            } else {
                viewHolder.mPhoneNum.setText(str);
                viewHolder.mBody.setText(SMSReader.INSTANCE.mSmsMap.get(str));

                final int POS = position;
                final String STR = str;
                final View CV = convertView;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SMSReader.INSTANCE.settingSMSList(STR);
                        android.app.Fragment f = getFragmentManager().findFragmentByTag("sms_letter_fragment");
                        if (f != null) {
                            getFragmentManager().popBackStack();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                                SlidingSmsFragment.mBack = CV;

                                getFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.animator.slide_up,
                                                R.animator.slide_down,
                                                R.animator.slide_up,
                                                R.animator.slide_down)
                                        .add(R.id.list_fragment_container, SlidingSmsFragment
                                                        .instantiate(SMSActivity.this, SlidingSmsFragment.class.getName()),
                                                "sms_letter_fragment"
                                        ).addToBackStack(null).commit();
                            }
                        }
                    }
                });

                return convertView;
            }
        }
    }
}