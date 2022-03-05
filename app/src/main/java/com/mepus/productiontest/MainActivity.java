package com.mepus.productiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mepus.productiontest.fragment.main.AnalyticsFragment;
import com.mepus.productiontest.fragment.main.PickNumberFragment;
import com.mepus.productiontest.fragment.main.RouletteFragment;
import com.mepus.productiontest.fragment.main.WinningNumberFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private int navigationId = 0;
    private long backKeyPressedTime = 0;

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
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, new RouletteFragment()).commit();
                        return true;
                    case R.id.item_analytics:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, new AnalyticsFragment()).commit();
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

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            Toast.makeText(this, "뒤로 가기 버튼을 한번 더 누르면\n어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
            backKeyPressedTime = System.currentTimeMillis();
        }
        else {
            finishAffinity();
        }
    }
}