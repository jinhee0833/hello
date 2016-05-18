package com.helloants.mm.helloants1.fragment.financeInfo.contentDetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;
import com.helloants.mm.helloants1.util.ImageFetcher;
import com.helloants.mm.helloants1.util.ImageWorker;
import com.helloants.mm.helloants1.util.Utils;

import org.w3c.dom.Text;

import uk.co.senab.photoview.PhotoView;

public class ContentDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "content_Detail";
    private String mImageUrl;
    private PhotoView mPhotoview;
    private ImageFetcher mImageFetcher;
    private String mContentLink;
    private TextView mLink;
    private WebView mWebView;

    public static ContentDetailFragment newInstance(String imageUrl,String contentLink) {
        final ContentDetailFragment f = new ContentDetailFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putString("link",contentLink);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mContentLink = getArguments() != null ? getArguments().getString("link"):null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_content_detail, container, false);
        mPhotoview = (PhotoView) v.findViewById(R.id.photoview_contentdetail);
        mLink= (TextView) v.findViewById(R.id.txt_link);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ContentDetailActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((ContentDetailActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mPhotoview);


            mLink.setVisibility(View.GONE);
            if(mContentLink != null){
                mLink.setVisibility(View.VISIBLE);
                mLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+mContentLink));
                        startActivity(intent);
                    }
                });
            }
            Log.v("디테일용량맥스:", String.valueOf(Runtime.getRuntime().maxMemory()));
            Log.v("디테일용량전체:", String.valueOf(Runtime.getRuntime().totalMemory()));
            Log.v("디테일용량여유:", String.valueOf(Runtime.getRuntime().freeMemory()));
            Log.v("디테일용량사용량:", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (View.OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
            mPhotoview.setOnClickListener((View.OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPhotoview != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mPhotoview);
            mPhotoview.setImageDrawable(null);
            mLink.setText(null);
        }
    }


}
