package com.mepus.productiontest.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adefruandta.spinningwheel.SpinningWheelView;

import com.mepus.productiontest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RouletteFragment extends Fragment {
    private static final int ITEM_COUNT = 45;

    private Button btn_pick, btn_clear;
    private TextView tv_number1, tv_number2, tv_number3, tv_number4, tv_number5, tv_number6;
    private ImageView iv_cancel1, iv_cancel2, iv_cancel3, iv_cancel4, iv_cancel5, iv_cancel6, iv_help;

    private boolean[] positionVisit = new boolean[6];
    private boolean[] numberVisit = new boolean[46];

    private SpinningWheelView wheelView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roulette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity context = getActivity();
        initWheelView(Objects.requireNonNull(context));
        initTextView(Objects.requireNonNull(context));
        initButton(Objects.requireNonNull(context));
    }

    private void initWheelView(FragmentActivity context) {
        wheelView = context.findViewById(R.id.wheelview);

        List<Integer> numberList = new ArrayList<>();
        for(int i = 1; i <= 45; i++) {
            numberList.add(i);
        }

        wheelView.setItems(numberList); // 초기 데이터 설정
        wheelView.setEnabled(false);    // 사용자가 룰렛을 직접 터치 불가
        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<Integer>() {
            @Override
            public void onRotation() { }

            @Override
            public void onStopRotation(Integer item) {
                for(int i = 0; i < positionVisit.length; i++) {
                    if(!positionVisit[i] && !numberVisit[item]) {
                        Log.d("@@@@@@@@@@@@@@@", "i: " + i + " item: " + item);
                        positionVisit[i] = true;
                        numberVisit[item] = true;
                        setNumbersText(i, Integer.toString(item));
                        break;
                    }
                }

                btn_pick.setClickable(true);
            }
        });
    }

    private void initTextView(FragmentActivity context) {
        iv_help = context.findViewById(R.id.iv_help);
        iv_help.setOnClickListener(view -> {
            showDialog(context);
        });

        iv_cancel1 = context.findViewById(R.id.iv_cancel1);
        iv_cancel1.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel1, tv_number1, 0);
        });
        iv_cancel2 = context.findViewById(R.id.iv_cancel2);
        iv_cancel2.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel2, tv_number2, 1);
        });
        iv_cancel3 = context.findViewById(R.id.iv_cancel3);
        iv_cancel3.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel3, tv_number3, 2);
        });
        iv_cancel4 = context.findViewById(R.id.iv_cancel4);
        iv_cancel4.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel4, tv_number4, 3);
        });
        iv_cancel5 = context.findViewById(R.id.iv_cancel5);
        iv_cancel5.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel5, tv_number5, 4);
        });
        iv_cancel6 = context.findViewById(R.id.iv_cancel6);
        iv_cancel6.setOnClickListener(view -> {
            cancelChoiceNumber(iv_cancel6, tv_number6, 5);
        });

        tv_number1 = context.findViewById(R.id.tv_number1);
        tv_number1.setOnClickListener(view -> {
            iv_cancel1.setVisibility(iv_cancel1.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        tv_number2 = context.findViewById(R.id.tv_number2);
        tv_number2.setOnClickListener(view -> {
            iv_cancel2.setVisibility(iv_cancel2.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        tv_number3 = context.findViewById(R.id.tv_number3);
        tv_number3.setOnClickListener(view -> {
            iv_cancel3.setVisibility(iv_cancel3.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        tv_number4 = context.findViewById(R.id.tv_number4);
        tv_number4.setOnClickListener(view -> {
            iv_cancel4.setVisibility(iv_cancel4.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        tv_number5 = context.findViewById(R.id.tv_number5);
        tv_number5.setOnClickListener(view -> {
            iv_cancel5.setVisibility(iv_cancel5.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        tv_number6 = context.findViewById(R.id.tv_number6);
        tv_number6.setOnClickListener(view -> {
            iv_cancel6.setVisibility(iv_cancel6.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
    }

    private void showDialog(FragmentActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("룰렛 설명서")
                .setMessage(getResources().getString(R.string.roulette_help))
                .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelChoiceNumber(ImageView imageView, TextView textView, int position) {
        int number = Integer.parseInt(textView.getText().toString());
        numberVisit[number] = false;
        positionVisit[position] = false;

        textView.setText("");
        imageView.setVisibility(View.INVISIBLE);
    }

    private void initButton(FragmentActivity context) {
        btn_pick = context.findViewById(R.id.roulette_btn_pick);
        btn_pick.setOnClickListener(view -> {
            btn_pick.setClickable(false);
            wheelView.rotate(360, 1500, 50);
        });

        btn_clear = context.findViewById(R.id.roulette_btn_clear);
        btn_clear.setOnClickListener(view -> {
            tv_number1.setText("");
            tv_number2.setText("");
            tv_number3.setText("");
            tv_number4.setText("");
            tv_number5.setText("");
            tv_number6.setText("");

            Arrays.fill(numberVisit, false);
            Arrays.fill(positionVisit, false);

            iv_cancel1.setVisibility(View.INVISIBLE);
            iv_cancel2.setVisibility(View.INVISIBLE);
            iv_cancel3.setVisibility(View.INVISIBLE);
            iv_cancel4.setVisibility(View.INVISIBLE);
            iv_cancel5.setVisibility(View.INVISIBLE);
            iv_cancel6.setVisibility(View.INVISIBLE);
        });
    }

    private void setNumbersText(int position, String str) {
        switch (position) {
            case 0:
                tv_number1.setText(str);
                break;
            case 1:
                tv_number2.setText(str);
                break;
            case 2:
                tv_number3.setText(str);
                break;
            case 3:
                tv_number4.setText(str);
                break;
            case 4:
                tv_number5.setText(str);
                break;
            case 5:
                tv_number6.setText(str);
                break;
        }
    }
}