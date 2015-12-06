package com.hasbrain.howfastareyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.hasbrain.howfastareyou.R;
import com.hasbrain.howfastareyou.utils.SettingData;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static int MIN_TIME_LIMIT = 5, MIN_RECORDS = 10;

    @Bind(R.id.tv_time_limit)
    AppCompatTextView tvTimeLimit;
    @Bind(R.id.sb_time_limit)
    SeekBar sbTimeLimit;
    @Bind(R.id.sc_record_high_score)
    SwitchCompat scRecordHighScore;
    @Bind(R.id.tv_max_record)
    AppCompatTextView tvMaxRecord;
    @Bind(R.id.sb_max_record)
    SeekBar sbMaxRecord;
    @Bind(R.id.ll_max_record)
    LinearLayout llMaxRecord;

    private SettingData settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ButterKnife.bind(this);

        settings = SettingData.fromSharedPreferences();
        // OR
        //settings = SettingData.fromDatabase();

        tvTimeLimit.setText(settings.timeLimit + " secs");
        sbTimeLimit.setProgress(settings.timeLimit - MIN_TIME_LIMIT);
        tvMaxRecord.setText(settings.maxRecords + "");
        sbMaxRecord.setProgress(settings.maxRecords - MIN_RECORDS);

        if (!settings.recordHighScore) {
            scRecordHighScore.setChecked(false);
            llMaxRecord.setVisibility(View.GONE);
        }

        sbTimeLimit.setOnSeekBarChangeListener(this);
        sbMaxRecord.setOnSeekBarChangeListener(this);

        scRecordHighScore.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == sbTimeLimit.getId()) {
            settings.timeLimit = progress + MIN_TIME_LIMIT;
            tvTimeLimit.setText(settings.timeLimit + " secs");
        } else {
            settings.maxRecords = progress + MIN_RECORDS;
            tvMaxRecord.setText(settings.maxRecords + "");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        settings.recordHighScore = isChecked;
        if (isChecked)
            llMaxRecord.setVisibility(View.VISIBLE);
        else
            llMaxRecord.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settings.saveToSharedPreferences();
        settings.saveToDatabase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent showGame = new Intent(this, TapCountActivity.class);
        startActivity(showGame);
        finish();
    }
}
