package com.example.diemquatrinh1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

  private final List<ImageItem> imageItems;
  private final List<ImageItem> selectedImages = new ArrayList<>();

  public ImageAdapter(List<ImageItem> imageItems) {
    this.imageItems = imageItems;
  }

  @NonNull
  @Override
  public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
    return new ImageViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
    ImageItem imageItem = imageItems.get(position);
    holder.checkBox.setOnClickListener(v -> {
      if (holder.checkBox.isChecked()) {
        selectedImages.add(imageItem);
      } else {
        selectedImages.remove(imageItem);
      }
    });
    holder.bind(imageItem);
  }

  @Override
  public int getItemCount() {
    return imageItems.size();
  }

  public static class ImageViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView textDate;
    private final CheckBox checkBox;

    public ImageViewHolder(@NonNull View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.image_view);
      textDate = itemView.findViewById(R.id.text_date);
      checkBox = itemView.findViewById(R.id.check_box);
    }

    public void bind(ImageItem imageItem) {
      imageView.setImageURI(imageItem.getImageUri());
      textDate.setText(imageItem.getCreationDate());
    }
  }

  public void updateImages(List<ImageItem> newImageItems) {
    this.imageItems.clear();
    this.imageItems.addAll(newImageItems);
    notifyDataSetChanged();
  }

  public List<ImageItem> getSelectedImages() {
    return selectedImages;
  }

  public List<ImageItem> getAllImages() {
    return imageItems;
  }
}
