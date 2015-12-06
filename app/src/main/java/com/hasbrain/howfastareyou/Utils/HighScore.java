package com.hasbrain.howfastareyou.utils;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang on 01/12/2015.
 */
public class HighScore {
    private static final String HIGH_SCORE_KEY = "HighScoreData",
            MAX_RECORDS_KEY = "MaxRecords";


    private int maxRecords;
    private List<Record> highScore;

    public static HighScore fromSharedPreferences() {
        SharedPreferences preferences = SharedPreferencesUtil.getInstance();
        String json = preferences.getString(HIGH_SCORE_KEY, null);
        int maxRecords = preferences.getInt(MAX_RECORDS_KEY, 10);
        return new HighScore(maxRecords, json);
    }

    public static HighScore fromDatabse() {
        SettingData setting = DatabaseHelper.getInstance().getSetting();
        List<Record> highScore = DatabaseHelper.getInstance().getHighScores();
        return new HighScore(setting.maxRecords, highScore);
    }

    public HighScore(int maxRecords, String fromString) {
        this.maxRecords = maxRecords;
        this.highScore = new ArrayList<>();
        if (fromString != null) {
            try {
                JSONArray array = new JSONArray(fromString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObject = array.getJSONObject(i);
                    highScore.add(new Record(jObject.getInt("SCORE"),
                            jObject.getLong("TIME"),
                            jObject.getInt("TIMECOUNT")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public HighScore(int maxRecords, List<Record> highScore) {
        this.maxRecords = maxRecords;
        this.highScore = highScore;
    }

    public void addRecord(int score, long time_end, int time_count) {
        boolean inserted = false;
        for (int i = 0; i < highScore.size(); i++) {
            if (score / (float) time_count >=
                    highScore.get(i).score / (float) highScore.get(i).time_count) {
                highScore.add(i, new Record(score, time_end, time_count));
                inserted = true;
                break;
            }
        }
        if (highScore.size() > maxRecords)
            highScore.remove(maxRecords);
        else if (!inserted)
            highScore.add(new Record(score, time_end, time_count));
    }

    public int getCount() {
        return highScore == null ? 0 : highScore.size();
    }

    public Record getItem(int pos) {
        return highScore.get(pos);
    }

    public void clearAll() {
        highScore.clear();
    }

    public void saveToDatabase() {
        SQLiteDatabase db = DatabaseHelper.openDatabase();
        // Clear old data
        db.delete(DatabaseHelper.HIGH_SCORE_TABLE, null, null);

        final SQLiteStatement statement = db.compileStatement(
                "INSERT INTO " + DatabaseHelper.HIGH_SCORE_TABLE + " VALUES( ? , ? , ?)");
        db.beginTransaction();
        try {
            for (Record record : highScore) {
                statement.clearBindings();
                statement.bindLong(1, record.score);
                statement.bindLong(2, record.time_end);
                statement.bindLong(3, record.time_count);
                statement.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        DatabaseHelper.closeDatabase();
    }

    public void saveToSharedPreferences() {
        SharedPreferences preferences = SharedPreferencesUtil.getInstance();
        preferences.edit()
                .putString(HIGH_SCORE_KEY, toString())
                .apply();
    }

    @Override
    public String toString() {
        JSONArray array = new JSONArray();
        for (Record record : highScore) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SCORE", record.score);
                jsonObject.put("TIME", record.time_end);
                jsonObject.put("TIMECOUNT", record.time_count);
                array.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }


    public static class Record {
        public int score;
        public int time_count;
        public long time_end;

        public Record(int score, long time, int time_count) {
            this.score = score;
            this.time_count = time_count;
            this.time_end = time;
        }
    }


}
