package com.example.root.ik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.root.ik.common.Common;
import com.example.root.ik.interfaces.ItemClickListener;
import com.example.root.ik.model.Post;
import com.example.root.ik.viewHolder.HomeViewHolder;
import com.example.root.ik.viewHolder.MyAdsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class MyAdsActivity extends AppCompatActivity {

    private static final String TAG = "MyAdsActivity";
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database;
    DatabaseReference myAds, categoryAds, homeAds;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Post, MyAdsViewHolder> adapter;

    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAdsActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        init();

        initFirebase();

        loadData();

    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerview_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        profile = findViewById(R.id.view_profile);


    }

    private void loadData() {
        Query myQuery = homeAds.orderByChild("userid").equalTo(auth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(myQuery,Post.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Post, MyAdsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(MyAdsViewHolder holder, int position, Post model) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.image_processing);
                requestOptions.error(R.drawable.no_image);

                Glide.with(getBaseContext())
                        .applyDefaultRequestOptions(requestOptions)
                        .setDefaultRequestOptions(requestOptions)
                        .load(model.getImageurl())
                        .into(holder.imageView);

                Log.e(TAG, "onBindViewHolder: " + model.getImageurl() );

                holder.txtMenuName.setText(model.getTitle());
                holder.price.setText(model.getPrice());
                holder.location.setText(model.getLocation());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(MyAdsActivity.this, PostDetailActivity.class);
                        intent.putExtra("postid", adapter.getRef(position).getKey());
                        intent.putExtra(Common.FROM_ACTIVITY,Common.MY_UPLOADS_ACTIVITY);
                        startActivity(intent);
                    }
                });
            }


            @Override
            public MyAdsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item_my_ads, viewGroup, false);

                return new MyAdsViewHolder(itemView);
            }
        };


        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void initFirebase() {
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
        database = FirebaseDatabase.getInstance();
        myAds = database.getReference(Common.NODE_POST_ITEM).child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        categoryAds = database.getReference("Post Item");

        homeAds = database.getReference(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST);
    }

    //code when the food list is long clicked
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){

            //showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){

            startActivity(new Intent(MyAdsActivity.this,HomeActivity.class));
            deleteFood(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        myAds.child(key).removeValue();
        homeAds.child(key).removeValue();
    }

//    private void showUpdateFoodDialog(final String key, final Post item) {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle("Edit Category");
//        alertDialog.setMessage("Please fill in the full information");
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        View add_menu_layout = inflater.inflate(R.layout.add_new_foodlist,null);
//
//        edtName = add_menu_layout.findViewById(R.id.edtNameAddNewFood);
//        edtDescription = add_menu_layout.findViewById(R.id.edtDescAddNewFood);
//        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscountAddNewFood);
//        edtPrice = add_menu_layout.findViewById(R.id.edtPriceAddNewFood);
//
//
//        //set default value for food
//        edtName.setText(item.getName());
//        edtDiscount.setText(item.getDiscount());
//        edtDescription.setText(item.getDescription());
//        edtPrice.setText(item.getPrice());
//
//        selectBtn = add_menu_layout.findViewById(R.id.btn_select);
//        uploadBtn = add_menu_layout.findViewById(R.id.btn_upload);
//
//        alertDialog.setView(add_menu_layout);
//        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
//
//        //upload button
//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                changeImage(item);
//            }
//        });
//        //select button
//        selectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//
//
//        //set button
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                dialogInterface.dismiss();
//                //here we just create new ccategory
//                if ( newFood!= null){
//
//                    //update info
//                    item.setName(edtName.getText().toString());
//                    item.setDiscount(edtDiscount.getText().toString());
//                    item.setDescription(edtDescription.getText().toString());
//                    item.setPrice(edtPrice.getText().toString());
//
//
//                    foodList.child(key).setValue(item);
//
//                    Snackbar.make(rootLayout,"Food " + item.getName()+ " was edited"
//                            ,Snackbar.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                dialogInterface.dismiss();
//            }
//        });
//        alertDialog.show();
//    }


    public void viewProfile(View view) {

        startActivity(new Intent(MyAdsActivity.this, ProfileActivity.class));
    }

}
