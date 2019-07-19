package com.bytedance.androidcamp.network.dou.listView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.androidcamp.network.dou.R;

public class StarViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;

    public  StarViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.star_list_name_text);
    }

    public static StarViewHolder create(Context context, ViewGroup root) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_list_item, root, false);
        return new StarViewHolder(v);
    }

    public void bind(final String name) {
        mTextView.setText(name);
    }

}
