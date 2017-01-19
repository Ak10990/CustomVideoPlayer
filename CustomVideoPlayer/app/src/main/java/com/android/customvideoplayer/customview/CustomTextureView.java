package com.android.customvideoplayer.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.customvideoplayer.R;
import com.android.customvideoplayer.utils.NetworkUtils;
import com.android.customvideoplayer.utils.Utils;

import java.io.IOException;

/**
 * Created by Akanksha on 14/7/16.
 * Custom TextureView with handling the Media player.
 */
public class CustomTextureView extends FrameLayout implements View.OnClickListener, View.OnTouchListener {

    private final int DURATION = 2000;//Seek showing duration
    private final int SEEK_INTERVAL = 1000;
    private TextureView textureView;
    private RelativeLayout seekBarLayout;
    private SeekBar seekBar;
    private TextView passedTimeView, totalTimeView, noNetView;
    private ImageView playBtn, thumbnail;
    private ProgressBar progressBar;
    private SeekMediaPlayer mediaPlayer = null;
    private CustomTextureViewListener listener;
    private Handler seekVisibilityHandler = new Handler();
    private Handler seekProgressHandler = new Handler();
    private String videoUrl;
    private int mediaState = SeekMediaPlayer.NOT_STARTED;
    private VideoAdapterData videoAdapterData;
    private int mpProgress = -1;
    private int totalDuration = 0;
    private boolean isMediaPrepared = false;
    private boolean isPauseOnPrepare = false;
    private boolean isMediaPlayCompleteMode = false;//Handling the case when onComplete gets triggered for all small media player errors.

    public CustomTextureView(Context context) {
        super(context);
        init();
    }

    public CustomTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initial Frame Views initialization.
     */
    private void init() {
        inflate(getContext(), R.layout.custom_textureview, this);
        textureView = (TextureView) findViewById(R.id.custom_texture_view);
        seekBarLayout = (RelativeLayout) findViewById(R.id.custom_seek_layout);
        passedTimeView = (TextView) findViewById(R.id.custom_passed_time_view);
        totalTimeView = (TextView) findViewById(R.id.custom_total_time_view);
        seekBar = (SeekBar) findViewById(R.id.custom_seekbar);
        progressBar = (ProgressBar) findViewById(R.id.custom_progress);
        playBtn = (ImageView) findViewById(R.id.custom_play_btn);
        thumbnail = (ImageView) findViewById(R.id.custom_thumbnail);
        noNetView = (TextView) findViewById(R.id.custom_no_net_view);

        playBtn.setOnClickListener(this);
        textureView.setOnTouchListener(this);
        Utils.tintSeekBar(seekBar);
        Utils.tintProgressBar(progressBar);
    }

    /**
     * Getters
     */

    public SeekMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Setting the values after init.
     */
    public void setValues(String thumbnailUrl, String videoUrl, VideoAdapterData videoAdapterData,
                          CustomTextureViewListener customTextureViewListener) {
        this.listener = customTextureViewListener;
        this.videoUrl = videoUrl;
        textureView.setVisibility(View.GONE);
        seekBarLayout.setVisibility(INVISIBLE);
        noNetView.setVisibility(GONE);
        playBtn.setVisibility(VISIBLE);
        setThumbnailView(thumbnailUrl);

        if (!(videoUrl != null && !videoUrl.equals(""))) {
            textureView.setVisibility(View.GONE);
            playBtn.setVisibility(View.GONE);
        }
        this.videoAdapterData = videoAdapterData;
        mediaPlayer = null;
        isMediaPrepared = false;
    }

    private void setThumbnailView(String thumbnailUrl) {
        thumbnail.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (mediaPlayer != null) {
                Surface oSurface = new Surface(surface);
                mediaPlayer.setSurface(oSurface);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            destroySurface();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (mediaPlayer != null) {
                Surface oSurface = new Surface(surface);
                mediaPlayer.setSurface(oSurface);
            }
        }

        private void destroySurface() {
            if (mediaPlayer != null) {
                mpProgress = mediaPlayer.getCurrentPosition();
                if (mediaState == SeekMediaPlayer.NOT_STARTED) {
                    mediaPlayer.release();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }

                if (listener != null) {
                    listener.onSurfaceFinished(CustomTextureView.this, mpProgress);
                }
            }
            mediaState = SeekMediaPlayer.RELEASE;
            onMediaStateChanged();
        }
    };

    /**
     * Pausing and resuming functions
     */
    public void resumeActivity() {
    }

    public void pauseActivity() {
        pause();
    }

