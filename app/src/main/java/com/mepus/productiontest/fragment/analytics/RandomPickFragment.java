package com.mepus.productiontest.fragment.analytics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mepus.productiontest.R;
import com.mepus.productiontest.recycler.AnalyticsRandomRecyclerViewAdapter;
import com.mepus.productiontest.recycler.RecyclerDecoration;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RandomPickFragment extends Fragment {
    List<int[]> list = new ArrayList<>();
    List<String> randomMaxList = new ArrayList<>();
    RecyclerView recyclerView;
    AnalyticsRandomRecyclerViewAdapter adapter;

    LinearLayoutManager linearLayoutManager;

    private ImageView iv_hlep;
    private ProgressBar analytics_pv;
    private Spinner randomMaxSpinner;

    private Disposable disposable;

    private final static Locale currentLocale = Locale.KOREA;
    private final static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);

    private int selectedSpinnerPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_pick, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity context = getActivity();

        initMaxList();
        initComponent(Objects.requireNonNull(context));
    }

    private void initMaxList() {
        for (int i = 1; i <= 10; i++) {
            randomMaxList.add(numberFormatter.format(100000 * i) + "번");
        }
    }

    private void startRxRandomNumber(int max) {
        list.clear();
        analytics_pv.setVisibility(View.VISIBLE);
        disposable = Observable.fromCallable(() -> {
            getRandomNumber(max);
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    analytics_pv.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                    disposable.dispose();
                });
    }

    private void initComponent(FragmentActivity context) {
        analytics_pv = context.findViewById(R.id.analytics_pv);

        iv_hlep = context.findViewById(R.id.iv_help2);
        iv_hlep.setOnClickListener(view -> {
            showDialog(context);
        });

        linearLayoutManager = new LinearLayoutManager(context);

        recyclerView = context.findViewById(R.id.random_pick_rv);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerDecoration recyclerDecoration = new RecyclerDecoration(24);
        recyclerView.addItemDecoration(recyclerDecoration);

        adapter = new AnalyticsRandomRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, randomMaxList);

        randomMaxSpinner = context.findViewById(R.id.random_spn_max_number);
        randomMaxSpinner.setAdapter(adapter);
        randomMaxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selectedSpinnerPosition != position) {
                    String temp = randomMaxList.get(position);
                    temp = temp.substring(0, temp.length() - 1);

                    startRxRandomNumber(parseStringToInt(temp));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, "선택된게 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int parseStringToInt(String str) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != ',') {
                sb.append(str.charAt(i));
            }
        }

        return Integer.parseInt(sb.toString());
    }

    private void getRandomNumber(int max) {
        SplittableRandom sr = new SplittableRandom();
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < max; i++) {
            int number = sr.nextInt(1, 46);

            map.put(number, map.getOrDefault(number, 0) + 1);
        }

        List<Integer> keyList = new ArrayList<>(map.keySet());
        keyList.sort(Comparator.comparingInt(map::get));

        for (int key : keyList) {
            list.add(new int[]{key, map.get(key)});
        }
    }

    private void showDialog(FragmentActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("설명서")
                .setMessage(getResources().getString(R.string.analytics_random_pick_help))
                .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}