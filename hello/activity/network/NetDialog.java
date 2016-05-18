package com.helloants.mm.helloants1.activity.network;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;


/**
 * Created by park on 2016-03-26.
 */
public class NetDialog extends Activity {
    private TextView mTitle;
    private TextView mContent;
    private Button mConfirm;
    private Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        setContentView(R.layout.activity_network_dialog);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 0.7f;
//        getWindow().setAttributes(layoutParams);

        mTitle = (TextView) findViewById(R.id.txv_title_network_dialog);
        mContent = (TextView) findViewById(R.id.txv_content_network_dialog);
        mConfirm = (Button) findViewById(R.id.btn_confirm_network_dialog);
        mCancel = (Button) findViewById(R.id.btn_cancel_network_dialog);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mTitle.setText(bundle.getString("title", ""));
            mContent.setText(bundle.getString("content", ""));
        }
    }
}
