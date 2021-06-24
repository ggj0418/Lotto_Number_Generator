package com.mepus.productiontest.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mepus.productiontest.R;
import com.mepus.productiontest.fragment.analytics.RandomPickFragment;

import java.util.Objects;

public class AnalyticsFragment extends Fragment {

    private Button analytics_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity context = getActivity();

        initComponent(Objects.requireNonNull(context));
    }

    private void initComponent(FragmentActivity context) {
        analytics_btn = context.findViewById(R.id.analytics_btn);
        analytics_btn.setOnClickListener(view -> {
            analytics_btn.setClickable(false);
            removeOldFragment(context, R.id.fl_analytics);
            context.getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics, new RandomPickFragment()).commit();
            analytics_btn.setClickable(true);
        });
    }

    private void removeOldFragment(FragmentActivity context, int id) {
        Fragment oldFragment = context.getSupportFragmentManager().findFragmentById(id);
        if(oldFragment != null) {   // 이미 다른 Fragment가 있으면 제거
            context.getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }
    }
}