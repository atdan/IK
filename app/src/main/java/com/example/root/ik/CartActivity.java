package com.example.root.ik;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.ik.common.Common;
import com.example.root.ik.database.Database;
import com.example.root.ik.model.Order;
import com.example.root.ik.viewHolder.CartAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
    RecyclerView recyclerView;
    RecyclerView.ViewHolder viewHolder;
    LinearLayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView textTotalPrice;
    FButton btnPlaceOrder;

    List<Order> carts = new ArrayList<>();
    CartAdapter adapter;

    String address = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //init views
        recyclerView = findViewById(R.id.list_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textTotalPrice= findViewById(R.id.totalPrice);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carts.size() >0)
                    showAlertDialog();
                else
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        //loadListCart();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address: ");




        LayoutInflater inflater = this.getLayoutInflater();

        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);



        final TextInputEditText edtComment = order_address_comment.findViewById(R.id.edtComment);
        final TextInputEditText edtAddress = order_address_comment.findViewById(R.id.edtshipAddress);


        //Radio
        //final RadioButton radioBtnShipToAddress = order_address_comment.findViewById(R.id.radioShipToAddress);
        final CheckBox radioBtnShipToHome = order_address_comment.findViewById(R.id.chkHomeAddress);

        radioBtnShipToHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                    if (Common.current_user.getHomeAddress() != null ||
                            !TextUtils.isEmpty(Common.current_user.getHomeAddress())){
                        address = Common.current_user.getHomeAddress();
                        edtAddress.setText(address);
                    }else {
                        Toast.makeText(CartActivity.this, "Please Update your home adress", Toast.LENGTH_SHORT).show();

                    }
                }else {
                    edtAddress.setText("");
                }
            }
        });



        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


//                if (!TextUtils.isEmpty(address)){
//                    Toast.makeText(CartActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
//                    //fix crash fragment
//                    getFragmentManager().beginTransaction()
//                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragmet))
//                            .commit();
//                    return;
//                }

                //TODO: from here
//                //create new request
//                Request request = new Request(Common.current_user.getPhone(),
//                        address,
//                        Common.current_user.getName(),
//                        textTotalPrice.getText().toString(),
//                        edtComment.getText().toString(),
//                        String.format("%s,%s",shipAddress.getLatLng().latitude,
//                                shipAddress.getLatLng().longitude),
//                        carts,
//                        "0");
//                //submit to firebase
//                //use System.getCurrentMilli to key
//
//
//                String order_number = String.valueOf(System.currentTimeMillis());
//                requests.child(order_number)
//                        .setValue(request);
//
//                //Delete cart
//                new Database(getApplicationContext()).cleanCart();
//
//                //send the notification when order is placed
//                sendNotificationOrder(order_number);
//                Toast.makeText(CartActivity.this, "Thank you, Your order has been placed", Toast.LENGTH_SHORT).show();
////                finish();
//
//                //remove fragmen
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragmet))
//                        .commit();
            }

        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

//                //remove fragmen
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragmet))
//                        .commit();

            }
        });

        alertDialog.show();


    }


    private void loadListCart() {

        carts = new Database(this).getCarts();
        adapter = new CartAdapter(carts,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for (Order order: carts){
            total += (Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity()));

        }

        Locale locale = new Locale("en","US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        textTotalPrice.setText(numberFormat.format(total));
    }

    private void deleteCart(int position) {
        // we need to remove item at List<> order by position
        carts.remove(position);

        //After that we need to delete all old data from SQLite
        new Database(this).cleanCart();

        //now update new data from List<Order> to SQLite
        for (Order item: carts){
            new Database(this).addToCart(item);
        }

        //refresh list
        loadListCart();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }
}
