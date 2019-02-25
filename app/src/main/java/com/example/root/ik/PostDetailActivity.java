package com.example.root.ik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.ik.common.Common;
import com.example.root.ik.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    ImageView profileImage, myUploadsImage, postImage;

    TextView price, title, location, description, posterName, posterPhoneNumber;

    CircleImageView posterProfileImage;

    String postId = "", categoryString = "";

    Post current_post;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database;
    DatabaseReference postrefHome, postRefMyAds, postRefCategory;

    FloatingActionButton chatMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);



        initFirebase();

        posterProfileImage = findViewById(R.id.profileImage);

        profileImage = findViewById(R.id.view_profile);

        myUploadsImage = findViewById(R.id.view_cart);

        postImage = findViewById(R.id.postImage);
        price = findViewById(R.id.price);
        title = findViewById(R.id.title);
        location = findViewById(R.id.location);
        description = findViewById(R.id.description);
        posterName = findViewById(R.id.name);
        posterPhoneNumber = findViewById(R.id.phoneNumber);




        //get food id
        if (getIntent() != null){
            postId = getIntent().getStringExtra("postid");
            categoryString = getIntent().getStringExtra("categoryString");
        }


        if (!postId.isEmpty() && postId != null) {
            if (Common.isConnectedToInternet(this)) {
                switch (getIntent().getStringExtra(Common.FROM_ACTIVITY)) {
                    case Common.HOME_ACTIVITY:
                        getDetailFoodHome(postId);
                        break;
                    case Common.MY_UPLOADS_ACTIVITY:

                        getDetailFoodMyUploads(postId);
                        break;
                    case Common.CATEGORIES_ACTIVITY:

                        getDetailFoodCategories(postId);
                        break;
                }


            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

        }

    }

    private void getDetailFoodCategories(String postid) {
        postRefCategory.child(categoryString).child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_post = dataSnapshot.getValue(Post.class);
                price.setText(current_post != null ? current_post.getPrice() : "0");
                title.setText(current_post.getTitle());
                location.setText(current_post.getLocation());
                description.setText(current_post.getDescription());
                posterName.setText(current_post.getName());
                posterPhoneNumber.setText(current_post.getPhone());

                Picasso.with(getBaseContext())
                        .load(current_post.getImageurl())
                        .into(postImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFoodMyUploads(String postId) {
        postRefMyAds.child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_post = dataSnapshot.getValue(Post.class);

                try {
                    price.setText(current_post != null ? current_post.getPrice() : "0");
                    title.setText(current_post.getTitle());
                    location.setText(current_post.getLocation());
                    description.setText(current_post.getDescription());
                    posterName.setText(current_post.getName());
                    posterPhoneNumber.setText(current_post.getPhone());

                    Picasso.with(getBaseContext())
                            .load(current_post.getImageurl())
                            .into(postImage);

                }catch (NullPointerException e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFoodHome(String postId) {


        postrefHome.child("Raw Post").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_post = dataSnapshot.getValue(Post.class);

                price.setText(current_post != null ? current_post.getPrice() : "0");
                title.setText(current_post.getTitle());
                location.setText(current_post.getLocation());
                description.setText(current_post.getDescription());
                posterName.setText(current_post.getName());
                posterPhoneNumber.setText(current_post.getPhone());

                Picasso.with(getBaseContext())
                        .load(current_post.getImageurl())
                        .into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //check user presence
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    finish();

                }
            }
        };
        //init Firebase
        database = FirebaseDatabase.getInstance();
        postrefHome = database.getReference("Post Item");
        postRefMyAds = database.getReference(Common.NODE_POST_ITEM);
        postRefCategory = database.getReference("Post Item");
    }

    public void viewProfile(View view) {
        startActivity(new Intent(PostDetailActivity.this, ProfileActivity.class));
    }

    public void viewYourUploads(View view) {
        startActivity(new Intent(PostDetailActivity.this, MyAdsActivity.class));
    }
}
