package com.atoz.johnnysapp.store.ui.error_and_feedback;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    public interface AsyncListener {
        void onStart();
        void onFinish();
        void onResult(boolean result);
    }

    public static void errorReport(Context context, String errorStr, AsyncListener asyncListener) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().error(Pref.getMobile(context), Pref.getPassword(context), errorStr);
            execute(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void feedback(Context context, String feedbackStr, ArrayList<Bundle> attachments, AsyncListener asyncListener) {
        try {
            MultipartBody.Part[] fileData= null;
            if(attachments != null) {
                fileData= new MultipartBody.Part[attachments.size()];
                int i= 0;
                for (Bundle attachment : attachments) {
                    File file= new File(attachment.getString("FILE"));
                    RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
                    fileData[i++]= MultipartBody.Part.createFormData("attachments", file.getName(), requestFile);
                }
            }

            Call<ResponseBody> call = RetrofitApiClass.getApi().feedback(
                    RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), Pref.getMobile(context)),
                    RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), Pref.getPassword(context)),
                    RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), feedbackStr), fileData);
            execute(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute(Context context, Call<ResponseBody> call, AsyncListener asyncListener) {
        try {
            if(asyncListener != null) {
                asyncListener.onStart();
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString= response.body().string();
                        if(!TextUtils.isEmpty(responseString) && asyncListener != null) {
                            Http.Response JResponse= new Http.Response(responseString);
                            asyncListener.onFinish();
                            asyncListener.onResult(JResponse.success());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if(asyncListener != null) {
                        asyncListener.onFinish();
                        asyncListener.onResult(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
