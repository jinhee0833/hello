package com.helloants.mm.helloants1.data.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helloants.mm.helloants1.LoadingActivity;
import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NotificationFormat {

    public static void NotificationPopup(final Context context, final String message)
    {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.toast, null);
                LinearLayout layout = (LinearLayout) linearLayout.findViewById(R.id.relativeLayout1);
                TextView tv = (TextView) linearLayout.findViewById(R.id.textView2);
                ImageView image = (ImageView) linearLayout.findViewById(R.id.imageView1);
                tv.setText(message);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.helloants.mm.helloants1");
                        context.startActivity(launchIntent);
                    }
                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.helloants.mm.helloants1");
                        context.startActivity(launchIntent);
                    }
                });
                PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                if(mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                mWakeLock.acquire();
                Toast toast = new Toast(context);
                toast.setView(linearLayout);
                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                Looper.loop();

            }
        }).start();
    }

    public static void NotificationPush2(Context context, String title, String message,String ticker,String filePath,String id,String subTitle,String firstFilePath){
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(title);
        notiStyle.setSummaryText(message);

        try{
            Bitmap logo2 = BitmapFactory.decodeStream((InputStream) new URL("http://d2exf4rydl6bqi.cloudfront.net/img/" + filePath).getContent());
            notiStyle.bigPicture(logo2);
        }catch (IOException e){
            e.printStackTrace();
        }catch(NetworkOnMainThreadException ex){
            ex.printStackTrace();
        }

        Intent intent = new Intent(context, ContentDetailActivity.class);
        intent.putExtra("id",Integer.parseInt(id));
        intent.putExtra("subTitle",subTitle);
        intent.putExtra("filePath",firstFilePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.largeicon);
        int width = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        Bitmap largeLogo = Bitmap.createScaledBitmap(logo, width, width, false);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.smallicon)
                .setLargeIcon(largeLogo)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(ticker)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setStyle(notiStyle);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());


    }

    public static void NotificationPush(Context context, String title, String message,String ticker){
        Intent intent = new Intent(context, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.largeicon);
        int width = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        Bitmap largeLogo = Bitmap.createScaledBitmap(logo, width, width, false);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.smallicon)
                .setLargeIcon(largeLogo)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(ticker)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
