package com.example.lenz;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "lenzdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "ScannedText";
    private static final String ID_COL = "id";
    private static final String TITLE_COL = "title";
    private static final String CONTENT_COL = "content";
    private static final String TIME_COL = "time";

    public DBHandler(Context context) {
        super(context, "lenzdb.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_COL + " TEXT,"
                + CONTENT_COL + " TEXT,"
                + TIME_COL + " TEXT)";
        db.execSQL(query);
    }


    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> ScannedTextList = new ArrayList<>();
        String query = "SELECT " + TITLE_COL +","+CONTENT_COL+","+TIME_COL+" FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> scannedText = new HashMap<>();
            scannedText.put("title",cursor.getString(cursor.getColumnIndex(TITLE_COL)));
            scannedText.put("content",cursor.getString(cursor.getColumnIndex(CONTENT_COL)));
            scannedText.put("time",cursor.getString(cursor.getColumnIndex(TIME_COL)));
            ScannedTextList.add(scannedText);
        }
        Collections.reverse(ScannedTextList);
        return ScannedTextList;
    }


    public void addNewScannedText(String title, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE_COL, title);
        values.put(CONTENT_COL, content);
        values.put(TIME_COL, time);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean updateScannedText(String title,String content,String time,String newTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE_COL, title);
        values.put(CONTENT_COL, content);
        values.put(TIME_COL, newTime);
        db.update(TABLE_NAME, values, "time=?", new String[]{time});
        db.close();
        return true;
    }

    public boolean deleteScannedText(String time){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "time=?", new String[]{time});
        db.close();
        return true;
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
