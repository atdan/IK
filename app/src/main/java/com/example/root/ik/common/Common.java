package com.example.root.ik.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.root.ik.model.User;

import java.security.PublicKey;

public class Common {
    public static User current_user;

    public static String PHONE_TEXT = "userPhone";

    public static final String INTENT_FOOD_ID = "FoodId";

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static final String  MOBILE_CATEGORY = "Mobile Phones and Accessories";

    public static final String FURNITURE_CATEGORY= "Furniture and Home Appliances";

    public static final String COMPUTER_CATEGORY = "Computers and Accessories";

    public static final String VEHICLE_CATEGORY = "Vehicles";

    public static final String JOBS_CATEGORY = "Jobs and Services";

    public static final String FASHION_CATEGORY = "Fashion and Beauty";

    public static final String CHILDREN_CATEGORY = "Children and Toys";

    public static final String FOOTWEAR_CATEGORY = "Footwear";

    public static final String MUSIC_CATEGORY = "Musical Instruments";

    public static final String OTHERS_CATEGORY = "Others";

    public static final String FROM_ACTIVITY = "from_activity";

    public static final String HOME_ACTIVITY = "home_activity";

    public static final String MY_UPLOADS_ACTIVITY = "my_uploads_activity";

    public static final String CATEGORIES_ACTIVITY = "category_activity";

    public static final String DELETE = "Delete";

    public static final String UPDATE = "Update";

    public static final String NODE_POST_ITEM = "Post Item";

    public static final String NODE_RAW_POST = "Raw Post";


    public static String convertCodeToStatus(String status) {
        if (status.equals("0")){
            return "Order Placed";
        }else if (status.equals("1")){
            return "On my way!";
        }else return "shipped";
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();

            if (infos != null){
                for (int i = 0; i< infos.length; i++){
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED){
                        return  true;
                    }
                }
            }
        }
        return false;
    }


    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";

}
