package com.example.root.ik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.root.ik.common.Common;
import com.example.root.ik.interfaces.ItemClickListener;
import com.example.root.ik.model.Category;
import com.example.root.ik.model.Post;
import com.example.root.ik.viewHolder.HomeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    ImageView searchImage, profileImage;

    boolean searchbarB = false;

    TextView categoryText;

    String categoryString = "";

    MaterialSearchBar searchBar;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference food_list, homeAds;


    FirebaseRecyclerAdapter<Post, HomeViewHolder> adapter;


    //Searchbar functionality
    FirebaseRecyclerAdapter<Post, HomeViewHolder> searchAdapter;

    List<String> suggestList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, CategoriesActivity.class);

                startActivity(intent);
            }
        });

        init();

        if (getIntent() != null) {
            categoryString = getIntent().getStringExtra("category");

        }
        if (!categoryString.isEmpty() && categoryString != null) {
            categoryText.setText(categoryString);

            if (Common.isConnectedToInternet(getBaseContext())) {
                loadCategoryList(categoryString);
            } else
                Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchbarB) {
                    searchBar.setVisibility(View.GONE);
                    searchbarB = true;
                } else if (searchbarB) {
                    searchBar.setVisibility(View.VISIBLE);
                    searchbarB = false;
                }
            }
        });


        setUpSwipeRefreshLayout();

        setUpSearchBar();

        loadSuggest();

        loadCategoryList(categoryString);


    }

    private void checkAndDelete(final String key) {

        homeAds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.child(key).exists()){

                        food_list.child(categoryString).child(key).removeValue();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void setUpSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //get posn
                if (getIntent() != null) {
                    categoryString = getIntent().getStringExtra("category");

                }
                if (!categoryString.isEmpty() && categoryString != null) {

                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadCategoryList(categoryString);
                    } else
                        Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //get posn
                if (getIntent() != null) {
                    categoryString = getIntent().getStringExtra("category");

                }
                if (!categoryString.isEmpty() && categoryString != null) {

                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadCategoryList(categoryString);
                    } else
                        Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });

    }

    private void init() {

        //init firebase
        database = FirebaseDatabase.getInstance();

        food_list = database.getReference("Post Item");

        homeAds = database.getReference("Post Item").child(Common.NODE_RAW_POST);

        searchImage = findViewById(R.id.search);

        profileImage = findViewById(R.id.profileImage);

        searchBar = findViewById(R.id.searchBar);

        categoryText = findViewById(R.id.category_text);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView = findViewById(R.id.recycler_category_list);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


    }

    private void startSearch(CharSequence text) {
        Query searchByTitle = food_list.child(categoryString).orderByChild("Title").equalTo(text.toString());

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByTitle, Post.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(HomeViewHolder viewHolder, int position, Post model) {
                viewHolder.txtMenuName.setText(model.getTitle());
                viewHolder.location.setText(model.getLocation());
                viewHolder.price.setText(model.getPrice());

                Picasso.with(getBaseContext()).load(model.getImageurl())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(CategoryActivity.this, PostDetailActivity.class);
                        intent.putExtra("postid", searchAdapter.getRef(position).getKey());
                        intent.putExtra(Common.FROM_ACTIVITY, Common.CATEGORIES_ACTIVITY);
                        intent.putExtra("categoryString", categoryString);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public HomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item, viewGroup, false);

                return new HomeViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void setUpSearchBar() {
        searchBar.setHint("Enter what you are searching for");
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);

        loadCategoryList(categoryString);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //when user types a text we will change the suggest list
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);

                    }
                    searchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // when searchbar is closed
                //restore original adapter

                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //when search is finished
                //show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void loadSuggest() {

        food_list.child(categoryString).orderByChild("postid")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Post item = postSnapshot.getValue(Post.class);
                            if (item != null) {
                                suggestList.add(item.getTitle());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadCategoryList(final String categoryString) {

        Query searchByTitle = homeAds.orderByChild("category").equalTo(categoryString);

        FirebaseRecyclerOptions<Post> foodOptions = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByTitle, Post.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Post, HomeViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder viewHolder, int position, Post model) {


//                checkAndDelete(adapter.getRef(position).getKey());


                viewHolder.price.setText(model.getPrice());
                viewHolder.location.setText(model.getLocation());
                viewHolder.txtMenuName.setText(model.getTitle());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.image_processing);
                requestOptions.error(R.drawable.no_image);


                Glide.with(getBaseContext())
                        .applyDefaultRequestOptions(requestOptions)
                        .setDefaultRequestOptions(requestOptions)
                        .load(model.getImageurl())
                        .into(viewHolder.imageView);


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(CategoryActivity.this, PostDetailActivity.class);
                        intent.putExtra("postid", adapter.getRef(position).getKey());
                        intent.putExtra(Common.FROM_ACTIVITY, Common.CATEGORIES_ACTIVITY);
                        intent.putExtra("categoryString", categoryString);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public HomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item, viewGroup, false);

                return new HomeViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void viewProfile(View view) {
        startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
    }
}
