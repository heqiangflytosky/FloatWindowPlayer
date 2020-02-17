package com.android.hq.floatwindowplayer.textureview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.hq.floatwindowplayer.Constants;
import com.android.hq.floatwindowplayer.FloatPlayerUI;
import com.android.hq.floatwindowplayer.IServiceHelper;
import com.android.hq.floatwindowplayer.MediaPlayerManager;
import com.android.hq.floatwindowplayer.R;
import com.android.hq.floatwindowplayer.Utils;

import java.io.IOException;

public class TextureViewFloatPlayUI extends FloatPlayerUI {
    private TextureView mTextureView;
    public TextureViewFloatPlayUI(Context context, IServiceHelper helper) {
        super(context, helper);
        init();

        // -----
        mMediaPlayer = MediaPlayerManager.getInstance().getMediaPlayer();
        // -----
    }

    @Override
    public View getVideoContentView() {
        return mTextureView;
    }

    @Override
    public void initVideoContentView() {
        mTextureView = new TextureView(mContext);

        // -----
        final SurfaceTexture surfaceTexture = new MySurfaceTexture(TextureVideo2Activity.mTextureName);
        mTextureView.setSurfaceTexture(surfaceTexture);
        // -----

        FrameLayout.LayoutParams surfaceViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceViewParams.gravity = Gravity.CENTER;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.float_window_root_width),
                getResources().getDimensionPixelSize(R.dimen.float_window_root_height),
                Gravity.CENTER);
        mTextureView.setLayoutParams(lp);
        if (mTextureView.getParent() == null) {
            removeAllViews();
            addView(mTextureView, surfaceViewParams);
        }

        mTextureView.setSurfaceTextureListener(mListener);

        // -----

        mTextureView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer.setSurface(new Surface(surfaceTexture));
                //mMediaPlayer.pause();
                //mMediaPlayer.start();
                updateVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());

                mController.onBeginPlay();
            }
        },500);

        // -----
    }

    TextureView.SurfaceTextureListener mListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e("Test","onSurfaceTextureSizeChanged");
            mMediaPlayer.setSurface(new Surface(surface));
            try {
                mMediaPlayer.setDataSource(Constants.VIDEO_URL);
                mMediaPlayer.prepareAsync();
                mController.showLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.e("Test","onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.e("Test","onSurfaceTextureDestroyed");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Log.e("Test","onSurfaceTextureUpdated");
        }
    };

    public void updateVideoSize(int width, int height){
        int playerWidth = getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
        int playerHeight = getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
        Utils.updateVideoSize(mContext, mTextureView, width, height, playerWidth, playerHeight);
    }
}
