package com.mepus.productiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mepus.productiontest.dto.LottoData;
import com.mepus.productiontest.retrofit.RetrofitAdapter;
import com.mepus.productiontest.retrofit.RetrofitService;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class WinningNumberActivity extends AppCompatActivity {
    private static final String TAG = WinningNumberActivity.class.getSimpleName();

    private int lottoTurn;
    private List<String> lottoTurnList = new ArrayList<>();

    private TextView tv_drawDate, tv_drawNumbers, tv_bonusNumber, tv_totalSellAmount, tv_winner_total_amount, tv_winner_count, tv_each_amount;
    private Spinner lottoTurnSpinner;
    private ProgressBar loadingProgressBar;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private RetrofitService retrofitService;

    private LottoData lottoData;
    private int selectedSpinnerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_number);

        setLottoTurnList(964);  // TODO 나중에 가장 최신 회차 숫자로 변경
        initComponent();
        setRetrofit();
    }

    private int parseToInt(String str) {
        return Integer.parseInt(str.substring(0, str.length() - 2));
    }

    private void startRxWinningLottoNumbers(int turn) {
        loadingProgressBar.setVisibility(View.VISIBLE);

        Observable<LottoData> observable = retrofitService.getWinningNumbers("getLottoNumber", turn);
        mCompositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LottoData>() {
                    @Override
                    public void onNext(@NonNull LottoData result) {
                        lottoData = result;
                        updateUI(lottoData);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "To get selected lotto turn's info is successful");
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                })
        );
    }

    private String parseNumberToMoney(long number) {
        return new DecimalFormat("###,###").format(number);
    }

    private String getWholeDrawNumber(LottoData l1) {
        return l1.getDrwtNo1() + " " + l1.getDrwtNo2() + " " + l1.getDrwtNo3() + " " + l1.getDrwtNo4() + " " + l1.getDrwtNo5() + " " + l1.getDrwtNo6();
    }

    private void updateUI(LottoData l1) {
        tv_drawDate.setText(l1.getDrwNoDate());
        tv_drawNumbers.setText(getWholeDrawNumber(l1));
        tv_bonusNumber.setText(String.valueOf(l1.getBnusNo()));
        tv_totalSellAmount.setText(parseNumberToMoney(l1.getTotSellamnt()));
        tv_winner_total_amount.setText(parseNumberToMoney(l1.getFirstAccumamnt()));
        tv_winner_count.setText(String.valueOf(l1.getFirstPrzwnerCo()));
        tv_each_amount.setText(parseNumberToMoney(l1.getFirstWinamnt()));
    }

    private void setRetrofit() {
        retrofitService = RetrofitAdapter.getInstance().getServiceApi();
    }

    private void setLottoTurnList(int turn) {
        lottoTurnList.add("---------");
        for(int i = turn; i > 0; i--) {
            lottoTurnList.add(i + "회차");
        }
    }

    private void initComponent() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, lottoTurnList);

        lottoTurnSpinner = findViewById(R.id.winning_spn_lotto_turn);
        lottoTurnSpinner.setAdapter(adapter);
        lottoTurnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0 || selectedSpinnerPosition == i) return;
//                Toast.makeText(getApplicationContext(), lottoTurnList.get(i) + " 가 선택되었습니다", Toast.LENGTH_SHORT).show();
                startRxWinningLottoNumbers(parseToInt(lottoTurnList.get(i)));
                selectedSpinnerPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "선택된게 없습니다", Toast.LENGTH_SHORT).show();
            }
        });

        tv_drawDate = findViewById(R.id.winning_tv_drawDate);
        tv_drawNumbers = findViewById(R.id.winning_tv_drawNumbers);
        tv_bonusNumber = findViewById(R.id.winning_tv_bonusNumber);
        tv_totalSellAmount = findViewById(R.id.winning_tv_totalSellAmount);
        tv_winner_total_amount = findViewById(R.id.winning_tv_winner_total_amount);
        tv_winner_count = findViewById(R.id.winning_tv_winner_count);
        tv_each_amount = findViewById(R.id.winning_tv_each_amount);

        loadingProgressBar = findViewById(R.id.winning_pb_loading);
    }
}