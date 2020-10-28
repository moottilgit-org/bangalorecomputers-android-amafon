package com.atoz.johnnysapp.store.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;

public class Table_Orders {
    public static final String TABLE_NAME= "orders";

    public static final String FIELD_LID= "lid";
    public static final String FIELD_ID= "id";
    public static final String FIELD_REF= "order_ref";
    public static final String FIELD_NAME= "order_name";
    public static final String FIELD_NOTES= "order_notes";
    public static final String FIELD_DATE= "order_date";
    public static final String FIELD_STATUS= "order_status";//'NEW','--ACCEPT--','REJECT','FINISH','CANCEL'
    public static final String FIELD_PAYMENT_DONE= "payment_done";
    public static final String FIELD_PAYMENT_TIME= "payment_time";
    public static final String FIELD_STAGE= "order_stage";

    public static final String FIELD_USER= "user_id";
    public static final String FIELD_USER_NAME= "user_name";
    public static final String FIELD_USER_NUMBER= "user_number";
    public static final String FIELD_USER_AVATAR= "user_avatar";

    public static final String FIELD_PLACE_NAME= "place_name";
    public static final String FIELD_ADDRESS= "place_address";
    public static final String FIELD_LANDMARK= "place_landmark";
    public static final String FIELD_DIRECTION= "place_direction";
    public static final String FIELD_LAT= "place_lat";
    public static final String FIELD_LNG= "place_lng";

