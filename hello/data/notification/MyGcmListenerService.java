package com.helloants.mm.helloants1.data.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        Boolean push = pref.getBoolean("push", true);
        String title = data.getString("title");
        final String message = data.getString("message");
        String AdContent = data.getString("filePath");
        String AdId = data.getString("id");
        String AdSubTitle = data.getString("subTitle");
        String AdFilePath = data.getString("firstFilePath");
        if (AdContent == null) {
            if (push == true) {
                NotificationFormat.NotificationPush(getApplicationContext(), title, message, message);
                NotificationFormat.NotificationPopup(getApplicationContext(), message);
            }
        } else {
            if (push == true) {
                NotificationFormat.NotificationPush2(getApplicationContext(), title, message, message, AdContent, AdId, AdSubTitle, AdFilePath);
            }
        }
    }
}