package com.helloants.mm.helloants1.fragment.sms;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.SMSLetterViewHolder;
import com.helloants.mm.helloants1.data.SMS.SMSReader;
import com.helloants.mm.helloants1.data.type.SMSType;
import com.helloants.mm.helloants1.db.sms.SMSDB;

import java.util.Date;

/**
 * Created by kingherb on 2016-04-13.
 */
public class SlidingSmsFragment extends Fragment {
    public static View mBack;
    public static boolean mIsVisible;
    private SMSLetterAdapter mAdapter;
    private ListView mListView;
    private int mSize;
    private java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd. HH:mm");
    private TextView mSend;
    private TextView mBtnBack;
    private Typeface fontFamily;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sliding_sms, container, false);
        mIsVisible = true;

        mListView = (ListView) v.findViewById(R.id.listview_sms_letter);
        mSend = (TextView) v.findViewById(R.id.btn_send_sms_letter);
        mBtnBack = (TextView) v.findViewById(R.id.btn_back_sms_letter);
        mAdapter = new SMSLetterAdapter();
        mListView.setAdapter(mAdapter);
        mSize = SMSReader.INSTANCE.mSmsList.size();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fontFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
        mSend.setTypeface(fontFamily);
        mBtnBack.setTypeface(fontFamily);

        mSend.setText("\uF1D9");
        mBtnBack.setText("\uF060");

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSDB.INSTANCE.insert();
                Toast.makeText(getActivity(), "감사합니다", Toast.LENGTH_SHORT).show();
                mIsVisible = false;
                mBack.performClick();
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsVisible = false;
                mBack.performClick();
            }
        });
    }

    private class SMSLetterAdapter extends BaseAdapter {
        private SMSLetterViewHolder viewHolder;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sms_letter_list, parent, false);

                viewHolder = new SMSLetterViewHolder();
                viewHolder.mPhoneNum = (TextView) convertView.findViewById(R.id.txv_sms_phone_number_letter);
                viewHolder.mBody = (TextView) convertView.findViewById(R.id.txv_sms_body_letter);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.txv_sms_date_letter);

                convertView.setTag(viewHolder);
            } else viewHolder = (SMSLetterViewHolder) convertView.getTag();

            TextView tv = (TextView) convertView.findViewById(R.id.txv_icon_phone_number_letter);
            tv.setTypeface(fontFamily);
            tv.setText("\uF003");

            SMSType type = SMSReader.INSTANCE.mSmsList.get(position);
            viewHolder.mPhoneNum.setText(type.mPhoneNumber);
            viewHolder.mBody.setText(type.mBody);
            viewHolder.mDate.setText(format.format(new Date(Long.parseLong(type.mDate))));

            if(type.mIsSelected) {
                convertView.setBackgroundColor(Color.rgb(255, 215, 119));
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }

            final View CV = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SMSReader.INSTANCE.mSmsList.get(position).mIsSelected) {
                        SMSReader.INSTANCE.mSmsList.get(position).mIsSelected = false;
                    } else {
                        SMSReader.INSTANCE.mSmsList.get(position).mIsSelected = true;
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}