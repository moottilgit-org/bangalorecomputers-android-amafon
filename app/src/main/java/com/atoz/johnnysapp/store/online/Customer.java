package com.atoz.johnnysapp.store.online;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Customer {
    public interface OnJSONResult {
        void onResult(JSONObject result);
    }
    public static void updateFCM(Context context, String token, OnJSONResult onResult) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().updateFCM(Pref.getMobile(context),
                                Http.getFCMToken(token), Http.getDeviceID(context), Http.getDeviceName());
            execute(context, call, onResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(Context context, String username, String password, OnJSONResult onResult) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().login(username, password,
                    Http.getFCMToken(), Http.getDeviceID(context), Http.getDeviceName());
            execute(context, call, onResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void register(Context context, String name, String phone, String email, String password, OnJSONResult onResult) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().register(name, phone, email, password,
                    Http.getFCMToken(), Http.getDeviceID(context), Http.getDeviceName());
            execute(context, call, onResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(Context context, String name, OnJSONResult onResult) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().updateProfile(name, Pref.getMobile(context),
                    Pref.getPassword(context),
                    Http.getFCMToken(), Http.getDeviceID(context), Http.getDeviceName());
            execute(context, call, onResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changePassword(Context context, String newPwd, OnJSONResult onResult) {
        try {
            Call<ResponseBody> call = RetrofitApiClass.getApi().changePassword(newPwd, Pref.getMobile(context),
                    Pref.getPassword(context),
                    Http.getFCMToken(), Http.getDeviceID(context), Http.getDeviceName());
            execute(context, call, onResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    private static void execute(Context context, Call<ResponseBody> call, OnJSONResult onResult) {
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString= response.body().string();
                        if(!TextUtils.isEmpty(responseString) && onResult!=null) {
                            JSONObject result = null;
                            try {
                                result = new JSONObject(responseString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            onResult.onResult(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    onResult.onResult(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
