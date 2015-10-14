package com.hasbrain.howfastareyou;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TapCountActivity extends AppCompatActivity {

    public static final int TIME_COUNT = 10000; //10s
    @Bind(R.id.bt_tap)
    Button btTap;
    @Bind(R.id.bt_start)
    Button btStart;
    @Bind(R.id.tv_time)
    Chronometer tvTime;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        ButterKnife.bind(this);
        tvTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (SystemClock.elapsedRealtime() - startTime >= TIME_COUNT) {
                    pauseTapping();
                    postResult();
                }
            }
        });
    }

    private void postResult() {

    }

    @OnClick(R.id.bt_start)
    public void onStartBtnClicked(View v) {
        startTapping();
    }

    @OnClick(R.id.bt_tap)
    public void onTapBtnClicked(View v) {
    }

    private void startTapping() {
        startTime = SystemClock.elapsedRealtime();
        tvTime.setBase(SystemClock.elapsedRealtime());
        tvTime.start();
        btTap.setEnabled(true);
        btStart.setEnabled(false);
    }

    private void pauseTapping() {
        btTap.setEnabled(false);
        tvTime.stop();
        btTap.setEnabled(false);
        btStart.setEnabled(true);
    }
}
