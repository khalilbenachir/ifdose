package ma.ifdose.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import timber.log.Timber;

/**
 * Created by aaaa on 10/24/2017.
 */

public class OnetimeAlarmReceiver extends BroadcastReceiver {

    final int REQUEST_CODE = 123;

    @Override
    @RequiresApi(api = 16)
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Repeating Alarm worked.", Toast.LENGTH_LONG).show();
        Timber.i("OnetimeAlarmReceiver :" + "OnReceive");

        int page = intent.getIntExtra("fragment", 0);
        String notificationTitle = context.getString(ma.ifdose.app.R.string.notification_title);
        String notificationText = context.getString(ma.ifdose.app.R.string.notification_text);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, GlycemiesActivity3.class);
        notificationIntent.putExtra("fragment", page);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context,
                REQUEST_CODE,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(ma.ifdose.app.R.drawable.ic_notifications_active_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        ma.ifdose.app.R.mipmap.ic_launcher))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText));

//                .addAction(R.drawable.ic_email_black_24dp, "Call", pIntent)
//                .addAction(R.drawable.ic_language_black_24dp, "More", pIntent)
//                .addAction(R.drawable.ic_button1, "And more", pIntent).build();


//        NotificationManagerCompat.from(context).notify(0, notification);

        notificationManager.notify(REQUEST_CODE, notificationBuilder.build());

    }
}
