package com.example.root.ik;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.root.ik.model.Post;
import com.example.root.ik.utils.Constants;
import com.example.root.ik.utils.Params;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.ik.common.Common;
import com.example.root.ik.utils.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PostsActivity extends AppCompatActivity {

    private static final String TAG = "PostsActivity";

    TextInputEditText title, price, name, phoneNumber, description, location;

    MaterialSpinner categorySpinner;

    TextView numberOfImagesChosen;
    int pos = 0;

    SharedPreferences mSettings;
    FirebaseUser user;

    String uid;
    Button submitButton, addImageButton;

    Uri saveUri = null;

    ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference categories, postRef, rawPostRef;
    FirebaseStorage storage;
    StorageReference storageReference;

    public Slider postSlider;
    Post newPost;

    ArrayList<String> mArrayUri = new ArrayList<String>();

    Uri[] imagesArray = new Uri[5];

    String mobileN = "", titleS = "", descriptionS = "", nameS = "", priceS = "",locationS = "", categoryS;

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final int REQUEST_CAMERA = 10;

    public static final int SELECT_FILE = 12;
    Uri downloadUri;
    UploadTask uploadTask;
    private Bitmap mBitmap;
    private byte[] mByteArray;
    private List<String> mPhotos;

    private DatabaseReference mFirebaseDatabase;
    private StorageReference mFirebaseStorage;
    private StorageTask mUploadTask;
    private ArrayList<String> uploadedImagesUri = new ArrayList<>();

    ProgressDialog mDialog;

    String[] categoriesArray = {Common.MOBILE_CATEGORY, Common.FURNITURE_CATEGORY,
            Common.COMPUTER_CATEGORY, Common.VEHICLE_CATEGORY,
            Common.JOBS_CATEGORY, Common.FASHION_CATEGORY, Common.CHILDREN_CATEGORY,
            Common.FOOTWEAR_CATEGORY, Common.MUSIC_CATEGORY};

    String formattedDate = "";
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList = new ArrayList<>();

    String userId, postId, imageUrl1, imageUrl2, imageUrl3, imageUrl4, imageUrl5;

    ImageView addImage;

    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Advert");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + e.getMessage());
        }


        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance().getReference();

        //init Firebase
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        postRef = database.getReference("Post Item");
        rawPostRef = database.getReference("Post Item");

        user = FirebaseAuth.getInstance().getCurrentUser();
