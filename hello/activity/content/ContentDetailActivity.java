package com.helloants.mm.helloants1.activity.content;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.helloants.mm.helloants1.LoadingActivity;
import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.adapters.ApplicationAdapter;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.constant.AreaType;
import com.helloants.mm.helloants1.data.constant.Icon;
import com.helloants.mm.helloants1.data.constant.ReplyLikeResult;
import com.helloants.mm.helloants1.data.network.GetNetState;
import com.helloants.mm.helloants1.data.type.ContentType;
import com.helloants.mm.helloants1.data.zoomCheck.CustomViewPager;
import com.helloants.mm.helloants1.db.content.ContentDB;
import com.helloants.mm.helloants1.db.content.ReplyDB;
import com.helloants.mm.helloants1.db.member.MemberDB;
import com.helloants.mm.helloants1.db.mypage.ScrapDB;
import com.helloants.mm.helloants1.fragment.financeInfo.contentDetail.ContentDetailFragment;
import com.helloants.mm.helloants1.fragment.financeInfo.contentDetail.ContentDetailLast;
import com.helloants.mm.helloants1.fragment.financeInfo.contentDetail.Gallery;
import com.helloants.mm.helloants1.fragment.financeInfo.reply.SlidingListFragment;
import com.helloants.mm.helloants1.loading.WaitDlg;
import com.helloants.mm.helloants1.login.Cryptogram;
import com.helloants.mm.helloants1.login.LoginData;
import com.helloants.mm.helloants1.util.ImageCache;
import com.helloants.mm.helloants1.util.ImageFetcher;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentDetailActivity extends FragmentActivity implements View.OnClickListener {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private CustomViewPager mPager;
    private int mID;
    private String mSubtitle;
    private String mFilePath;
    private String mAddress;
    private WaitDlg mWaitDlg;
    private ArrayList<ContentType> mContentList;
    private ArrayList<String> mFilePathList;
    public String shareId;
    public String shareSubTitle;
    public String shareFilePath;
    private int mPosition;

    private LinearLayout gallery;
    private ArrayList<AppInfo> apps;
    TextView backText;
    TextView shareText;
    TextView likeText;
    TextView replyText;
    private TextView scrapText;
    private TextView imgText;
    private TextView imageListText;
    private List<PackageInfo> packs;
    private LinearLayout li;
    private String total;
    private String currentPage;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);

        if(!FacebookSdk.isInitialized()) FacebookSdk.sdkInitialize(getApplicationContext());

        Typeface fontFamily = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/fontawesome.ttf");
        backText = (TextView) findViewById(R.id.text_back_cDetail);
        shareText = (TextView) findViewById(R.id.text_share_cDetail);
        likeText = (TextView) findViewById(R.id.text_like_cDetail);
        replyText = (TextView) findViewById(R.id.text_reply_cDetail);
        imgText = (TextView) findViewById(R.id.image_number);
        imageListText = (TextView) findViewById(R.id.text_image_list);
        scrapText = (TextView) findViewById(R.id.text_scrap_cDetail);
        scrapText.setTypeface(fontFamily);
        backText.setTypeface(fontFamily);
        shareText.setTypeface(fontFamily);
        likeText.setTypeface(fontFamily);
        replyText.setTypeface(fontFamily);
        imageListText.setTypeface((fontFamily));
        imageListText.setText(Icon.TH);
        backText.setText(Icon.ARROW_LEFT);
        shareText.setText(Icon.SHARE_ALT);
        likeText.setText(Icon.HEART);
        scrapText.setText(Icon.BOOK);
        replyText.setText(Icon.COMMENT);

        GetNetState.INSTANCE.checkNetwork(ContentDetailActivity.this);

        if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
            initContentData();
            cache();
            initScrapBtn();
            initBackBtn();
            initGallery();
            initShareBtn();
            initLikeBtn();
            initReplyBtn();

            ContentDB.INSTANCE.contentViewUp(mID);
        } else {
            Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplication(), MainActivity.class));
            ContentDetailActivity.this.finish();
        }

    }
    private void cache() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ContentDB.INSTANCE.getContent(AreaType.CONTENT_DETAIL, mID);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {}

        mContentList = ContentDB.INSTANCE.mContentList;

        Bitmap blurredBitmap = BlurBuilder.blur(this, mContentList.get(0).mFilePath);

        mContentList.remove(0);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);


        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mContentList.size());
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);
        mPager.setCurrentItem(mPosition);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(mPosition);
            }
        });


        BitmapDrawable background = new BitmapDrawable(getResources(),blurredBitmap);
        background.setAlpha(50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mPager.setBackground(background);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }

    private void initContentData() {
        DeviceSize.init(getApplicationContext());
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            MemberDB.INSTANCE.init(ContentDetailActivity.this);
            MemberDB.INSTANCE.setLoginData();

            String email = "";
            try {
                email = Cryptogram.Decrypt(LoginData.mEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if("".equals(email)) {
                Intent load = new Intent(ContentDetailActivity.this, LoadingActivity.class);
                startActivity(load);
            } else {
                mID = Integer.parseInt(uri.getQueryParameter("id"));
                mSubtitle = uri.getQueryParameter("subTitle");
                mFilePath = uri.getQueryParameter("filePath");
            }
        } else {
            mID = intent.getIntExtra("id", 0);
            shareId = "id=" + mID;
            mAddress = "http://www.helloants.com/" + mID;
            mSubtitle = intent.getStringExtra("subTitle");
            shareSubTitle = "subTitle=" + mSubtitle;
            mFilePath = intent.getStringExtra("filePath");
            shareFilePath = "filePath=" + mFilePath;
        }
        mPosition = intent.getIntExtra("position", 0);
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
////                ContentDB.INSTANCE.getContent(AreaType.CONTENT_DETAIL, mID);
//            }
//        };
//        thread.start();
//        try {
//            thread.join();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void initScrapBtn() {
        scrapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                    try {
                        if (LoginData.mEmail.equals("")) {
                            Toast.makeText(ContentDetailActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            mWaitDlg = new WaitDlg(ContentDetailActivity.this, "Please Wait", "Loading...");
                            mWaitDlg.start();
                            if (ScrapDB.INSTANCE.isScrap(mID)) {
                                ScrapDB.INSTANCE.remove(mID);
                                scrapText.setTextColor(Color.WHITE);
                                Toast.makeText(ContentDetailActivity.this, "스크랩 취소 했습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                final EditText AssetName = new EditText(ContentDetailActivity.this);
                                new AlertDialog.Builder(ContentDetailActivity.this)
                                        .setTitle("스크랩 담기")
                                        .setMessage("이름을 입력해 주세요.")
                                        .setView(AssetName)
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String name = AssetName.getText().toString();
                                                if (ScrapDB.INSTANCE.isDuplicate(name)) {
                                                    Toast.makeText(ContentDetailActivity.this, "중복된 이름입니다.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    ScrapDB.INSTANCE.insert(mID, name);
                                                    scrapText.setTextColor(Color.parseColor("#FFD237"));
                                                    Toast.makeText(ContentDetailActivity.this, "추가 했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).show();
                            }
                            WaitDlg.stop(mWaitDlg);
                        }
                    } catch (NullPointerException e) {
                    }
                } else {
                    Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initShareBtn() {
        shareText.setTextColor(Color.WHITE);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                    mWaitDlg = new WaitDlg(ContentDetailActivity.this, "Please Wait", "Loading...");
                    mWaitDlg.start();

                    Dialog d = new Dialog(ContentDetailActivity.this);
                    LayoutInflater l = (LayoutInflater) ContentDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = l.inflate(R.layout.icon_gridview, null, false);
                    GridView grid = (GridView) view.findViewById(R.id.gridList);
                    grid.setBackgroundColor(Color.WHITE);
                    apps = getPackages();
                    ApplicationAdapter listadaptor = new ApplicationAdapter(ContentDetailActivity.this, R.layout.snippet_list,apps);
                    grid.setAdapter(listadaptor);
                    d.setTitle("공유하기");
                    d.getWindow().setTitleColor(getResources().getColor(R.color.helloants_yellow));
                    d.setContentView(view);

                    d.show();
                    WaitDlg.stop(mWaitDlg);
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AppInfo app = apps.get(position);
                            try {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(app.pname);
                                if (null != intent) {
                                    if (app.appname.equals("카카오톡")) {
                                        StringBuilder temp = new StringBuilder();
                                        temp.append(mFilePathList.get(1));
                                        try {
                                            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getBaseContext());
                                            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                                            kakaoTalkLinkMessageBuilder.addText(mSubtitle);
                                            kakaoTalkLinkMessageBuilder.addImage(temp.toString(), 378, 378);
                                            kakaoTalkLinkMessageBuilder.addAppButton("앱에서 보기",
                                                    new AppActionBuilder()
                                                            .addActionInfo(AppActionInfoBuilder
                                                                    .createAndroidActionInfoBuilder()
                                                                    .setExecuteParam(shareId + "&" + shareSubTitle + "&" + shareFilePath)
                                                                    .setMarketParam("market://details?id=com.helloants.mm.helloants1")
                                                                    .build())
                                                            .addActionInfo(AppActionInfoBuilder
                                                                    .createiOSActionInfoBuilder()
                                                                    .setExecuteParam("execparamkey1=1111")
                                                                    .setMarketParam(mAddress)
                                                                    .build())
                                                            .setUrl("http://www.helloants.com")
                                                            .build());
                                            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, ContentDetailActivity.this);
                                        } catch (KakaoParameterException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (app.appname.equals("Facebook")) {
                                        callbackManager = CallbackManager.Factory.create();
                                        shareDialog = new ShareDialog(ContentDetailActivity.this);
                                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                                    .setContentTitle("HelloAnt$")
                                                    .setContentDescription(
                                                            mSubtitle)
                                                    .setContentUrl(Uri.parse(mAddress))
                                                    .build();
                                            shareDialog.show(linkContent);
                                        }
                                    } else {
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setPackage(app.pname);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, mAddress);
                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mSubtitle);
                                        startActivity(shareIntent);
                                    }
                                }
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(ContentDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<AppInfo> getPackages() {
        ArrayList<AppInfo> apps = getInstalledApps(false);
        return apps;
    }

    private ArrayList<AppInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<AppInfo> res = new ArrayList<AppInfo>();
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            if (p.packageName.equals("com.kakao.talk") || p.packageName.equals("com.kakao.story") || p.applicationInfo.loadLabel(getPackageManager()).equals("Facebook")) {
                AppInfo newInfo = new AppInfo();
                newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
                newInfo.pname = p.packageName;
                newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
                res.add(newInfo);
            }
        }
        return res;
    }

    private void initReplyBtn() {
        if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
            ReplyDB.INSTANCE.init(ContentDetailActivity.this);
            replyText.setTextColor(Color.WHITE);
            replyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleList();
                }
            });
        } else {
            Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleList() {
        android.app.Fragment f = getFragmentManager().findFragmentByTag("list_fragment");
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                mWaitDlg = new WaitDlg(ContentDetailActivity.this, "Please Wait", "Loading...");
                mWaitDlg.start();

                ReplyDB.INSTANCE.mID = mID;
                ReplyDB.INSTANCE.setCurrentPage(2, ReplyLikeResult.CONTEXT);

                synchronized (ContentDetailActivity.this) {
                    try {
                        ContentDetailActivity.this.wait();
                    } catch (InterruptedException e) {}
                }

                SlidingListFragment.setTextView(replyText);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.list_fragment_container, SlidingListFragment
                                        .instantiate(this, SlidingListFragment.class.getName()),
                                "list_fragment"
                        ).addToBackStack(null).commit();
                WaitDlg.stop(mWaitDlg);
            }
        }
    }

    private void initBackBtn() {
        backText.setTextColor(Color.WHITE);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initLikeBtn() {
        if (ContentDB.INSTANCE.isLike(mID)) likeText.setTextColor(Color.parseColor("#FF0066"));
        else likeText.setTextColor(Color.WHITE);
        if (ScrapDB.INSTANCE.isScrap(mID)) scrapText.setTextColor(Color.parseColor("#FFD237"));
        else scrapText.setTextColor(Color.WHITE);

        likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                    try {
                        if (LoginData.mEmail.equals("")) {
                            Toast.makeText(ContentDetailActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            mWaitDlg = new WaitDlg(ContentDetailActivity.this, "Please Wait", "Loading...");
                            mWaitDlg.start();
                            ContentDB.INSTANCE.clickLike(mID);

                            if (ContentDB.INSTANCE.isLike(mID)) {
                                likeText.setTextColor(Color.parseColor("#FF0066"));
                            } else {
                                likeText.setTextColor(Color.WHITE);
                            }
                            WaitDlg.stop(mWaitDlg);
                        }
                    } catch (NullPointerException e) {
                    }
                } else {
                    Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Thread threadLike = new Thread() {
            @Override
            public void run() {
                ContentDB.INSTANCE.getContent(AreaType.CONTENT_DETAIL, mID);
            }
        };
        threadLike.start();
        try {
            threadLike.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {

        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

    }

    public class AppInfo {
        public String appname = "";
        public String pname = "";
        public Drawable icon;
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size+1;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if(mSize-position == 1){
                return ContentDetailLast.newInstance();
            }
            else{
                return ContentDetailFragment.newInstance(mContentList.get(position).mFilePath,mContentList.get(position).mLink);
            }
        }
    }

    private void initGallery() {
        li = (LinearLayout) findViewById(R.id.content_detailbar);
        HorizontalScrollView bottomBar = (HorizontalScrollView) findViewById(R.id.content_detail_bottombar);
        total = String.valueOf(mContentList.size());
        imgText.setText((mPosition + 1) + "of" + total);
        imgText.setTextColor(Color.WHITE);
        imageListText.setTextColor(Color.WHITE);
//        bottomBar.setBackgroundColor(Color.parseColor("#51585A"));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                currentPage = String.valueOf(position + 1);
                imgText.setText(currentPage + "of" + total);
                li.setVisibility(View.VISIBLE);

                if (mContentList.size() - position == 0) {
                    li.setVisibility(View.GONE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mFilePathList = new ArrayList<String>();
        for (int i = 0; i < mContentList.size(); ++i) {
            mFilePathList.add(mContentList.get(i).mFilePath);
        }
//        gallery = (LinearLayout) findViewById(R.id.img_gallery);
//        gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
//                    Gallery.setIsVisible(true);
//                    Bundle bundle = new Bundle();
//                    bundle.putStringArrayList("imageList", mFilePathList);
//                    bundle.putInt("id", mID);
//                    bundle.putString("subTitle", mSubtitle);
//                    Gallery gallery = new Gallery();
//                    gallery.setArguments(bundle);
//                    if (gallery != null) {
//                        FragmentManager fm = getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                        fragmentTransaction.addToBackStack("");
//                        fragmentTransaction.add(R.id.content_detail, gallery).commit();
//                    }
//                } else {
//                    Toast.makeText(ContentDetailActivity.this, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        packs = getPackageManager().getInstalledPackages(0);
    }

    @Override
    public void onBackPressed() {
        if (SlidingListFragment.isGetVisible()) {
            SlidingListFragment.setIsVisible(false);
            replyText.performClick();
        } else if (Gallery.isGetVisible()) {
            Gallery.setIsVisible(false);
            Intent ContentActivity = new Intent(this, ContentDetailActivity.class);
            ContentActivity.putExtra("id", mID);
            ContentActivity.putExtra("subTitle", mSubtitle);
            ContentActivity.putExtra("filePath", mFilePath);
            startActivity(ContentActivity);
        } else {
            super.onBackPressed();
            ContentDetailActivity.this.finish();
        }
    }

    public static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS =2f;

        public static Bitmap blur(Context context, final String filePath) {
            final URL[] url = {null};
            final Bitmap[] image = {null};
            try {
                final Thread iThread = new Thread() {

                    @Override
                    public void run() {

                        try {
                            url[0] = new URL(filePath);
                            HttpURLConnection conn = (HttpURLConnection) url[0].openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 1;
                            image[0] = BitmapFactory.decodeStream(is, null, options);
                        } catch (IOException ex) {
                        }catch (IllegalArgumentException e) {
                        }
                    }
                };
                if (GetNetState.INSTANCE.mWifi || GetNetState.INSTANCE.mMobile) {
                    iThread.start();
                    iThread.join();
                } else {
                    Toast.makeText(context, "네트워크 연결이 불안정합니다", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException e) {
            }


            int width = Math.round(image[0].getWidth() * BITMAP_SCALE);
            int height = Math.round(image[0].getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image[0], width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                theIntrinsic.setRadius(BLUR_RADIUS);
                theIntrinsic.setInput(tmpIn);
                theIntrinsic.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);
            }
            return outputBitmap;
        }
    }


}
