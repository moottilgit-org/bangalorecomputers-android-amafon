package com.atoz.johnnysapp.store.receivers_services;

import android.content.Context;
import android.os.Bundle;

import com.atoz.johnnysapp.store.data.DatabaseHandler;
import com.atoz.johnnysapp.store.data.Table_OrderMessages;
import com.atoz.johnnysapp.store.data.Table_Orders;
import com.atoz.johnnysapp.store.online.AsyncListener;
import com.atoz.johnnysapp.store.online.Reader;
import com.atoz.johnnysapp.store.online.Writer;
import com.atoz.johnnysapp.store.ui.Main;
import com.atoz.johnnysapp.store.ui.order_details.OrderDetails;
import com.atoz.johnnysapp.store.utils.Http;

import org.json.JSONObject;

public class Store {
    public static boolean handleStoreNotification(Context context, String title, String body, Bundle receivedData) {
        try {
            DatabaseHandler Db= new DatabaseHandler(context);
            try {
                switch (receivedData.getString("task")) {
                    case "message":
                        JSONObject msg_data= new JSONObject(receivedData.getString("msg_data"));
                        if(msg_data != null) {
                            Table_OrderMessages messages= new Table_OrderMessages();
                            messages.setFieldInt(Table_OrderMessages.FIELD_LID, 0);
                            messages.setFieldInt(Table_OrderMessages.FIELD_ID, Reader.getJInt(msg_data,"id"));
                            messages.setFieldInt(Table_OrderMessages.FIELD_OID, Reader.getJInt(msg_data,"order_id"));
                            messages.setField(Table_OrderMessages.FIELD_SENT_BY, Reader.getJString(msg_data,"sent_by"));
                            messages.setField(Table_OrderMessages.FIELD_SENT_ON, Reader.getJString(msg_data,"sent_on"));
                            messages.setField(Table_OrderMessages.FIELD_MSG_TEXT, Reader.getJString(msg_data,"msg_text"));
                            messages.setFieldInt(Table_OrderMessages.FIELD_REF, Reader.getJInt(msg_data,"msg_ref"));
                            messages.setField(Table_OrderMessages.FIELD_REF_SRC, Reader.getJString(msg_data,"msg_ref_src"));
                            messages.setField(Table_OrderMessages.FIELD_STATUS, "D");
                            messages.save(Db);
                            Writer.msg_got(context, Reader.getJString(msg_data,"id"), null);

                            try {
                                Table_Orders orders = Table_Orders.get(Db, Reader.getJInt(msg_data, "order_id"));
                                orders.setField(Table_Orders.FIELD_STATUS, receivedData.getString("order_status"));
                                orders.setField(Table_Orders.FIELD_STAGE, receivedData.getString("order_stage"));
                                orders.save(Db);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            refreshView();
                        }
                    break;
                    case "new_order":
                        Reader.read(context, new AsyncListener() {
                            @Override
                            public void onStart() { }
                            @Override
                            public void onProgressUpdate(Integer... values) { }
                            @Override
                            public void onFinish() { }
                            @Override
                            public void onResult(Boolean result, Http.Response response) {
                                refreshView();
                            }
                        });
                    break;
                    case "message_got":
                        int msgid1= Integer.parseInt(receivedData.getString("message_id"));
                        Table_OrderMessages messages1= Table_OrderMessages.get(Db, msgid1);
                        if(messages1 != null) {
                            messages1.setField(Table_OrderMessages.FIELD_STATUS, "D");
                            messages1.save(Db);
                            refreshView();
                        }
                    return true;
                    case "message_read":
                        int msgid2= Integer.parseInt(receivedData.getString("message_id"));
                        Table_OrderMessages messages2= Table_OrderMessages.get(Db, msgid2);
                        if(messages2 != null) {
                            messages2.setField(Table_OrderMessages.FIELD_STATUS, "R");
                            messages2.save(Db);
                            refreshView();
                        }
                    return true;
                }
                return false;
            } finally {
                Db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void refreshView() {
        try {
            if(Main.mInstance != null) {
                Main.mInstance.runOnUiThread(Main.mInstance::refreshList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(OrderDetails.mInstance != null) {
                OrderDetails.mInstance.runOnUiThread(()->OrderDetails.mInstance.refreshData(true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
