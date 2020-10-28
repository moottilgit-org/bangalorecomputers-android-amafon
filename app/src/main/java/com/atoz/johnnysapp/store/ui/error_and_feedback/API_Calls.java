package com.atoz.johnnysapp.store.ui.error_and_feedback;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface API_Calls {
    String BASE_URL = "https://smebusinesssoftware.com/wisecat/";

    @GET("crm/api/main.html?xhr=yes&action=errors&sub_action=report")
    Call<ResponseBody> error(@Query("username") String username, @Query("password") String password, @Query("error_str") String error_str);

    @Multipart
    @POST("crm/api/main.html?xhr=yes&action=errors&sub_action=feedback")
    Call<ResponseBody> feedback(@Part("username") RequestBody username, @Part("password") RequestBody password,
                          @Part("feedback_str") RequestBody feedback_str, @Part MultipartBody.Part[] attachments);


}
