package com.example.root.ik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.root.ik.model.Post;
import com.example.root.ik.model.User;
import com.example.root.ik.networksync.CheckInternetConnection;
import com.example.root.ik.user_session.UserSession;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.ik.adapter.HomeSliderAdpter;
import com.example.root.ik.common.Common;
import com.example.root.ik.interfaces.ItemClickListener;
import com.example.root.ik.viewHolder.HomeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    FirebaseDatabase database;
    DatabaseReference category;

    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recyclerView_menu;

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Post, HomeViewHolder> adapter;


    FirebaseAuth auth;

    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference userDatabase, myAds;


    User user;
    String nameS, mobileS, photoUrlS, addressS;
    CircleImageView userDrawerImage;
    ImageView profileAc, cartAc;
    TextView userDrawerName, userDrawerEmail;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialSearchBar searchBar;
    //Searchbar functionality
    FirebaseRecyclerAdapter<Post, HomeViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    private UserSession session;

    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        userDrawerImage = headerView.findViewById(R.id.userDrawerImageView);

        userDrawerEmail = headerView.findViewById(R.id.userDrawerEmail);

        userDrawerName = headerView.findViewById(R.id.userDrawerName);


        session = new UserSession(this);

        if (session.getFirstTime()) {
            //tap target view
            tapview();
            session.setFirstTime(false);
        }

        init();
//        loadData();

        loadList();
        loadSuggest();
        loadUserDrawer();
    }

    private void init() {

        //init Firebase

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //check user presence
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    finish();

//                    Intent moveToHome = new Intent(HomeActivity.this, HomeActivity.class);
//                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(moveToHome);
                }
            }
        };
        database = FirebaseDatabase.getInstance();
        category = database.getReference(Common.NODE_POST_ITEM).child(Common.NODE_RAW_POST);
        myAds = database.getReference(Common.NODE_POST_ITEM).child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        collapsingToolbarLayout = findViewById(R.id.collapsing);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        //init VIews

        searchBar = findViewById(R.id.searchBar);
        recyclerView_menu = findViewById(R.id.recyclerview_menu);
        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView_menu.setLayoutManager(layoutManager);

        profileAc = findViewById(R.id.view_profile);
        cartAc = findViewById(R.id.view_cart);


        initSwipeLayout();

        initSearchBar();


        profileAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProfile(view);
            }
        });

        cartAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //viewCart(view);

                startActivity(new Intent(HomeActivity.this, MyAdsActivity.class));
            }
        });

        Slider banerSlider = findViewById(R.id.banner_slider1);
        banerSlider.setAdapter(new HomeSliderAdpter());
    }

    private void checkAndDelete(final String key){

        myAds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(key).exists()){
                    category.child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initSearchBar() {
        searchBar.setHint("What do you need?");
        searchBar.setTextHintColor(R.color.primary_dark);
        searchBar.setTextColor(R.color.gen_black);
        searchBar.setCardViewElevation(10);

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

                if (!enabled)
                    recyclerView_menu.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }


    private void initSwipeLayout() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutHome);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
//                    loadData();
                    loadList();
                } else {
                    Toasty.error(getBaseContext(), "please check your internet connection").show();
                    //Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
//                    loadData();
                    loadList();
                } else {
                    Toasty.error(getBaseContext(), "please check your internet connection").show();
                    //Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void startSearch(CharSequence text) {

        Query searchByTitle = category.orderByChild("title").equalTo(text.toString());

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByTitle, Post.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull Post model) {

                holder.location.setText(model.getLocation());
                holder.price.setText(model.getPrice());
                holder.txtMenuName.setText(model.getTitle());

                Picasso.with(getBaseContext())
                        .load(model.getImageurl())
                        .into(holder.imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(HomeActivity.this, PostDetailActivity.class);
                        intent.putExtra("postid", searchAdapter.getRef(position).getKey());
                        intent.putExtra(Common.FROM_ACTIVITY,Common.HOME_ACTIVITY);
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
        recyclerView_menu.setAdapter(searchAdapter);


    }


    public void viewProfile(View view) {
        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(HomeActivity.this, CartActivity.class));
    }

    public void Notifications(View view) {
        startActivity(new Intent(HomeActivity.this, NotifcationActivity.class));
    }


    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.notifintro), "Notifications", "Latest offers will be available here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.first),
                        TapTarget.forView(findViewById(R.id.view_profile), "Profile", "You can view and edit your profile here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third),
                        TapTarget.forView(findViewById(R.id.view_cart), "Your Uploads", "Here is Shortcut to your uploads!")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.second),
                        TapTarget.forView(findViewById(R.id.swipe_down), "Refresh", "Swipe down to refresh page!")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.white)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.green_light)
                )
