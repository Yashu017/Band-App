package com.example.hfilproject;

import com.example.hfilproject.Model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface for_login {

    @FormUrlEncoded

    @POST("api/auth/signup/")
     Call<User> createAccount(@Header("access-token")String token,
                              @FieldMap Map<String, Object> params)
            ;

}