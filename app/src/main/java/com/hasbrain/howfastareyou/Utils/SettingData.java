package com.hasbrain.howfastareyou.utils;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Khang on 05/12/2015.
 */
public class SettingData {
    private static String TIME_COUNT_KEY = "time_count",
            RECORD_HIGH_SCORE = "record_high_score",
            MAX_RECORDS = "max_score";

    public int timeLimit, maxRecords;
    public boolean recordHighScore;

    public SettingData(boolean recordHighScore, int timeLimit, int maxRecords) {
        this.recordHighScore = recordHighScore;
        this.timeLimit = timeLimit;
        this.maxRecords = maxRecords;
    }

    public static SettingData fromSharedPreferences() {
        SharedPreferences preferences = SharedPreferencesUtil.getInstance();
        int time_count = preferences.getInt(TIME_COUNT_KEY, 10);
        boolean recordHighScore = preferences.getBoolean(RECORD_HIGH_SCORE, true);
        int maxRecords = preferences.getInt(MAX_RECORDS, 10);
        return new SettingData(recordHighScore, time_count, maxRecords);
    }

    public static SettingData fromDatabase() {
        return DatabaseHelper.getInstance().getSetting();
    }

    public void saveToSharedPreferences() {
        SharedPreferencesUtil.getInstance().edit()
                .putInt(TIME_COUNT_KEY, timeLimit)
                .putBoolean(RECORD_HIGH_SCORE, recordHighScore)
                .putInt(MAX_RECORDS, maxRecords)
                .commit();
    }

    public void saveToDatabase() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TIME_LIMIT_COLUMN, timeLimit);
        values.put(DatabaseHelper.SAVE_RECORD, recordHighScore);
        values.put(DatabaseHelper.MAX_RECORDS, maxRecords);

        SQLiteDatabase db = DatabaseHelper.openDatabase();
        db.update(DatabaseHelper.SETTING_TABLE, values, null, null);
        DatabaseHelper.closeDatabase();
    }
}
