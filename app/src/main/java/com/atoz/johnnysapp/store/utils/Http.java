package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.atoz.johnnysapp.store.receivers_services.Firebase_MessagingService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

public class Http {
    public static String HTTP_AVATAR= "https://smebusinesssoftware.com/johnnysapp/assets/avatar/";

    public static String getFCMToken() {
        return getFCMToken(null);
    }
    public static String getFCMToken(String token) {
        return TextUtils.isEmpty(token)?FirebaseInstanceId.getInstance().getToken():token;
    }
    public static String getDeviceID(Context context) {
        return Firebase_MessagingService.getUniqueID(context);
    }
    public static String getDeviceName() {
        return Firebase_MessagingService.getDeviceName();
    }


    public static boolean isConnectingToInternet(Context context){
        try {
            ConnectivityManager connectivity= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch(Exception e) {
            Log.e("isConnectingToInternet", e.toString());
            return false;
        }
    }

    public static JSONObject getJSON(String textInJSON) {
        try {
            return new JSONObject(textInJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Response {
        private final String mResponse;
        private JSONObject jResponse;
        public Response(String response) {
            response= TextUtils.isEmpty(response)?"":response;
            mResponse= response;
            jResponse= getJSON(response);
        }
        public boolean success() {
            try {
                if(jResponse==null) {
                    return TextUtils.equals("success", mResponse.toLowerCase())
                            || mResponse.toLowerCase().startsWith("success:");
                } else if(jResponse.has("result")) {
                    return TextUtils.equals("success", jResponse.getString("result"));
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public boolean old_version() {
            try {
                if(success()) {
                    return false;
                } if(jResponse != null && jResponse.has("result")) {
                    return TextUtils.equals("old_version", jResponse.getString("result"));
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public String error() {
            try {
                if(success()) {
                    return "";
                } else if(jResponse!=null && jResponse.has("result")) {
                    return jResponse.getString("result");
                } else {
                    return mResponse.contains(":")?mResponse.split(":")[0]:mResponse;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        public String message() {
            try {
                String msg= "";
                if(jResponse!=null && jResponse.has("msg")) {
                    msg= jResponse.getString("msg");
                } else {
                    msg= mResponse.contains(":")?mResponse.split(":")[1]:mResponse;
                }
                return TextUtils.isEmpty(msg)?"Connection Error(1)":msg;
            } catch (Exception e) {
                e.printStackTrace();
                return "Unknown Error(e)";
            }
        }
        public String errorMessage() {
            try {
                String error= error();
                error+= (TextUtils.isEmpty(error)?"":": ") + message();
                error= TextUtils.equals(error, ": ")?"":error;
                return error;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        public JSONObject data() {
            try {
                if(jResponse!=null && jResponse.has("data")) {
                    return jResponse.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public JSONObject get() {
            return jResponse;
        }

        //{"result":"success","msg":"Test Message","data":{"data1":"abc text","data2":[1,2,3,4]}}
        //{"result":"error","msg":"Test Error Message","data":{"data1":"abc text","data2":[1,2,3,4]}}
        //{"result":"old_version","msg":"Your version 0 is older than the server version 1. Please update your app","data":{"current_version":1}}
    }
}
