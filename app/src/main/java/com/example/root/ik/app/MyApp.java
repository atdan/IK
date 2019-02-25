package com.example.root.ik.app;

import android.app.Application;

import com.example.root.ik.image_slider_service.PImageLoadingService;
import com.example.root.ik.image_slider_service.PicassoImageLoadingService;
import com.google.firebase.database.FirebaseDatabase;

import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //PicassoImageLoadingService picassoImageLoadingService = new PicassoImageLoadingService(this);
        Slider.init(new PicassoImageLoadingService(this));

        Slider.init(new PImageLoadingService(this));
    }
}
