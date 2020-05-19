package com.example.hfilproject;

import com.example.hfilproject.Model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface for_login {

    @FormUrlEncoded

    @POST("api/auth/signup/")
    Call<User> createAccount(@Header("access-token") String token,
                             @FieldMap Map<String, Object> params)
            ;

    @FormUrlEncoded
    @POST("/api/location/")
    Call<UserLocation> userLocation(@Header("access-token") String token, @FieldMap Map<String, Object> params);


    @FormUrlEncoded
    @POST("/api/temperature/")
    Call<UserTemp> userTemp(@Header("access-token") String token, @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("/api/notification/")
    Call<UserNotification> userNotify(@Header("access-token") String token1, @FieldMap Map<String, Object> params);



    @FormUrlEncoded
    @POST("/api/getUser")
    Call<User> ReInstall(@FieldMap Map<String,Object> params);


    @GET("/api/getTemperatures")
    Call<GetTemp> getTemp(@Header("access-token") String token2);

    @GET("/api/getLocations")
    Call<GetLocation> getLoc(@Header("access-token") String token3);

    @GET("/api/getNotifications")
    Call<GetNotification> getNoti(@Header("access-token") String token4);

}
