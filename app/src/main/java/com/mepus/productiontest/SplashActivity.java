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
import com.google.gson.JsonSyntaxException;
import com.mepus.productiontest.dto.LottoData;
import com.mepus.productiontest.retrofit.RetrofitAdapter;
import com.mepus.productiontest.retrofit.RetrofitService;
import com.mepus.productiontest.shared.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    private static final int STANDARD_DRAW_TURN = 961;
    private final RetrofitService retrofitService = RetrofitAdapter.getInstance().getServiceApi();

    private int globalTurn = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") != -1) {
            globalTurn = (int) PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn") + 1;
        }
        setPreference();
        startTimer((int) PreferenceManager.getLong(getApplicationContext(), "latest_draw_turn"));
        startLoading();
    }

    private void startTimer(int turn) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(globalTurn <= turn) {
                    callWinningNumbers(globalTurn);
                    globalTurn++;
                }
                else {
                    timer.cancel();
                }
            }
        }, 0, 5);
    }

    private void callWinningNumbers(int turn) {
        Call<LottoData> call =  retrofitService.getWinningNumbers("getLottoNumber", turn);
        call.enqueue(new Callback<LottoData>() {
            @Override
            public void onResponse(@NotNull Call<LottoData> call, @NotNull Response<LottoData> response) {
                LottoData lottoData = response.body();
                Log.d("callWinningNumbers", Objects.requireNonNull(lottoData).getReturnValue() + turn);
            }

            @Override
            public void onFailure(@NotNull Call<LottoData> call, @NotNull Throwable t) {
                if(t instanceof IllegalStateException || t instanceof JsonSyntaxException) {
                    Toast.makeText(getApplicationContext(), "사이트가 점검중입니다", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startLoading() {
        Handler mHandler = new Handler();
        //수행할 작업 작성
        mHandler.postDelayed(this::startNextActivity, 500);
    }

    private void startNextActivity() {
        Intent toMainIntent = new Intent(this, MainActivity.class);
        startActivity(toMainIntent);
    }

    private long getSeven(long number) {
        while (number % 7 != 0) {
            number--;
        }

        return number;
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

    private void setPreference() {
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

    @Override
    public void onBackPressed() {
    }
}