package com.bytedance.androidcamp.network.dou.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bytedance.androidcamp.network.dou.MyConstants;
import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.listView.MyListAdapter;
import com.bytedance.androidcamp.network.dou.model.GetVideoResponse;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePage extends Fragment {

    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private MyListAdapter mAdapter;
    private RefreshLayout refreshLayout;
    private SearchView searchView;
    private Spinner spinner;

    private Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService=retrofit.create(IMiniDouyinService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_homepage, container, false);
        mRv = v.findViewById(R.id.rv);
        refreshLayout = v.findViewById(R.id.RefreshLayout);
        searchView = v.findViewById(R.id.search_view);
        spinner = v.findViewById(R.id.search_spinner);

        searchView.setBackgroundColor(Color.WHITE);
        spinner.setBackgroundColor(Color.WHITE);

        initRecyclerView();
        initRefreshLayout();
        initSearchView();
        initSpinner();
        fetchFeed();
        return v;
    }

    private void initRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager;
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRv.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new MyListAdapter(getActivity());
        mRv.setAdapter(mAdapter);
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                fetchFeed();
                searchView.setQuery("", false);
                searchView.clearFocus();
                refreshLayout.finishRefresh(1000);
            }
        });
        refreshLayout.setRefreshHeader(new BezierCircleHeader(getContext()));
    }

    private void initSearchView() {
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void search(String s){
        if(s.isEmpty()){
            mAdapter.setVideos(mVideos);
            return;
        }
        String searchType = spinner.getSelectedItem().toString();
        List<Video> searchResult = new ArrayList<>();
        for(int i = 0; i < mVideos.size(); i++){
            switch (searchType) {
                case "By ID":
                    if(mVideos.get(i).getStudentId().contains(s))
                        searchResult.add(mVideos.get(i));
                    break;
                case "By Name":
                    if(mVideos.get(i).getUserName().contains(s))
                        searchResult.add(mVideos.get(i));
                    break;
            }
        }
        mAdapter.setVideos(searchResult);
    }

    private void initSpinner() {
        final String[] spinnerItem = {"By ID", "By Name"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item, spinnerItem);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }


    public void fetchFeed() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideoResponse>() {
            @Override
            public void onResponse(Call<GetVideoResponse> call, Response<GetVideoResponse> response) {
                if(response.body()!=null && response.isSuccessful()){
                    mVideos=response.body().getVideos();
                    mAdapter.setVideos(mVideos);
                    MyConstants.videoList = mVideos;
                    //Toast.makeText(getContext(), "获取成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetVideoResponse> call, Throwable throwable) {
                //Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // ToDo: post videos
    /* post something
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(MainActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        Call<PostVideoResponse> call = miniDouyinService.postVideo("3170104246","wjy",coverImagePart,videoPart);
        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if(response.body()!=null && response.isSuccessful()){
                    mBtn.setText(R.string.select_an_image);
                    mBtn.setEnabled(true);
                    Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        });

    }
    */
}
