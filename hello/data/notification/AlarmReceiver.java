package com.helloants.mm.helloants1.data.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by park on 2016-03-23.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Boolean push =  pref.getBoolean("push", true);
        if(push == true) {
            NotificationFormat.NotificationPush(context,"헬로앤츠","야호! 오늘은 월급날! 한달동안 고생하셨어요","신난다! 재미난다! 오늘은 월급날!");
            NotificationFormat.NotificationPopup(context,"월급날은 헬로앤츠! 한달동안 고생하셨습니다 이번달도 힘내서 달려보아요!");
        }
    }
}