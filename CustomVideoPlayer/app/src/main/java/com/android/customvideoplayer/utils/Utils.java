package com.android.customvideoplayer.utils;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.util.HashMap;

/**
 * Date and UI Utils
 */
public class Utils {

    /**
     * Util to convert duration into hr:min:sec
     *
     * @param duration time in mSec
     * @return hh:mm:ss
     */
    public static String getTimeFromDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (seconds < 60) {
            return "0:" + getTwoDigit(seconds);
        } else if (minutes < 60) {
            seconds = seconds - (minutes * 60);
            return minutes + ":" + getTwoDigit(seconds);
        } else {
            minutes = minutes - (hours * 60);
            seconds = seconds - (hours * 60 * 60) - (minutes * 60);
            return hours + ":" + getTwoDigit(minutes) + ":" + getTwoDigit(seconds);
        }
    }

    /**
     * Util to get the duration of video played in sec.
     *
     * @param startProgress Starting progress in mSec
     * @param endProgress   ending progress in mSec.
     * @return Total progress played in sec.
     */
    public static int getDurationPlayed(int startProgress, int endProgress) {
        return (endProgress - startProgress) / 1000;
    }

    /**
     * Util to convert a value into 2 digit. 1->01, 10->10.
     */
    public static String getTwoDigit(long value) {
        String twoDigit = "";
        if (value < 10) {
            twoDigit = "0";
        }
        twoDigit += value;
        return twoDigit;
    }

    public static int getSec(int mSec) {
        return mSec / 1000;
    }

    public static int getMsec(int sec) {
        return sec * 1000;
    }

    /**
     * Tint Utils
     */
    public static void tintSeekBar(SeekBar seekBar) {
        int progressColor = 0xFF854EC5;
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        seekBar.getProgressDrawable().setColorFilter(progressColor, mode);
//        new ColorStateList()
//        seekBar.setProgressBackgroundTintList(seekBar.getBackgroundTintList().getColorForState(progressColor));
        /*int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked}, // disabled
                new int[]{android.R.attr.state_checked} // enabled
        };
        int[] colors = new int[]{ContextCompat.getColor(getContext(), R.color.pink), ContextCompat.getColor(getContext(), R.color.dark_pink)};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        Drawable wrappedDrawable = seekBar.getThumb();
        DrawableCompat.setTintList(wrappedDrawable, colorStateList);
        seekBar.setThumb(wrappedDrawable);*/
    }

    public static void tintProgressBar(@NonNull ProgressBar progressBar) {
        int progressColor = 0xFF854EC5;
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        if (progressBar.getIndeterminateDrawable() != null)
            progressBar.getIndeterminateDrawable().setColorFilter(progressColor, mode);
    }

    /**
     * Trigger event to track error of video.
     */
    public static void triggerVideoErrorEvent(String videoUrl, int what, int extra) {

        String whatStr = "";
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                whatStr = "MEDIA_ERROR_UNKNOWN";
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                whatStr = "MEDIA_ERROR_SERVER_DIED";
                break;
        }
        String extraStr = "";
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                extraStr = "MEDIA_ERROR_IO";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                extraStr = "MEDIA_ERROR_MALFORMED";
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                extraStr = "MEDIA_ERROR_UNSUPPORTED";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                extraStr = "MEDIA_ERROR_TIMED_OUT";
                break;
        }

        HashMap<String, Object> oObj = new HashMap<>();
        oObj.put("videoUrl", videoUrl);
        oObj.put("what", whatStr);
        oObj.put("extra", extraStr);
        //Push this event
    }

}