//        uploadImage = findViewById(R.id.add_photo);


        initViews();

        init();

        showSlider();

    }

    private void initViews() {
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        phoneNumber = findViewById(R.id.mobile_number);
        submitButton = findViewById(R.id.submit);
        location = findViewById(R.id.location);
        //numberOfImagesChosen = findViewById(R.id.txtNumberOfImagesChosen);

        database = FirebaseDatabase.getInstance();

        addImageButton = findViewById(R.id.add_image);

        mDialog = new ProgressDialog(this);

        categorySpinner = findViewById(R.id.categorySpinner);

        categorySpinner.setItems(categoriesArray);

        addImage = findViewById(R.id.add_image_img);

        //postSlider = findViewById(R.id.images_slider_post);
        //postSlider.setAdapter(new PostSliderAdapter());

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mSettings = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
        String username = mSettings.getString("name", "");
        String mobileNumber = mSettings.getString("mobile", "");
        String userLocation = mSettings.getString("location","");
        name.setText(username);
        phoneNumber.setText(mobileNumber);
        location.setText(userLocation);
        if (user != null) {
            String userEmail = user.getEmail();

            uid = user.getUid();


        }
        if (!Utility.isNetworkAvailable(this)) {

            Toast.makeText(this,
                    "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSlider() {
        class PostSliderAdapter extends SliderAdapter {

            @Override
            public int getItemCount() {


                //Toast.makeText(PostsActivity.this, imagesEncodedList.size() +" images added", Toast.LENGTH_SHORT).show();

                if (mArrayUri.isEmpty()) {

                    return 1;

                } else {

                    return mArrayUri.size();

                }
            }

            @Override
            public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {

                if (!mArrayUri.isEmpty()) {
                    if (mArrayUri.size() == 5) {
                        Log.d(TAG, "onBindImageSlide: gotten 5 images");
                        switch (position) {
                            case 0:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                                break;
                            case 1:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                                break;
                            case 2:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                                break;
                            case 3:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(3)));
                                break;
                            case 4:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(4)));
                                break;
                        }
                    }
                    if (mArrayUri.size() == 4) {
                        Log.d(TAG, "onBindImageSlide: gotten 4 images");
                        switch (position) {
                            case 0:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                                break;
                            case 1:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                                break;
                            case 2:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                                break;
                            case 3:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(3)));
                                break;
                        }
                    }
                    if (mArrayUri.size() == 3) {
                        Log.d(TAG, "onBindImageSlide: gotten 3 images");
                        switch (position) {
                            case 0:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                                break;
                            case 1:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                                break;
                            case 2:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                                break;
                        }
                    }
                    if (mArrayUri.size() == 2) {
                        Log.d(TAG, "onBindImageSlide: gotten 2 images");
                        switch (position) {
                            case 0:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                                break;
                            case 1:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                                break;
                        }
                    }
                    if (mArrayUri.size() == 1) {
                        Log.d(TAG, "onBindImageSlide: gotten 1 image");
                        switch (position) {
                            case 0:
                                viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                                break;
                        }


                    }
                }


                if (mArrayUri.isEmpty()) {

                    Log.e(TAG, "onBindImageSlide: it thinks no image in array list");
                    switch (position) {
                        case 0:
                            Toast.makeText(PostsActivity.this, "no image yet", Toast.LENGTH_SHORT).show();
                            viewHolder.bindImageSlide(R.drawable.no_image);
                    }

                }
            }
            //Toast.makeText(PostsActivity.this, "images added", Toast.LENGTH_SHORT).show();
        }

        //postSlider.setAdapter(new PostSliderAdapter());
    }


    public boolean validate() {
        boolean valid = true;

        mobileN = phoneNumber.getText().toString();
        titleS = title.getText().toString();
        descriptionS = description.getText().toString();
        nameS = name.getText().toString();
        priceS = price.getText().toString();
        locationS = location.getText().toString();


        if (mobileN.isEmpty() || mobileN.length() != 11) {
            phoneNumber.setError("Please enter valid number");
            valid = false;
        } else {
            phoneNumber.setError(null);
        }


        if (titleS.isEmpty() || titleS.length() < 5) {

            title.setError("Please enter title not less than 5 letters");
            valid = false;
        } else {
            title.setError(null);

        }

        if (descriptionS.isEmpty() || descriptionS.length() < 10) {

            description.setError("Please enter description not less than 10 letters");
            valid = false;
        } else {
            description.setError(null);

        }

        if (nameS.isEmpty()) {

            name.setError("Please enter your name");
            valid = false;
        } else {
            name.setError(null);

        }

        if (priceS.isEmpty()) {

            price.setError("Please enter price");
            valid = false;
        } else {
            price.setError(null);

        }
        if (locationS.isEmpty()){
            location.setError("Please enter your location");
            valid = false;
        }else {
            location.setError(null);
        }


        return valid;
    }


    private void init() {


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    titleS = title.getText().toString();
                    descriptionS = description.getText().toString();
                    mobileN = phoneNumber.getText().toString();
                    categoryS = categoriesArray[categorySpinner.getSelectedIndex()];
                    priceS = price.getText().toString();
                    nameS = name.getText().toString();

                    Log.d(TAG, "onClick: submit button clicked");
                    uploadImage();
                    resetFields();
                }

            }
        });


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mArrayUri.isEmpty()) {
                    mArrayUri.clear();
                }
                chooseImage();

            }
        });
    }

