package com.bytedance.androidcamp.network.dou.api;

import com.bytedance.androidcamp.network.dou.model.GetVideoResponse;
import com.bytedance.androidcamp.network.dou.model.PostVideoResponse;
import com.bytedance.androidcamp.network.dou.model.Video;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IMiniDouyinService {
    // TODO 7: Define IMiniDouyinService
    String BASE_URL = "http://test.androidcamp.bytedance.com/mini_douyin/invoke/";
    String PATH = "video";
    @Multipart
    @POST(value = PATH)
    Call<PostVideoResponse>postVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,@Part MultipartBody.Part video);
    @GET(PATH)
    Call<GetVideoResponse> getVideos();

    @GET(PATH)
    Call<GetVideoResponse> getVideosWithCondition(@Query("student_id") String studentId);

}
