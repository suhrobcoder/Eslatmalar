package uz.suhrob.eslatmalar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uz.suhrob.eslatmalar.models.Date;
import uz.suhrob.eslatmalar.models.Event;

/**
 * Created by User on 17.12.2019.
 */

public class EventAlarm extends BroadcastReceiver {

    private static String EVENT_NAME = "name";
    private static String EVENT_CONTENT = "content";
    private static String NOTIFICATION_ID = "notify_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Suhrob");
        wl.acquire(1000L /*10 minutes*/);

        String eventName = intent.getExtras().getString(EVENT_NAME);
        String eventContent = intent.getExtras().getString(EVENT_CONTENT);

        int notificationId = intent.getExtras().getInt(NOTIFICATION_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(eventName)
                .setContentText(eventContent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(path);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ID",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("ID");
        }
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }


        wl.release();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 86400*1000);

        setAlarm(context, calendar, eventName, eventContent, notificationId);
    }

    public void setAlarm(Context context, Calendar calendar, String name, String content, int id) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, EventAlarm.class);
        intent.putExtra(EVENT_NAME, name);
        intent.putExtra(EVENT_CONTENT, content);
        intent.putExtra(NOTIFICATION_ID, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, 0);
        if (am != null) {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    public void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(context, EventAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }
}
