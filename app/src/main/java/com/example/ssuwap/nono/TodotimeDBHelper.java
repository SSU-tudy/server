package com.example.ssuwap.nono;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TodotimeDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "study.db";
    private static final int DATABASE_VERSION = 1;

    public TodotimeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Subjects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "totalDuration INTEGER DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE Sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subjectId INTEGER, " +
                "startMillis INTEGER, " +
                "endMillis INTEGER, " +
                "FOREIGN KEY(subjectId) REFERENCES Subjects(id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Subjects");
        db.execSQL("DROP TABLE IF EXISTS Sessions");
        onCreate(db);
    }

    public void addSession(int subjectId, long startMillis, long endMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subjectId", subjectId);
        values.put("startMillis", startMillis);
        values.put("endMillis", endMillis);
        db.insert("Sessions", null, values);
        db.close();
    }

    public void updateTotalDuration(int subjectId, long duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Subjects SET totalDuration = totalDuration + ? WHERE id = ?",
                new Object[]{duration, subjectId});
        db.close();
    }

    public List<long[]> getSessions(int subjectId) {
        List<long[]> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT startMillis, endMillis FROM Sessions WHERE subjectId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subjectId)});

        if (cursor.moveToFirst()) {
            do {
                long startMillis = cursor.getLong(cursor.getColumnIndexOrThrow("startMillis"));
                long endMillis = cursor.getLong(cursor.getColumnIndexOrThrow("endMillis"));
                sessions.add(new long[]{startMillis, endMillis});
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sessions;
    }

}
