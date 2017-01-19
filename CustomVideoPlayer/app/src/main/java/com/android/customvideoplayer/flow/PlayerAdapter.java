package com.android.customvideoplayer.flow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.customvideoplayer.R;
import com.android.customvideoplayer.customview.CustomTextureView;
import com.android.customvideoplayer.customview.VideoAdapterData;
import com.android.customvideoplayer.data.VideoData;

import java.util.List;

/**
 * Created by akanksha on 26/7/16.
 */
public class PlayerAdapter extends BaseAdapter {

    private Context mContext;
    private List<VideoData> mVideoList;
    private CustomTextureView customTextureView = null;
    private boolean mAutoPlay;

    PlayerAdapter(Context context, List<VideoData> mVideoList) {
        this.mContext = context;
        this.mVideoList = mVideoList;
    }

    @Override
    public int getCount() {
        if (mVideoList != null) {
            return mVideoList.size();
        }
        return 0;
    }

    @Override
    public VideoData getItem(int position) {
        return mVideoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoData aVideoData = getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_player, parent, false);
        CustomTextureView customVideo = (CustomTextureView) convertView.findViewById(R.id.custom_video);

        String screenName = "PlayerList";

        if (position == 0) {
            customVideo.setValues("", aVideoData.getVideoUrl(), new VideoAdapterData(aVideoData.getVideoUrl(), screenName), customTextureViewListener);
            customTextureView = customVideo;
        }


        return convertView;
    }

    private CustomTextureView.CustomTextureViewListener customTextureViewListener = new CustomTextureView.CustomTextureViewListener() {

        @Override
        public void onPlayClicked(View customVideo) {

        }

        @Override
        public void onMediaPlayed(View customVideo, int duration) {

        }

        @Override
        public void onMediaError(String error) {

        }

        @Override
        public void onCompletionVideo() {

        }

        @Override
        public void onSurfaceFinished(View customVideo, int mpProgress) {

        }
    };

    public void autoPlayFirstVideo() {
        if (mAutoPlay) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (customTextureView != null) {
                        customTextureView.autoPlay();
                    }
                }
            }, 300);
            mAutoPlay = false;
        }
    }
}
