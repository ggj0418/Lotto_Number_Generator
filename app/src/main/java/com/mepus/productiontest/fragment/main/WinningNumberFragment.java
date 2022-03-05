package com.mepus.productiontest.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.JsonSyntaxException;
import com.mepus.productiontest.R;
import com.mepus.productiontest.dto.LottoData;
import com.mepus.productiontest.retrofit.RetrofitAdapter;
import com.mepus.productiontest.retrofit.RetrofitService;
import com.mepus.productiontest.shared.PreferenceManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class WinningNumberFragment extends Fragment {

    private List<String> lottoTurnList = new ArrayList<>();

    private TextView tv_drawDate, tv_drawNumbers, tv_bonusNumber, tv_totalSellAmount, tv_winner_total_amount, tv_winner_count, tv_each_amount;
    private Spinner lottoTurnSpinner;
    private ProgressBar loadingProgressBar;
    private Button bt_search;
    private EditText et_search;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private RetrofitService retrofitService;

    private LottoData lottoData;
    private int selectedSpinnerPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_winning_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLottoTurnList(PreferenceManager.getLong(getActivity(), "latest_draw_turn"));
        initComponent(getActivity());
        setRetrofit();
    }

    private int parseToInt(String str) {
        return Integer.parseInt(str.substring(0, str.length() - 2));
    }

    private void startRxWinningLottoNumbers(int turn, FragmentActivity context) {
        loadingProgressBar.setVisibility(View.VISIBLE);

        Observable<LottoData> observable = retrofitService.getWinningNumbersRxJava("getLottoNumber", turn);
        mCompositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LottoData>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull LottoData result) {
                        lottoData = result;
                        updateUI(lottoData);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if(e instanceof IllegalStateException || e instanceof JsonSyntaxException) {
                            Toast.makeText(context, "사이트가 점검중입니다", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete() {
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

    private void setLottoTurnList(long turn) {
        lottoTurnList.add("---------");
        for(long i = turn; i > 0; i--) {
            lottoTurnList.add(i + "회차");
        }
    }

    private void initComponent(FragmentActivity context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, lottoTurnList);

        lottoTurnSpinner = context.findViewById(R.id.winning_spn_lotto_turn);
        lottoTurnSpinner.setAdapter(adapter);
        lottoTurnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0 || selectedSpinnerPosition == i) return;
                startRxWinningLottoNumbers(parseToInt(lottoTurnList.get(i)), context);
                selectedSpinnerPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(context, "선택된게 없습니다", Toast.LENGTH_SHORT).show();
            }
        });

        tv_drawDate = context.findViewById(R.id.winning_tv_drawDate);
        tv_drawNumbers = context.findViewById(R.id.winning_tv_drawNumbers);
        tv_bonusNumber = context.findViewById(R.id.winning_tv_bonusNumber);
        tv_totalSellAmount = context.findViewById(R.id.winning_tv_totalSellAmount);
        tv_winner_total_amount = context.findViewById(R.id.winning_tv_winner_total_amount);
        tv_winner_count = context.findViewById(R.id.winning_tv_winner_count);
        tv_each_amount = context.findViewById(R.id.winning_tv_each_amount);

        loadingProgressBar = context.findViewById(R.id.winning_pb_loading);

        et_search = context.findViewById(R.id.winning_et_lotto_turn);
        bt_search = context.findViewById(R.id.winning_bt_search);
        bt_search.setOnClickListener(view2 -> {
            String editTurnText = et_search.getText().toString().trim();    // 스페이스바 제거
            int turn;

            if(editTurnText.getBytes().length <= 0) {
                Toast.makeText(context, "찾고자 하는 회차번호를 입력해주세요", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                turn = Integer.parseInt(et_search.getText().toString());
            }
            int latestDrawTurn = (int) PreferenceManager.getLong(context, "latest_draw_turn");

            if(turn > latestDrawTurn) {
                Toast.makeText(context, "최신 회차는 " + latestDrawTurn + " 입니다", Toast.LENGTH_LONG).show();
            }
            else {
                int spinnerIndex = (int) PreferenceManager.getLong(context, "latest_draw_turn") - turn + 1;
                et_search.setText("");
                lottoTurnSpinner.setSelection(spinnerIndex);
                selectedSpinnerPosition = spinnerIndex;
                startRxWinningLottoNumbers(turn, context);
                hideKeyboard(context);
                et_search.clearFocus();
            }
        });
    }

    private void hideKeyboard(FragmentActivity context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
