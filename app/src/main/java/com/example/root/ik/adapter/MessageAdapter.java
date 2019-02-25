package com.example.root.ik.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.root.ik.R;
import com.example.root.ik.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chatList;
    private String imageUrl;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chatList, String imageUrl){
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Chat chat = chatList.get(i);
        viewHolder.show_mesage.setText(chat.getMessage());

        if (imageUrl.equals("default")){
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Glide.with(context).load(imageUrl).into(viewHolder.profileImage);
        }

        if (i == chatList.size()-1){
            if (chat.isIsseen()){
                viewHolder.txt_seen.setText("Seen");

            }else {
                viewHolder.txt_seen.setText("Delivered");

            }
        }else {
            viewHolder.txt_seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_mesage;
        public TextView txt_seen;
        public ImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_mesage = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else return MSG_TYPE_LEFT;
    }
}
