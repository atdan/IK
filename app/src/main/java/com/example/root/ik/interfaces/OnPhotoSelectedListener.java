package com.example.root.ik.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

public interface OnPhotoSelectedListener {
    void getImagePath(Uri imagePath);
    void getImageBitmap(Bitmap bitmap);
}
