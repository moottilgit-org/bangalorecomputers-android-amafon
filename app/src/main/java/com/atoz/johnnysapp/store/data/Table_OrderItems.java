package com.atoz.johnnysapp.store.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;

public class Table_OrderItems {
    public static final String TABLE_NAME= "order_items";

    public static final String FIELD_LID= "lid";
    public static final String FIELD_ID= "id";
    public static final String FIELD_OID= "order_id";
    public static final String FIELD_ITEM_NAME= "item_name";
    public static final String FIELD_ITEM_LINK= "item_link";
    public static final String FIELD_QTY= "qty";
    public static final String FIELD_RATE= "rate";
    public static final String FIELD_ITEM_STATUS= "item_status";//'AVAIL','EMPTY'
    public static final String FIELD_ORD= "item_order";

    private ContentValues mData;
    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                FIELD_LID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_ID + " INTEGER," +
                FIELD_OID + " INTEGER," +
                FIELD_ITEM_NAME + " VARCHAR(50)," +
                FIELD_ITEM_LINK + " INTEGER," +
                FIELD_QTY + " DOUBLE," +
                FIELD_RATE + " DOUBLE," +
                FIELD_ITEM_STATUS + " VARCHAR(5)," +
                FIELD_ORD + " INTEGER)";
    }

    public static String[] getUpgradeTable() {
        return new String[] {
        };
    }

    public Table_OrderItems() {
        mData = new ContentValues();
        mData.put(FIELD_LID, 0);
        mData.put(FIELD_ID, 0);
        mData.put(FIELD_OID, 0);
        mData.put(FIELD_ITEM_NAME, "");
        mData.put(FIELD_ITEM_LINK, 0);
        mData.put(FIELD_QTY, 0);
        mData.put(FIELD_RATE, 0);
        mData.put(FIELD_ITEM_STATUS, "AVAIL");
        mData.put(FIELD_ORD, 0);

    }

    public void setData(Cursor c) {
        if(c==null||c.getCount()==0||c.getPosition()<0) {
            return;
        }
        mData.put(FIELD_LID, c.getInt(c.getColumnIndex(FIELD_LID)));
        mData.put(FIELD_ID, c.getInt(c.getColumnIndex(FIELD_ID)));
        mData.put(FIELD_OID, c.getInt(c.getColumnIndex(FIELD_OID)));
        mData.put(FIELD_ITEM_NAME, c.getString(c.getColumnIndex(FIELD_ITEM_NAME)));
        mData.put(FIELD_ITEM_LINK, c.getInt(c.getColumnIndex(FIELD_ITEM_LINK)));
        mData.put(FIELD_QTY, c.getDouble(c.getColumnIndex(FIELD_QTY)));
        mData.put(FIELD_RATE, c.getDouble(c.getColumnIndex(FIELD_RATE)));
        mData.put(FIELD_ITEM_STATUS, c.getString(c.getColumnIndex(FIELD_ITEM_STATUS)));
        mData.put(FIELD_ORD, c.getInt(c.getColumnIndex(FIELD_ORD)));

    }
    public void setData(ContentValues data) {
        mData.putAll(data);
    }
    public ContentValues getData() {
        return mData;
    }


    public static String getAllFields() {
        return " " + FIELD_LID + ", " + FIELD_ID + ", " + FIELD_OID + ", " + FIELD_ITEM_NAME + ", " +
                FIELD_ITEM_LINK + ", " + FIELD_QTY + ", " + FIELD_RATE + ", " + FIELD_ITEM_STATUS + ", " +
                FIELD_ORD + " ";
    }

    public String getField(String field) {
        return mData.getAsString(field);
    }
    public int getFieldInt(String field) {
        return mData.getAsInteger(field);
    }
    public double getFieldDouble(String field) {
        return mData.getAsDouble(field);
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

    public static Table_OrderItems get(DatabaseHandler Db, int ID) {
        return get(Db, FIELD_ID, ID);
    }
    public static Table_OrderItems get(DatabaseHandler Db, String field, int value) {
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE " + field + "=" + value)){
            if(c!=null && c.getCount()>0 && c.moveToFirst()) {
                Table_OrderItems data= new Table_OrderItems();
                data.setData(c);
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Table_OrderItems> getList(DatabaseHandler Db, int OID) {
           return getList(Db, " AND " + FIELD_OID + "='" + OID + "' ");
    }
    public static ArrayList<Table_OrderItems> getList(DatabaseHandler Db, String condition) {
        String filter= TextUtils.isEmpty(condition)?"":condition;
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE ID>0 " + filter +
                    " ORDER BY " + FIELD_ORD + " ASC")){
            if(c!=null && c.getCount()>0) {
                ArrayList<Table_OrderItems> list= new ArrayList<>();
                while(c.moveToNext()) {
                    Table_OrderItems data= new Table_OrderItems();
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
}
