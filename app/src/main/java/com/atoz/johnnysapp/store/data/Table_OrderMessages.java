package com.atoz.johnnysapp.store.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.atoz.johnnysapp.store.ui.order_details.ChatHelper;

import java.util.ArrayList;

public class Table_OrderMessages {
    public static final String TABLE_NAME= "order_messages";

    public static final String FIELD_LID= "lid";
    public static final String FIELD_ID= "id";
    public static final String FIELD_OID= "order_id";
    public static final String FIELD_SENT_BY= "sent_by";//'S','U'
    public static final String FIELD_SENT_ON= "sent_on";
    public static final String FIELD_MSG_TEXT= "msg_text";
    public static final String FIELD_STATUS= "msg_status";//'S','D','R'
    public static final String FIELD_REF= "msg_ref";
    public static final String FIELD_REF_SRC= "msg_ref_src";

    private ContentValues mData;
    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                FIELD_LID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_ID + " INTEGER," +
                FIELD_OID + " INTEGER," +
                FIELD_SENT_BY + " VARCHAR(1)," +
                FIELD_SENT_ON + " DATETIME," +
                FIELD_MSG_TEXT + " TEXT," +
                FIELD_STATUS + " VARCHAR(1)," +
                FIELD_REF + " INTEGER," +
                FIELD_REF_SRC + " VARCHAR(1))";
    }

    public static String[] getUpgradeTable() {
        return new String[] {
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_REF + " INTEGER ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_REF_SRC + " VARCHAR(1) ",
        };
    }

    public Table_OrderMessages() {
        mData = new ContentValues();
        mData.put(FIELD_LID, 0);
        mData.put(FIELD_ID, 0);
        mData.put(FIELD_OID, 0);
        mData.put(FIELD_SENT_BY, "S");
        mData.put(FIELD_SENT_ON, "");
        mData.put(FIELD_MSG_TEXT, "");
        mData.put(FIELD_STATUS, "S");
        mData.put(FIELD_REF, 0);
        mData.put(FIELD_REF_SRC, "I");
    }

    public void setData(Cursor c) {
        if(c==null||c.getCount()==0||c.getPosition()<0) {
            return;
        }
        mData.put(FIELD_LID, c.getInt(c.getColumnIndex(FIELD_LID)));
        mData.put(FIELD_ID, c.getInt(c.getColumnIndex(FIELD_ID)));
        mData.put(FIELD_OID, c.getInt(c.getColumnIndex(FIELD_OID)));
        mData.put(FIELD_SENT_BY, c.getString(c.getColumnIndex(FIELD_SENT_BY)));
        mData.put(FIELD_SENT_ON, c.getString(c.getColumnIndex(FIELD_SENT_ON)));
        mData.put(FIELD_MSG_TEXT, c.getString(c.getColumnIndex(FIELD_MSG_TEXT)));
        mData.put(FIELD_STATUS, c.getString(c.getColumnIndex(FIELD_STATUS)));
        mData.put(FIELD_REF, c.getInt(c.getColumnIndex(FIELD_REF)));
        mData.put(FIELD_REF_SRC, c.getString(c.getColumnIndex(FIELD_REF_SRC)));
    }
    public void setData(ContentValues data) {
        mData.putAll(data);
    }
    public ContentValues getData() {
        return mData;
    }


    public static String getAllFields() {
        return " " + FIELD_LID + ", " + FIELD_ID + ", " + FIELD_OID + ", " + FIELD_SENT_BY + ", " +
                FIELD_SENT_ON + ", " + FIELD_MSG_TEXT + ", " + FIELD_STATUS + ", " +
                FIELD_REF + ", " + FIELD_REF_SRC;
    }

    public String getField(String field) {
        return mData.getAsString(field);
    }
    public int getFieldInt(String field) {
        return mData.getAsInteger(field);
    }

    public void setField(String field, String value) {
        mData.put(field, value);
    }
    public void setFieldInt(String field, int value) {
        mData.put(field, value);
    }

    public void save(DatabaseHandler Db) {
        if(getFieldInt(FIELD_LID)>0) {
            Db.update(TABLE_NAME, mData, FIELD_LID + "=?", new String[] { ""+getFieldInt(FIELD_LID) } );
        } else {
            mData.remove(FIELD_LID);
            int ID= (int) Db.insert(TABLE_NAME, FIELD_LID, mData);
            setFieldInt(FIELD_LID, ID);
        }
    }

    public static boolean delete(DatabaseHandler Db, int ID) {
        try {
            Db.getWritableDatabase().delete(TABLE_NAME, FIELD_ID + "=?", new String[] {"" + ID});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteAll(DatabaseHandler Db) {
        try {
            Db.getWritableDatabase().delete(TABLE_NAME, "", null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Table_OrderMessages get(DatabaseHandler Db, int ID) {
        return get(Db, FIELD_ID, ID);
    }

    public static Table_OrderMessages get(DatabaseHandler Db, String field, int value) {
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE " + field + "=" + value)){
            if(c!=null && c.getCount()>0 && c.moveToFirst()) {
                Table_OrderMessages data= new Table_OrderMessages();
                data.setData(c);
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Table_OrderMessages> getList(DatabaseHandler Db, int OID) {
        return getList(Db, " AND " + FIELD_OID + "='" + OID + "' ");
    }

    public static ArrayList<Table_OrderMessages> getUnread(DatabaseHandler Db, int OID) {
        return getList(Db, " AND " + FIELD_OID + "='" + OID + "' " +
                " AND " + FIELD_SENT_BY + "='U' AND " + FIELD_STATUS + "<>'R' ");
    }
    public static ArrayList<Table_OrderMessages> getList(DatabaseHandler Db, String condition) {
        String filter= TextUtils.isEmpty(condition)?"":condition;
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE ID>0 " + filter +
                    " ORDER BY " + FIELD_SENT_ON + " ASC")){
            if(c!=null && c.getCount()>0) {
                ArrayList<Table_OrderMessages> list= new ArrayList<>();
                while(c.moveToNext()) {
                    Table_OrderMessages data= new Table_OrderMessages();
                    data.setData(c);
                    list.add(data);
                }
                return list;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMessageRef(DatabaseHandler Db, int ref, String type) {
        try {
            if(TextUtils.equals("I", type)) {
                Table_OrderItems items= Table_OrderItems.get(Db, ref);
                if(items != null) {
                    return "Item: " + items.getField(Table_OrderItems.FIELD_ITEM_NAME);
                }
            } else if(TextUtils.equals("M", type)) {
                Table_OrderMessages message= Table_OrderMessages.get(Db, ref);
                if(message != null) {
                    String text= message.getField(Table_OrderMessages.FIELD_MSG_TEXT);
                    text= TextUtils.equals(text, ChatHelper.ATTACHED_AUDIO)?"Voice Recording":text;
                    return "Msg: " + text;
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
