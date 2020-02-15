package com.android.hq.floatwindowplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

public class FloatPlayerUI extends FrameLayout implements IMediaPlayer{
    public final static String TAG="FloatPlayerUI";
    private Context mContext;
    private FloatPlayerController mController;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private IServiceHelper mServiceHelper;
    public FloatPlayerUI(Context context, IServiceHelper helper) {
        super(context);
        mContext = context;
        mServiceHelper = helper;

        initMediaPlayer();
        initSurfaceView();
        initController();
    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
    }

    private void initSurfaceView(){
        mSurfaceView = new SurfaceView(mContext);
        FrameLayout.LayoutParams surfaceViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceViewParams.gravity = Gravity.CENTER;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.float_window_root_width),
                getResources().getDimensionPixelSize(R.dimen.float_window_root_height),
                Gravity.CENTER);
        mSurfaceView.setLayoutParams(lp);
        if (mSurfaceView.getParent() == null) {
            removeAllViews();
            addView(mSurfaceView, surfaceViewParams);
        }

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initController(){
        mController = new FloatPlayerController(mContext, this);
        RelativeLayout.LayoutParams controllParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        controllParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (mController.getParent() == null) {
            addView(mController, controllParams);
        }
        mController.setVisibility(View.VISIBLE);
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mMediaPlayer.setDisplay(surfaceHolder);
            try {
                mMediaPlayer.setDataSource(Constants.VIDEO_URL);
                mMediaPlayer.prepareAsync();
                mController.showLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.e("Test", "onCompletion");
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Log.e("Test", "onError");
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mMediaPlayer.start();
            Log.e("Test", "onPrepared");
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            Log.e("Test", "onVideoSizeChanged width = " + width + ", height=" + height);
            //updateVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
            int playerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
            int playerHeight = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
            Utils.updateVideoSize(mContext, mSurfaceView,width, height, playerWidth, playerHeight);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            Log.e("Test","onBufferingUpdate i = "+i);
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            Log.e("Test","onInfo what = "+what);
            switch (what){
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    mController.onBeginPlay();
                    break;
            }
            return false;
        }
    };

    @Override
    public void playPause() {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }else{
            mMediaPlayer.start();
        }
    }

    @Override
    public void closePlayer() {
        mMediaPlayer.stop();
        mServiceHelper.closeFloatWindow();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void exitFloatWindow(){

    }
}
