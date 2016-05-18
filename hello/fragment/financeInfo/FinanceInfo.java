package com.helloants.mm.helloants1.fragment.financeInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;
import com.helloants.mm.helloants1.adapters.RecyclerViewAdapter;
import com.helloants.mm.helloants1.adapters.RecyclerViewAdapter2;
import com.helloants.mm.helloants1.adapters.RecyclerViewAdapter3;
import com.helloants.mm.helloants1.data.ContentImage;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.network.GetNetState;
import com.helloants.mm.helloants1.data.type.ContentType;
import com.helloants.mm.helloants1.data.type.NoticeType;
import com.helloants.mm.helloants1.db.content.ContentDB;
import com.helloants.mm.helloants1.db.content.NoticeDB;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FinanceInfo extends Fragment {
    View v;
    private static RecyclerView recyclerView;
    private ArrayList<ContentType> mContentList;
    List<ContentImage> contentImage;
    ArrayList<Bitmap> bitmap;
    Bitmap bit;

    private static RecyclerView recyclerView2;
    private ArrayList<ContentType> mContentList2;
    List<ContentImage> contentImage2;
    ArrayList<Bitmap> bitmap2;
    Bitmap bit2;

    private static RecyclerView recyclerView3;
    private ArrayList<ContentType> mContentList3;
    List<ContentImage> contentImage3;
    ArrayList<Bitmap> bitmap3;
    Bitmap bit3;
    RelativeLayout moreBtn;
    RelativeLayout moreBtn2;
    RelativeLayout moreBtn3;
    android.support.v4.app.FragmentTransaction fragmentTransaction;

    private ViewFlipper mNtcVwFlipper;
    ArrayList<NoticeType> nList;
    float xAtDown;
    float xAtUp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_finance_info,container,false);
        moreBtn = (RelativeLayout) v.findViewById(R.id.more);
        moreBtn2 = (RelativeLayout) v.findViewById(R.id.more2);
        moreBtn3 = (RelativeLayout) v.findViewById(R.id.more3);
        mNtcVwFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        initNotice();
        initViews();
        initViews2();
        initViews3();
        populateRecyclerView();

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_area, new News());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                iconChange();
            }
        });
        moreBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_area, new Pcmt());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                iconChange();
            }
        });
        moreBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_area, new Tip());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                iconChange();
            }
        });
        return v;
    }

    public void iconChange(){
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        TabLayout tab = (TabLayout) getActivity().findViewById(R.id.tabs);
        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.viewpager);
        vp.setVisibility(View.GONE);
        tab.setVisibility(View.GONE);
        final MainActivity mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolbar();

        if(News.isGetVisible()|| Pcmt.isGetVisible() ||Tip.isGetVisible()) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mainActivity.onBackPressed();

                }
            });
        }

    }
    private void initViews(){
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        try {
            final Thread iThread = new Thread() {
                @Override
                public void run() {
                    try {
                        mContentList = ContentDB.INSTANCE.mNewsList;
                        bitmap = new ArrayList();

                        for (int i = 0; i < 6; ++i) {
                            URL url = new URL(mContentList.get(i).mFilePath);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap preBitmap = BitmapFactory.decodeStream(is, null, options);
                            bit = Bitmap.createScaledBitmap(preBitmap,
                                    DeviceSize.mWidth / 2,
                                    DeviceSize.mWidth / 2,
                                    true);
                            bitmap.add(bit);
                            if (preBitmap != bit) {
                                preBitmap.recycle();
                            }
                            preBitmap = null;
                        }
                    } catch (IOException ex) {
                    } catch (IllegalArgumentException e) {
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
        contentImage = new ArrayList<ContentImage>();
        Iterator<ContentType> iter = mContentList.iterator();
        int i = 0;
        while(iter.hasNext()) {
            if(i==6) {
                break;
            }
            ContentType ct = iter.next();
            contentImage.add(new ContentImage(ct.mSubTitle, bitmap.get(i++), ct.mID,ct.mFilePath));
        }
    }

    private void initViews2(){
        recyclerView2 = (RecyclerView)v.findViewById(R.id.recycler_view2);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        try {
            final Thread iThread = new Thread() {
                @Override
                public void run() {
                    try {
                        mContentList2 = ContentDB.INSTANCE.mPcmtList;
                        bitmap2 = new ArrayList();

                        for (int i = 0; i < 6; ++i) {
                            URL url = new URL(mContentList2.get(i).mFilePath);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap preBitmap = BitmapFactory.decodeStream(is, null, options);
                            bit2 = Bitmap.createScaledBitmap(preBitmap,
                                    DeviceSize.mWidth / 2,
                                    DeviceSize.mWidth / 2,
                                    true);
                            bitmap2.add(bit2);
                            if (preBitmap != bit2) {
                                preBitmap.recycle();
                            }
                            preBitmap = null;
                        }
                    } catch (IOException ex) {
                    } catch (IllegalArgumentException e) {
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
        contentImage2 = new ArrayList<ContentImage>();
        Iterator<ContentType> iter = mContentList2.iterator();
        int i = 0;
        while(iter.hasNext()) {
            if(i==6) {
                break;
            }
            ContentType ct = iter.next();
            contentImage2.add(new ContentImage(ct.mSubTitle, bitmap2.get(i++),ct.mID,ct.mFilePath));

        }


    }

    private void initViews3(){
        recyclerView3 = (RecyclerView)v.findViewById(R.id.recycler_view3);
        recyclerView3.setNestedScrollingEnabled(false);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        try {
            final Thread iThread = new Thread() {
                @Override
                public void run() {
                    try {
                        mContentList3 = ContentDB.INSTANCE.mTipList;
                        bitmap3 = new ArrayList();

                        for (int i = 0; i < 6; ++i) {
                            URL url = new URL(mContentList3.get(i).mFilePath);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap preBitmap = BitmapFactory.decodeStream(is, null, options);
                            bit3 = Bitmap.createScaledBitmap(preBitmap,
                                    DeviceSize.mWidth / 2,
                                    DeviceSize.mWidth / 2,
                                    true);
                            bitmap3.add(bit3);
                            if (preBitmap != bit3) {
                                preBitmap.recycle();
                            }
                            preBitmap = null;
                        }
                    } catch (IOException ex) {
                    } catch (IllegalArgumentException e) {
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
        contentImage3 = new ArrayList<ContentImage>();
        Iterator<ContentType> iter = mContentList3.iterator();
        int i = 0;
        while(iter.hasNext()) {
            if(i==6) {
                break;
            }
            ContentType ct = iter.next();
            contentImage3.add(new ContentImage(ct.mSubTitle, bitmap3.get(i++),ct.mID,ct.mFilePath));

        }


    }

    private void populateRecyclerView(){
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(),contentImage,recyclerView);
        RecyclerViewAdapter2 adapter2 = new RecyclerViewAdapter2(getActivity(),contentImage2,recyclerView2);
        RecyclerViewAdapter3 adapter3 = new RecyclerViewAdapter3(getActivity(),contentImage3,recyclerView3);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
        recyclerView3.setAdapter(adapter3);
        adapter3.notifyDataSetChanged();
    }

    private void initNotice() {
        nList = NoticeDB.INSTANCE.mNoticeList;

        for (NoticeType nt : nList) {
            ImageView img = new ImageView(getContext());
            img.setImageBitmap(nt.mBitmap);

            final NoticeType NT = nt;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                        Intent ContentActivity = new Intent(getContext(), ContentDetailActivity.class);
                        ContentActivity.putExtra("id", NT.mContentID);
                        getContext().startActivity(ContentActivity);
                    } else {
                        Toast.makeText(getActivity(), "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mNtcVwFlipper.addView(img);
        }
        Animation showIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        mNtcVwFlipper.setInAnimation(showIn);
        mNtcVwFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        mNtcVwFlipper.setFlipInterval(3000);
        mNtcVwFlipper.startFlipping();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (nList != null) {
            nList.clear();
        }
    }
}