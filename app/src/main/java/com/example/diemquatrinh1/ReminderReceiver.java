package com.example.diemquatrinh1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    // Lấy thời gian chụp ảnh gần nhất
    long lastCaptureTime = context.getSharedPreferences("DailySelfieApp", Context.MODE_PRIVATE)
            .getLong("lastCaptureTime", 0);
    // Fake da chup tu 1 ngay truoc
//     lastCaptureTime -= 24 * 60 * 60 * 1000;

    // Lấy thời gian thông báo đã thiết lập
    int hour = context.getSharedPreferences("DailySelfieApp", Context.MODE_PRIVATE)
            .getInt("hour", 0);
    int minute = context.getSharedPreferences("DailySelfieApp", Context.MODE_PRIVATE)
            .getInt("minute", 0);

    // Tạo thời gian thông báo trong ngày
    Calendar notificationTime = Calendar.getInstance();
    notificationTime.set(Calendar.HOUR_OF_DAY, hour);
    notificationTime.set(Calendar.MINUTE, minute);
    notificationTime.set(Calendar.SECOND, 0);
    // Thoi gian bat dau cua ngay
    Calendar startDayTime = Calendar.getInstance();
    startDayTime.set(Calendar.HOUR_OF_DAY, 0);
    startDayTime.set(Calendar.MINUTE, 0);
    startDayTime.set(Calendar.SECOND, 0);


    // Nếu người dùng đã chụp ảnh trước thời gian thông báo, không hiển thị thông báo
    if (lastCaptureTime <= notificationTime.getTimeInMillis() && lastCaptureTime >= startDayTime.getTimeInMillis()) {
      Log.d("ReminderReceiver", "User has already captured an image today. Skipping notification.");
      return;
    }

    // Hiển thị thông báo nếu chưa chụp ảnh
    Log.d("ReminderReceiver", "Displaying notification.");
    sendNotification(context);
  }

  private void sendNotification(Context context) {
    String channelId = "DailySelfieChannel";
    Intent notificationIntent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
              channelId,
              "Daily Selfie Reminder",
              NotificationManager.IMPORTANCE_HIGH
      );
      notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("Daily Selfie Reminder")
            .setContentText("Don't forget to take your selfie today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

    notificationManager.notify(1001, builder.build());
  }


}
