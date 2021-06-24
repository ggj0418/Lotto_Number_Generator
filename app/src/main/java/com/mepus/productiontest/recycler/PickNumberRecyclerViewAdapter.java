package com.mepus.productiontest.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mepus.productiontest.R;

import java.util.List;

public class PickNumberRecyclerViewAdapter extends RecyclerView.Adapter<PickNumberRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;

    public PickNumberRecyclerViewAdapter(List<String> list) {
        mData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.text1);
            iv = itemView.findViewById(R.id.iv_main_close);
        }
    }

    @NonNull
    @Override
    public PickNumberRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pick_number_recyclerview_item, parent, false);

        PickNumberRecyclerViewAdapter.ViewHolder vh = new PickNumberRecyclerViewAdapter.ViewHolder(view) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PickNumberRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tv.setText(mData.get(position));

        holder.iv.setOnClickListener(view -> {
            mData.remove(position);
            notifyDataSetChanged();
        });
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
