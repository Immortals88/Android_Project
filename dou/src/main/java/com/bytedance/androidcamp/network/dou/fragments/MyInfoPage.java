package com.bytedance.androidcamp.network.dou.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bytedance.androidcamp.network.dou.MyConstants;
import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.VideoActivity;
import com.bytedance.androidcamp.network.dou.database.DouDatabase;
import com.bytedance.androidcamp.network.dou.database.UserEntity;
import com.bytedance.androidcamp.network.dou.listView.StarListAdapter;
import com.bytedance.androidcamp.network.dou.model.Video;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyInfoPage extends Fragment {

    private View v;
    private ImageView imageBg;
    private CircleImageView displayPhoto;
    private RecyclerView mRecyclerView;
    private StarListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_myinfo, container, false);

        TextView userNameText = v.findViewById(R.id.user_name);
        userNameText.setText(MyConstants.StuentName);
        TextView userIDText = v.findViewById(R.id.user_id);
        userIDText.setText(MyConstants.StudentID);

        imageBg = v.findViewById(R.id.myinfo_bg);
        Glide.with(getActivity())
                .load(R.drawable.info_bg)
                .into(imageBg);

        displayPhoto = v.findViewById(R.id.display_photo);
        Glide.with(getActivity())
                .load(R.drawable.diplay_photo)
                .into(displayPhoto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = v.findViewById(R.id.star_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new StarListAdapter();

        mAdapter.setOnItemClickListener(new StarListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                TextView nameText = view.findViewById(R.id.star_list_name_text);
                String name = nameText.getText().toString();

                List<Video> urlList = new ArrayList<>();
                for(int i = 0; i < MyConstants.videoList.size(); i++){
                    if(name.equals(MyConstants.videoList.get(i).getUserName())) {
                        urlList.add(MyConstants.videoList.get(i));
                    }
                }

                VideoActivity.launch(MyInfoPage.this.getActivity(), urlList.get(0).getVideoUrl(), urlList);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(MyConstants.DEBUG_TAG, "Start");

        RefreshTask refreshTask = new RefreshTask();
        refreshTask.execute();
    }

    private class RefreshTask extends AsyncTask<Void, Integer, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<UserEntity> userEntityList = new ArrayList<>();
            userEntityList = DouDatabase.getDatabase(v.getContext().getApplicationContext()).getUserEntityDao().getUserList();
            List<String> StarNameList = new ArrayList<>();
            StarNameList.clear();
            for(int i = 0; i < userEntityList.size(); i++){
                StarNameList.add(userEntityList.get(i).getName());
            }
            return StarNameList;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            mAdapter.setNameList(strings);
        }
    }
}



