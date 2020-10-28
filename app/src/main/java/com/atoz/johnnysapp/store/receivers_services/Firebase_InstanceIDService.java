package com.atoz.johnnysapp.store.receivers_services;

import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;


public class Firebase_InstanceIDService extends FirebaseInstanceIdService {
    public static final String TOPIC_APP_UPDATE= "app_updates";
    public static final String TOPIC_APP_ALERTS= "app_alerts";


    private static final String TAG = "InstanceIDService";
    private static Runnable runAfter= null;
    @Override
    public void onTokenRefresh() {
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        if(runAfter!=null) {
            runAfter.run();
            runAfter= null;
        }
    }

    public static void subscribeToTopic(String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        } catch (Exception e) {}
    }

    public static void unsubscribeFromTopic(String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        } catch (Exception e) {}
    }

    public static void getFCMToken(Context context, Runnable after) {
        try {
            runAfter= after;
            String token= FirebaseInstanceId.getInstance().getToken();
            if(token==null || token.equals("")) {
                //Nothing to do,
            } else {
                if(runAfter!=null) {
                    runAfter.run();
                    runAfter= null;
                }
            }
        } catch (Exception e) {
            if(runAfter!=null) {
                runAfter.run();
                runAfter= null;
            }
        }
    }
}
