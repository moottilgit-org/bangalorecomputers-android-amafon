package com.atoz.johnnysapp.store.online;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface API_Calls {
    String BASE_URL = "https://smebusinesssoftware.com/johnnysapp/";

    @GET("store/mobile.html?xhr=yes&action=orders&task=get_active_orders")
    Call<ResponseBody> read(@Query("username") String username, @Query("password") String password,
                      @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);





    @GET("store/mobile.html?xhr=yes&action=orders")
    Call<ResponseBody> orderStatus(@Query("task") String task_type, @Query("order_id") Integer order_id,
                             @Query("stage") Integer stage,
                             @Query("username") String username, @Query("password") String password,
                             @Query("token") String token, @Query("device_id") String device_id,
                             @Query("device_name") String device_name);

    @Multipart
    @POST("store/mobile.html?xhr=yes&action=orders&task=send_message&utype=S")
    Call<ResponseBody> sendMessage(@Part("order_id") RequestBody order_id, @Part("message_text") RequestBody message_text,
                             @Part("msg_ref") RequestBody msg_ref, @Part("msg_ref_src") RequestBody msg_ref_src,
                             @Part MultipartBody.Part attachment,
                             @Part("username") RequestBody username, @Part("password") RequestBody password,
                             @Part("token") RequestBody token, @Part("device_id") RequestBody device_id,
                             @Part("device_name") RequestBody device_name);

    @GET("store/mobile.html?xhr=yes&action=orders&task=update_upi")
    Call<ResponseBody> updatePayment(@Query("upi_id") String upi_id, @Query("upi_uri") String upi_uri,
                               @Query("username") String username, @Query("password") String password,
                               @Query("token") String token, @Query("device_id") String device_id,
                               @Query("device_name") String device_name);

    @FormUrlEncoded
    @POST("store/mobile.html?xhr=yes&action=orders&task=order_update")
    Call<ResponseBody> updateOrder(@Query("order_id") Integer order_id, @FieldMap Map<String, String> order_items,
                             @Query("username") String username, @Query("password") String password,
                             @Query("token") String token, @Query("device_id") String device_id,
                             @Query("device_name") String device_name);


    @GET("store/mobile.html?xhr=yes&action=orders&utype=S")
    Call<ResponseBody> updateMessage(@Query("task") String task_type, @Query("msg_id") String msg_ids,
                               @Query("username") String username, @Query("password") String password,
                               @Query("token") String token, @Query("device_id") String device_id,
                               @Query("device_name") String device_name);

    @GET("store/mobile.html?xhr=yes&action=orders&task=refer")
    Call<ResponseBody> refer(@Query("number") String number, @Query("name") String name,
                       @Query("username") String username, @Query("password") String password,
                       @Query("token") String token, @Query("device_id") String device_id,
                       @Query("device_name") String device_name);



    @Streaming
    @GET("assets/audios/{filename}")
    Call<ResponseBody> downloadAudio(@Path("filename") String filename);





    @GET("store/mobile.html?xhr=yes&action=customers&task=update_fcm")
    Call<ResponseBody> updateFCM(@Query("email") String emailID,
                           @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);

    @GET("store/mobile.html?xhr=yes&action=customers&task=login")
    Call<ResponseBody> login(@Query("username") String username, @Query("password") String password,
                       @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);

    @GET("store/mobile.html?xhr=yes&action=customers&task=register")
    Call<ResponseBody> register(@Query("name") String name, @Query("phone") String phone,
                          @Query("email") String email, @Query("password") String password,
                          @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);

    @GET("store/mobile.html?xhr=yes&action=customers&task=update_profile&utype=S")
    Call<ResponseBody> updateProfile(@Query("name") String name, @Query("username") String username,
                               @Query("password") String password,
                               @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);

    @GET("store/mobile.html?xhr=yes&action=customers&task=change_pwd&utype=S")
    Call<ResponseBody> changePassword(@Query("new_password") String new_password, @Query("username") String username,
                                @Query("password") String password,
                                @Query("token") String token, @Query("device_id") String device_id, @Query("device_name") String device_name);
}
