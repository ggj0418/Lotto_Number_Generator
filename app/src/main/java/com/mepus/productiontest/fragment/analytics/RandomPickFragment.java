package com.mepus.productiontest.fragment.analytics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mepus.productiontest.R;
import com.mepus.productiontest.recycler.AnalyticsRandomRecyclerViewAdapter;
import com.mepus.productiontest.recycler.RecyclerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    RecyclerView recyclerView;
    AnalyticsRandomRecyclerViewAdapter adapter;

    LinearLayoutManager linearLayoutManager;

    private ImageView iv_hlep;
    private ProgressBar analytics_pv;

    private Disposable disposable;

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

        initComponent(Objects.requireNonNull(context));

        startRxRandomNumber();
    }

    private void startRxRandomNumber() {
        analytics_pv.setVisibility(View.VISIBLE);
        disposable = Observable.fromCallable(() -> {
            getRandomNumber();
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
    }

    private void getRandomNumber() {
        SplittableRandom sr = new SplittableRandom();
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < 100000; i++) {
            int number = sr.nextInt(1, 46);

            map.put(number, map.getOrDefault(number, 0) + 1);
        }

        List<Integer> keyList = new ArrayList<>(map.keySet());
        keyList.sort((o1, o2) -> (map.get(o1) - map.get(o2)));

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