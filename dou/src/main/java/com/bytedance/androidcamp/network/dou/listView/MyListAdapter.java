package com.bytedance.androidcamp.network.dou.listView;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;

import com.bytedance.androidcamp.network.dou.model.Video;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Video> videos = new ArrayList<>();
    private Activity activity;

    public MyListAdapter(Activity act){
        super();
        activity = act;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return MyViewHolder.create(viewGroup.getContext(), viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        final Video video = videos.get(i);
        viewHolder.bind(activity, video, videos);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video> mvideos){
        if(mvideos == null) return;
        videos = mvideos;
        notifyDataSetChanged();
    }
}
