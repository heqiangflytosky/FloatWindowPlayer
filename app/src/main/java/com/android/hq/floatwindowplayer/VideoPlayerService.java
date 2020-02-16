package com.android.hq.floatwindowplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class VideoPlayerService extends Service implements IServiceHelper {
    private static Activity mActivity;
    public static View mFloatPlayerUI;
    public static IFloatPlayer mFloatPlayer;
    public static WindowManager mWindowManager = null;
    public static WindowManager.LayoutParams wmParams = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FloatPlayerUI floatPlayerUI= new FloatPlayerUI(mActivity,this);
        initWindowFloatView(floatPlayerUI,floatPlayerUI);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initWindowFloatView(View floatView,IFloatPlayer floatPlayer) {
        mFloatPlayer = floatPlayer;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.float_window_root_width),
                getResources().getDimensionPixelSize(R.dimen.float_window_root_height));
        mFloatPlayerUI = floatView;
        mFloatPlayerUI.setLayoutParams(params);
        mFloatPlayerUI.setBackgroundColor(Color.BLACK);
        mFloatPlayerUI.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            //设置TYPE_PHONE，需要申请android.permission.SYSTEM_ALERT_WINDOW权限
            //TYPE_TOAST同样可以实现悬浮窗效果，不需要申请其他权限
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            wmParams.token = mFloatPlayerUI.getApplicationWindowToken();
        }
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        wmParams.verticalWeight = 0;
        wmParams.horizontalWeight = 0;
        wmParams.width = getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
        wmParams.height = getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
        wmParams.x = (width - wmParams.width) / 2;
        wmParams.y = (height - wmParams.height) / 2;
        wmParams.alpha = 1.0f;
        mWindowManager.addView(mFloatPlayerUI, wmParams);
    }

    public static void startService(Activity activity){
        mActivity = activity;
        if(mActivity != null){
            Intent mIntent = new Intent(mActivity, VideoPlayerService.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startService(mIntent);
        }
    }

    public static void stopService(Activity activity){
        if(activity == null) {
            return;
        }
        Intent mIntent = new Intent(activity, VideoPlayerService.class);
        activity.stopService(mIntent);
    }

    @Override
    public void closeFloatWindow() {
        if (mFloatPlayer != null) {
            mFloatPlayer.exitFloatWindow();
        }
        if(mFloatPlayerUI != null){
            if(mFloatPlayerUI.isAttachedToWindow()){
                mWindowManager.removeView(mFloatPlayerUI);
                mFloatPlayerUI = null;
            }
        }
        stopSelf();
    }
}
