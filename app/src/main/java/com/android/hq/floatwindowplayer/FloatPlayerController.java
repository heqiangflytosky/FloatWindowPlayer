package com.android.hq.floatwindowplayer;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FloatPlayerController extends FrameLayout {
    private static final String TAG = "FloatPlayerController";
    private Context mContext;
    private LayoutInflater mInflater;
    private IMediaPlayer mPlayer;

    private RelativeLayout mRoot;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mFloatWindowBtnControllLayout;
    private ImageView mCloseFloatWindowBtn;
    private ImageView mPlayPauseBtn;
    private ImageView mFloatToFullScreenBtn;

    private boolean mIsScaling = false;
    private boolean mIsTouchUp = true;
    private boolean mIsMultiTouchMode = false;

    private float mDownY = 0;
    private float mDownX = 0;
    private float mTouchX = 0;
    private float mTouchY = 0;

    public FloatPlayerController(Context context,IMediaPlayer iPlayer) {
        super(context);
        mContext = context;
        mPlayer = iPlayer;
        mInflater = LayoutInflater.from(context);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        requestFocus();
        setBackgroundColor(getResources().getColor(R.color.black));

        initFloatView();
    }

    private void initFloatView() {
        mRoot = (RelativeLayout) mInflater.inflate(R.layout.floatwindow_controller, null);
        mFloatWindowBtnControllLayout = (RelativeLayout) mRoot.findViewById(R.id.float_window_btn_controll_layout);
        mLoadingLayout = (RelativeLayout) mRoot.findViewById(R.id.float_loading_layout);
        mCloseFloatWindowBtn = (ImageView) mRoot.findViewById(R.id.close_float_window);
        mCloseFloatWindowBtn.setOnClickListener(mOnClickListener);
        mPlayPauseBtn = (ImageView) mRoot.findViewById(R.id.float_play_pause);
        mPlayPauseBtn.setOnClickListener(mOnClickListener);
        mFloatToFullScreenBtn = (ImageView) mRoot.findViewById(R.id.float_to_fullscreen);
        mFloatToFullScreenBtn.setOnClickListener(mOnClickListener);
        FrameLayout.LayoutParams mFloatLayoutLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mFloatLayoutLP.gravity = Gravity.BOTTOM;
        addView(mRoot, mFloatLayoutLP);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            mTouchX = event.getRawX();
            mTouchY = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsMultiTouchMode = false;
                    if (!mIsScaling && mIsTouchUp) {
                        mDownX = event.getX();
                        mDownY = event.getY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mIsScaling && mIsTouchUp && !mIsMultiTouchMode) {
                        if (Math.abs(mTouchX - mDownX) > 10 || Math.abs(mTouchY - mDownY) > 10) {
                            updateFloatWindow();
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    mIsTouchUp = true;
                    Log.d(TAG, "video onTouchEvent ACTION_UP: ");
                    break;
                case MotionEvent.ACTION_POINTER_2_UP:
                    Log.d(TAG, "video onTouchEvent ACTION_POINTER_2_UP: ");
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "video onTouchEvent ACTION_POINTER_UP: ");
            }
        }
        else{
            mIsMultiTouchMode = true;
        }
        return true;
    }

    private void updateFloatWindow() {
        Log.d(TAG, "video updateFloatWindow mTouchY:" + mTouchY + " mDownY: " + mDownY);
        Log.d(TAG, "video updateFloatWindow  wmParams.y:" + VideoPlayerService.wmParams.y);
        if (!mIsScaling) {
            VideoPlayerService.wmParams.x = (int) (mTouchX - mDownX);
            VideoPlayerService.wmParams.y = (int) (mTouchY - mDownY);
            if (VideoPlayerService.wmParams.y < 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_height)) {
                VideoPlayerService.wmParams.y = 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
            } else if (VideoPlayerService.wmParams.y > getResources().getDisplayMetrics().heightPixels - 150) {
                VideoPlayerService.wmParams.y = getResources().getDisplayMetrics().heightPixels - 150;
            }
            if(VideoPlayerService.wmParams.x < 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_width)){
                VideoPlayerService.wmParams.x = 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
            } else if (VideoPlayerService.wmParams.x > getResources().getDisplayMetrics().widthPixels - 150){
                VideoPlayerService.wmParams.x = getResources().getDisplayMetrics().widthPixels - 150;
            }
            VideoPlayerService.wmParams.width = getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
            VideoPlayerService.wmParams.height = getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
        }
        VideoPlayerService.wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        VideoPlayerService.mWindowManager.updateViewLayout(VideoPlayerService.mFloatPlayerUI, VideoPlayerService.wmParams);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.close_float_window:
                    closeFloatWindowClick();
                    break;
                case R.id.float_play_pause:
                    playPauseClick();
                    break;
                case R.id.float_to_fullscreen:
                    switchToFullScreen();
                    break;
                default:

                    break;
            }
        }
    };


    private void closeFloatWindowClick() {
        mPlayer.closePlayer();
    }

    private void playPauseClick() {
        if (mPlayer.isPlaying()) {
            mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
        } else {
            mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_pause));
        }
        mPlayer.playPause();
    }

    private void switchToFullScreen() {

    }

    public void onAllComplete() {
        mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
        mFloatWindowBtnControllLayout.setVisibility(View.VISIBLE);
    }

    public void onBeginPlay() {
        setPlayPauseButtonVisibility(true);
        setBackgroundColor(0x00000000);
        finishLoading();
    }

    public void showPause() {
        mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
        mFloatWindowBtnControllLayout.setVisibility(View.VISIBLE);
    }

    public void showPlaying() {
        mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_pause));
    }

    public void showLoading() {
        setPlayPauseButtonVisibility(false);
        showPlaying();
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    public void setPlayPauseButtonVisibility(boolean visible){
        if(visible){
            mPlayPauseBtn.setVisibility(View.VISIBLE);
        }else{
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void updatePlayPauseIcon(){
        if (mPlayer.isPlaying()) {
            showPlaying();
        } else {
            showPause();
        }
    }

    public void finishLoading() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    public void showBuffering() {
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    public void bufferingEnd() {
        mLoadingLayout.setVisibility(View.GONE);
    }
}
