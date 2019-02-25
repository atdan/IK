package com.example.root.ik.interfaces;



import com.example.root.ik.notification.MyResponse;
import com.example.root.ik.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAdmnseBI:APA91bEBWX1w-8ZPscHeLo0iJjehwWBquxatL0SuOBP2aXsF3Mov0POgSF0FsoeGY6mOWFVwtrRt7hUeOoPTgHcMfXuUYp2OhKQLniWtNlJBIW69ybC2JlkxINkAR33EK8256llSPaDf"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
