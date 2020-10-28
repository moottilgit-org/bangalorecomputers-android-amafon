package com.atoz.johnnysapp.store.utils.updater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.atoz.johnnysapp.store.BuildConfig;
import com.atoz.johnnysapp.store.utils.ImageUtils;
import com.atoz.johnnysapp.store.utils.Pref;
import com.atoz.johnnysapp.store.utils.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class UpdaterHelper extends AsyncTask<Void,Void,Boolean> {
    private static final String UPDATE_JSON_URL= "https://smebusinesssoftware.com/downloads/updater_jstore.json";
    private static final String UPDATE_CHECK_LAST_DAY= "UPDATE_CHECK_LAST_DAY";


    private static final String TAG= UpdaterHelper.class.getSimpleName();
    private final String urlString;
    private final File saveToFile;
    private final UpdaterListener mListener;

    private boolean aborted;
    private String mErrors= "";

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr == null ? null : connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
            //InetAddress.getByName(hostName).isReachable(timeOut)
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
/*

    public static UpdaterHelper saveUrl(Context context, String urlString, File saveToFile, UpdaterListener listener) {
        if(!isOnline(context)) {
            if(listener != null) {
                listener.onError("No Internet Connection");
            }
            return null;
        }
        UpdaterHelper downloader= new UpdaterHelper(urlString, saveToFile, listener);
        downloader.start();
        return downloader;
    }
*/

    public static void CheckForUpdatesIfRequired(Activity context, boolean showProgress) {
        try {
            int lastDay= Pref.init(context).getInt(UPDATE_CHECK_LAST_DAY, 0);
            if(lastDay==0 || lastDay != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                UpdaterHelper.CheckForUpdates(context, showProgress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void CheckForUpdates(Activity context, boolean showProgress) {
        if(!isOnline(context)) {
            if(showProgress) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        ProgressDialog progressDialog= new ProgressDialog(context);
        if(showProgress) {
            progressDialog.setTitle("Checking for updates");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setProgressPercentFormat(null);
        }

        File file= new File(context.getCacheDir(), "updater.json");
        UpdaterHelper updater= new UpdaterHelper(UPDATE_JSON_URL, file, new UpdaterListener() {
            @Override
            public void onStart() { try { if(showProgress) progressDialog.show(); } catch (Exception e) { e.printStackTrace(); } }
            @Override
            public void onFinish() {
                try { if(showProgress) progressDialog.dismiss(); } catch (Exception e) { e.printStackTrace(); }
                readFileAndUpdate(context, file, showProgress);
            }
            @Override
            public void onProgress(long readBytes, long totalBytes) { }
            @Override
            public void onError(String error) {
                try { if(showProgress) progressDialog.dismiss(); } catch (Exception e) { e.printStackTrace(); }
                if(showProgress) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        updater.start();
    }

    public static void readFileAndUpdate(Activity context, File file, boolean showProgress) {
       try {
           if(!file.exists()) {
               if(showProgress) {
                   Toast.makeText(context, "Invalid response from server[NO_FILE]", Toast.LENGTH_SHORT).show();
               }
               return;
           }

           String contents= readFile(file);
           JSONObject object= new JSONObject(contents);
/*
{
	"versionCode": 16,
    "versionName": "3.1",
	"size": 4345266,
	"url": "https://smebusinesssoftware.com/downloads/leadstodollars.apk"
}
*/
           int versionCode= object.getInt("versionCode");
           if(versionCode <= BuildConfig.VERSION_CODE) {
               if(showProgress) {
                   Toast.makeText(context, "No new updates. You are using the latest version!", Toast.LENGTH_SHORT).show();
               }
               return;
           }
           long total= object.getInt("size");
           String url= object.getString("url");
           boolean importantUpdate= object.has("important") && object.getInt("important")>BuildConfig.VERSION_CODE;
           File apkFile= new File(context.getExternalCacheDir(), "app.apk");
           boolean fileDownloaded= false;
           try {
               final PackageManager pm = context.getPackageManager();
               PackageInfo info= pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
               fileDownloaded= info.versionCode==versionCode && total==apkFile.length();
           } catch (Exception e) {
               e.printStackTrace();
           }

           if(fileDownloaded) {
               AlertDialog.Builder builder= new AlertDialog.Builder(context);
               builder.setTitle("Update")
                       .setMessage((importantUpdate?"Important":"New")+" Update Available. Install now?")
                       .setPositiveButton("Install", (dialog, which) -> {
                           installUpdate(context);
                       })
                        .setNegativeButton("Later", (dialog, which) -> {
                            if(importantUpdate) {
                                System.exit(0);
                            }
                        })
                       .setOnDismissListener(dialog -> {
                           Pref.init(context).setInt(UPDATE_CHECK_LAST_DAY, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                       })
                        .show();
               return;
           }

           AlertDialog.Builder builder= new AlertDialog.Builder(context);
           builder.setTitle("Update")
                   .setMessage((importantUpdate?"Important":"New")+" Update Available. Download now ("+fileSizeStr(total)+")?")
                   .setPositiveButton("Download", (dialog, which) -> {
                       ProgressDialog progressDialog= new ProgressDialog(context);
                       progressDialog.setTitle("Downloading");
                       progressDialog.setIndeterminate(false);
                       progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                       progressDialog.setProgressNumberFormat(null);
                       progressDialog.setCancelable(false);
                       progressDialog.setMax(100);

                       UpdaterHelper updater= new UpdaterHelper(url, apkFile, new UpdaterListener() {
                           @Override
                           public void onStart() {
                               try { progressDialog.show(); } catch (Exception e) { e.printStackTrace(); }
                           }
                           @Override
                           public void onFinish() {
                               try { progressDialog.dismiss(); } catch (Exception e) { e.printStackTrace(); }
                               try { Pref.init(context).setInt(UPDATE_CHECK_LAST_DAY, 0); } catch (Exception e) { e.printStackTrace(); }
                               installUpdate(context);
                           }
                           @Override
                           public void onProgress(long readBytes, long totalBytes) {
                               try {
                                   context.runOnUiThread(() -> {
                                       try {
                                           progressDialog.setProgress((int) ((readBytes * 100) / total));
                                           progressDialog.setTitle(Html.fromHtml("<small>Downloading " +
                                                   fileSizeStr(readBytes) + " / " +
                                                   fileSizeStr(total) + "</small>"));
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   });
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           }
                           @Override
                           public void onError(String error) { try { progressDialog.dismiss(); } catch (Exception e) { e.printStackTrace(); } }
                       });
                       updater.start();
                   })
                   .setNegativeButton("Later", (dialog, which) -> {
                       if(importantUpdate) {
                           System.exit(0);
                       }
                   })
                   .setOnDismissListener(dialog -> {
                       Pref.init(context).setInt(UPDATE_CHECK_LAST_DAY, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                   })
                   .show();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public static void installUpdate(Activity context) {
        try {
            File apkFile= new File(context.getExternalCacheDir(), "app.apk");
            if(!apkFile.exists()) {
                return;
            }
            Uri uri =ImageUtils.getUriForFile(context, apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UpdaterHelper(String urlString, File saveToFile, UpdaterListener listener) {
        this.urlString= urlString;
        this.saveToFile= saveToFile;
        this.mListener= listener;
        this.aborted= false;
    }

    public void start() {
        executeOnExecutor(Executors.newSingleThreadExecutor());
    }
    public void abort() {
        this.aborted= true;
    }

    private boolean checkAborted() {
        if(this.aborted) {
            if(saveToFile.exists()) {
                saveToFile.delete();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mErrors= "";
        if(mListener != null) {
            mListener.onStart();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            int count;
            URL url= new URL(Uri.decode(urlString));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            if(checkAborted()) { return null; }
            //connection.connect();
            if(checkAborted()) { return null; }
            try(
                InputStream input= new BufferedInputStream(connection.getInputStream());
                OutputStream output= new FileOutputStream(saveToFile);
            ) {
                long readBytes= 0, totalBytes= input.available();
                byte[] data = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    readBytes+= count;
                    if (checkAborted()) { return null; }
                    if(mListener != null) {
                        mListener.onProgress(readBytes, totalBytes);
                    }
                    output.write(data, 0, count);
                }
            }
            return true;
        } catch(Exception e) {
            Log.e(TAG, e.toString());
            mErrors= e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(checkAborted()) {
            return;
        }
        if(mListener != null) {
            if(result) {
                mListener.onFinish();
            } else {
                mListener.onError(mErrors);
            }
        }
    }

    /*public static String saveAUrl(final String urlString, final File saveToFile) {
        try {
            int count;
            URL url= new URL(Uri.decode(urlString));
            URLConnection conection= url.openConnection();
            conection.setConnectTimeout(5000);
            conection.setReadTimeout(10000);
            //conection.connect();
            if(!saveToFile.exists()) {
                saveToFile.createNewFile();
            }
            try(
                    InputStream input= new BufferedInputStream(connection.getInputStream());
                    OutputStream output= new FileOutputStream(saveToFile);
            ) {
                byte[] data = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            }
            return "";
        } catch(Exception e) {
            Log.e(TAG, e.toString());
            return e.getMessage();
        }
    }
    public static String readFile(String path, String name) {
        return readFile(new File(path, name));
    }
    */

    public static String readFile(File file) {
        try {
            if(!file.exists()) {
                return "";
            }
            try (BufferedReader br= new BufferedReader(new FileReader(file))) {
                StringBuilder output= new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString().trim();
            }
        } catch (Exception e) {
            Log.e("readFile", e.toString());
            return "";
        }
    }

    public static String fileSizeStr(double size) {
        try {
            String[] sizes= new String[] { "bytes", "KB", "MB", "GB", "TB" };
            for(int i=0; i<sizes.length; i++) {
                if(size < 1024) {
                    return String.format(Locale.getDefault(), "%.2f", size) + " " + sizes[i];
                } else {
                    size= size/1024;
                }
            }
            return Math.round(size) + " bytes";
        } catch(Exception e) {
            Log.e("fileSizeStr", e.toString());
            return "";
        }
    }
}
