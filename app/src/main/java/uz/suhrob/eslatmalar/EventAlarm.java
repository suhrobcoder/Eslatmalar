package uz.suhrob.eslatmalar;

import android.app.AlarmManager;
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
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import uz.suhrob.eslatmalar.database.EventDBHelper;
import uz.suhrob.eslatmalar.models.Event;
import uz.suhrob.eslatmalar.models.EventType;
import uz.suhrob.eslatmalar.models.Notify;

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
        wl.acquire(1000L);

        String eventName = intent.getExtras().getString(EVENT_NAME);
        String eventContent = intent.getExtras().getString(EVENT_CONTENT);
        int notificationId = intent.getExtras().getInt(NOTIFICATION_ID);

        Calendar calendar = Calendar.getInstance();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(eventName)
                .setContentText("" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " da." + eventContent)
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

        EventDBHelper dbHelper = new EventDBHelper(context);
        Notify notify = dbHelper.getNotify(notificationId);
        dbHelper.deleteNotify(notificationId);
        long new_id = dbHelper.insertNotify(notify);
        Event event = dbHelper.getOne(notify.getEventId());
        if (event.getType().equals(EventType.WEEKLY.name())) {
            calendar = AddEventActivity.whenNextAlarm(event);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis() + 86400*1000);
        }

        wl.release();

        if (!event.getType().equals(EventType.ONCE.name())) {
            setAlarm(context, calendar, eventName, eventContent, (int)new_id);
        }
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
        Log.d("AlarmTime", calendar.toString());
    }

    public void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(context, EventAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        EventDBHelper dbHelper = new EventDBHelper(context);
        dbHelper.deleteNotify(id);
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }
}
