package com.example.diemquatrinh1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d("Receive", "Reminder received");
    // Tạo intent để mở MainActivity khi người dùng nhấn vào thông báo
    Intent notificationIntent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    // Tạo thông báo
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager == null) {
      Log.e("Notification", "Notification Manager is null");
      return;
    }
    // Tạo NotificationChannel (cho Android 8.0 trở lên)
    String channelId = "DailySelfieChannel";
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
              channelId,
              "Daily Selfie Reminder",
              NotificationManager.IMPORTANCE_HIGH // Ensure this is set to HIGH
      );
      channel.setDescription("Channel for daily selfie reminders");
      notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Daily Selfie Reminder")
            .setContentText("Don't forget to take your selfie today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);


    notificationManager.notify((int) System.currentTimeMillis(), builder.build());

  }
}
