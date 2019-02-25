package com.example.root.ik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.root.ik.common.Common;

public class CategoriesActivity extends AppCompatActivity {

    CardView phonesAndAccessories, computersAndElectronics, furniture, services, automobile,
                fashion, children, footwear, musicalInstruments, others;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        phonesAndAccessories = findViewById(R.id.phoneCategory);
        computersAndElectronics = findViewById(R.id.computer_category);
        furniture = findViewById(R.id.furniture_category);
        services = findViewById(R.id.service_category);
        automobile = findViewById(R.id.automobile_category);
        fashion = findViewById(R.id.fashion_category);
        children = findViewById(R.id.children_category);
        footwear = findViewById(R.id.shoe_category);
        musicalInstruments = findViewById(R.id.music_Category);
        others = findViewById(R.id.others_category);


        clickListeners();
    }

    private void clickListeners() {
        phonesAndAccessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.MOBILE_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        computersAndElectronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.COMPUTER_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.FURNITURE_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.JOBS_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        automobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.VEHICLE_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.FASHION_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        children.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.CHILDREN_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        footwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.FOOTWEAR_CATEGORY);
                startActivity(phoneIntent);
            }
        });

        musicalInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.MUSIC_CATEGORY);
                startActivity(phoneIntent);
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                phoneIntent.putExtra("category", Common.OTHERS_CATEGORY);
                startActivity(phoneIntent);
            }
        });
    }


    public void viewProfile(View view) {
        startActivity(new Intent(CategoriesActivity.this, ProfileActivity.class));
    }


}
