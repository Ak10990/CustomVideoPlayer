package com.android.customvideoplayer.flow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.android.customvideoplayer.R;
import com.android.customvideoplayer.data.VideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akanksha on 26/7/16.
 */
public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initValues();
    }

    private void initValues() {
        initViews();
    }

    private void initViews() {
        ListView mainListview = (ListView) findViewById(R.id.main_listview);

        List<VideoData> list = new ArrayList<>();
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/2a2b3ee3-e63f-4017-8b34-fda066ca345b01-final.mp4", R.drawable.dummy1));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/58690f04-5f27-46f4-a64b-d8e7c3a6897501-final.mp4", R.drawable.dummy2));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/0ffd7411-69b3-4fa5-89af-77355eae18d001-final.mp4", R.drawable.dummy3));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/5e74d353-3023-43de-90b1-a1a15387b110-final.mp4", R.drawable.dummy4));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/80f6b415-3a4f-4064-9afa-c7cfd5d29c39-final.mp4", R.drawable.dummy5));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/a7dff82d-6801-4ac3-b1c6-11b38462c3d5-final.mp4", R.drawable.dummy6));
        list.add(new VideoData("https://hiq-video-reviews.s3-ap-southeast-1.amazonaws.com/f17f5ed0-9912-445e-aabc-8d1d6cc4eea7-final.mp4", R.drawable.dummy7));

        mainListview.setAdapter(new PlayerAdapter(this, list));
    }
}