//                        TapTarget.forView(findViewById(R.id.visitingcards), "Categories", "Product Categories have been listed here !")
//                                .targetCircleColor(R.color.colorAccent)
//                                .titleTextColor(R.color.colorAccent)
//                                .titleTextSize(25)
//                                .descriptionTextSize(15)
//                                .descriptionTextColor(R.color.accent)
//                                .drawShadow(true)
//                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
//                                .tintTarget(true)
//                                .transparentTarget(true)
//                                .outerCircleColor(R.color.fourth))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        session.setFirstTime(false);
                        Toasty.success(HomeActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }


    private void loadUserDrawer() {


        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    User user = dataSnapshot.getValue(User.class);
                    userDrawerName.setText(user.getName());
                    userDrawerEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    Uri imageUri = Uri.parse(user.getImageUrl());
                    Glide.with(getBaseContext())
                            .load(imageUri)
                            .into(userDrawerImage);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadList() {
        Query searchByDate = category.orderByChild("postid").limitToLast(20);

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(searchByDate, Post.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Post, HomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull Post model) {


//                checkAndDelete(adapter.getRef(position).getKey());
                //Uri imageUri = Uri.parse(model.getImageurl());

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

                        Intent intent = new Intent(HomeActivity.this, PostDetailActivity.class);
                        intent.putExtra("postid", adapter.getRef(position).getKey());
                        intent.putExtra(Common.FROM_ACTIVITY,Common.HOME_ACTIVITY);
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
        recyclerView_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void loadSuggest() {
        category.orderByChild("postid")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post item = postSnapshot.getValue(Post.class);
                            suggestList.add(item.getTitle());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

//    private void loadData() {
//        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
//                .setQuery(category, Category.class)
//                .build();
//
//        adapter = new FirebaseRecyclerAdapter<Category, HomeViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull Category model) {
//
//                holder.txtMenuName.setText(model.getName());
//                holder.price.setText(model.getPrice());
//                Picasso.with(getBaseContext()).load(model.getImage())
//                        .into(holder.imageView);
//                final Category clickItem = model;
//                holder.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        Toast.makeText(HomeActivity.this, "You clicked: " + clickItem.getName(), Toast.LENGTH_SHORT).show();
//                        Intent menu_intent = new Intent(HomeActivity.this, ItemListActivity.class);
//                        menu_intent.putExtra("CategoryId", adapter.getRef(position).getKey());
//                        startActivity(menu_intent);
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View itemView = LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.menu_item, viewGroup, false);
//                return new HomeViewHolder(itemView);
//            }
//        };
//
//        adapter.startListening();
//        recyclerView_menu.setAdapter(adapter);
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            // Handle the camera action
            startActivity(new Intent(HomeActivity.this, CategoriesActivity.class));
        } else if (id == R.id.nav_chat) {


        } else if (id == R.id.nav_change_password) {
            startActivity(new Intent(HomeActivity.this, ForgotPasswordActivity.class));

        } else if (id == R.id.nav_profile) {

            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_posts) {
            startActivity(new Intent(HomeActivity.this, PostsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));


        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
        if (searchAdapter != null)
            searchAdapter.startListening();
        loadList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
    }
}
