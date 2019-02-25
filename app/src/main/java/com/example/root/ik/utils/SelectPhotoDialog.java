package com.example.root.ik.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.ik.R;
import com.example.root.ik.interfaces.OnPhotoSelectedListener;

public class SelectPhotoDialog extends DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 1234;
    private static final int CAMERA_REQUEST_CODE = 4321;

    OnPhotoSelectedListener onPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_selectphoto, container, false);

        TextView selectPhoto = view.findViewById(R.id.dialogChoosePhoto);
        TextView takePhoto = view.findViewById(R.id.dialogOpenCamera);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: accessing phone memory");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: starting camera");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // intent.setType("image/*");
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Results when selecting a new image from memory
         */
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri" + selectedImageUri);

            //TODO: send the uri to post fragment and dismiss dialog
            onPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }/**
         Results when taking a new photo with camera
         */
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking new photo");
            Bitmap bitmap;
            bitmap = (Bitmap)data.getExtras().get("data");
            //TODO: send the bitmap to PostFragment and dismiss dialog
            onPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPhotoSelectedListener = (OnPhotoSelectedListener)getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: Class CastException "+ e.getMessage() );
        }
    }


}