    private ContentValues mData;
    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                FIELD_LID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_ID + " INTEGER," +
                FIELD_REF + " INTEGER," +
                FIELD_NAME + " VARCHAR(50)," +
                FIELD_NOTES + " TEXT," +
                FIELD_DATE + " DATETIME," +
                FIELD_STATUS + " VARCHAR(10)," +
                FIELD_STAGE + " INTEGER," +
                FIELD_PAYMENT_DONE + " VARCHAR(1)," +
                FIELD_PAYMENT_TIME + " DATETIME," +
                FIELD_USER + " INTEGER," +
                FIELD_USER_NAME + " VARCHAR(50)," +
                FIELD_USER_NUMBER + " VARCHAR(15)," +
                FIELD_USER_AVATAR + " VARCHAR(100)," +
                FIELD_PLACE_NAME + " VARCHAR(50)," +
                FIELD_ADDRESS + " TEXT," +
                FIELD_LANDMARK + " TEXT," +
                FIELD_DIRECTION + " TEXT," +
                FIELD_LAT + " DOUBLE," +
                FIELD_LNG + " DOUBLE)";
    }

    public static String[] getUpgradeTable() {
        return new String[] {
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_PAYMENT_DONE + " VARCHAR(1) ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_PAYMENT_TIME + " DATETIME ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_PLACE_NAME + " VARCHAR(50) ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_ADDRESS + " TEXT ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_LANDMARK + " TEXT ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_DIRECTION + " TEXT ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_LAT + " DOUBLE ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_LNG + " DOUBLE ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_STAGE + " INTEGER ",
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + FIELD_REF + " INTEGER "
        };
    }

    public Table_Orders() {
        mData = new ContentValues();
        mData.put(FIELD_LID, 0);
        mData.put(FIELD_ID, 0);
        mData.put(FIELD_REF, 0);
        mData.put(FIELD_NAME, "");
        mData.put(FIELD_NOTES, "");
        mData.put(FIELD_DATE, "");
        mData.put(FIELD_STATUS, "NEW");
        mData.put(FIELD_STAGE, 1);
        mData.put(FIELD_PAYMENT_DONE, "");
        mData.put(FIELD_PAYMENT_TIME, "");
        mData.put(FIELD_USER, 0);
        mData.put(FIELD_USER_NAME, "");
        mData.put(FIELD_USER_NUMBER, "");
        mData.put(FIELD_USER_AVATAR, "");
        mData.put(FIELD_PLACE_NAME, "");
        mData.put(FIELD_ADDRESS, "");
        mData.put(FIELD_LANDMARK, "");
        mData.put(FIELD_DIRECTION, "");
        mData.put(FIELD_LAT, 0);
        mData.put(FIELD_LNG, 0);
    }

    public void setData(Cursor c) {
        if(c==null||c.getCount()==0||c.getPosition()<0) {
            return;
        }
        mData.put(FIELD_LID, c.getInt(c.getColumnIndex(FIELD_LID)));
        mData.put(FIELD_ID, c.getInt(c.getColumnIndex(FIELD_ID)));
        mData.put(FIELD_REF, c.getInt(c.getColumnIndex(FIELD_REF)));
        mData.put(FIELD_NAME, c.getString(c.getColumnIndex(FIELD_NAME)));
        mData.put(FIELD_NOTES, c.getString(c.getColumnIndex(FIELD_NOTES)));
        mData.put(FIELD_DATE, c.getString(c.getColumnIndex(FIELD_DATE)));
        mData.put(FIELD_STATUS, c.getString(c.getColumnIndex(FIELD_STATUS)));
        mData.put(FIELD_STAGE, c.getInt(c.getColumnIndex(FIELD_STAGE)));
        mData.put(FIELD_PAYMENT_DONE, c.getString(c.getColumnIndex(FIELD_PAYMENT_DONE)));
        mData.put(FIELD_PAYMENT_TIME, c.getString(c.getColumnIndex(FIELD_PAYMENT_TIME)));
        mData.put(FIELD_USER, c.getInt(c.getColumnIndex(FIELD_USER)));
        mData.put(FIELD_USER_NAME, c.getString(c.getColumnIndex(FIELD_USER_NAME)));
        mData.put(FIELD_USER_NUMBER, c.getString(c.getColumnIndex(FIELD_USER_NUMBER)));
        mData.put(FIELD_USER_AVATAR, c.getString(c.getColumnIndex(FIELD_USER_AVATAR)));

        mData.put(FIELD_PLACE_NAME, c.getString(c.getColumnIndex(FIELD_PLACE_NAME)));
        mData.put(FIELD_ADDRESS, c.getString(c.getColumnIndex(FIELD_ADDRESS)));
        mData.put(FIELD_LANDMARK, c.getString(c.getColumnIndex(FIELD_LANDMARK)));
        mData.put(FIELD_DIRECTION, c.getString(c.getColumnIndex(FIELD_DIRECTION)));
        mData.put(FIELD_LAT, c.getDouble(c.getColumnIndex(FIELD_LAT)));
        mData.put(FIELD_LNG, c.getDouble(c.getColumnIndex(FIELD_LNG)));
    }
    public void setData(ContentValues data) {
        mData.putAll(data);
    }
    public ContentValues getData() {
        return mData;
    }


    public static String getAllFields() {
        return " " + FIELD_LID + ", " + FIELD_ID + ", " + FIELD_REF + ", " + FIELD_NAME + ", " + FIELD_NOTES + ", " +
                FIELD_DATE + ", " + FIELD_STATUS + ", " + FIELD_STAGE + ", " + FIELD_PAYMENT_DONE + ", " +
                FIELD_PAYMENT_TIME + ", " + FIELD_USER + ", " + FIELD_USER_NAME + ", " +
                FIELD_USER_NUMBER + ", " + FIELD_USER_AVATAR + ", " + FIELD_PLACE_NAME + ", " +
                FIELD_ADDRESS + ", " + FIELD_LANDMARK + ", " + FIELD_DIRECTION + ", " +
                FIELD_LAT + ", " + FIELD_LNG + " ";
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
    public void setFieldDouble(String field, double value) {
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

    public static Table_Orders get(DatabaseHandler Db, int ID) {
        return get(Db, FIELD_ID, ID);
    }
    public static Table_Orders get(DatabaseHandler Db, String field, int value) {
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE " + field + "=" + value)){
            if(c!=null && c.getCount()>0 && c.moveToFirst()) {
                Table_Orders data= new Table_Orders();
                data.setData(c);
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Table_Orders> getList(DatabaseHandler Db) {
        return getList(Db, "");
    }
    public static ArrayList<Table_Orders> getActive(DatabaseHandler Db) {
        return getList(Db, " AND ("+FIELD_STATUS+" IN ('NEW','ACCEPT') OR DATE("+FIELD_DATE+")=DATE() )");
    }
    public static ArrayList<Table_Orders> getList(DatabaseHandler Db, String condition) {
        String filter= TextUtils.isEmpty(condition)?"":condition;
        try (Cursor c= Db.query("SELECT " + getAllFields() + " FROM " + TABLE_NAME +
                    " WHERE ID>0 " + filter +
                    " ORDER BY " + FIELD_DATE + " DESC")){
            if(c!=null && c.getCount()>0) {
                ArrayList<Table_Orders> list= new ArrayList<>();
                while(c.moveToNext()) {
                    Table_Orders data= new Table_Orders();
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
