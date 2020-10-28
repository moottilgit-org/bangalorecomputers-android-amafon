package com.atoz.johnnysapp.store.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

public class PermissionManager {
    private static final int REQUEST_PERMISSIONS= 5697;
    private static final int REQUEST_OVERLAY_PERMISSIONS= 5698;
    private static final int REQUEST_POWER_PERMISSIONS= 5699;

    public static String[] PERMISSIONS_ALL= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

            Manifest.permission.CAMERA,

            Manifest.permission.WAKE_LOCK,

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,

            Manifest.permission.SEND_SMS,

            Manifest.permission.RECORD_AUDIO,

            Manifest.permission.GET_ACCOUNTS,

            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,

            Manifest.permission.USE_FINGERPRINT,

            Manifest.permission.INSTALL_SHORTCUT,
            Manifest.permission.UNINSTALL_SHORTCUT,

            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static String[] PERMISSION_STORAGE= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] PERMISSION_LOCATION= {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static String[] PERMISSION_CONTACTS= {
            Manifest.permission.READ_CONTACTS,
    };
    public static String[] PERMISSION_CALL_LOG= {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
    };
    public static String[] PERMISSION_SMS= {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
    };
    public static String[] PERMISSION_TELEPHONE_CONTACT_SMS= {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
    };
    public static String[] PERMISSION_SEND_SMS= {
            Manifest.permission.SEND_SMS
    };
    public static String[] PERMISSION_RECORD= {
            Manifest.permission.RECORD_AUDIO,
    };
    public static String[] PERMISSION_TELEPHONE= {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            //Manifest.permission.PROCESS_OUTGOING_CALLS,
    };
    public static String[] PERMISSION_CAMERA= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static String[] PERMISSION_CALLLOG_SMS_BOTH= {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
    };
    public static String[] PERMISSION_CALLLOG_SMS_CALLLOG= {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG
    };
    public static String[] PERMISSION_CALLLOG_SMS_SMS= {
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
    };

    public static int PERMISSION_OVERLAY= 55,
                      PERMISSION_BATTERY= 66;

    public static final int PERMISSION_STATUS_NONEED= 1,
                            PERMISSION_STATUS_GRANTING= 2,
                            PERMISSION_STATUS_GRANTED= 3,
                            PERMISSION_STATUS_DENIED= 4,

                            REQUEST_MODE_ALL= 1,
                            REQUEST_MODE_SINGLE= 2;

    public interface AfterPermissions {
        void afterResult(final int result);
    }
    private AfterPermissions mAfterPermissions;
    private Activity mActivity;
    private int REQUEST_MODE= REQUEST_MODE_SINGLE;
    public static PermissionManager mPermissionManager= null;
    private PermissionManager(Activity activity) {
        mActivity= activity;
    }
    public static PermissionManager getInstance(Activity activity) {
        mPermissionManager= mPermissionManager==null?new PermissionManager(activity):mPermissionManager;
        mPermissionManager.mActivity= activity;
        return mPermissionManager;
    }
    public PermissionManager set(AfterPermissions afterPermissions) {
        mAfterPermissions= afterPermissions;
        return this;
    }
    public PermissionManager request(String[] permissionSet) {
        REQUEST_MODE= REQUEST_MODE_SINGLE;
        int result= PERMISSION_STATUS_DENIED;
        try {
            if (isMNOP()) {
                result = requestPermissions(permissionSet);
            } else {
                result = PERMISSION_STATUS_NONEED;
            }
        } catch (Exception e) {

        }
        exec(result);
        return this;
    }
    public PermissionManager request(int permission) {
        REQUEST_MODE= REQUEST_MODE_SINGLE;
        int result= PERMISSION_STATUS_DENIED;
        try {
            if (isMNOP()) {
                if(permission==PERMISSION_OVERLAY) {
                    result = requestOverlayPermission(result);
                } else if(permission==PERMISSION_BATTERY) {
                    result = requestPowerManager(result);
                }
            } else {
                result = PERMISSION_STATUS_NONEED;
            }
        } catch (Exception e) {

        }
        exec(result);
        return this;
    }
    public PermissionManager requestAllPermissions() {
        REQUEST_MODE= REQUEST_MODE_ALL;
        int result= PERMISSION_STATUS_DENIED;
        try {
            if (isMNOP()) {
                result = requestPermissions(null);
                result = requestOverlayPermission(result);
                result = requestPowerManager(result);
            } else {
                result = PERMISSION_STATUS_NONEED;
            }
        } catch (Exception e) {

        }
        exec(result);
        return this;
    }
    public void finish() {
        mAfterPermissions= null;
        mPermissionManager= null;
    }
    private void exec(int result) {
        if(result!=PERMISSION_STATUS_GRANTING) {
            if (mAfterPermissions != null) {
                mAfterPermissions.afterResult(result);
            }
        }
    }



    private int requestPermissions(String[] permissionSet) {
        int result= PERMISSION_STATUS_NONEED;
        try {
            permissionSet = permissionSet == null ? PERMISSIONS_ALL : permissionSet;
            if (isMNOP()) {
                if (PermissionManager.hasSelfPermission(mActivity, permissionSet)) {
                    result= PERMISSION_STATUS_GRANTED;
                } else {
                    if (mActivity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    }
                    mActivity.requestPermissions(permissionSet, REQUEST_PERMISSIONS);
                    result= PERMISSION_STATUS_GRANTING;
                }
            }
        } catch (Exception e) {
            result= PERMISSION_STATUS_DENIED;
        }
        return result;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int result= PERMISSION_STATUS_NONEED;
        try {
            if (requestCode == REQUEST_PERMISSIONS) {
                if (PermissionManager.verifyPermissions(grantResults)) {
                    result = PERMISSION_STATUS_GRANTED;
                    if (REQUEST_MODE == REQUEST_MODE_ALL) {
                        result = requestOverlayPermission(result);
                        result = requestPowerManager(result);
                    }
                } else {
                    result = PERMISSION_STATUS_DENIED;
                }
            }
        } catch (Exception e) {

        }
        exec(result);
    }
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    public static boolean hasSelfPermission(Activity activity, String[] permissions) {
        try {
            if (!isMNOP()) {
                return true;
            }
            for (String permission : permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public static boolean hasSelfPermission(Activity activity, String permission) {
        try {
            if (!isMNOP()) {
                return true;
            }
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean overlayPermitted() {
        try {
            if (needOverlayPermission()) {
                if (isMNOP()) {
                    return Settings.canDrawOverlays(mActivity);
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    public boolean powerManagerEnabled() {
        try {
            if (isMNOP()) {
                PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
                return pm.isIgnoringBatteryOptimizations(mActivity.getPackageName());
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean needOverlayPermission() {
        try {
            if(!isMNOP()) {
                return false;
            }
            String[] permissions = mActivity.getPackageManager()
                    .getPackageInfo(mActivity.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
            if(permissions!=null) {
                for(String perm: permissions) {
                    if(Manifest.permission.SYSTEM_ALERT_WINDOW.equalsIgnoreCase(perm)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    private int requestOverlayPermission() {
        return requestOverlayPermission(PERMISSION_STATUS_NONEED);
    }
    private int requestOverlayPermission(int result) {
        try {
            if(needOverlayPermission()) {
                if (result != PERMISSION_STATUS_GRANTING) {
                    if (isMNOP()) {
                        if (Settings.canDrawOverlays(mActivity)) {
                            result = PERMISSION_STATUS_GRANTED;
                        } else {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + mActivity.getPackageName()));
                            mActivity.startActivityForResult(intent, REQUEST_OVERLAY_PERMISSIONS);
                            result = PERMISSION_STATUS_GRANTING;
                        }
                    }
                }
            }
        } catch (Exception e) {
            result= PERMISSION_STATUS_DENIED;
        }
        return result;
    }

    private int requestPowerManager() {
        return requestPowerManager(PERMISSION_STATUS_NONEED);
    }
    private int requestPowerManager(int result) {
        try {
            if (result != PERMISSION_STATUS_GRANTING) {
                if (isMNOP()) {
                    Intent intent = new Intent();
                    PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(mActivity.getPackageName())) {
                        result= PERMISSION_STATUS_GRANTED;
                    } else {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        mActivity.startActivityForResult(intent, REQUEST_POWER_PERMISSIONS);
                        result= PERMISSION_STATUS_GRANTING;
                    }
                }
            }
        } catch (Exception e) {
            result= PERMISSION_STATUS_DENIED;
        }
        return result;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int result= PERMISSION_STATUS_NONEED;
        try {
            if (requestCode == PermissionManager.REQUEST_OVERLAY_PERMISSIONS) {
                if (isMNOP()) {
                    result = Settings.canDrawOverlays(mActivity) ? PERMISSION_STATUS_GRANTED : PERMISSION_STATUS_DENIED;
                    result = requestPowerManager(result);
                }
            } else if (requestCode == PermissionManager.REQUEST_POWER_PERMISSIONS) {
                if (isMNOP()) {
                    PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(mActivity.getPackageName())) {
                        result= PERMISSION_STATUS_GRANTED;
                    } else {
                        result= PERMISSION_STATUS_DENIED;
                    }
                }
            }
        } catch (Exception e) {

        }
        exec(result);
    }


    public static boolean isMNOP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    public static boolean isNOP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
    public static boolean is26() {
        return Build.VERSION.SDK_INT >= 26;
    }
    public static boolean is23() {
        return Build.VERSION.SDK_INT >= 23;
    }
    public static boolean is22() {
        return Build.VERSION.SDK_INT >= 22;
    }
    public static boolean is21() {
        return Build.VERSION.SDK_INT >= 21;
    }
    public static boolean is20() {
        return Build.VERSION.SDK_INT >= 20;
    }
    public static boolean is19() {
        return Build.VERSION.SDK_INT >= 19;
    }
}
