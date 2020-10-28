package com.atoz.johnnysapp.store.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION= 7;
    private static final String DATABASE_NAME= "database.db";
    public final Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //super(context, context.getExternalCacheDir() +"/" + DATABASE_NAME, null, DATABASE_VERSION);
        mContext= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try { db.execSQL(Table_Items.getCreateTable()); } catch (Exception e) {/**/}
        try { db.execSQL(Table_Orders.getCreateTable()); } catch (Exception e) {/**/}
        try { db.execSQL(Table_OrderItems.getCreateTable()); } catch (Exception e) {/**/}
        try { db.execSQL(Table_OrderMessages.getCreateTable()); } catch (Exception e) {/**/}
        try { db.execSQL(Table_Stages.getCreateTable()); } catch (Exception e) {/**/}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        onUpgrade(db, Table_Items.getUpgradeTable());
        onUpgrade(db, Table_Orders.getUpgradeTable());
        onUpgrade(db, Table_OrderItems.getUpgradeTable());
        onUpgrade(db, Table_OrderMessages.getUpgradeTable());
        onUpgrade(db, Table_Stages.getUpgradeTable());
    }

    public void onUpgrade(SQLiteDatabase db, String[] qrys) {
        try {
            if(qrys!=null && qrys.length > 0) {
                for(int i=0; i< qrys.length; i++) {
                    try { db.execSQL(qrys[i]); } catch (Exception e) {/**/}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor query(String SQL) {
        return query(SQL, null);
    }
    public Cursor query(String SQL, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(SQL, selectionArgs);
    }

    public void execute(String SQL) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
    }
    public void execute(String SQL, Object[] bindArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL, bindArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(table, values, whereClause, whereArgs);
    }
}
