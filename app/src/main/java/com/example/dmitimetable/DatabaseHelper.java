package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "timetable.db";
    private static final String TABLE_NAME = "timetable";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_MODULE = "module";
    private static final String COL_LECTURER = "lecturer";
    private static final String COL_DEPARTMENT = "department";
    private static final String COL_YEAR = "year";
    private static final String COL_SEMESTER = "semester";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_MODULE + " TEXT, " +
                COL_LECTURER + " TEXT, " +
                COL_DEPARTMENT + " TEXT, " +
                COL_YEAR + " TEXT, " +
                COL_SEMESTER + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addTimetableEntry(TimetableEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, entry.getDate());
        contentValues.put(COL_TIME, entry.getTime());
        contentValues.put(COL_MODULE, entry.getModule());
        contentValues.put(COL_LECTURER, entry.getLecturer());
        contentValues.put(COL_DEPARTMENT, entry.getDepartment());
        contentValues.put(COL_YEAR, entry.getYear());
        contentValues.put(COL_SEMESTER, entry.getSemester());

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Return true if insertion was successful
    }

    public ArrayList<TimetableEntry> getAllTimetableEntries() {
        ArrayList<TimetableEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") TimetableEntry entry = new TimetableEntry(
                        cursor.getString(cursor.getColumnIndex(COL_DATE)),
                        cursor.getString(cursor.getColumnIndex(COL_TIME)),
                        cursor.getString(cursor.getColumnIndex(COL_MODULE)),
                        cursor.getString(cursor.getColumnIndex(COL_LECTURER)),
                        cursor.getString(cursor.getColumnIndex(COL_DEPARTMENT)),
                        cursor.getString(cursor.getColumnIndex(COL_YEAR)),
                        cursor.getString(cursor.getColumnIndex(COL_SEMESTER))
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public void deleteTimetableEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
