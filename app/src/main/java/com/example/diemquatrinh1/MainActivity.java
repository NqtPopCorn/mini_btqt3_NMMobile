package com.example.diemquatrinh1;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.Manifest;


public class MainActivity extends AppCompatActivity {

  private Uri imageUri;
  private ImageAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //cap quyen nhan thong bao
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
      }
    }

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    File imageFolder = new File(getFilesDir(), "DailySelfies");
    if (!imageFolder.exists()) {
      imageFolder.mkdir();
    }

    ImageButton captureButton = findViewById(R.id.capture_button);
    captureButton.setOnClickListener(v -> {
      startCameraIntent();
    });

    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    List<ImageItem> imageItems = loadImagesFromFolder();
    adapter = new ImageAdapter(imageItems);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    checkAndRequestExactAlarmPermission();
  }

  private void checkAndRequestExactAlarmPermission() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

      if (!alarmManager.canScheduleExactAlarms()) {
        Toast.makeText(this, "Exact Alarm Permission Required", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivity(intent);
      }
    }
  }

  private void startCameraIntent() {
    File imageFolder = new File(getFilesDir(), "DailySelfies");
    if (!imageFolder.exists()) {
      imageFolder.mkdir();
    }
    String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
    File imageFile = new File(imageFolder, filename);
    Uri imageUri = FileProvider.getUriForFile(this, "com.example.diemquatrinh1.fileprovider", imageFile);
    takePictureLauncher.launch(imageUri);
  }

  private void showContinueDialog() {
    new AlertDialog.Builder(this)
            .setTitle("Continue?")
            .setMessage("Do you want to capture another photo?")
            .setPositiveButton("Yes", (dialog, which) -> {
              // Tiếp tục chụp ảnh
              startCameraIntent();
            })
            .setNegativeButton("No", (dialog, which) -> {
              dialog.dismiss();
            })
            .show();
  }

  private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
          new ActivityResultContracts.TakePicture(),
          result -> {
            if (result) {
              saveLastCaptureTime();
              Toast.makeText(this, "Image captured!", Toast.LENGTH_SHORT).show();
              adapter.updateImages(loadImagesFromFolder());
              showContinueDialog();
            } else {
              Toast.makeText(this, "Capture cancelled", Toast.LENGTH_SHORT).show();
            }
          }
  );

  private List<ImageItem> loadImagesFromFolder() {
    List<ImageItem> imageItems = new ArrayList<>();
    File imageFolder = new File(getFilesDir(), "DailySelfies");

    if (imageFolder.exists() && imageFolder.isDirectory()) {
      for (File file : imageFolder.listFiles()) {
        Uri imageUri = Uri.fromFile(file);
        String creationDate = new SimpleDateFormat("dd MMM. yyyy HH:mm:ss", Locale.getDefault())
                .format(new Date(file.lastModified()));

        imageItems.add(new ImageItem(imageUri, creationDate));
      }
    }
    return imageItems;
  }

  private void saveLastCaptureTime() {
    long currentTime = System.currentTimeMillis();
    getSharedPreferences("DailySelfieApp", MODE_PRIVATE)
            .edit()
            .putLong("lastCaptureTime", currentTime)
            .apply();
  }


  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_options, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if(item.getItemId() == R.id.action_delete) {
      List<ImageItem> selectedImages = adapter.getSelectedImages();
      for (ImageItem imageItem : selectedImages) {
        File imageFile = new File(new File(getFilesDir(), "DailySelfies"), imageItem.getImageUri().getLastPathSegment());
        if (imageFile.exists()) {
          imageFile.delete();
        }
      }
      selectedImages.clear();
      adapter.updateImages(loadImagesFromFolder());
    } else if(item.getItemId() == R.id.action_deleteAll) {
      File imageFolder = new File(getFilesDir(), "DailySelfies");
      if (imageFolder.exists()) {
        for (File file : imageFolder.listFiles()) {
          file.delete();
        }
      }
      adapter.updateImages(loadImagesFromFolder());
    } else if(item.getItemId() == R.id.action_notification) {
      Intent intent = new Intent(this, NotificationSettingActivity.class);
      startActivity(intent);
    }

    return super.onOptionsItemSelected(item);
  }


}

