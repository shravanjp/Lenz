package com.example.lenz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "LenzDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "ScannedText";
    private static final String ID_COL = "id";
    private static final String HEADING_COL = "heading";
    private static final String CONTENT_COL = "content";
    private static final String TIME_COL = "time";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HEADING_COL + " TEXT,"
                + CONTENT_COL + " TEXT,"
                + TIME_COL + " TEXT)";
        db.execSQL(query);
    }

    public void addNewScannedText(String heading, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HEADING_COL, heading);
        values.put(CONTENT_COL, content);
        values.put(TIME_COL, time);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
