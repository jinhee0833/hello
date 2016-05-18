package com.helloants.mm.helloants1.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.viewholder.ImgListViewHolder;
import com.helloants.mm.helloants1.data.ContentImage;

import java.util.List;


/**
 * Created by JJ on 2016-01-15.
 */
public class ImgListAdapter extends ArrayAdapter<ContentImage> {
    Context mContext;
    int mResLayout;
    List<ContentImage> mListContentImage;
    int mStartCount;
    int mStepNumber;
    int mCount;
    ImgListViewHolder viewHolder;

    public ImgListAdapter(Context context, int resLayout, List<ContentImage> listContentImage, int startCount, int stepNumber) {
        super(context, resLayout, listContentImage);

        mContext = context;
        mResLayout = resLayout;
        mListContentImage = listContentImage;
        mStartCount = Math.min(startCount, listContentImage.size());
        mCount = mStartCount;
        mStepNumber = stepNumber;
    }

    public boolean showMore() {
        if (mCount == mListContentImage.size()) {
            return true;
        } else {
            mCount = Math.min(mCount + mStepNumber, mListContentImage.size());
            notifyDataSetChanged();
            return endReached();
        }
    }

    public boolean endReached() {
        return mCount == mListContentImage.size();
    }

    public int getCount() {
        return mCount;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(mContext, mResLayout, null);
            viewHolder = new ImgListViewHolder();

            viewHolder.mContentImage = (ImageView) convertView.findViewById(R.id.content_image);
            viewHolder.mTvSubTitle = (TextView) convertView.findViewById(R.id.subTitle);

            convertView.setTag(viewHolder);
        } else viewHolder = (ImgListViewHolder) convertView.getTag();

        ContentImage cImage = mListContentImage.get(position);
        viewHolder.mContentImage.setImageBitmap(cImage.mBitmap);
        viewHolder.mTvSubTitle.setText(cImage.mSubTitle);
        return convertView;
    }
}
