package com.atoz.johnnysapp.store.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.atoz.johnnysapp.store.data.DatabaseHandler;
import com.atoz.johnnysapp.store.ui.Login;
import com.atoz.johnnysapp.store.utils.DateUtils;
import com.atoz.johnnysapp.store.utils.PermissionManager;
import com.atoz.johnnysapp.store.utils.Pref;

public class ActivityEx extends AppCompatActivity {
    private static final String TAG = ActivityEx.class.getSimpleName();
    protected Context mContext;

    public static final int REQ_UPLOAD= 100;
    public static final int REQ_ATTACH= 150;
    public static final int REQ_WEBVIEW= 200;
    public static final int REQ_ADD_ENQUIRY= 400;
    public static final int REQ_EDIT_ENQUIRY= 450;
    public static final int REQ_PICK_NEW= 600;
    public static final int REQ_PICK_LOG= 640;
    public static final int REQ_PICK_LOG_EX= 641;
    public static final int REQ_PICK_CONTACT= 650;
    public static final int REQ_UPDATE_STAGE= 700;
    public static final int REQ_ADD_TRN= 800;
    public static final int REQ_EDIT_TRN= 850;
    public static final int REQ_COLLECT_MORE= 900;
    public static final int REQ_IM_CHATS= 1100;
    public static final int REQ_IM_VIEW= 1200;

    public DatabaseHandler DB;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext= this;
        DateUtils.init(this);

        DB= new DatabaseHandler(this);
    }

    @Override
    protected void onDestroy() {
        hideFocus(null);
        try { DB.close(); } catch (Exception e) { e.printStackTrace(); }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        hideFocus(null);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try { PermissionManager.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults); }
        catch (Exception e) { e.printStackTrace(); }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void hideFocus(View view) {
        try {
            hideFocus(mContext, view);
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
    }

    public static void hideFocus(Context context, View view) {
        try {
            IBinder token= ((Activity) context).getCurrentFocus()==null?null:((Activity) context).getCurrentFocus().getWindowToken();
            hideFocus(context, token);
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
        try {
            hideFocus(context, view==null?null:view.getWindowToken());
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
    }

    public static void hideFocus(Context context, IBinder token) {
        try {
            if(token==null) {
                return;
            }
            InputMethodManager ipmm= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            ipmm.hideSoftInputFromWindow(token, 0);
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
    }

    public static void showFocus(Context context, View view) {
        try {
            view.requestFocus();
            InputMethodManager ipmm= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            ipmm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch(Exception e) {
            Log.e("showFocus", e.toString());
        }
    }

    protected void logOut() {
        try {
            Pref.init(mContext).setBoolean(Pref.IS_LOGGED_IN, false);
            Intent intent= new Intent(mContext, Login.class);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
