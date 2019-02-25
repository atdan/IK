package com.example.root.ik.viewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.ik.R;
import com.example.root.ik.interfaces.ItemClickListener;

public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtMenuName, price, location;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.menu_title);
        imageView = itemView.findViewById(R.id.menu_image);
        price = itemView.findViewById(R.id.menu_price);
        location = itemView.findViewById(R.id.menu_location);



        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);
    }



}
