package com.atoz.johnnysapp.store;

import android.content.Intent;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
public class MyApplication extends MultiDexApplication {
    private static Thread.UncaughtExceptionHandler defaultUEH= null;
    @Override
    public void onCreate() {
        super.onCreate();
        // Setup handler for uncaught exceptions.
        defaultUEH= defaultUEH==null?Thread.getDefaultUncaughtExceptionHandler():defaultUEH;
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
    }

    private void handleUncaughtException (Thread t, Throwable e) {
        try {
            e.printStackTrace();// not all Android versions will print the stack trace automatically

            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction(BuildConfig.APPLICATION_ID + ".SEND_LOG");// see step 5.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// required when starting from Application
            intent.putExtra(Intent.EXTRA_TEXT, errorToStr(t, e));
            startActivity(intent);

            System.exit(1);// kill off the crashed app

            //defaultUEH.uncaughtException(t, e);
        } catch (Exception e1) {
            e.printStackTrace();
        }
    }

    private boolean isUIThread(){
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private String errorToStr(Thread t, Throwable e) {
        try {
            String errorMessage= e.toString();
            String errorCause= "";

            StackTraceElement[] arr = e.getStackTrace();
            String threadInfo = t.toString();
            String report = "\n--------- Error ---------\n" + errorMessage + "\n\n";
            report += "--------- Thread ---------\n" + threadInfo + "\n\n";
            report += "--------- Stack trace ---------\n";
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
            report += "-------------------------------\n\n";

            report += "--------- Cause ---------\n";
            Throwable cause = e.getCause();
            if (cause != null) {
                errorCause= cause.toString();
                report += errorCause + "\n\n";
                arr = cause.getStackTrace();
                for (int i = 0; i < arr.length; i++) {
                    report += "    " + arr[i].toString() + "\n";
                }
            }
            report += "-------------------------------\n\n";
            return report;
        } catch (Exception e1) {
            e.printStackTrace();
        }
        return e.toString();
    }
}
