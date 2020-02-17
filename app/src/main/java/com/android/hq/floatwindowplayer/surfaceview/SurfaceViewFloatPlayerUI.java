package com.android.hq.floatwindowplayer.surfaceview;

import android.content.Context;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.hq.floatwindowplayer.Constants;
import com.android.hq.floatwindowplayer.FloatPlayerUI;
import com.android.hq.floatwindowplayer.IServiceHelper;
import com.android.hq.floatwindowplayer.R;

import java.io.IOException;

public class SurfaceViewFloatPlayerUI extends FloatPlayerUI {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    public SurfaceViewFloatPlayerUI(Context context, IServiceHelper helper) {
        super(context, helper);
        init();
    }

    @Override
    public void initVideoContentView(){
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

    @Override
    public View getVideoContentView() {
        return mSurfaceView;
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
}
