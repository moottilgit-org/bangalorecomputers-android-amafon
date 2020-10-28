package com.atoz.johnnysapp.store.utils.updater;

public interface UpdaterListener {
    void onStart();
    void onFinish();
    void onProgress(long readBytes, long totalBytes);
    void onError(String error);
}