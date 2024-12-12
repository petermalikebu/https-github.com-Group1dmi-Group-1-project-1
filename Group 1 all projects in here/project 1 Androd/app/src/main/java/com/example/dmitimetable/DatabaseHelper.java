package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Import for logging

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "timetable.db";
    private static final String TABLE_NAME = "timetable";

    // Column names
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_MODULE = "module";
    private static final String COL_LECTURER = "lecturer";
    private static final String COL_DEPARTMENT = "department";
    private static final String COL_YEAR = "year";
    private static final String COL_SEMESTER = "semester";
    private static final String COL_CLASSROOM = "classroom"; // Added classroom column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2); // Increment database version to 2
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Updated table schema to include classroom
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_MODULE + " TEXT, " +
                COL_LECTURER + " TEXT, " +
                COL_DEPARTMENT + " TEXT, " +
                COL_YEAR + " TEXT, " +
                COL_SEMESTER + " TEXT, " +
                COL_CLASSROOM + " TEXT)"; // Added classroom to table schema
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database by adding the classroom column if it doesn't exist
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_CLASSROOM + " TEXT");
        }
    }

    public boolean addTimetableEntry(TimetableEntry entry) {
        // Check for conflicts before adding a new entry
        if (hasConflict(entry.getDate(), entry.getTime(), entry.getLecturer(), entry.getClassroom())) {
            Log.d("DatabaseHelper", "Conflict found for entry: " + entry);
            return false; // Conflict detected
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, entry.getDate());
        contentValues.put(COL_TIME, entry.getTime());
        contentValues.put(COL_MODULE, entry.getModule());
        contentValues.put(COL_LECTURER, entry.getLecturer());
        contentValues.put(COL_DEPARTMENT, entry.getDepartment());
        contentValues.put(COL_YEAR, entry.getYear());
        contentValues.put(COL_SEMESTER, entry.getSemester());
        contentValues.put(COL_CLASSROOM, entry.getClassroom()); // Add classroom value

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close(); // Close the database connection
        return result != -1; // Return true if insertion was successful
    }

    public boolean hasConflict(String date, String time, String lecturer, String classroom) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE date = ? AND time = ? AND (lecturer = ? OR classroom = ?)";
        Cursor cursor = db.rawQuery(query, new String[]{date, time, lecturer, classroom});
        boolean conflictFound = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            conflictFound = (count > 0); // If there's 1 or more entries, there is a conflict
        }
        cursor.close();
        return conflictFound;
    }

    public ArrayList<TimetableEntry> getAllTimetableEntries() {
        ArrayList<TimetableEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                TimetableEntry entry = new TimetableEntry(
                        cursor.getInt(cursor.getColumnIndex(COL_ID)), // Get ID from cursor
                        cursor.getString(cursor.getColumnIndex(COL_DATE)),
                        cursor.getString(cursor.getColumnIndex(COL_TIME)),
                        cursor.getString(cursor.getColumnIndex(COL_MODULE)),
                        cursor.getString(cursor.getColumnIndex(COL_LECTURER)),
                        cursor.getString(cursor.getColumnIndex(COL_DEPARTMENT)),
                        cursor.getString(cursor.getColumnIndex(COL_YEAR)),
                        cursor.getString(cursor.getColumnIndex(COL_SEMESTER)),
                        cursor.getString(cursor.getColumnIndex(COL_CLASSROOM)) // Retrieve classroom
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close(); // Close the database connection
        return entries;
    }

    public boolean deleteTimetableEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Log the id before deletion
        Log.d("DatabaseHelper", "Deleting entry with ID: " + id);
        int rowsDeleted = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close(); // Close the database connection
        // Log the result of deletion
        Log.d("DatabaseHelper", "Rows deleted: " + rowsDeleted);
        return rowsDeleted > 0; // Return true if deletion was successful
    }
}