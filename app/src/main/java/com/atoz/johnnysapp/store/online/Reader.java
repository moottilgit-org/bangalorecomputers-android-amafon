package com.atoz.johnnysapp.store.online;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.atoz.johnnysapp.store.data.DatabaseHandler;
import com.atoz.johnnysapp.store.data.Table_Items;
import com.atoz.johnnysapp.store.data.Table_OrderItems;
import com.atoz.johnnysapp.store.data.Table_OrderMessages;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.data.Table_Stages;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reader {
    public static boolean IS_READING= false;

    private final Context mContext;
    private final AsyncListener mAsyncListener;

    private Http.Response JResponse;
    public Reader(Context context, AsyncListener asyncListener) {
        mContext= context;
        mAsyncListener = asyncListener;
    }

    public static void downloadAudio(Context context, String filename, File saveAs, Runnable runnable) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.downloadAudio(filename);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        try (InputStream input= response.body().byteStream()) {
                            try (OutputStream output = new FileOutputStream(saveAs)) {
                                byte[] data = new byte[1024 * 1024];
                                int count;
                                while ((count = input.read(data)) != -1) {
                                    output.write(data, 0, count);
                                }
                                output.flush();
                            }
                            if(runnable != null) { runnable.run(); }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read(Context context, AsyncListener asyncListener) {
        try {
            if(IS_READING) {
                if(asyncListener != null) {
                    asyncListener.onFinish();
                }
                return;
            }
            AsyncListener mListener= new AsyncListener() {
                @Override
                public void onStart() {
                    IS_READING= true;
                    if(asyncListener != null) {
                        asyncListener.onStart();
                    }
                }
                @Override
                public void onProgressUpdate(Integer... values) {
                    IS_READING= true;
                    if(asyncListener != null) {
                        asyncListener.onProgressUpdate();
                    }
                }
                @Override
                public void onFinish() {
                    IS_READING= false;
                    if(asyncListener != null) {
                        asyncListener.onFinish();
                    }
                }
                @Override
                public void onResult(Boolean result, Http.Response response) {
                    IS_READING= false;
                    if(asyncListener != null) {
                        asyncListener.onResult(result, response);
                    }
                }
            };

            Reader sync= new Reader(context, mListener);
            sync.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void read() {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.read(Pref.getMobile(mContext), Pref.getPassword(mContext),
                                         Http.getFCMToken(), Http.getDeviceID(mContext), Http.getDeviceName());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString= response.body().string();
                        if(!TextUtils.isEmpty(responseString)) {
                            JResponse= new Http.Response(responseString);
                            if(JResponse.success()) {
                                JSONObject data= JResponse.data();
                                DatabaseHandler Db= new DatabaseHandler(mContext);
                                try {
                                    saveItems(Db, data);
                                    saveStages(Db, data);
                                    saveOrders(Db, data);
                                    saveOrderItems(Db, data);
                                    saveOrderMessages(Db, data);
                                } finally {
                                    Db.close();
                                }
                                onPostExecute(true);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onPostExecute(false);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    JResponse= new Http.Response(t.getMessage());
                    Toast.makeText(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    onPostExecute(false);
                }
            });
            onPreExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPreExecute() {
        if(mAsyncListener != null) {
            mAsyncListener.onStart();
        }
    }

    protected void onProgressUpdate(Integer... values) {
        if(mAsyncListener != null) {
            mAsyncListener.onProgressUpdate(values);
        }
    }

    protected void onPostExecute(Boolean result) {
        try {
            if (mAsyncListener != null) {
                mAsyncListener.onFinish();
                mAsyncListener.onResult(result, JResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrders(DatabaseHandler Db, JSONObject mainData) {
        try {
            if(mainData != null && mainData.has("orders")) {
                JSONArray dataList= mainData.getJSONArray("orders");
                Table_Orders.deleteAll(Db);
                for(int i=0; i<dataList.length(); i++) {
                    JSONObject data= dataList.getJSONObject(i);
                    Table_Orders item= new Table_Orders();
                    item.setFieldInt(Table_Orders.FIELD_ID, getJInt(data, "id"));
                    item.setFieldInt(Table_Orders.FIELD_REF, getJInt(data, "order_ref"));
                    item.setField(Table_Orders.FIELD_NAME, getJString(data, "order_name"));
                    item.setField(Table_Orders.FIELD_NOTES, getJString(data, "order_notes"));
                    item.setField(Table_Orders.FIELD_DATE, getJString(data, "order_date"));
                    item.setField(Table_Orders.FIELD_STATUS, getJString(data, "order_status"));
                    item.setFieldInt(Table_Orders.FIELD_STAGE, getJInt(data, "order_stage"));
                    item.setField(Table_Orders.FIELD_PAYMENT_DONE, getJString(data, "payment_done"));
                    item.setField(Table_Orders.FIELD_PAYMENT_TIME, getJString(data, "payment_time"));
                    item.setFieldInt(Table_Orders.FIELD_USER, getJInt(data, "user_id"));
                    item.setField(Table_Orders.FIELD_USER_NAME, getJString(data, "user_name"));
                    item.setField(Table_Orders.FIELD_USER_NUMBER, getJString(data, "user_number"));
                    item.setField(Table_Orders.FIELD_USER_AVATAR, getJString(data, "user_avatar"));
                    item.setField(Table_Orders.FIELD_PLACE_NAME, getJString(data, "place_name"));
                    item.setField(Table_Orders.FIELD_ADDRESS, getJString(data, "place_address"));
                    item.setField(Table_Orders.FIELD_LANDMARK, getJString(data, "place_landmark"));
                    item.setField(Table_Orders.FIELD_DIRECTION, getJString(data, "place_direction"));
                    item.setFieldDouble(Table_Orders.FIELD_LAT, getJDouble(data, "place_lat"));
                    item.setFieldDouble(Table_Orders.FIELD_LNG, getJDouble(data, "place_lng"));
                    item.save(Db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrderItems(DatabaseHandler Db, JSONObject mainData) {
        try {
            if(mainData != null && mainData.has("order_items")) {
                JSONArray dataList= mainData.getJSONArray("order_items");
                Table_OrderItems.deleteAll(Db);
                for(int i=0; i<dataList.length(); i++) {
                    JSONObject data= dataList.getJSONObject(i);
                    Table_OrderItems item= new Table_OrderItems();
                    item.setFieldInt(Table_OrderItems.FIELD_ID, getJInt(data, "id"));
                    item.setFieldInt(Table_OrderItems.FIELD_OID, getJInt(data, "order_id"));
                    item.setField(Table_OrderItems.FIELD_ITEM_NAME, getJString(data, "item_name"));
                    item.setFieldInt(Table_OrderItems.FIELD_ITEM_LINK, getJInt(data, "item_link"));
                    item.setField(Table_OrderItems.FIELD_QTY, getJString(data, "qty"));
                    item.setField(Table_OrderItems.FIELD_RATE, getJString(data, "rate"));
                    item.setField(Table_OrderItems.FIELD_ITEM_STATUS, getJString(data, "item_status"));
                    item.setFieldInt(Table_OrderItems.FIELD_ORD, getJInt(data, "item_order"));
                    item.save(Db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrderMessages(DatabaseHandler Db, JSONObject mainData) {
        try {
            if(mainData != null && mainData.has("order_msgs")) {
                JSONArray dataList= mainData.getJSONArray("order_msgs");
                Table_OrderMessages.deleteAll(Db);
                for(int i=0; i<dataList.length(); i++) {
                    JSONObject data= dataList.getJSONObject(i);
                    Table_OrderMessages item= new Table_OrderMessages();
                    item.setFieldInt(Table_OrderMessages.FIELD_ID, getJInt(data, "id"));
                    item.setFieldInt(Table_OrderMessages.FIELD_OID, getJInt(data, "order_id"));
                    item.setField(Table_OrderMessages.FIELD_SENT_BY, getJString(data, "sent_by"));
                    item.setField(Table_OrderMessages.FIELD_SENT_ON, getJString(data, "sent_on"));
                    item.setField(Table_OrderMessages.FIELD_MSG_TEXT, getJString(data, "msg_text"));
                    item.setField(Table_OrderMessages.FIELD_STATUS, getJString(data, "msg_status"));
                    item.setFieldInt(Table_OrderMessages.FIELD_REF, getJInt(data, "msg_ref"));
                    item.setField(Table_OrderMessages.FIELD_REF_SRC, getJString(data, "msg_ref_src"));
                    item.save(Db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveItems(DatabaseHandler Db, JSONObject mainData) {
        try {
            if(mainData != null && mainData.has("items")) {
                JSONArray dataList= mainData.getJSONArray("items");
                Table_Items.deleteAll(Db);
                for(int i=0; i<dataList.length(); i++) {
                    JSONObject data= dataList.getJSONObject(i);
                    Table_Items item= new Table_Items();
                    item.setFieldInt(Table_Items.FIELD_ID, getJInt(data, "id"));
                    item.setField(Table_Items.FIELD_NAME, getJString(data, "item_name"));
                    item.setField(Table_Items.FIELD_RATE, getJString(data, "rate"));
                    item.setField(Table_Items.FIELD_NAMES, getJString(data, "other_names"));
                    item.save(Db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStages(DatabaseHandler Db, JSONObject mainData) {
        try {
            if(mainData != null && mainData.has("stages")) {
                JSONArray dataList= mainData.getJSONArray("stages");
                Table_Stages.deleteAll(Db);
                for(int i=0; i<dataList.length(); i++) {
                    JSONObject data= dataList.getJSONObject(i);
                    Table_Stages item= new Table_Stages();
                    item.setFieldInt(Table_Stages.FIELD_ID, getJInt(data, "id"));
                    item.setField(Table_Stages.FIELD_NAME, getJString(data, "stage_name"));
                    item.setFieldInt(Table_Stages.FIELD_ORDER, getJInt(data, "stage_order"));
                    item.save(Db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJString(JSONObject data, String key) {
        try {
            return data.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static int getJInt(JSONObject data, String key) {
        try {
            return data.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static double getJDouble(JSONObject data, String key) {
        try {
            return data.getDouble(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
