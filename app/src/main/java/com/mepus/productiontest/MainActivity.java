package com.mepus.productiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mepus.productiontest.fragment.PickNumberFragment;
import com.mepus.productiontest.fragment.RouletteFragment;
import com.mepus.productiontest.fragment.WinningNumberFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private int navigationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        initBottomNavigationView();
    }

    private void initComponent() {
        bottomNavigationView = findViewById(R.id.bnv_main);
    }

    @SuppressLint("NonConstantResourceId")
    private void initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bnv_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(navigationId != item.getItemId()) {
                navigationId = item.getItemId();

                switch (navigationId) {
                    case R.id.item_pick_number:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, new PickNumberFragment()).commit();
                        return true;
                    case R.id.item_roulette:
//                        Toast.makeText(getApplicationContext(), "준비중입니다. 다음 업데이트를 기다려주세요!", Toast.LENGTH_SHORT).show();
//                        return false;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, new RouletteFragment()).commit();
                        return true;
                    case R.id.item_winning_number:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, new WinningNumberFragment()).commit();
                        return true;
                }
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.item_pick_number);
    }
}