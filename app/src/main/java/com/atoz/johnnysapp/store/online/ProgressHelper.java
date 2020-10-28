package com.atoz.johnnysapp.store.online;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressHelper {
    private final ProgressDialog progressDialog;
    public static ProgressHelper get(Context context) {
        return new ProgressHelper(context);
    }

    private ProgressHelper(Context context) {
        progressDialog= new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
    public ProgressHelper title(int titleID) {
        try {
            progressDialog.setTitle(titleID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public ProgressHelper title(String title) {
        try {
            progressDialog.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public ProgressHelper show(int titleID) {
        title(titleID);
        return show();
    }
    public ProgressHelper show(String title) {
        title(title);
        return show();
    }
    public ProgressHelper show() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public ProgressHelper hide() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
