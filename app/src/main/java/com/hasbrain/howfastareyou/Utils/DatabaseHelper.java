package com.hasbrain.howfastareyou.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hasbrain.howfastareyou.MainApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang on 05/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "My database.db";
    public static final String SETTING_TABLE = "Setting",
            HIGH_SCORE_TABLE = "HighScore";

    //SETTING COLUMNS
    public static final String TIME_LIMIT_COLUMN = "time_limit",
            SAVE_RECORD = "save_record",
            MAX_RECORDS = "max_record";

    //HIGH SCORE COLUMNS
    public static final String //ID = "id",
            SCORE = "score",
            TIME_END = "time_end",
            TIME_COUNT = "time_count";


    private static DatabaseHelper mInstance;
    private static SQLiteDatabase mSqLiteDatabase;

    private static int mOpenCounter = 0;

    public static synchronized DatabaseHelper getInstance() {
        if (mInstance == null)
            mInstance = new DatabaseHelper();
        return mInstance;
    }

    public static synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            // OPEN new database
            mSqLiteDatabase = getInstance().getWritableDatabase();
        }
        return mSqLiteDatabase;
    }

    public static synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            // Closing database
            // ALL thread have closed database
            mSqLiteDatabase.close();
        }
    }


    public DatabaseHelper() {
        super(MainApplication.sharedContext,
                DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables
        db.execSQL("CREATE TABLE " + SETTING_TABLE + " (" +
                TIME_LIMIT_COLUMN + " INTEGER," +
                SAVE_RECORD + " BOOLEAN," +
                MAX_RECORDS + " INTEGER)");
        db.execSQL("CREATE TABLE " + HIGH_SCORE_TABLE + " (" +
                SCORE + " INTEGER," +
                TIME_END + " INTEGER," +
                TIME_COUNT + " INTEGER)");

        //Insert default settings
        ContentValues values = new ContentValues();
        values.put(TIME_LIMIT_COLUMN, 10);
        values.put(SAVE_RECORD, true);
        values.put(MAX_RECORDS, 10);
        db.insert(SETTING_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //no upgrade
    }

    public List<HighScore.Record> getHighScores() {
        List<HighScore.Record> result = new ArrayList<>();

        SQLiteDatabase database = openDatabase();

        Cursor cur = database.query(HIGH_SCORE_TABLE, null, null, null, null, null, null);
        if (cur != null && cur.moveToFirst()) {
            int score_column = cur.getColumnIndex(SCORE),
                    time_end_column = cur.getColumnIndex(TIME_END),
                    time_count_column = cur.getColumnIndex(TIME_COUNT);
            do {
                int score = cur.getInt(score_column);
                long time_end = cur.getLong(time_end_column);
                int time_count = cur.getInt(time_count_column);
                result.add(new HighScore.Record(score, time_end, time_count));
            } while (cur.moveToNext());

            cur.close();
        }

        closeDatabase();

        return result;
    }

    public SettingData getSetting() {
        int timeLimit = 10, maxRecords = 10;
        boolean recordHighScore = true;
        SQLiteDatabase database = openDatabase();

        Cursor cur = database.query(SETTING_TABLE, null, null, null, null, null, null);

        if (cur != null && cur.moveToFirst()) {
            timeLimit = cur.getInt(cur.getColumnIndex(TIME_LIMIT_COLUMN));
            maxRecords = cur.getInt(cur.getColumnIndex(MAX_RECORDS));
            recordHighScore = cur.getInt(cur.getColumnIndex(SAVE_RECORD)) == 1;
            cur.close();
        }

        closeDatabase();

        return new SettingData(recordHighScore, timeLimit, maxRecords);
    }
}
