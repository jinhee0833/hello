package com.helloants.mm.helloants1.fragment.financeInfo.contentDetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;
import com.helloants.mm.helloants1.data.image.RecyclingImageView;
import com.helloants.mm.helloants1.util.ImageCache;
import com.helloants.mm.helloants1.util.ImageFetcher;
import com.helloants.mm.helloants1.util.Utils;

import java.util.ArrayList;

public class Gallery extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<String> mContentList;
    private static boolean mIsVisible;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;


    public static void setIsVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }

    public static boolean isGetVisible() {
        return mIsVisible;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f);


        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        final GridView grid = (GridView) v.findViewById(R.id.image_list);
        mContentList = getArguments().getStringArrayList("imageList");
        TextView close = (TextView) v.findViewById(R.id.gallery_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gallery.setIsVisible(false);
                Intent ContentActivity = new Intent(getContext(), ContentDetailActivity.class);
                ContentActivity.putExtra("id", getArguments().getInt("id"));
                ContentActivity.putExtra("subTitle", getArguments().getString("subTitle"));
                ContentActivity.putExtra("filePath", getArguments().getString("filePath"));
                startActivity(ContentActivity);

            }
        });
        mIsVisible = true;
        grid.setAdapter(mAdapter);
        grid.setOnItemClickListener(this);
        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        grid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        final int numColumns = 3;
                        if (numColumns > 0) {
                            final int columnWidth =
                                    (grid.getWidth() / numColumns) - mImageThumbSpacing;
                            mAdapter.setItemHeight(columnWidth);
                            if (Utils.hasJellyBean()) {
                                grid.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            } else {
                                grid.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                        }

                    }
                });
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Gallery.setIsVisible(false);
        Intent ContentActivity = new Intent(getActivity(), ContentDetailActivity.class);
        ContentActivity.putExtra("id", getArguments().getInt("id"));
        ContentActivity.putExtra("subTitle", getArguments().getString("subTitle"));
        ContentActivity.putExtra("filePath", mContentList.get(0));
        ContentActivity.putExtra("position", position);
        ContentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ContentActivity);
        getActivity().finish();
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

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }

        @Override
        public int getCount() {
            return mContentList.size();
        }

        @Override
        public Object getItem(int position) {
            return  mContentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }



        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            mImageFetcher.loadImage(mContentList.get(position), imageView);
            return imageView;

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