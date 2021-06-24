package com.mepus.productiontest.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mepus.productiontest.R;
import com.mepus.productiontest.recycler.RecyclerDecoration;
import com.mepus.productiontest.recycler.PickNumberRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PickNumberFragment extends Fragment {
    List<String> list = new ArrayList<>();
    RecyclerView recyclerView;
    Button lottoBtn, weBtn, clearBtn;
    PickNumberRecyclerViewAdapter adapter;
    TextView textView;

    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_pick_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponent(view);
    }

    private void initComponent(View view) {
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerDecoration recyclerDecoration = new RecyclerDecoration(24);
        recyclerView.addItemDecoration(recyclerDecoration);

        adapter = new PickNumberRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        textView = view.findViewById(R.id.number_tv);

        lottoBtn = view.findViewById(R.id.button1);
        weBtn = view.findViewById(R.id.button2);
        clearBtn = view.findViewById(R.id.main_bt_clear);

        lottoBtn.setOnClickListener(v -> {
            textView.setText(makeLottoNumber());
            list.add(textView.getText().toString());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(list.size() - 1);
        });

        weBtn.setOnClickListener(v -> {
            textView.setText(makeWeNumber());
            list.add(textView.getText().toString());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(list.size() - 1);
        });

        clearBtn.setOnClickListener(v -> {
            adapter.clear();
        });
    }

    private String makeLottoNumber() {
        StringBuilder sb = new StringBuilder();

        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        while (set.size() < 6) {
            set.add(random.nextInt(45) + 1);
        }

        List<Integer> tempList = new ArrayList<>(set);
        Collections.sort(tempList);

        for (Integer number : tempList) {
            sb.append(number).append(" ");
        }

        return sb.toString();
    }

    private String makeWeNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        sb.append(random.nextInt(5) + 1).append("조 ");

        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(6) + 1).append(" ");
        }

        return sb.toString();
    }
}
