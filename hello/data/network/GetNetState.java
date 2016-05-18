package com.helloants.mm.helloants1.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by park on 2016-03-28.
 */
public enum GetNetState {
    INSTANCE;

    public boolean mWifi;
    public boolean mMobile;

    public void checkNetwork(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (activeNetwork != null) {
                if (wifi.isConnected()) {
                    GetNetState.INSTANCE.mWifi = true;
                }
                else if (mobile.isConnected()) {
                    GetNetState.INSTANCE.mWifi = false;
                    GetNetState.INSTANCE.mMobile = true;
                }
            } else {
                GetNetState.INSTANCE.mWifi = false;
                GetNetState.INSTANCE.mMobile = false;
            }
        } catch (NullPointerException e) {
            GetNetState.INSTANCE.mWifi = false;
            GetNetState.INSTANCE.mMobile = false;
        }
    }
}