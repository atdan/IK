package com.example.root.ik;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.ik.model.User;
import com.example.root.ik.utils.PhotoFullPopupWindow;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;

    TextView name, phoneNumber, email, address;

    private static final int REQUEST_CAMERA = 123;
    private static final int PICKFILE_REQUEST_CODE = 12;
    private static final String TAG = "EditProfileActivty";
    Button saveProfileButton;

    com.google.android.material.floatingactionbutton.FloatingActionButton changeProfileImage;
    EditText editName, editMobile, editAddress;
    FirebaseAuth auth;

    FirebaseUser firebaseUser;

    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference userDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    User user;

    UploadTask uploadTask;
    Uri imageHoldUri = null;

    ProgressDialog progressDialog;
    String nameS, mobileS, photoUrlS, addressS;

    private static final int IMAGE_REQUEST = 1;
    private Uri saveUri;
    //private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        initViews();
        initFirebase();
        loadProfile();

    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //check user presence
                FirebaseUser user = firebaseUser;

                if (user != null){
                    finish();

                    Intent moveToHome = new Intent(ProfileActivity.this, HomeActivity.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToHome);
                }
            }
        };

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        userDatabase.keepSynced(true);


            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    name.setText(user != null ? user.getName() : null);
                    email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    phoneNumber.setText(user != null ? user.getPhone() : null);
                    address.setText(user != null ? user.getHomeAddress() : null);


                    nameS = name.getText().toString();

                    mobileS = phoneNumber.getText().toString();

                    addressS = address.getText().toString();

                    changeProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profilePictureSelection();
                        }
                    });


                    //Uri imageUri = Uri.parse(user != null ? user.getImageurl() : null);
                    if (user != null){
                        String imageUri = user.getImageUrl();
                        if (imageUri == null || imageUri.equals("default")){
                            Glide.with(getBaseContext())
                                    .load(R.drawable.user_profile_pic)
                                    .into(profile_image);

                            photoUrlS = null;
                        }else {
                            Glide.with(getBaseContext())
                                    .load(Uri.parse(imageUri))
                                    .into(profile_image);

                            photoUrlS = imageUri.toString();
                        }


                    }else {
                        Toast.makeText(ProfileActivity.this, "No Profile Picture", Toast.LENGTH_SHORT).show();

                        Glide.with(getBaseContext())
                                .load(R.drawable.user_profile_pic)
                                .into(profile_image);

                        photoUrlS = null;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private void initViews() {
        profile_image = findViewById(R.id.profileImage);

        name = findViewById(R.id.profileName);

        phoneNumber = findViewById(R.id.phone_profile);

        email = findViewById(R.id.emailProfile);

        address = findViewById(R.id.address_profile);

        saveProfileButton = findViewById(R.id.editProfileButton);

        changeProfileImage = findViewById(R.id.change_profile_pic);


        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadProfilePicToFirebaseStorage();
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rename();

            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Profile Activity", "onClick: Phone Clicked" );
                changeMobile();
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAddress();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullImage(view);
            }
        });

    }

    private void loadProfile() {

    }

    private void profilePictureSelection() {

        final CharSequence[] items = {"Take Photos", "Choose from gallery", "Cancel" };


        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add photos");

        //set items and listeners
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Take Photos")){
                    cameraIntent();
                }else if (items[i].equals("Choose from gallery")){
                    galleryIntent();
                }else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    private void galleryIntent() {
        Log.d(TAG, "onClick: accessing phone memory");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);


    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }

    private void uploadProfilePicToFirebaseStorage(){
        if (saveUri != null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving profile");
            progressDialog.setMessage("please wait...");
            progressDialog.show();

            final StorageReference childRef = storageReference.child("Users")
                    .child(firebaseUser.getUid())
                    .child("profile picture")
                    .child(Objects.requireNonNull(imageHoldUri.getLastPathSegment()));

            uploadTask = childRef.putFile(saveUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }else {
                        return childRef.getDownloadUrl();
                    }

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        Toasty.success(getBaseContext(), "Upload Successful").show();
                        final Uri saveUriLoc = task.getResult();

                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);

                                if (user != null){
                                    user.setImageUrl(saveUriLoc.toString());

                                    userDatabase.setValue(user);
                                }else Toasty.error(getBaseContext(),"No user").show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{

                        Toasty.error(getBaseContext(),"Image upload failed").show();
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_REQUEST_CODE  && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            saveUri = imageUri;

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

            if (imageHoldUri != null){
                progressDialog.setTitle("Saving profile");
                progressDialog.setMessage("please wait...");
                progressDialog.show();
                StorageReference childRef = storageReference.child("Users").child(Objects.requireNonNull(imageHoldUri.getLastPathSegment()));

                String profilePicUrl = imageHoldUri.getLastPathSegment();

                childRef.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        //Uri downloadUrl = urlTask.getResult();
                        final  Uri imageUrl = urlTask.getResult();
                        saveUri = imageUrl;

                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);


                                if (user != null) {
                                    assert imageUrl != null;
                                    user.setImageUrl(imageUrl.toString());
                                }

                                userDatabase.setValue(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });






                        userDatabase.child("userId").setValue(auth.getCurrentUser().getUid());

                        //userDatabase.child("imageUrl").setValue(imageUrl.toString());



                        progressDialog.dismiss();

                    }
                });
            }
        }else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){

            assert data != null;
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

            if (imageHoldUri != null){
                progressDialog.setTitle("Saving profile");
                progressDialog.setMessage("please wait...");
                progressDialog.show();
                StorageReference childRef = storageReference.child("User Profile").child(Objects.requireNonNull(imageHoldUri.getLastPathSegment()));

                String profilePicUrl = imageHoldUri.getLastPathSegment();

                childRef.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        //Uri downloadUrl = urlTask.getResult();
                        final  Uri imageUrl = urlTask.getResult();

                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);


                                if (user != null) {
                                    assert imageUrl != null;
                                    user.setImageUrl(imageUrl.toString());
                                }

                                userDatabase.setValue(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });






                        userDatabase.child("userId").setValue(auth.getCurrentUser().getUid());

                        //userDatabase.child("imageUrl").setValue(imageUrl.toString());



                        progressDialog.dismiss();

                    }
                });
            }

        }

        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                assert result != null;
                imageHoldUri = result.getUri();

                profile_image.setImageURI(imageHoldUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result != null ? result.getError() : null;
            }
        }
    }


    private void changeAddress() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Set Address");
        alertDialog.setMessage("Enter your Address");

        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.change_address,null);

        editAddress = add_menu_layout.findViewById(R.id.change_address);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_home_black_48dp);

        editAddress.setText(addressS);

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = new User();
                        user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            user.setHomeAddress(editAddress.getText().toString());
                        }

                        userDatabase.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();
    }

    public void showFullImage(View view){

        if (photoUrlS != null){
            new PhotoFullPopupWindow(this, R.layout.popup_photo_full, view, photoUrlS, null);
        }else {
            new PhotoFullPopupWindow(this, R.layout.popup_photo_full, view, photoUrlS, null);
        }


    }


    public void rename(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Set Name");
        alertDialog.setMessage("Enter your Name");

        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.change_name,null);

        editName = add_menu_layout.findViewById(R.id.change_name);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_perm_identity_black_48dp);

        editName.setText(nameS);

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            user.setName(editName.getText().toString());
                        }

                        userDatabase.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();
    }

    public void changeMobile(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Set Mobile Number");
        alertDialog.setMessage("Enter your Phone Number");

        LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.change_mobile,null);

        editMobile = add_menu_layout.findViewById(R.id.change_mobile);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_phone_android_black_24dp);

        if (mobileS != null){
            editMobile.setText(mobileS);
        }


        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        try {
                            if (user != null) {
                                user.setPhone(editMobile.getText().toString());
                            }
                        }catch (Exception e){

                        }


                        userDatabase.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        dialogInterface.dismiss();
                    }
                });

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}
