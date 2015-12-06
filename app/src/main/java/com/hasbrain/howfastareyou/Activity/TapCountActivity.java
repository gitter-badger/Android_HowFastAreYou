package com.hasbrain.howfastareyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.hasbrain.howfastareyou.R;
import com.hasbrain.howfastareyou.fragment.TapCountResultFragment;
import com.hasbrain.howfastareyou.utils.SettingData;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TapCountActivity extends AppCompatActivity {
    private static final String TIME_PAUSED = "TIME_PAUSED";
    private static final String START_TIME = "START_TIME";
    private static final String TAP_COUNT = "TAP_COUNT";
    private static final String IS_PAUSE = "IS_PAUSE";
    private static final String TV_TIME_TEXT = "TV_TIME_TEXT";


    @Bind(R.id.bt_tap)
    Button btTap;
    @Bind(R.id.bt_start)
    Button btStart;
    @Bind(R.id.tv_time)
    Chronometer tvTime;
    @Bind(R.id.tv_count)
    AppCompatTextView tvTabCount;

    private long startTime;
    private SettingData mSettingData;
    private int tap_count = 0;
    private boolean isRunning = false;

    private long time_paused;
    private boolean isPause = false;

    TapCountResultFragment tapCountResultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);


        mSettingData = SettingData.fromDatabase();
//          OR
//        mSettingData = SettingData.fromSharedPreferences();

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            startTime = savedInstanceState.getLong(START_TIME, 0);
            tap_count = savedInstanceState.getInt(TAP_COUNT, 0);
            time_paused = savedInstanceState.getLong(TIME_PAUSED, 0);
            isPause = savedInstanceState.getBoolean(IS_PAUSE, false);
        }

        if (isPause) {
            btStart.setText("RESUME");
            tvTime.setBase(SystemClock.elapsedRealtime() - time_paused + startTime);
            tvTabCount.setText(tap_count + "");
        }

        tvTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (SystemClock.elapsedRealtime() - startTime >= mSettingData.timeLimit * 1000) {
                    stopTapping();
                }
            }
        });

        if (mSettingData.recordHighScore) {
            tapCountResultFragment = new TapCountResultFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_result_fragment, tapCountResultFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent showSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(showSettingsActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_start)
    public void onStartBtnClicked(View v) {
        startTapping();
    }

    @OnClick(R.id.bt_tap)
    public void onTapBtnClicked(View v) {
        tap_count++;
        tvTabCount.setText(tap_count + "");
        Log.e("COUNT", "" + tap_count);
    }

    private void startTapping() {
        isRunning = true;
        if (isPause) {
            isPause = false;
            startTime = SystemClock.elapsedRealtime()
                    - (time_paused - startTime);
            tvTime.setBase(startTime);
        } else {
            tap_count = 0;
            tvTabCount.setText("0");
            startTime = SystemClock.elapsedRealtime();
            tvTime.setBase(startTime);
        }
        tvTime.start();
        btTap.setEnabled(true);
        btStart.setEnabled(false);
    }

    private void stopTapping() {
        Toast.makeText(TapCountActivity.this, "STOP TAPPING", Toast.LENGTH_SHORT).show();
        isRunning = false;
        btTap.setEnabled(false);
        tvTime.stop();
        btStart.setEnabled(true);
        btStart.setText("START");
        if (mSettingData.recordHighScore) {
            tapCountResultFragment.addRecord(tap_count,
                    new Date().getTime(),
                    mSettingData.timeLimit);
        }
    }

    private void pauseTapping() {
        if (isRunning) {
            isRunning = false;
            isPause = true;
            time_paused = SystemClock.elapsedRealtime();

            btTap.setEnabled(false);
            tvTime.stop();

            btStart.setEnabled(true);
            btStart.setText("RESUME");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(START_TIME, startTime);
        outState.putInt(TAP_COUNT, tap_count);
        outState.putLong(TIME_PAUSED, time_paused);
        outState.putBoolean(IS_PAUSE, isPause);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTapping();
    }
}
