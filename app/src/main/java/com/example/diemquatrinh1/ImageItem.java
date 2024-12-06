package com.example.diemquatrinh1;

import android.net.Uri;

public class ImageItem {
  private final Uri imageUri;
  private final String creationDate;

  public ImageItem(Uri imageUri, String creationDate) {
    this.imageUri = imageUri;
    this.creationDate = creationDate;
  }

  public Uri getImageUri() {
    return imageUri;
  }

  public String getCreationDate() {
    return creationDate;
  }
}

