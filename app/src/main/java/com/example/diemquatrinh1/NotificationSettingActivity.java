package com.example.diemquatrinh1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NotificationSettingActivity extends AppCompatActivity {

  private TimePicker timePicker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification_setting);

    timePicker = findViewById(R.id.time_picker);
    Button saveButton = findViewById(R.id.btn_save);

    saveButton.setOnClickListener(v -> {
      int hour = timePicker.getHour();
      int minute = timePicker.getMinute();

      // Lưu thời gian vào SharedPreferences
      saveReminderTime(hour, minute);

      // Đặt thông báo nhắc nhở
      setDailyReminder(hour, minute);

      finish();
    });
  }

  private void saveReminderTime(int hour, int minute) {
    getSharedPreferences("DailySelfieApp", MODE_PRIVATE)
            .edit()
            .putInt("hour", hour)
            .putInt("minute", minute)
            .apply();
  }

  @SuppressLint("ScheduleExactAlarm")
  private void setDailyReminder(int hour, int minute) {
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(this, ReminderReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);

    if (calendar.before(Calendar.getInstance())) {
      // Nếu thời gian đã qua trong ngày, đặt thông báo cho ngày hôm sau
      calendar.add(Calendar.DATE, 1);
    }

//    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    // Sử dụng setExactAndAllowWhileIdle để đảm bảo thông báo lặp lại hàng ngày
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    Log.d("NotificationSettingActivity", "Daily reminder set for " + hour + ":" + minute);
    Toast.makeText(this, "Daily reminder set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
  }
}

