package com.atoz.johnnysapp.store.online;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.atoz.johnnysapp.store.data.Table_OrderItems;
import com.atoz.johnnysapp.store.utils.Http;
import com.atoz.johnnysapp.store.utils.Pref;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Writer {
    public static boolean IS_WRITING= false;

    public static void accepted(Context context, int id, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.orderStatus("order_accepted", id, 0,
                                                 Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                                                 Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void reject(Context context, int id, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.orderStatus("order_rejected", id,0,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void finish(Context context, int id, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.orderStatus("order_finished", id,0,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStage(Context context, int orderID, int stageID, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.orderStatus("update_stage", orderID,stageID,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void paymentReceived(Context context, int id, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.orderStatus("payment_received", id, 0,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void message(Context context, int id, String message, File file, int msgRef, String msgRefSrc, AsyncListener asyncListener) {
        try {
            if(TextUtils.isEmpty(message)) {
                return;
            }
            MultipartBody.Part fileData= null;
            if(file != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
                fileData = MultipartBody.Part.createFormData("attachment", file.getName(), requestFile);
            }
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.sendMessage(
                    RequestBody.create(MediaType.parse("text/plain"),"" + id),
                    RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), message),
                    RequestBody.create(MediaType.parse("text/plain"), "" + msgRef),
                    RequestBody.create(MediaType.parse("text/plain"), msgRefSrc),
                    fileData,
                    RequestBody.create(MediaType.parse("text/plain"), Pref.getMobile(context)),
                    RequestBody.create(MediaType.parse("text/plain"), Pref.getPassword(context)),
                    RequestBody.create(MediaType.parse("text/plain"), Http.getFCMToken()),
                    RequestBody.create(MediaType.parse("text/plain"), Http.getDeviceID(context)),
                    RequestBody.create(MediaType.parse("text/plain"), Http.getDeviceName()));
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void update_upi(Context context, String upi_id, String upi_uri, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.updatePayment(upi_id,upi_uri,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(Context context, int id, ArrayList<Table_OrderItems> mItems, AsyncListener asyncListener) {
        try {
            HashMap<String, String> order_items= new HashMap<>();
            for(int i=0, j=mItems.size(); i<j; i++) {
                order_items.put("order_items["+i+"][id]", mItems.get(i).getField(Table_OrderItems.FIELD_ID));
                order_items.put("order_items["+i+"][item_name]", mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_NAME));
                if(mItems.get(i).getData().containsKey("item_name_linked")) {
                    order_items.put("order_items["+i+"][item_name_linked]", mItems.get(i).getField("item_name_linked"));
                } else {
                    order_items.put("order_items["+i+"][item_name_linked]", mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_NAME));
                }
                order_items.put("order_items["+i+"][item_link]", mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_LINK));
                order_items.put("order_items["+i+"][qty]", mItems.get(i).getField(Table_OrderItems.FIELD_QTY));
                order_items.put("order_items["+i+"][rate]", mItems.get(i).getField(Table_OrderItems.FIELD_RATE));
                order_items.put("order_items["+i+"][item_status]", mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_STATUS));
                order_items.put("order_items["+i+"][item_order]", mItems.get(i).getField(Table_OrderItems.FIELD_ORD));
            }

            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.updateOrder(id, order_items,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            MultipartEntity entity = new MultipartEntity();
//            entity.addPart("task", new StringBody("order_update"));
//            entity.addPart("order_id", new StringBody("" + id));
//
//            for(int i=0, j=mItems.size(); i<j; i++) {
//                entity.addPart("order_items["+i+"][id]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ID)));
//                entity.addPart("order_items["+i+"][item_name]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_NAME), Charset.forName("UTF-8")));
//                if(mItems.get(i).getData().containsKey("item_name_linked")) {
//                    entity.addPart("order_items["+i+"][item_name_linked]", new StringBody(mItems.get(i).getField("item_name_linked"), Charset.forName("UTF-8")));
//                } else {
//                    entity.addPart("order_items["+i+"][item_name_linked]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_NAME), Charset.forName("UTF-8")));
//                }
//                entity.addPart("order_items["+i+"][item_link]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_LINK)));
//                entity.addPart("order_items["+i+"][qty]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_QTY)));
//                entity.addPart("order_items["+i+"][rate]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_RATE)));
//                entity.addPart("order_items["+i+"][item_status]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ITEM_STATUS)));
//                entity.addPart("order_items["+i+"][item_order]", new StringBody(mItems.get(i).getField(Table_OrderItems.FIELD_ORD)));
//            }
//            write(context, entity, asyncListener);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void msg_got(Context context, String ids, AsyncListener asyncListener) {
        if(TextUtils.isEmpty(ids)) {
            return;
        }
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.updateMessage("msg_got", ids,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void msg_read(Context context, String ids, AsyncListener asyncListener) {
        if(TextUtils.isEmpty(ids)) {
            return;
        }
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.updateMessage("msg_read", ids,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refer(Context context, String number, String name, AsyncListener asyncListener) {
        try {
            API_Calls api = RetrofitApiClass.getApi();
            Call<ResponseBody> call = api.refer(number, name,
                    Pref.getMobile(context), Pref.getPassword(context), Http.getFCMToken(),
                    Http.getDeviceID(context), Http.getDeviceName());
            write(context, call, asyncListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void write(Context context, Call<ResponseBody> call, AsyncListener asyncListener) {
        try {
            if(IS_WRITING) {
                if(asyncListener != null) {
                    asyncListener.onFinish();
                }
                return;
            }
            AsyncListener mListener= new AsyncListener() {
                @Override
                public void onStart() {
                    IS_WRITING= true;
                    if(asyncListener != null) {
                        asyncListener.onStart();
                    }
                }
                @Override
                public void onProgressUpdate(Integer... values) {
                    IS_WRITING= true;
                    if(asyncListener != null) {
                        asyncListener.onProgressUpdate();
                    }
                }
                @Override
                public void onFinish() {
                    IS_WRITING= false;
                    if(asyncListener != null) {
                        asyncListener.onFinish();
                    }
                }
                @Override
                public void onResult(Boolean result, Http.Response response) {
                    IS_WRITING= false;
                    if(asyncListener != null) {
                        asyncListener.onResult(result, response);
                    }
                }
            };

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseString= response.body().string();
                        Http.Response JResponse= new Http.Response(responseString);
                        if (mListener != null) {
                            mListener.onFinish();
                            mListener.onResult(JResponse.success(), JResponse);
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListener != null) {
                        mListener.onFinish();
                        mListener.onResult(false, null);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Http.Response JResponse= new Http.Response(t.getMessage());
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    if (mListener != null) {
                        mListener.onFinish();
                        mListener.onResult(false, JResponse);
                    }
                }
            });

            if(mListener != null) {
                mListener.onStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getJString(JSONObject data, String key) {
        try {
            return data.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private static int getJInt(JSONObject data, String key) {
        try {
            return data.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
