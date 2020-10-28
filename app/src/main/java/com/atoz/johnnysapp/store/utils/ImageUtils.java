package com.atoz.johnnysapp.store.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.atoz.johnnysapp.store.BuildConfig;
import com.atoz.johnnysapp.store.R;
import com.atoz.johnnysapp.store.ui.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();
    public static boolean saveBmpToFile(Bitmap bitmap, File fileName, boolean recycle) {
        return saveBmpToFile(bitmap, fileName, Bitmap.CompressFormat.JPEG, 90, recycle);
    }
    public static boolean saveBmpToFile(Bitmap bitmap, File fileName, Bitmap.CompressFormat format, int quality, boolean recycle) {
        try {
            if (bitmap != null) {
                OutputStream outputStream = new FileOutputStream(fileName);
                bitmap.compress(format, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                if(recycle) {
                    bitmap.recycle();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean resizeBitmap(Bitmap bitmap, File dest, int width, int height) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            if (w/width < h/height) {
                // the height ratio is greater so shrink the width and center horiz
                width= w / (h / height);
            } else {
                height= h / (w / width);
            }

            float scaleWidth = ((float) width) / w;
            float scaleHeight = ((float) height) / h;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BITMAP
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap newBitmap= Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
            boolean result= saveBmpToFile(newBitmap, dest, true);
            if(!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean resizeBitmap(File source, File dest, int width, int height) {
        return resizeBitmap(BitmapFactory.decodeFile(source.getAbsolutePath()), dest, width, height);
    }

    public static Uri getUriForFile(Context context, File file) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
                :Uri.fromFile(file);
    }

    public static void applyAvatar(Activity activity, ImageView imgAvatar, String avatar) {
        try {
            if(TextUtils.isEmpty(avatar) || TextUtils.equals(avatar, "default.png")) {
                imgAvatar.setImageResource(R.drawable.wmv);
            } else {
                Picasso.with(activity).load(Http.HTTP_AVATAR + avatar).into(imgAvatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bmp, File file) {
        try {
            if(bmp == null) return;
            FileOutputStream fo= new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fo);
            fo.close();
        } catch (Exception e) {
            android.util.Log.e("saveBitmap", e.toString());
        }
    }
}
