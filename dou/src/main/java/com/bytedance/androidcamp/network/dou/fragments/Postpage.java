package com.bytedance.androidcamp.network.dou.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bytedance.androidcamp.network.dou.MainActivity;
import com.bytedance.androidcamp.network.dou.MyConstants;
import com.bytedance.androidcamp.network.dou.R;
import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.PostVideoResponse;
import com.bytedance.androidcamp.network.dou.util.ResourceUtils;
import com.bytedance.androidcamp.network.dou.util.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static com.bytedance.androidcamp.network.dou.MyConstants.PICK_IMAGE;


public class Postpage extends Fragment {
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int VIDEO_WITH_CAMERA=567;
    private static final String TAG = "Post";
    private ImageView preview;
    private ImageView camera;
    private ImageView filePic;

    private boolean prepareCancel=false;

    private VideoView videoView;
    private TextView mBtn;
    private Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService=retrofit.create(IMiniDouyinService.class);

    private String[] permissions = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_postpage, container, false);

        camera= v.findViewById(R.id.iv_camera);
        filePic = v.findViewById(R.id.iv_file);
        videoView = v.findViewById(R.id.vv_show);
        camera.setImageResource(R.drawable.camera);
        filePic.setImageResource(R.drawable.file);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prepareCancel){
                    preview.setBackgroundResource(0);
                    videoView.pause();
                    mSelectedImage=null;
                    mSelectedVideo=null;
                    mBtn.setText(R.string.select_an_image);
                    prepareCancel=false;
                    camera.setVisibility(View.GONE);
                    filePic.setImageResource(R.drawable.file);
                }
                else{
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //设置视频录制的最长时间
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    //设置视频录制的画质
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.2);
                    startActivityForResult(intent, VIDEO_WITH_CAMERA);
                }
            }
        });
        preview = v.findViewById(R.id.iv_show);
        mBtn = v.findViewById(R.id.tv_info);
        filePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!utils.isPermissionsReady(getActivity(), permissions)){
                    utils.reuqestPermissions(getActivity(), permissions, MyConstants.REQUEST_EXTERNAL_STORAGE);
                    return;
                }

                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                        prepareCancel=false;
                        camera.setImageResource(R.drawable.camera);
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                                + mSelectedVideo
                                + ", mSelectedImage = "
                                + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });
        camera.setVisibility(View.GONE);
        return v;
    }

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
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
                preview.setImageURI(mSelectedImage);
                mBtn.setText(R.string.select_a_video);
                filePic.setImageResource(R.drawable.videofile);
                camera.setVisibility(View.VISIBLE);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                videoView.setVideoURI(mSelectedVideo);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
                filePic.setImageResource(R.drawable.post);
                camera.setImageResource(R.drawable.cancel);
                camera.setVisibility(View.VISIBLE);
                prepareCancel=true;
            }
            else if(requestCode==VIDEO_WITH_CAMERA){
                mSelectedVideo=data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(mSelectedVideo, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d(TAG, "path = " + picturePath);
            /*    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(picturePath);
                Bitmap bitmap2 = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);*/

                Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Images.Thumbnails.MICRO_KIND);
                //Bitmap bitmap2= BitmapUtil.createVideoThumbnail(url,MediaStore.Video.Thumbnails.MINI_KIND);
                //Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(mSelectedVideo.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                //Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(mSelectedVideo.getPath(),MediaStore.Images.Thumbnails.MINI_KIND);
                if(bitmap2!=null) {
                    /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "CameraDemo");

                    File file = new File(mediaStorageDir.getPath());
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    if (file != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            mSelectedImage = FileProvider.getUriForFile(getContext(), "com.bytedance.camera.demo", file);
                        else
                            mSelectedImage = Uri.fromFile(file);
                    }
                    preview.setImageURI(mSelectedImage);*/
                    //preview.setImageBitmap(bitmap2);
                }

           // mSelectedImage=Uri.parse("file://"+picturePath);
              //  preview.setImageURI(mSelectedImage);

                videoView.setVideoURI(mSelectedVideo);
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
                mBtn.setText(R.string.post_it);
                filePic.setImageResource(R.drawable.post);
                camera.setImageResource(R.drawable.cancel);
                prepareCancel=true;
            }
        }

    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(getContext(), uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    };

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        Call<PostVideoResponse> call = miniDouyinService.postVideo(MyConstants.StudentID,MyConstants.StuentName,coverImagePart,videoPart);
        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if(response.body()!=null && response.isSuccessful()){
                    mBtn.setText(R.string.select_an_image);
                    mBtn.setEnabled(true);
                    filePic.setImageResource(R.drawable.file);
                    Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
