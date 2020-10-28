package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Log {
    public static String LOG_FILE_PATH= "";
    public static void initLog(Context mContext) {
        try {
            File file = mContext.getExternalFilesDir("logs");
            file.mkdirs();
            LOG_FILE_PATH= file.getAbsolutePath();
        } catch (Exception e) {
            //nothing to do
        }
    }
    public static void log(String msg, String fileName) {
        try {
            File file= new File(LOG_FILE_PATH + "/" + fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            String time= "";
            try {
                time= DateUtils.getLocalDateTime(true);
            } catch (Exception e) {
                time= DateUtils.getDateTimeStr();
            }
            Log.writeFile(file, "\n" + time + ": " + msg);
        } catch (Exception e) {
            android.util.Log.e("log", e.toString());
        }
    }

    public static void writeFile(File file, String text) {
        try {
            if(file.length()/1024.0 >= 512) {
                file.renameTo(new File(file.toString().replace(".log", "_" + String.valueOf(System.currentTimeMillis()) + ".log")));
            }
        } catch (Exception e) {}

        write(file, text);
    }

    public static void write(File file, String text) {
        FileWriter fileWriter= null;
        BufferedWriter bufferedWriter= null;
        try {
            if(!file.exists()) {
                file.createNewFile();
            }

            fileWriter= new FileWriter(file, true);
            bufferedWriter= new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
        } catch (Exception e) {
            android.util.Log.e("write", e.toString());
        } finally {
            try { bufferedWriter.close(); } catch (Exception e) { }
            try { fileWriter.close(); } catch (Exception e) { }
        }
    }

    public static String removeVowels(String text) {
        return TextUtils.isEmpty(text)||text.length()<2?text:
                text.charAt(1) + text.substring(2).replaceAll("[AaEeIiOoUu]", "");
    }
}
