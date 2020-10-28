package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.atoz.johnnysapp.store.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static void shareAFile(Context context, String path) {
        shareAFile(context, new File(path));
    }
    public static void shareAFile(Context context, File file) {
        openOrShareFile(Intent.ACTION_SEND, context, file);
    }
    public static void openAFile(Context context, String path) {
        openAFile(context, new File(path));
    }
    public static void openAFile(Context context, File file) {
        openOrShareFile(Intent.ACTION_VIEW, context, file);
    }
    public static void openOrShareFile(String action, Context context, File file) {
        try {
            Uri uri= Build.VERSION.SDK_INT >= Build.VERSION_CODES.N?getUriForFile(context, file):Uri.fromFile(file);
            Intent intent= new Intent(action);

            String mime = "*/*";
            if (file.isDirectory()) {
                mime = "resource/folder";
            } else {
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String name= file.getPath().replace(" ", "").replace("@","");
                if (mimeTypeMap.hasExtension(MimeTypeMap.getFileExtensionFromUrl(name))) {
                    mime = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(name));
                }
            }
            if(action==Intent.ACTION_VIEW) {
                intent.setDataAndType(uri, mime);
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType(mime);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            //context.startActivity(Intent.createChooser(intent, "View with..."));
            context.startActivity(intent);
        } catch(Exception e) {
            Log.e("openAFile", e.toString());
        }
    }
    public static Uri getUriForFile(Context context, File file) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
                :Uri.fromFile(file);
    }

    public static void moveFile(File from, File to, boolean delete) {
        try {
            if(from.exists() && from.isFile()) {
                FileInputStream fi= new FileInputStream(from);
                FileOutputStream fo= new FileOutputStream(to);
                try {
                    FileChannel src= fi.getChannel();
                    FileChannel dst= fo.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } finally {
                    fi.close();
                    fo.close();
                }
                if(delete) {
                    from.delete();
                }
            }
        } catch(Exception e) {
            Log.e("moveFile", e.toString());
        }
    }

    public static void clearADirectory(File file) {
        try {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    if(aFile.isDirectory()) {
                        clearADirectory(aFile);
                    }
                    aFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
