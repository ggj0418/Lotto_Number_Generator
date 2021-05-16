package com.mepus.productiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mepus.productiontest.recycler.RecyclerDecoration;
import com.mepus.productiontest.recycler.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    RecyclerView recyclerView;
    Button lottoBtn, weBtn, clearBtn;
    FloatingActionButton menuFab, winningNumberFab;
    RecyclerViewAdapter adapter;
    TextView textView;

    LinearLayoutManager linearLayoutManager;

    Animation fabOpenAnim, fabCloseAnim;

    boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAnimation();
        initComponent();
    }

    private void setAnimation() {
        fabOpenAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);
    }

    private void initComponent() {
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerDecoration recyclerDecoration = new RecyclerDecoration(24);
        recyclerView.addItemDecoration(recyclerDecoration);

        adapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);

        textView = findViewById(R.id.number_tv);

        lottoBtn = findViewById(R.id.button1);
        weBtn = findViewById(R.id.button2);
        clearBtn = findViewById(R.id.main_bt_clear);

        menuFab = findViewById(R.id.main_fab_menu);
        winningNumberFab = findViewById(R.id.main_fab_winning_number);

        lottoBtn.setOnClickListener(view -> {
            textView.setText(makeLottoNumber());
            list.add(textView.getText().toString());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(list.size() - 1);
        });

        weBtn.setOnClickListener(view -> {
            textView.setText(makeWeNumber());
            list.add(textView.getText().toString());
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(list.size() - 1);
        });

        clearBtn.setOnClickListener(view -> {
            adapter.clear();
        });

        menuFab.setOnClickListener(view -> {
            reverseFabStatus();
        });

        winningNumberFab.setOnClickListener(view -> {
            Intent toWinningIntent = new Intent(this, WinningNumberActivity.class);
            startActivity(toWinningIntent);
        });
    }

    private void reverseFabStatus() {
        if(isFabOpen) {
            winningNumberFab.setVisibility(View.INVISIBLE);
            winningNumberFab.startAnimation(fabCloseAnim);
            winningNumberFab.setClickable(false);
        } else {
            winningNumberFab.setVisibility(View.VISIBLE);
            winningNumberFab.startAnimation(fabOpenAnim);
            winningNumberFab.setClickable(true);
        }
        isFabOpen = !isFabOpen;
    }

    private String makeLottoNumber() {
        StringBuilder sb = new StringBuilder();

        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        while(set.size() < 6) {
            set.add(random.nextInt(45)+1);
        }

        List<Integer> tempList = new ArrayList<>(set);
        Collections.sort(tempList);

        for(Integer number : tempList) {
            sb.append(number).append(" ");
        }

        return sb.toString();
    }

    private String makeWeNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        sb.append(random.nextInt(5)+1).append("조 ");

        for(int i=0;i<6;i++) {
            sb.append(random.nextInt(6) + 1).append(" ");
        }

        return sb.toString();
    }
}