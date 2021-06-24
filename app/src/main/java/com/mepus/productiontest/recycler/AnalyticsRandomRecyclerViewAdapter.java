package com.mepus.productiontest.recycler;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mepus.productiontest.R;

import java.text.DecimalFormat;
import java.util.List;

public class AnalyticsRandomRecyclerViewAdapter extends RecyclerView.Adapter<AnalyticsRandomRecyclerViewAdapter.ViewHolder>{
    private List<int[]> mData;

    public AnalyticsRandomRecyclerViewAdapter(List<int[]> list) {
        mData = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView number_tv, count_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number_tv = itemView.findViewById(R.id.random_pick_number_tv);
            count_tv = itemView.findViewById(R.id.random_pick_count_tv);
        }
    }

    @NonNull
    @Override
    public AnalyticsRandomRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pick_analytics_recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AnalyticsRandomRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.number_tv.setText(String.valueOf(mData.get(position)[0]));
        holder.count_tv.setText(parseNumberToMoney(mData.get(position)[1]) + " 회");
    }

    private String parseNumberToMoney(int number) {
        return new DecimalFormat("###,###").format(number);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
}
