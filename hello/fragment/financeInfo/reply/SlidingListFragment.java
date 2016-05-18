package com.helloants.mm.helloants1.fragment.financeInfo.reply;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.ReplyViewHolder;
import com.helloants.mm.helloants1.data.constant.Icon;
import com.helloants.mm.helloants1.data.constant.ReplyLikeResult;
import com.helloants.mm.helloants1.data.network.GetNetState;
import com.helloants.mm.helloants1.data.type.ReplyType;
import com.helloants.mm.helloants1.db.MongoQuery;
import com.helloants.mm.helloants1.db.content.ReplyDB;
import com.helloants.mm.helloants1.loading.WaitDlg;

import java.util.ArrayList;

/**
 * Created by paveld on 4/17/14.
 */
public class SlidingListFragment extends Fragment implements AbsListView.OnScrollListener {
    private ArrayList<ReplyType> mBestReplyList;
    private ArrayList<ReplyType> mReplyList;
    private int mReplySize;
    private TextView mClose;
    private static TextView mParentTextView;
    private static boolean mIsVisible;
    private Typeface mFontFamily;
    private WaitDlg mWaitDlg;
    private ReplyAdapter mReplyAdapter;
    private int mNumber;
    private View mView;

    public static void setTextView(TextView tv) {
        mParentTextView = tv;
    }
    public static void setIsVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }
    public static boolean isGetVisible() {
        return mIsVisible;
    }

    private void setReply() {
        mBestReplyList = ReplyDB.INSTANCE.mBestReplyList;
        mReplyList = ReplyDB.INSTANCE.mReplyList;
        mReplySize = ReplyDB.INSTANCE.mSize;
    }

    private void initInsertBtn() {
        final EditText CONTENT = (EditText) mView.findViewById(R.id.reply_content_text);
        Button insert = (Button) mView.findViewById(R.id.reply_insert);
        insert.setTypeface(mFontFamily);
        insert.setText(Icon.REPLY_INSERT);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (CONTENT.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else if (!(GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile)) {
                        Toast.makeText(getActivity(), "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                    } else {
                        mWaitDlg = new WaitDlg(getActivity(), "Please Wait", "Loading...");
                        mWaitDlg.start();
                        ReplyDB.INSTANCE.insert(CONTENT.getText().toString());
                        synchronized (SlidingListFragment.this) {
                            try {
                                SlidingListFragment.this.wait();
                            } catch (InterruptedException e) {}
                        }
                        ReplyDB.INSTANCE.setCurrentPage(1, ReplyLikeResult.FRAGMENT);
                        synchronized (SlidingListFragment.this) {
                            try {
                                SlidingListFragment.this.wait();
                            } catch (InterruptedException e) {}
                        }
                        setReply();
                        CONTENT.setText("");
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(CONTENT.getWindowToken(), 0);
                        mReplyAdapter.notifyDataSetChanged();
                        WaitDlg.stop(mWaitDlg);
                    }
                } catch (NullPointerException e) {}
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setReply();
        mIsVisible = true;
        mNumber = 1;
        mFontFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
        mReplyAdapter = new ReplyAdapter();
        ReplyDB.INSTANCE.init(SlidingListFragment.this);
        ReplyDB.INSTANCE.init(mReplyAdapter);

        return inflater.inflate(R.layout.fragment_sliding_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mClose = (TextView) view.findViewById(R.id.reply_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsVisible = false;
                mParentTextView.performClick();
            }
        });

        ListView lv = (ListView) view.findViewById(R.id.list);
        lv.setOnScrollListener(this);
        lv.setAdapter(mReplyAdapter);

        TextView count = (TextView) view.findViewById(R.id.reply_count);
        count.setText("댓글 " + String.valueOf(mReplySize));

        initInsertBtn();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && !mReplyAdapter.endReached()) {
            boolean noMoreToShow = mReplyAdapter.showMore();
        }
    }

    private class ReplyAdapter extends BaseAdapter {
        private int mCount = 0;
        private ReplyViewHolder viewHolder;
        private int mBestListSize = mBestReplyList.size();

        @Override
        public int getCount() {
            return (mReplyList.size() + mBestReplyList.size());
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
            if (position < mBestListSize) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_best_reply_sliding, parent, false);
                viewHolder = new ReplyViewHolder();

                viewHolder.mPersonName = (TextView) convertView.findViewById(R.id.reply_person_name);
                viewHolder.mContent = (TextView) convertView.findViewById(R.id.reply_content);
                viewHolder.mDateIcon = (TextView) convertView.findViewById(R.id.reply_date_icon);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.reply_date);
                viewHolder.mLikeIcon = (TextView) convertView.findViewById(R.id.reply_like_icon);
                viewHolder.mLike = (TextView) convertView.findViewById(R.id.reply_like_count);
                viewHolder.mDateIcon.setTypeface(mFontFamily);
                viewHolder.mLikeIcon.setTypeface(mFontFamily);

                convertView.setTag(viewHolder);
            } else {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_view_item, parent, false);
                viewHolder = new ReplyViewHolder();

                viewHolder.mPersonName = (TextView) convertView.findViewById(R.id.reply_person_name);
                viewHolder.mContent = (TextView) convertView.findViewById(R.id.reply_content);
                viewHolder.mDateIcon = (TextView) convertView.findViewById(R.id.reply_date_icon);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.reply_date);
                viewHolder.mLikeIcon = (TextView) convertView.findViewById(R.id.reply_like_icon);
                viewHolder.mLike = (TextView) convertView.findViewById(R.id.reply_like_count);
                viewHolder.mDateIcon.setTypeface(mFontFamily);
                viewHolder.mLikeIcon.setTypeface(mFontFamily);

                convertView.setTag(viewHolder);
            }

            initLike(position);

            viewHolder.mDateIcon.setText(Icon.DATE);
            viewHolder.mLikeIcon.setText(Icon.LIKE);

            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd. HH:mm");

            if (position < mBestListSize) {
                viewHolder.mPersonName.setText(mBestReplyList.get(position).mWriter);
                viewHolder.mContent.setText(mBestReplyList.get(position).mContent);
                viewHolder.mDate.setText(format.format(mBestReplyList.get(position).mDate));
                viewHolder.mLike.setText(String.valueOf(mBestReplyList.get(position).mLike));
            } else {
                viewHolder.mPersonImg = (TextView) convertView.findViewById(R.id.reply_person);
                viewHolder.mPersonImg.setTypeface(mFontFamily);
                viewHolder.mPersonImg.setText(Icon.PERSON);

                int result = position - mBestListSize;
                viewHolder.mPersonName.setText(mReplyList.get(result).mWriter);
                viewHolder.mContent.setText(mReplyList.get(result).mContent);
                viewHolder.mDate.setText(format.format(mReplyList.get(result).mDate));
                viewHolder.mLike.setText(String.valueOf(mReplyList.get(result).mLike));
            }
            convertView.setOnClickListener(null);
            return convertView;
        }

        private void initLike(int position) {
            final int ID = (position < mBestListSize) ?
                    mBestReplyList.get(position).mID : mReplyList.get(position - mBestListSize).mID;
            final TextView LIKEICON = viewHolder.mLikeIcon;
            final TextView LIKE = viewHolder.mLike;

            if (ReplyDB.INSTANCE.isLike(String.valueOf(ID))) {
                LIKEICON.setTextColor(Color.BLUE);
                LIKE.setTextColor(Color.BLUE);
            } else {
                LIKEICON.setTextColor(Color.GRAY);
                LIKE.setTextColor(Color.GRAY);
            }

            LIKEICON.setOnClickListener(new LikeListener(ID, LIKEICON, LIKE));
            LIKE.setOnClickListener(new LikeListener(ID, LIKEICON, LIKE));
        }

        public boolean showMore() {
            if (mCount == mReplySize) {
                return true;
            } else {
                mWaitDlg = new WaitDlg(getActivity(), "Please Wait", "Loading...");
                mWaitDlg.start();
                mCount = Math.min(mCount + MongoQuery.INSTANCE.REPLY_PAGE_LIMIT, mReplySize);
                ReplyDB.INSTANCE.setCurrentPage(++mNumber, ReplyLikeResult.ADAPTER);
                synchronized (mReplyAdapter) {
                    try {
                        mReplyAdapter.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setReply();
                notifyDataSetChanged();
                WaitDlg.stop(mWaitDlg);
                return endReached();
            }
        }

        public boolean endReached() {
            return mCount == mReplySize;
        }

        private class LikeListener implements View.OnClickListener {
            private int mID;
            private TextView mLikeIcon;
            private TextView mLike;

            LikeListener(int id, TextView likeIcon, TextView like) {
                mID = id;
                mLikeIcon = likeIcon;
                mLike = like;
            }

            @Override
            public void onClick(View v) {
                try {
                    mWaitDlg = new WaitDlg(getActivity(), "Please Wait", "Loading...");
                    mWaitDlg.start();
                    ReplyDB.INSTANCE.clickLike(mID);
                    synchronized (mReplyAdapter) {
                        try {
                            mReplyAdapter.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ReplyDB.INSTANCE.setCurrentPage(1, ReplyLikeResult.FRAGMENT);
                    synchronized (SlidingListFragment.this) {
                        try {
                            SlidingListFragment.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    setReply();
                    mReplyAdapter.notifyDataSetChanged();
                    if (ReplyDB.INSTANCE.isLike(String.valueOf(mID))) {
                        mLikeIcon.setTextColor(Color.BLUE);
                        mLike.setTextColor(Color.BLUE);
                    } else {
                        mLikeIcon.setTextColor(Color.GRAY);
                        mLike.setTextColor(Color.GRAY);
                    }
                    WaitDlg.stop(mWaitDlg);
                } catch (NullPointerException e) {}
            }
        }
    }
}