package com.example.eticaretapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import com.example.eticaretapp.databasemodels.ActiveUserDbModel;
import com.example.eticaretapp.databasemodels.UserDbModel;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    //DATABASE NAME
    public static final String DB_NAME = "ETICARETDATABASE";

    //DATABASE VERSİON
    public static final int DB_VERSION = 6;


    public DBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String activeUserCreateQuery = "CREATE TABLE " + ActiveUserDbModel.TABLE + " ("
                + ActiveUserDbModel.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ActiveUserDbModel.ACTIVE_USER_ID + " INTEGER REFERENCES " + UserDbModel.TABLE + "(" + UserDbModel.ID + ") )";
        db.execSQL(activeUserCreateQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertActiveUser(String activeUserID) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ActiveUserDbModel.ACTIVE_USER_ID, activeUserID);

        database.insert(ActiveUserDbModel.TABLE, null, values);
        database.close();
    }


    public void updateActiveUser(String activeUserID) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ActiveUserDbModel.ACTIVE_USER_ID, activeUserID);

        String[] selectionArgs = {"1"};

        database.update(ActiveUserDbModel.TABLE, values, ActiveUserDbModel.ID + " = ?", selectionArgs);
        database.close();

    }





    public ArrayList<HashMap<String, String>> getArrayList(String query) {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    int columnType = cursor.getType(i);

                    if (columnType == Cursor.FIELD_TYPE_BLOB) {
                        byte[] byteArray = cursor.getBlob(i);
                        map.put(cursor.getColumnName(i), Base64.encodeToString(byteArray, Base64.DEFAULT));
                        continue;
                    }

                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }
                list.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return list;
    }


    public String getActiveUser() {

        String activeUserId = null;
        String query = "SELECT " + ActiveUserDbModel.ACTIVE_USER_ID + " FROM " + ActiveUserDbModel.TABLE;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            if (cursor.getString(0) != null)
                activeUserId = cursor.getString(0);
        }

        cursor.close();
        database.close();


        return activeUserId;
    }







}
