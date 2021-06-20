package com.mepus.productiontest.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;
import com.mepus.productiontest.R;
import com.mepus.productiontest.roulette.MaterialColor;
import com.mepus.productiontest.roulette.TextDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RouletteFragment extends Fragment {
    private static final int ITEM_COUNT = 45;

    private Button btn_pick, btn_clear;
    private TextView tv_number1, tv_number2, tv_number3, tv_number4, tv_number5, tv_number6;
    private ImageView iv_cancel1, iv_cancel2, iv_cancel3, iv_cancel4, iv_cancel5, iv_cancel6, iv_help;

    private boolean[] positionVisit = new boolean[6];
    private boolean[] numberVisit = new boolean[46];

    private Timer rotateWheelTimer;
    private WheelView wheelView;

    private long lastWheelAngle;

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
        initTextView(context);
        initButton(context);
        initWheelView(context);
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
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

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
            if (btn_pick.getText().toString().equals("돌리기")) {
                initWheelTask(context);
                btn_pick.setText("멈추기");

                initCount();
            }
            else if (btn_pick.getText().toString().equals("멈추기")) {
                rotateWheelTimer.cancel();
                btn_pick.setText("돌리기");

                int selectedNumber = wheelView.getSelectedPosition() + 1;

                for (int i = 0; i < 6; i++) {
                    if (!positionVisit[i] && !numberVisit[selectedNumber]) {
                        positionVisit[i] = true;
                        numberVisit[selectedNumber] = true;
                        setNumbersText(i, Integer.toString(selectedNumber));
                        break;
                    }
                }
            }
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

            initCount();
        });
    }

    private void initCount() {
        wheelView.setAngle(0);
        lastWheelAngle = 0;
    }

    private void initWheelView(FragmentActivity context) {
        wheelView = context.findViewById(R.id.wheelview);

        //create data for the adapter
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(ITEM_COUNT);
        for (int i = 0; i < ITEM_COUNT; i++) {
            Map.Entry<String, Integer> entry = MaterialColor.random(context, "\\D*_500$");
            entries.add(entry);
        }

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
        wheelView.setAdapter(new MaterialColorAdapter(entries));

        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener((parent, itemDrawable, position) -> {
            //get the item at this position
            Map.Entry<String, Integer> selectedEntry = ((MaterialColorAdapter) parent.getAdapter()).getItem(position);
            parent.setSelectionColor(getContrastColor(selectedEntry));
        });

        //initialise the selection drawable with the first contrast color
        wheelView.setSelectionColor(getContrastColor(entries.get(0)));

        rotateWheelTimer = new Timer();
    }

    private void initWheelTask(FragmentActivity context) {
        TimerTask rotateWheelTask = new TimerTask() {
            @Override
            public void run() {
                if(lastWheelAngle > Long.MAX_VALUE - 100) {
                    initCount();
                }
                lastWheelAngle += 10;
                context.runOnUiThread(() -> {
                    wheelView.setAngle(lastWheelAngle);
                });
            }
        };

        rotateWheelTimer = new Timer();
        rotateWheelTimer.schedule(rotateWheelTask, 0, 10);
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

    //get the materials darker contrast
    private int getContrastColor(Map.Entry<String, Integer> entry) {
        String colorName = MaterialColor.getColorName(entry);
        return MaterialColor.getContrastColor(colorName);
    }

    static class MaterialColorAdapter extends WheelArrayAdapter<Map.Entry<String, Integer>> {
        MaterialColorAdapter(List<Map.Entry<String, Integer>> entries) {
            super(entries);
        }

        @Override
        public Drawable getDrawable(int position) {
            Drawable[] drawable = new Drawable[]{
                    createOvalDrawable(getItem(position).getValue()),
                    new TextDrawable(String.valueOf(position + 1))
            };
            return new LayerDrawable(drawable);
        }

        private Drawable createOvalDrawable(int color) {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }
    }
}