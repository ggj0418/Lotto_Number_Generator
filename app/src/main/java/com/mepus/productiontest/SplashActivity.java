package com.mepus.productiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.mepus.productiontest.shared.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar pb_waiting;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    private static final int STANDARD_DRAW_TURN = 961;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startLoading();
    }

    private void startLoading() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            setComponent();
            setPreference();
            startNextActivity();
            //수행할 작업 작성
            finish();
        }, 1500);
    }

    private void startNextActivity() {
        Intent toMainIntent = new Intent(this, MainActivity.class);
        startActivity(toMainIntent);
    }

    private void setPreference() {
        pb_waiting.setVisibility(View.VISIBLE);
        // 가장 처음 어플을 설치해서 실행한 경우
        if(PreferenceManager.getString(getApplicationContext(), "latest_draw_day").equals("")
                || PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") == -1)
        {
            PreferenceManager.setString(getApplicationContext(), "latest_draw_day", getDrawDay("2021-05-02"));
            PreferenceManager.setLong(getApplicationContext(), "latest_draw_turn", getDrawTurn("2021-05-02"));
        }
        // 이전에 실행한 이력이 있는 경우 가장 최신 회차번호 인가를 판별해서 그럴 경우 최신 회차 번호로
        else {
            String day = PreferenceManager.getString(getApplicationContext(), "latest_draw_day");
            PreferenceManager.setString(getApplicationContext(), "latest_draw_day", getDrawDay(day));
            PreferenceManager.setLong(getApplicationContext(), "latest_draw_turn", getDrawTurn(day));
        }
        pb_waiting.setVisibility(View.INVISIBLE);
    }

    private void setComponent() {
        pb_waiting = findViewById(R.id.splash_pb_waiting);
    }

    private long getDrawTurn(String dateStr) {
        Calendar calendar = Calendar.getInstance();

        Date today = new Date(calendar.getTimeInMillis());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String todayDateStr = dateFormat.format(today);

        long todayTimeStamp, standardTimeStamp;

        try {
            todayTimeStamp = Objects.requireNonNull(dateFormat.parse(todayDateStr)).getTime();
            standardTimeStamp = Objects.requireNonNull(dateFormat.parse(dateStr)).getTime();
        } catch (ParseException e) {
            todayTimeStamp = -1;
            standardTimeStamp = -1;
        }

        long diff = todayTimeStamp - standardTimeStamp;
        long dayDiff = diff / (24 * 60 * 60 * 1000);

        return (PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") == -1)
                ? STANDARD_DRAW_TURN + getTurn(dayDiff)
                : PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") + getTurn(dayDiff);
    }

    private String getDrawDay(String dateStr) {
        Calendar calendar = Calendar.getInstance();

        Date today = new Date(calendar.getTimeInMillis());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String todayDateStr = dateFormat.format(today);

        long todayTimeStamp, standardTimeStamp;

        try {
            todayTimeStamp = Objects.requireNonNull(dateFormat.parse(todayDateStr)).getTime();
            standardTimeStamp = Objects.requireNonNull(dateFormat.parse(dateStr)).getTime();
        } catch (ParseException e) {
            todayTimeStamp = -1;
            standardTimeStamp = -1;
        }

        long diff = todayTimeStamp - standardTimeStamp;
        long dayDiff = diff / (24 * 60 * 60 * 1000);

        dayDiff = getSeven(dayDiff);

        standardTimeStamp += dayDiff * (24 * 60 * 60 * 1000);

        return dateFormat.format(standardTimeStamp);
    }

    private long getTurn(long number) {
        while(number % 7 != 0) {
            number--;
        }

        return number / 7;
    }

    private long getSeven(long number) {
        while(number % 7 != 0) {
            number--;
        }

        return number;
    }

}