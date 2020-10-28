package com.atoz.johnnysapp.store.online;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClass {
   public static Retrofit get(){
       return new Retrofit.Builder()
               .baseUrl(API_Calls.BASE_URL)
               .client(new OkHttpClient().newBuilder()
                       .connectTimeout(10, TimeUnit.SECONDS)
                       .readTimeout(20, TimeUnit.SECONDS)
                       .writeTimeout(60, TimeUnit.SECONDS)
                       .build())
               .addConverterFactory(GsonConverterFactory.create())
               .build();
    }

    public static API_Calls getApi() {
        return RetrofitApiClass.get().create(API_Calls.class);
    }
}
