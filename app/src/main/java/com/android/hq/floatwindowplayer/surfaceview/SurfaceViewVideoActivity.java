package com.android.hq.floatwindowplayer.surfaceview;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.android.hq.floatwindowplayer.Constants;
import com.android.hq.floatwindowplayer.MediaPlayerManager;
import com.android.hq.floatwindowplayer.R;
import com.android.hq.floatwindowplayer.Utils;
import com.android.hq.floatwindowplayer.VideoPlayerService;

import java.io.IOException;

public class SurfaceViewVideoActivity extends Activity {
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_video);
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(mCallback);
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mMediaPlayer = MediaPlayerManager.getInstance().getMediaPlayer();
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void playFloatWindow(View view) {
        VideoPlayerService.startService(this,Constants.WINDOW_TYPE_SV);
    }

    public void updateVideoSize(int width, int height){
        int playerWidth = getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
        int playerHeight = getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
        Utils.updateVideoSize(this, mSurfaceView, width, height, playerWidth, playerHeight);
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mMediaPlayer.setDisplay(holder);
            try {
                mMediaPlayer.setDataSource(Constants.VIDEO_URL);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            updateVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
            mMediaPlayer.start();
            Log.e("Test", "onPrepared");
        }
    };
}
