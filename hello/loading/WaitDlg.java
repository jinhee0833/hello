package com.helloants.mm.helloants1.loading;

/**
 * Created by park on 2016-01-21.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.HandlerThread;

public class WaitDlg extends HandlerThread {
    private Context mContext;
    private String mTitle;
    private String mMsg;
    private ProgressDialog mProgress;

    public WaitDlg(Context context, String title, String msg) {
        super("waitdlg");
        mContext = context;
        mTitle = title;
        mMsg = msg;

        setDaemon(true);
    }

    protected void onLooperPrepared() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setTitle(mTitle);
        mProgress.setMessage(mMsg);
        mProgress.setCancelable(false);
        mProgress.show();
    }

    public static void stop(WaitDlg dlg) {
        try {
            dlg.mProgress.dismiss();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dlg.getLooper().quit();
        } catch (NullPointerException e) {}
    }
}
