package com.shollmann.events.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.shollmann.events.helper.Constants;

import java.lang.reflect.Type;

public abstract class DbHelperTemplate {
    protected String tableName;
    protected String columnKey;
    protected String columnData;
    protected String columnDate;
    protected boolean autoPurge;

    private SQLiteDatabase db = null;
    private final Gson gson = newGSONInstance();

    private static Gson newGSONInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if (f.getAnnotation(ExcludedFromDBSerialization.class) != null) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                if (clazz.getAnnotation(ExcludedFromDBSerialization.class) != null) {
                    return true;
                }
                return false;
            }
        });
        return gsonBuilder.create();
    }

    public DbHelperTemplate(Context context, String dbName, int dbVersion,
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

    public void close() {
        db.close();
        db = null;
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
        }
    }

    public void insert(String key, Object data) {
        ContentValues cv = new ContentValues();
        cv.put(columnKey, key);
        cv.put(columnData, gson.toJson(data));
        cv.put(columnDate, System.currentTimeMillis() + (60 * 60 * 24 * 365 * 1000));

        int rows = db.update(tableName, cv, columnKey + "=?", new String[]{key});
        if (rows == 0) {
            db.insert(tableName, columnKey, cv);
        }
    }

    public void updateExpiration(String keyPattern, long newExpirationOffset) {
        ContentValues cv = new ContentValues();
        cv.put(columnDate, System.currentTimeMillis() + (newExpirationOffset * 1000));

        db.update(tableName, cv, columnKey + " LIKE ? ESCAPE ?", new String[]{keyPattern, "\\"});
    }

    public void delete(String key) {
        db.delete(tableName, columnKey + "=?", new String[]{key});
    }

    public void deleteAll(String key) {
        db.delete(tableName, columnKey + " LIKE ?", new String[]{key});
    }

    public void clearCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete(tableName, null, null);
            }
        }).start();
    }

    public void invalidate(String cacheKey) {
        ContentValues cv = new ContentValues();
        cv.put(columnDate, 0l);
        db.update(tableName, cv, columnKey + "=?", new String[]{cacheKey});
    }

    public void purgeDB() {
        if (autoPurge) {
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

    public Object getAPIResponse(String key, Type type) {
        Cursor c = db.query(tableName, new String[]{columnKey, columnData, columnDate},
                columnKey + "=?", new String[]{key}, null, null, null);
        Object response = null;
        if (c.moveToFirst()) {
            String json = Constants.EMPTY_STRING;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                response = gson.fromJson(json, type);
            } catch (JsonSyntaxException jse) {
            } catch (Throwable e) {
            }
        }
        c.close();
        return response;
    }

    public <T> DbItem<T> getDbItem(String key, Type type) {
        if (key == null || type == null) {
            return null;
        }
        Cursor c = db.query(tableName, new String[]{columnKey, columnData, columnDate},
                columnKey + "=?", new String[]{key}, null, null, null);
        DbItem<T> response = null;
        if (c.moveToFirst()) {
            String json = Constants.EMPTY_STRING;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                long date = c.getLong(c.getColumnIndex(columnDate));
                response = new DbItem<>((T) gson.fromJson(json, type), date);
            } catch (JsonSyntaxException jse) {
            } catch (Throwable e) {
            }
        }
        c.close();
        return response;
    }

    public <T> T loadCache(String key, Type clazz) {
        Cursor c = db.query(tableName, new String[]{columnKey, columnData}, columnKey + "=?", new String[]{key}, null, null, null);
        T response = null;
        if (c.moveToFirst()) {
            String json = Constants.EMPTY_STRING;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                response = gson.fromJson(json, clazz);
            } catch (JsonSyntaxException jse) {
            } catch (Throwable e) {
            }
        }
        c.close();
        return response;
    }

    public void clearDb() {
        db.delete(tableName, null, null);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String dbName, int dbVersion) {
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