    public boolean isPause() {
        if (mediaState == SeekMediaPlayer.PAUSE) {
            return true;
        }
        return false;
    }

    /**
     * External UI changes
     */
    /**
     * Hiding/Showing play button
     */
    public void hideShowPlayBtn(boolean isShow) {
        if (isShow) {
            playBtn.setVisibility(VISIBLE);
        } else {
            playBtn.setVisibility(GONE);
        }
    }

    /**
     * Media state changing functions.
     */
    private void stop() {
        if (mediaPlayer != null) {
            if (mediaState == SeekMediaPlayer.NOT_STARTED) {
                mediaPlayer.release();
            } else {
                mpProgress = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
        mediaState = SeekMediaPlayer.STOP;
        onMediaStateChanged();
    }

    public void release() {
        if (mediaPlayer != null) {
            if (mediaState == SeekMediaPlayer.NOT_STARTED) {
                mediaPlayer.release();
            } else {
                mpProgress = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
        mediaState = SeekMediaPlayer.RELEASE;
        onMediaStateChanged();
    }

    public void pause() {
        if (mediaPlayer != null) {
            if (mediaState == SeekMediaPlayer.NOT_STARTED) {
               /* playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_play));
                progressBar.setVisibility(GONE);
                playBtn.setVisibility(VISIBLE);
                mediaPlayer.reset();*///Media player duration issue.
                //mediaPlayer.release();--crash at reset
                //mediaPlayer=null;--media plays surface not visible
                isPauseOnPrepare = true;
            } else if (mediaPlayer.isPlaying() || mediaState == SeekMediaPlayer.PLAYING) {
                onMediaStateChanged();
            }
        }
    }

    public void autoPlay() {
        if (mediaState != SeekMediaPlayer.PLAYING) {
            if (mediaPlayer == null) {
                mediaPlayer = new SeekMediaPlayer();
                mediaPlayer.setListeners();
                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
            onMediaStateChanged();
        }
    }

    private void play() {
        if (mediaPlayer == null) {
            mediaPlayer = new SeekMediaPlayer();
            mediaPlayer.setListeners();
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }

        if ((mediaState == SeekMediaPlayer.NOT_STARTED || mediaState == SeekMediaPlayer.PAUSE) && listener != null) {
            listener.onPlayClicked(CustomTextureView.this);
        }
        onMediaStateChanged();
    }

    /**
     * Handling click, touch of all the views.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.custom_play_btn:
                play();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.custom_texture_view:
                if (mediaState > -1) {
                    textureTouch(seekBarLayout.getVisibility() == VISIBLE);
                }
                break;
        }
        return false;
    }

    /**
     * For changing the view corresponding media state change.
     */
    private void onMediaStateChanged() {
        if (mediaState == SeekMediaPlayer.NOT_STARTED) {
            /**
             * Before starting texture view video
             */
            playBtn.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(videoUrl);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            textureView.setVisibility(VISIBLE);
            textureView.setKeepScreenOn(true);
            playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_pause));
        } else if (mediaState == SeekMediaPlayer.STOP || mediaState == SeekMediaPlayer.RELEASE) {
            /**
             * Media ended.
             */
            playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_play));
            playBtn.setVisibility(VISIBLE);
            seekBar.setProgress(0);
            passedTimeView.setText("0:00");
            seekBarLayout.setVisibility(INVISIBLE);
            progressBar.setVisibility(GONE);
            textureView.setKeepScreenOn(false);
            if (mediaPlayer != null) {
                if (mpProgress > 0 && isMediaPlayCompleteMode) {
                    //triggerDurationPlayedEvent
                    isMediaPlayCompleteMode = false;
                }
            }
            if (mediaState == SeekMediaPlayer.RELEASE) {
                mpProgress = -1;
                mediaPlayer = null;
                isMediaPrepared = false;
                mediaState = SeekMediaPlayer.NOT_STARTED;
            } else {
                mpProgress = 0;
                mediaState = SeekMediaPlayer.PAUSE;
            }
        } else if (mediaState == SeekMediaPlayer.PLAYING) {
            /**
             * Play state to pause state
             */
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_play));
            mediaState = SeekMediaPlayer.PAUSE;
            textureView.setKeepScreenOn(false);
            seekProgressHandler.removeCallbacks(progressSeekRunnable);
            if (mediaPlayer != null) {
                mpProgress = mediaPlayer.getCurrentPosition();
            }
        } else if (mediaState == SeekMediaPlayer.PAUSE) {
            /**
             * Pause state to play state
             */
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            textureTouch(false);

            textureView.setVisibility(VISIBLE);
            textureView.setKeepScreenOn(true);
            if (mediaState != SeekMediaPlayer.PAUSE) {
                if (listener != null && mediaPlayer != null) {
                    listener.onMediaPlayed(CustomTextureView.this, totalDuration);
                }
            }
            playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_pause));
            seekProgressHandler.postDelayed(progressSeekRunnable, SEEK_INTERVAL);
            mediaState = SeekMediaPlayer.PLAYING;
        }
    }

    /**
     * To handle the touch of texture view for showing seek view.
     *
     * @param isImmediate true if needs to be shown in 0.0 sec, false otherwise.
     */
    private void textureTouch(boolean isImmediate) {
        int duration = 0;

        if (!isImmediate) {
            seekBarLayout.setVisibility(VISIBLE);
            playBtn.setVisibility(VISIBLE);
            duration = DURATION;
        }
        seekVisibilityHandler.postDelayed(visibilitySeekRunnable, duration);
    }

    Runnable visibilitySeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isMediaPrepared && mediaPlayer.isPlaying()) {
                seekBarLayout.setVisibility(INVISIBLE);
                playBtn.setVisibility(GONE);
            }
        }
    };
    private Runnable progressSeekRunnable = new Runnable() {
        public void run() {
            if (mediaPlayer != null) {
                int currentDuration = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(Utils.getSec(currentDuration));
                passedTimeView.setText(Utils.getTimeFromDuration(currentDuration));
                if (currentDuration != totalDuration) {
                    seekProgressHandler.postDelayed(this, SEEK_INTERVAL);
                }
            }
        }
    };

    /**
     * Media player with seek bar integrating with media functions.
     */
    public final class SeekMediaPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

        /**
         * Media states
         */
        public final static int NOT_STARTED = -1;
        public final static int PLAYING = 0;
        public final static int PAUSE = 1;
        public final static int STOP = 2;
        public final static int RELEASE = 3;

        public void setListeners() {
            if (mediaPlayer != null) {
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setOnCompletionListener(this);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            totalDuration = mp.getDuration();
            mp.setLooping(false);
            /**
             * Seekbar
             */
            totalTimeView.setText(Utils.getTimeFromDuration(totalDuration));
            seekBar.setMax(Utils.getSec(totalDuration));
            seekBar.setProgress(0);
            passedTimeView.setText("0:00");
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer != null) {
                        mediaPlayer.seekTo(Utils.getMsec(progress));
                        seekVisibilityHandler.removeCallbacks(visibilitySeekRunnable);
                        if (mediaState == PLAYING) {
                            mediaPlayer.start();
                            seekVisibilityHandler.postDelayed(visibilitySeekRunnable, DURATION);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            thumbnail.setVisibility(View.GONE);
            textureView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            playBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_media_pause));
            textureTouch(seekBarLayout.getVisibility() == VISIBLE);
            if (mpProgress > 0) {
                mp.seekTo(mpProgress);
            }
            mp.start();
            seekProgressHandler.postDelayed(progressSeekRunnable, SEEK_INTERVAL);

            isMediaPrepared = true;
            if (mediaPlayer != null) {
                //triggerVideoReviewEvent
                isMediaPlayCompleteMode = true;
            }
            if (listener != null) {
                listener.onMediaPlayed(CustomTextureView.this, totalDuration);
            }
            mediaState = PLAYING;
            if (isPauseOnPrepare) {
                onMediaStateChanged();
                isPauseOnPrepare = false;
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("HIQ", "Media Player error" + extra);
            Utils.triggerVideoErrorEvent(videoUrl, what, extra);
            if (!NetworkUtils.isInternetConnected(getContext())) {
                noNetView.setVisibility(VISIBLE);
                playBtn.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }
            if (listener != null) {
                listener.onMediaError("Media Player error" + what + extra);
            }
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            int mpTemp = mp.getCurrentPosition();
            if (mpTemp > 0) {
                mediaState = STOP;
                mpProgress = mpTemp;
                onMediaStateChanged();
                if (listener != null) {
                    listener.onCompletionVideo();
                }
            }
        }
    }

    /**
     * Listener if required to do any further on any states.
     */
    public interface CustomTextureViewListener {

        /**
         * On Play button clicked first time or changed to play mode from pause.
         */
        void onPlayClicked(View customVideo);

        /**
         * On media player started.
         */
        void onMediaPlayed(View customVideo, int duration);

        /**
         * On Media Error.
         */
        void onMediaError(String error);

        /**
         * On Video Completion.
         */
        void onCompletionVideo();

        /**
         * Set the duration next seek.
         */
        void onSurfaceFinished(View customVideo, int mpProgress);
    }

}