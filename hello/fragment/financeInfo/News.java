package com.helloants.mm.helloants1.fragment.financeInfo;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;
import com.helloants.mm.helloants1.adapters.viewholder.ImgListViewHolder;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.network.GetNetState;
import com.helloants.mm.helloants1.data.type.ContentType;
import com.helloants.mm.helloants1.db.content.ContentDB;
import com.helloants.mm.helloants1.util.ImageCache;
import com.helloants.mm.helloants1.util.ImageFetcher;
import com.helloants.mm.helloants1.util.Utils;

import java.util.ArrayList;

public class News extends Fragment implements AdapterView.OnItemClickListener{
    private static boolean mIsVisible;
    GridView list;
    private ArrayList<ContentType> mContentList;
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    public static boolean isGetVisible() {
        return mIsVisible;
    }

    public static void setmIsVisible(boolean mIsVisible) {
        News.mIsVisible = mIsVisible;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mAdapter = new ImageAdapter(getActivity(), R.layout.content_image_format);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
            list = (GridView) v.findViewById(R.id.listView);
            mIsVisible = true;
            initData();
        } else {
            Toast.makeText(getActivity(), "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    private void initData() {
        try {
            final Thread iThread = new Thread() {
                @Override
                public void run() {
                    try {
                        mContentList = ContentDB.INSTANCE.mNewsList;
                    }  catch (IllegalArgumentException e) {
                    }
                }
            };
            if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                iThread.start();
                iThread.join();
            } else {
                Toast.makeText(getActivity(), "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
        }

        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        list.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {

                        final int numColumns = 2;
                        if (numColumns > 0) {
                            final int columnWidth =
                                    (list.getWidth() / numColumns) - mImageThumbSpacing;
                            mAdapter.setItemHeight(columnWidth);
                            if (Utils.hasJellyBean()) {
                                list.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            } else {
                                list.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                        }

                    }
                });
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent i = new Intent(getActivity(), ContentDetailActivity.class);
        i.putExtra("id", mContentList.get(position).mID);
        i.putExtra("subTitle", mContentList.get(position).mSubTitle);
        i.putExtra("filePath", mContentList.get(position).mFilePath);
        if (Utils.hasJellyBean()) {
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private int mResLayout;
        ImgListViewHolder viewHolder;
        FrameLayout.LayoutParams params;
        FrameLayout.LayoutParams parm;
        public ImageAdapter(Context context, int resLayout) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mResLayout = resLayout;
        }

        @Override
        public int getCount() {
            return mContentList.size();
        }

        @Override
        public Object getItem(int position) {
            return  mContentList.get(position).mFilePath;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;
            if (convertView == null) {
                convertView = View.inflate(mContext,mResLayout,null);

                viewHolder = new ImgListViewHolder();

                viewHolder.mContentImage = (ImageView) convertView.findViewById(R.id.content_image);
                viewHolder.mTvSubTitle = (TextView) convertView.findViewById(R.id.subTitle);
                parm = new FrameLayout.LayoutParams(DeviceSize.mWidth/2, DeviceSize.mWidth/2);
                convertView.setTag(viewHolder);
                params = (FrameLayout.LayoutParams) convertView.getLayoutParams();
                viewHolder.mContentImage.setLayoutParams(parm);
            } else {

                viewHolder = (ImgListViewHolder) convertView.getTag();
            }



            if(viewHolder.mContentImage.getHeight() != mItemHeight){
                viewHolder.mContentImage.setLayoutParams(parm);
            }

            mImageFetcher.loadImage(mContentList.get(position).mFilePath, viewHolder.mContentImage);
            viewHolder.mTvSubTitle.setText(mContentList.get(position).mSubTitle);
            return convertView;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }
    }
}