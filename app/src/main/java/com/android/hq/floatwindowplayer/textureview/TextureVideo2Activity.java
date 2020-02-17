package com.android.hq.floatwindowplayer.textureview;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.hq.floatwindowplayer.Constants;
import com.android.hq.floatwindowplayer.MediaPlayerManager;
import com.android.hq.floatwindowplayer.R;
import com.android.hq.floatwindowplayer.Utils;
import com.android.hq.floatwindowplayer.VideoPlayerService;

import java.io.IOException;

public class TextureVideo2Activity extends Activity
        implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener{
    private TextureView mTextureView;
    private MediaPlayer mMediaPlayer;

    private SurfaceTexture mTexture;
    private SurfaceHolder mHolder;
    private Surface mSurface;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mScreenWidth;
    private int mScreenHeigth;

    private MySurfaceTexture mSurfaceTexture;

    public static int mTextureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_texture_video);
        mTextureView = findViewById(R.id.textureView);

        int[] arr = new int[1];
        GLES20.glGenTextures(1, arr, 0);
        mTextureName = arr[0];
        mSurfaceTexture = new MySurfaceTexture(mTextureName);
        mTextureView.setSurfaceTexture(mSurfaceTexture);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();
        lp.width = dm.widthPixels;
        lp.height = (int) ((float) lp.width / 16 * 9);
        mTextureView.setLayoutParams(lp);

        mTextureView.setSurfaceTextureListener(TextureVideo2Activity.this);
        final View root = findViewById(R.id.root);
        root.post(new Runnable() {
            @Override
            public void run() {
                mScreenWidth = root.getWidth();
                mScreenHeigth = root.getHeight();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp != null) {
            updateVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            mp.start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        playVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void initMediaPlayer() {
        mMediaPlayer = MediaPlayerManager.getInstance().getMediaPlayer();

        try {
            mMediaPlayer.setDataSource(Constants.VIDEO_URL);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setLooping(true);
        } catch (IOException |IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void playVideo() {
        if (mMediaPlayer == null) {
            mTexture = mTextureView.getSurfaceTexture();
            mSurface = new Surface(mTexture);
            initMediaPlayer();
        }
    }

    public void play(View view) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        } else {
            playVideo();
        }
    }

    public void updateVideoSize(int width, int height){
        int playerWidth = getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
        int playerHeight = getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
        Utils.updateVideoSize(this, mTextureView, width, height, playerWidth, playerHeight);
    }

    public void playFloatWindow(View view) {
        VideoPlayerService.startService(this, Constants.WINDOW_TYPE_TV);
    }
}