//    private void uploadImage() {
//        if (validate()) {
//            Log.d(TAG, "uploadImage: validated all input");
//            if (!mArrayUri.isEmpty()) {
//
//
//                Log.e(TAG, "uploadImage: image in array");
//
//                final int count = mArrayUri.size();
//
//                if (mDialog == null) {
//                    mDialog = new ProgressDialog(this);
//                    mDialog.setTitle("Please wait");
//                    mDialog.show();
//                }
//
//
//                for (int i   = 1; i < count; i++) {
//                    final int j = i;
//
//                    if (mDialog != null) {
//
//                        mDialog.setMessage("Uploading " + String.valueOf(i) + "/" + count);
//                    }
//
//
//                    String imageName = UUID.randomUUID().toString(), postNumber = UUID.randomUUID().toString();
//                    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    final StorageReference imageFolder = storageReference.child("images/" +
//                            currentUser + "/" + imageName);
//                    String uri = mArrayUri.get(i);
//
//
//                    uploadTask = imageFolder.putFile(Uri.parse(uri));
//// .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
////                        @Override
////                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
////                            if (!task.isSuccessful()) {
////                                throw Objects.requireNonNull(task.getException());
////                            }
////                            return imageFolder.getDownloadUrl();
////                        }
////                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Uri> task) {
////
////                            if (task.isSuccessful()){
////                                Uri downloadUri = task.getResult();
////                                if (downloadUri != null) {
////                                    uploadedImagesUri.add(downloadUri.toString());
////                                }
////                            }
////                        }
////                    });
//                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                        @Override
//                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                            if (!task.isSuccessful()) {
//                                throw Objects.requireNonNull(task.getException());
//                            }
//                            return imageFolder.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                Uri downloadUri = task.getResult();
//
//                                database.getReference(Common.NODE_POST_ITEM).child(user.getUid()).child("imageUrl"+j)
//                                .push().setValue(downloadUri);
//                                mDialog.dismiss();
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(PostsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            mDialog.dismiss();
//                        }
//                    });
//
//
//                }
//
//
//                Log.e(TAG, "uploadImage: images array size  = " + uploadedImagesUri.size());
//
//
//                Log.e(TAG, "uploadImage: after the for loop");
//                Date c = Calendar.getInstance().getTime();
//
//
//                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//
//                formattedDate = df.format(c);
//                categories = database.getReference(Common.NODE_POST_ITEM).child(categoriesArray[categorySpinner.getSelectedIndex()]);
//                postRef = database.getReference(Common.NODE_POST_ITEM).child(user.getUid());
//                String key = categories.push().getKey();
//
//
//                userId = user.getUid();
//
//
//                Post post = new Post(titleS, priceS, descriptionS, categoryS, nameS, mobileN,
//                        userId, formattedDate, imageUrl1, imageUrl2,
//                        imageUrl3, imageUrl4, imageUrl5);
//
//                Map<String, Object> postValues = post.toMap();
//
//                Map<String, Object> childValues = new HashMap<>();
//
//                assert key != null;
//                childValues.put(key, postValues);
//
//                categories.updateChildren(childValues);
//
//                startActivity(new Intent(PostsActivity.this, HomeActivity.class));
//                finish();
//                //postRef.updateChildren(childValues);
//
//
//            } else {
//                Toast.makeText(this, "Please select at least one picture", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("please wait");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString(), postNumber = UUID.randomUUID().toString();
            String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            final StorageReference ref = storageReference.child("images/" +
                    currentUser + "/user posts/" + imageName+postNumber);

            uploadTask = ref.putFile(filePath);


            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult();

                               // database.getReference(Common.NODE_POST_ITEM).child(user.getUid()).child("imageUrl")
//                                        .push().setValue(downloadUri);

                                Date c = Calendar.getInstance().getTime();


                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                formattedDate = df.format(c);
                                categories = database.getReference(Common.NODE_POST_ITEM).child(categoriesArray[categorySpinner.getSelectedIndex()]);
                                postRef = database.getReference(Common.NODE_POST_ITEM).child(user.getUid());
                                rawPostRef = database.getReference(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST);
                                String key = categories.push().getKey();
                                String keyP = postRef.push().getKey();
                                String keyR = rawPostRef.push().getKey();


                                userId = user.getUid();


                                Post post = new Post(titleS, priceS, descriptionS, categoryS, nameS, mobileN,
                                        userId, formattedDate,locationS , downloadUri.toString());

                                Map<String, Object> postValues = post.toMap();
                                Map<String, Object> postValues2 = post.toMap();
                                Map<String, Object> postValues3 = post.toMap();


                                Map<String, Object> childValues = new HashMap<>();
                                Map<String, Object> childValues2 = new HashMap<>();
                                Map<String, Object> childValues3 = new HashMap<>();

                                assert key != null;
                                childValues.put(key, postValues);
                                assert keyP != null;
                                childValues2.put(keyP,postValues2);
                                assert keyR != null;
                                childValues3.put(keyR,postValues3);

                                categories.updateChildren(childValues);
                                postRef.updateChildren(childValues2);
                                rawPostRef.updateChildren(childValues3);

                                startActivity(new Intent(PostsActivity.this, HomeActivity.class));
                                finish();

                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            HashMap<String ,String> hashMap = new HashMap<>();
//
//                            Uri imageLocationUri = taskSnapshot.getTask().getResult().ge
//                            hashMap.put()
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//
//                            Toast.makeText(PostsActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//
//                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
//
//                        }
//                    });

        }
    }

