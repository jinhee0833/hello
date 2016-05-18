package com.helloants.mm.helloants1.data.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by park on 2016-03-23.
 */
public class NwCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        final Context CONTEXT = context;
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            try {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetwork = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileNetwork = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifiNetwork != null) {
                    if (wifiNetwork.isAvailable() && wifiNetwork.isConnected()) {
                        GetNetState.INSTANCE.mWifi = true;
                    }
                    else if (mobileNetwork.isAvailable() && mobileNetwork.isConnected()) {
                        GetNetState.INSTANCE.mWifi = false;
                        GetNetState.INSTANCE.mMobile = true;
                    } else {
                        GetNetState.INSTANCE.mWifi = false;
                        GetNetState.INSTANCE.mMobile = false;
                    }
                }
            } catch (NullPointerException e) {
                GetNetState.INSTANCE.mWifi = false;
                GetNetState.INSTANCE.mMobile = false;
            }
        }
    }
}