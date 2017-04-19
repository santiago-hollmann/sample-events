package com.shollmann.events.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public abstract class DbHelper {
    private final Gson gson = new GsonBuilder().create();
    private String tableName;
    private String columnKey;
    private String columnData;
    private String columnDate;
    private boolean autoPurge;
    private SQLiteDatabase db = null;

    DbHelper(Context context, String dbName, int dbVersion,
             String tableName, String columnKey, String columnData, String columnDate, boolean autoPurge) {

        this.tableName = tableName;
        this.columnKey = columnKey;
        this.columnData = columnData;
        this.columnDate = columnDate;
        this.autoPurge = autoPurge;

        SQLiteOpenHelper helper = new DatabaseHelper(context, dbName, dbVersion);
        this.db = helper.getWritableDatabase();

        if (autoPurge) {
            purgeDB();
        }
    }

    public void insert(String key, Object data, long expirationOffset) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(columnKey, key);
            cv.put(columnData, gson.toJson(data));
            cv.put(columnDate, System.currentTimeMillis() + (expirationOffset * 1000));

            int rows = db.update(tableName, cv, columnKey + "=?", new String[]{key});
            if (rows == 0) {
                db.insert(tableName, columnKey, cv);
            }
        } catch (Throwable t) {
            //TODO Log issue when trying to insert an object on the database
        }
    }

    private void purgeDB() {
        //TODO test this change
        if (!autoPurge) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = (System.currentTimeMillis() - (1000 * 60 * 60 * 6));
                db.delete(tableName, columnDate + "<? AND " + columnDate + "!=0", new String[]{String.valueOf(time)});
            }
        }).start();
    }

    public <T> DbItem<T> getDbItem(String key, Type type) {
        if (key == null || type == null) {
            return null;
        }
        Cursor c = db.query(tableName, new String[]{columnKey, columnData, columnDate},
                columnKey + "=?", new String[]{key}, null, null, null);
        DbItem<T> response = null;
        if (c.moveToFirst()) {
            String json;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                long date = c.getLong(c.getColumnIndex(columnDate));
                response = new DbItem<>((T) gson.fromJson(json, type), date);
            } catch (Throwable e) {
                //TODO Log issue with traying to retrieve an object from the database
            }
        }
        c.close();
        return response;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context, String dbName, int dbVersion) {
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s LONG);",
                    tableName, columnKey, columnData, columnDate));
            db.execSQL(String.format("CREATE INDEX %s_%s ON %s(%s);",
                    tableName, columnDate, tableName, columnDate));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);
        }
    }
}