//    private void uploadImage() {
//        if (saveUri != null) {
//
//            if (validate()) {
//                final ProgressDialog mDialog = new ProgressDialog(this);
//                mDialog.setTitle("Please wait");
//                mDialog.setMessage("Uploading...");
//                mDialog.show();
//
//                String imageName = UUID.randomUUID().toString();
//                final StorageReference imageFolder = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + imageName);
////                imageFolder.putFile(saveUri)
////                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                            @Override
////                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                                mDialog.dismiss();
//////                                Toast.makeText(PostsActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
////
////                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                                    @Override
////                                    public void onSuccess(final Uri uri) {
////                                        //set value for new Category
////
//////                                        Date c = Calendar.getInstance().getTime();
//////                                        //System.out.println("Current time => " + c);
////
////                                        //Toast.makeText(PostsActivity.this, "Gotten download url" + uri.toString(), Toast.LENGTH_SHORT).show();
////                                        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
//////                                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//////                                        String formattedDate = df.format(c);
////
////                                        categories = database.getReference("Category").child(String.valueOf(categorySpinner.getSelectedIndex()));
////
////                                        categories.addValueEventListener(new ValueEventListener() {
////                                            @Override
////                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////
////                                                try {
////                                                    Post item = dataSnapshot.getValue(Post.class);
////                                                    item.setPrice(priceS);
////                                                    Log.d(TAG, "onDataChange: " + priceS);
////                                                    item.setTitle(titleS);
////                                                    item.setDescription(descriptionS);
////                                                    item.setCategory(String.valueOf(categorySpinner.getSelectedIndex()));
////                                                    item.setName(nameS);
////                                                    item.setPhone(mobileN);
////                                                    item.setUserid(user.getUid());
////                                                    item.setImageurl(uri.toString());
////                                                    item.setPostid(dateFormat.toString());
////
////                                                    categories.setValue(item);
////                                                    Toast.makeText(getBaseContext(), "Your post was uploaded successfully", Toast.LENGTH_SHORT).show();
////                                                    resetFields();
////
////                                                } catch (Exception e) {
////                                                    Log.e(TAG, "onDataChange: " + e.getMessage(), e);
////                                                }
////
////                                            }
////
////                                            @Override
////                                            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                            }
////                                        });
////
////
////                                        startActivity(new Intent(PostsActivity.this, HomeActivity.class));
////
////                                    }
////                                });
////                            }
////                        }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        mDialog.dismiss();
////                        Toast.makeText(PostsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
////                    }
////                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
////                    @Override
////                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                        double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
////                        mDialog.setMessage("Uploading... ");
////                    }
////                });
//
//
//                //Toast.makeText(this, "new item not null", Toast.LENGTH_SHORT).show();
//                //categories.child(String.valueOf(categorySpinner.getSelectedIndex())).push().setValue(newPost);
//
//
//            }
//
//        } else {
//            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                if (data.getData() != null) {

                    filePath = data.getData();

                    saveUri = filePath;

                    CropImage.activity(filePath)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(this);

