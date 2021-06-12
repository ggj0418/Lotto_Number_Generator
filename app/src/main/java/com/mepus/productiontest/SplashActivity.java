package com.mepus.productiontest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
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
    private static final int PROBLEM_VERSION = 12;
    private static final int UPDATE_REQUEST_CODE = 9000;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE) {
            // 업데이트가 성공적으로 끝나지 않은 경우
            if (resultCode != RESULT_OK) {
                // 업데이트가 취소되거나 실패하면 업데이트를 다시 요청할 수 있다.,
                // 업데이트 타입을 선택한다 (IMMEDIATE || FLEXIBLE).
                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // flexible한 업데이트를 위해서는 AppUpdateType.FLEXIBLE을 사용한다.
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // 업데이트를 다시 요청한다.
                        requestUpdate(appUpdateInfo);
                    }
                });
            }
            else {
                startLoading();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkUpdateVersion();
    }

    private void checkUpdateVersion() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) || appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                            requestUpdate(appUpdateInfo);
                        }
                    } else {
                        startLoading();
                    }
                });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // 'getAppUpdateInfo()' 에 의해 리턴된 인텐트
                    appUpdateInfo,
                    // 'AppUpdateType.FLEXIBLE': 사용자에게 업데이트 여부를 물은 후 업데이트 실행 가능
                    // 'AppUpdateType.IMMEDIATE': 사용자가 수락해야만 하는 업데이트 창을 보여줌
                    AppUpdateType.IMMEDIATE,
                    // 현재 업데이트 요청을 만든 액티비티, 여기선 MainActivity.
                    this,
                    // onActivityResult 에서 사용될 REQUEST_CODE.
                    UPDATE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (PreferenceManager.getString(getApplicationContext(), "latest_draw_day").equals("")
                || PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") == -1) {
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
        while (number % 7 != 0) {
            number--;
        }

        return number / 7;
    }

    private long getSeven(long number) {
        while (number % 7 != 0) {
            number--;
        }

        return number;
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            UPDATE_REQUEST_CODE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }
}