package com.atoz.johnnysapp.store.receivers_services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.atoz.johnnysapp.store.BuildConfig;
import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.online.Customer;
import com.atoz.johnnysapp.store.ui.Main;
import com.atoz.johnnysapp.store.ui.order_details.OrderDetails;
import com.atoz.johnnysapp.store.utils.PermissionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class Firebase_MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static int NOTIFICATION_ID= 1200;
    private static int TRACKER_NOTIFICATION_ID= 1190;
    private static final String NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + "_v1";
    private static final String NOTIFICATION_CHANNEL_NAME = "Internal Messages";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            String title = "";
            String body = "";
            Bitmap image = null;
            Log.d(TAG, "From: " + remoteMessage.getFrom());//topics/topicname or some long value

            boolean isTopic = remoteMessage.getFrom().startsWith("/topics/");
            String topic = "";
            if (isTopic) {
                String[] topics = remoteMessage.getFrom().split("/");// /topics/app_updates;
                topic = topics[topics.length - 1];
            }

            Map<String, String> tmp = remoteMessage.getData();
            Bundle receivedData = new Bundle();
            if (tmp != null && tmp.size() > 0) {
                for (String key : tmp.keySet()) {
                    receivedData.putString(key, tmp.get(key));
                }
            }

            if (receivedData.size() > 0) {
                title = receivedData.getString("title", "");
                body = receivedData.getString("body", "");

                /*
                if(receivedData.containsKey("image")) {
                    String imageUri= receivedData.getString("image", "");
                    image= imageUri!=null&&!imageUri.equals("")?getBitmapfromUrl(imageUri):null;
                }*/
            }

            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();
                Log.d(TAG, "Message Notification Body: " + body);
            }

            showNotification(this, title, body, "", image, receivedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleJob() {

    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    public static void showNotification(Context context, String title, String body, String info, Bitmap image, Bundle receivedData) {
        try {
            if(handleWhat(context, title, body, receivedData)) {
                return;
            }
            String what= receivedData != null && receivedData.containsKey("what")?receivedData.getString("what"):"";
            what= TextUtils.isEmpty(what)?"":what;

            if(TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
                return;
            }

            int nid= NOTIFICATION_ID++;
            Intent intent;
            if(what.equals("shopping_order")) {
                intent = new Intent(context, OrderDetails.class);
                intent.putExtra("ID", Integer.parseInt(receivedData.getString("order_id")));
                intent.putExtra("MSGS", TextUtils.equals(receivedData.getString("task"), "message"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                intent= new Intent(context, Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("nid", nid);
                intent.putExtra("FCM_MESSAGE", true);
                intent.putExtra("data", receivedData);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,
                    createNotificationChannel(context, NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(title == null || title.equals("") ? context.getString(R.string.app_name) : title)
                    .setContentText(body)
                    .setContentInfo(info)
                    .setSubText("Message")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

            notificationBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
            if(PermissionManager.is26()) {
                notificationBuilder.setPriority(NotificationManager.IMPORTANCE_MAX);
            } else {
                notificationBuilder.setPriority(android.app.Notification.PRIORITY_MAX);
            }
            //notificationBuilder.setFullScreenIntent(pendingIntent, true);

            if (image != null) {
                notificationBuilder
                        .setLargeIcon(image)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            }

            android.app.Notification notification= notificationBuilder.build();
            notification.flags |= android.app.Notification.FLAG_NO_CLEAR | android.app.Notification.FLAG_ONGOING_EVENT;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(nid, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createNotificationChannel(Context context, String channelId, String channelName) {
        if(Build.VERSION.SDK_INT >= 26) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), attributes);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if(manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        return channelId;
    }

    public static void sendFCMToServer(Context context, String token) {
        try {
            Customer.updateFCM(context, token, result -> {
                try {
                    if (result != null) {
                        String resultStr = result.getString("result");
                        if(resultStr.equalsIgnoreCase("success")) {
                            //nothing to do
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean handleWhat(Context context, String title, String body, Bundle receivedData) {
        boolean handled= false;
        try {
            String what= receivedData != null && receivedData.containsKey("what")?receivedData.getString("what"):"";
            what= TextUtils.isEmpty(what)?"new_im":what;
            handled= true;
            switch (what) {
                case "new_im":

                break;
                case "activity_alert":
                    handled= false;
                break;
                case "shopping_order":
                    return Store.handleStoreNotification(context, title, body, receivedData);
            }
            return handled;
        } catch (Exception e) {
            e.printStackTrace();
            return handled;
        }
    }

    public static String getUniqueID(Context mContext) {
        String result= "";
        try {
            result= android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            result= android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        }
        return TextUtils.isEmpty(result)?"":result;
    }
    public static String getDeviceName() {
        String name= "";
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                name = model;
            } else {
                name = manufacturer + " " + model;
            }

            if (name.length() == 0) {
                return "";
            }
            char first = name.charAt(0);
            if (Character.isUpperCase(first)) {
                return name;
            } else {
                return Character.toUpperCase(first) + name.substring(1);
            }
        } catch (Exception e) {
            Log.e("getDeviceName", e.toString());
            return "unknown";
        }
    }
}

