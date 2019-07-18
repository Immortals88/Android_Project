package com.bytedance.androidcamp.network.dou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.TestLooperManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.wx.goodview.GoodView;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private static List<String> allUrlList = new ArrayList<>();
    //private List<String> playList = new ArrayList<>();
    private int playCursor;

    public static void launch(Activity activity, String url, final List<String> urlList) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra("url", url);
        allUrlList = urlList;
        activity.startActivity(intent);
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        String url = getIntent().getStringExtra("url");
        VideoView videoView = findViewById(R.id.video_container);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               progressBar.setVisibility(View.GONE);
            }
        });
       progressBar.setVisibility(View.VISIBLE);


    }*/
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    final String DEBUG_TAG = "----------->";
    MyLayoutManager myLayoutManager;
    private OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String nowUrl = getIntent().getStringExtra("url");
        allUrlList.remove(allUrlList.indexOf(nowUrl));
        allUrlList.add(0, nowUrl);

        initView();
        initListener();

    }
    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_video);
        myLayoutManager = new MyLayoutManager(this, OrientationHelper.VERTICAL,false);

        mAdapter = new MyAdapter();
        mRecyclerView.setLayoutManager(myLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener(){
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                int index = 0;
                if (isNext){
                    index = 0;
                }else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isNext) {
                int index = 0;
                playVideo(index, position);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        public MyAdapter(){
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //增加封面
            ImageView imageView = new ImageView(VideoActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.bg_tab);
            holder.videoPlayer.setThumbImageView(imageView);
            //增加title
            holder.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
            //设置返回键
            holder.videoPlayer.getBackButton().setVisibility(View.VISIBLE);
            //设置旋转
            orientationUtils = new OrientationUtils(VideoActivity.this, holder.videoPlayer);
            //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
            holder.videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orientationUtils.resolveByClick();
                }
            });
            holder.videoPlayer.setLooping(true);
            //是否可以滑动调整
            holder.videoPlayer.setIsTouchWiget(true);
            //设置返回按键功能
            holder.videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            holder.videoPlayer.startPlayLogic();
        }



        @Override
        public int getItemCount() {
            return 15;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView img_thumb;
            ImageView img_play;
            StandardGSYVideoPlayer videoPlayer;
            RelativeLayout rootView;

            private TextView LikeText, StarText;
            private ImageButton LikeBtn, StarBtn;
            private GoodView goodView;

            private int likeNum;
            private boolean isLiked = false, isStar = false;

            public ViewHolder(View itemView) {
                super(itemView);
                videoPlayer = itemView.findViewById(R.id.video_view);

                LikeText = itemView.findViewById(R.id.like_text);
                LikeBtn = itemView.findViewById(R.id.like_btn);
                StarText = itemView.findViewById(R.id.star_text);
                StarBtn = itemView.findViewById(R.id.star_btn);

                likeNum = (int)(Math.random() * 1000);
                goodView = new GoodView(itemView.getContext());

                LikeText.setText("" + likeNum);

                LikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isLiked){
                            LikeBtn.setBackground(getResources().getDrawable(R.drawable.like_white));
                            LikeText.setText("" + (--likeNum));
                            isLiked = false;
                        } else {
                            LikeBtn.setBackground(getResources().getDrawable(R.drawable.like));
                            LikeText.setText("" + (++likeNum));
                            showGoodView(v, R.drawable.like);
                            isLiked = true;
                        }
                    }
                });

                StarBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isStar){
                            StarText.setText("收藏");
                            StarBtn.setBackground(getResources().getDrawable(R.drawable.star_white));
                            isStar = false;
                        } else {
                            StarText.setText("已收藏");
                            StarBtn.setBackground(getResources().getDrawable(R.drawable.star));
                            isStar = true;
                            showGoodView(v, R.drawable.star);
                        }
                    }
                });

            }

            private void showGoodView(View v, int drawableID) {
                goodView.setImage(drawableID);
                goodView.setWidth(200);
                goodView.setHeight(200);
                goodView.show(v);
            }
        }
    }

    private void releaseVideo(int index){
        View itemView = mRecyclerView.getChildAt(index);
        final StandardGSYVideoPlayer videoPlayer = itemView.findViewById(R.id.video_view);
        videoPlayer.onVideoPause();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void playVideo(int position, int cursor) {

        View itemView = mRecyclerView.getChildAt(0);
        final StandardGSYVideoPlayer videoPlayer = itemView.findViewById(R.id.video_view);
        videoPlayer.setUp(allUrlList.get(cursor), false, "返回首页");
        videoPlayer.startPlayLogic();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

}
