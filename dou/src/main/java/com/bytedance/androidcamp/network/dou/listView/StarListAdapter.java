package com.bytedance.androidcamp.network.dou.listView;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StarListAdapter extends RecyclerView.Adapter<StarViewHolder> {

    private List<String> nameList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        holder.bind(nameList.get(position));
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClickListener(holder.itemView, pos);
                }
            });
        }
    }

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return StarViewHolder.create(parent.getContext(), parent);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void setNameList(List<String> list){
        if(list == null) return;
        nameList = list;
        notifyDataSetChanged();
    }
}
