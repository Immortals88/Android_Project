package com.bytedance.androidcamp.network.dou.listView;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.VideoActivity;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView img;
    private TextView username;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.img);
        username=itemView.findViewById(R.id.tv_username);
    }

    public static MyViewHolder create(Context context, ViewGroup root){
        View v = LayoutInflater.from(context).inflate(R.layout.video_item_view, root, false);
        return new MyViewHolder(v);
    }

    public void bind(final Activity activity, final Video video) {
        ImageHelper.displayWebImage(video.getImageUrl(), img);
        username.setText(video.getUserName());
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoActivity.launch(activity, video.getVideoUrl());
            }
        });
    }
}