//                    mArrayUri.add(mImageUri);
//
//
//                    numberOfImagesChosen.setText("1");
                } else {
//                    if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//
//                        int count = mClipData.getItemCount();
//                        if (count > 5) {
//                            Toast.makeText(this, "Can't select more than 5 pictures", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        for (int i = 0; i < count; i++) {
//
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            String uri = item.getUri().toString();
//                            mArrayUri.add(uri);
//
//                        }
//                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
//                        if (!mArrayUri.isEmpty()) {
//                            numberOfImagesChosen.setText(String.valueOf(mArrayUri.size()));
//                        }
//                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                saveUri = result.getUri();
                addImage.setImageURI(saveUri);
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                assert result != null;
                Log.e(TAG, "onActivityResult: "+ result.getError() );
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        showSlider();
    }

    private void resetFields() {

        name.setText("");
        description.setText("");
        price.setText("");
        title.setText("");
        phoneNumber.setText("");
        location.setText("");
    }


    private boolean isEmpty(String s) {
        return s.equals("");
    }


    class PostSliderAdapter extends SliderAdapter {

        @Override
        public int getItemCount() {


            //Toast.makeText(PostsActivity.this, imagesEncodedList.size() +" images added", Toast.LENGTH_SHORT).show();

            if (mArrayUri.isEmpty()) {

                return 1;

            } else {

                return mArrayUri.size();

            }
        }

        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {

            if (!mArrayUri.isEmpty()) {
                if (mArrayUri.size() == 5) {
                    Log.d(TAG, "onBindImageSlide: gotten 5 images");
                    switch (position) {
                        case 0:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                            break;
                        case 1:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                            break;
                        case 2:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                            break;
                        case 3:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(3)));
                            break;
                        case 4:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(4)));
                            break;
                    }
                }
                if (mArrayUri.size() == 4) {
                    Log.d(TAG, "onBindImageSlide: gotten 4 images");
                    switch (position) {
                        case 0:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                            break;
                        case 1:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                            break;
                        case 2:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                            break;
                        case 3:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(3)));
                            break;
                    }
                }
                if (mArrayUri.size() == 3) {
                    Log.d(TAG, "onBindImageSlide: gotten 3 images");
                    switch (position) {
                        case 0:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                            break;
                        case 1:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                            break;
                        case 2:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(2)));
                            break;
                    }
                }
                if (mArrayUri.size() == 2) {
                    Log.d(TAG, "onBindImageSlide: gotten 2 images");
                    switch (position) {
                        case 0:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                            break;
                        case 1:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(1)));
                            break;
                    }
                }
                if (mArrayUri.size() == 1) {
                    Log.d(TAG, "onBindImageSlide: gotten 1 image");
                    switch (position) {
                        case 0:
                            viewHolder.bindImageSlide(String.valueOf(mArrayUri.get(0)));
                            break;
                    }


                }
            }


            if (mArrayUri.isEmpty()) {

                Log.e(TAG, "onBindImageSlide: it thinks no image in array list");
                switch (position) {
                    case 0:
                        Toast.makeText(PostsActivity.this, "no image yet", Toast.LENGTH_SHORT).show();
                        viewHolder.bindImageSlide(R.drawable.no_image);
                }

            }
        }


        //Toast.makeText(PostsActivity.this, "images added", Toast.LENGTH_SHORT).show();
    }


}
