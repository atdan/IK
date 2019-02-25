package com.example.root.ik.viewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.root.ik.CartActivity;
import com.example.root.ik.R;
import com.example.root.ik.common.Common;
import com.example.root.ik.database.Database;
import com.example.root.ik.interfaces.ItemClickListener;
import com.example.root.ik.model.Order;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{

public TextView textCartName,textPrice;
public ElegantNumberButton qtyBtn;
public ImageView cart_Image;

private ItemClickListener itemClickListener;

public void setTextCartName(TextView textCartName){
        this.textCartName=textCartName;
        }

public CartViewHolder(@NonNull View itemView){
        super(itemView);

        textCartName=itemView.findViewById(R.id.cart_item_name);
        textPrice=itemView.findViewById(R.id.cart_item_price);
        qtyBtn=itemView.findViewById(R.id.btn_quantity_cart);
        cart_Image=itemView.findViewById(R.id.cart_image);


        itemView.setOnCreateContextMenuListener(this);
        }

@Override
public void onClick(View view){

        }

@Override
public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){

        contextMenu.setHeaderTitle("Select Action");
        contextMenu.add(0,0,getAdapterPosition(),Common.DELETE);
        }
        }

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private CartActivity cartActivity;

    public CartAdapter(List<Order> listData, CartActivity cartActivity) {
        this.listData = listData;
        this.cartActivity = cartActivity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(cartActivity);
        View itemView = inflater.inflate(R.layout.cart_item_layout, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, final int i) {

//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(""+listData.get(i).getQuantity(), Color.RED);
//        cartViewHolder.imageCartCount.setImageDrawable(drawable);

        //load Image into cart
        Picasso.with(cartActivity.getBaseContext())
                .load(listData.get(i).getImage())
                .resize(70, 70)//70dp
                .centerCrop()
                .into(cartViewHolder.cart_Image);

        cartViewHolder.qtyBtn.setNumber(listData.get(i).getQuantity());
        cartViewHolder.qtyBtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(i);
                order.setQuantity(String.valueOf(newValue));
                new Database(cartActivity).updateCart(order);

                //calculate new total price and set
                //calculate total price

                List<Order> orders = new Database(cartActivity).getCarts();
                int total = 0;
                for (Order item : orders) {
                    //total += (Integer.parseInt(order.getPrice()) * Integer.parseInt(item.getQuantity()));

                    total += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQuantity()));

                }

                Locale locale = new Locale("en", "US");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

                cartActivity.textTotalPrice.setText(numberFormat.format(total));
            }
        });


        Locale locale = new Locale("en", "US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(i).getPrice()) * Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.textPrice.setText(numberFormat.format(price));
        cartViewHolder.textCartName.setText(listData.get(i).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
