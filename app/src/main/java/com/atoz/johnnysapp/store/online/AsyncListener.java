package com.atoz.johnnysapp.store.online;

import com.atoz.johnnysapp.store.utils.Http;

public interface AsyncListener {
    void onStart();
    void onProgressUpdate(Integer... values);
    void onFinish();
    void onResult(Boolean result, Http.Response response);
}