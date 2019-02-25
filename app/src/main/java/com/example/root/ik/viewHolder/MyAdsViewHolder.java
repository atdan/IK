package com.example.root.ik.viewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.ik.R;
import com.example.root.ik.common.Common;
import com.example.root.ik.interfaces.ItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
    View.OnClickListener{

    public TextView txtMenuName, price, location;
    public ImageView imageView, deleteImage;

    private ItemClickListener itemClickListener;
    public MyAdsViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.menu_title);
        imageView = itemView.findViewById(R.id.menu_image);
        price = itemView.findViewById(R.id.menu_price);
        location = itemView.findViewById(R.id.menu_location);



        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
