package com.arc.agni.todotoday.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.service.NotificationMusicService;

import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_DESCRIPTION;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_PRIORITY;
import static com.arc.agni.todotoday.constants.AppConstants.INTENT_EXTRA_TASK_TIME;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_COLORS;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_VALUES;

public class AlarmScreenActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
            this.setShowWhenLocked(true);
        } else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        findViewById(R.id.alarm_screen).setBackgroundColor(getResources().getColor(PRIORITY_COLORS.get(PRIORITY_VALUES.indexOf(getIntent().getStringExtra(INTENT_EXTRA_TASK_PRIORITY)))));
        ((TextView) findViewById(R.id.as_task_description_value)).setText(getIntent().getStringExtra(INTENT_EXTRA_TASK_DESCRIPTION));
        ((TextView) findViewById(R.id.as_task_time)).setText(getIntent().getStringExtra(INTENT_EXTRA_TASK_TIME));

        findViewById(R.id.stop_alarm).setOnClickListener(v -> stopAlarm());

        // The below code will disable the alarm after 1 minute if it is not cancelled by user
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                stopAlarm();
            }
        }.start();
    }

    /**
     * This method stops the alarm.
     */
    public void stopAlarm() {
        stopService(new Intent(AlarmScreenActivity.this, NotificationMusicService.class));
        finish();
    }

    /**
     * This method is triggered when back button is pressed
     */
    @Override
    public void onBackPressed() {
        stopAlarm();
    }
}
