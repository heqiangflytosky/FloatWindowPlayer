package com.android.hq.floatwindowplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class Utils {
    public static void updateVideoSize(Context context, View player, int videoWidth, int videoHeight, int playerWidth, int playerHeight){
        if(context == null || player == null) {
            return;
        }
        int viewWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int viewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        if(videoWidth>0 && videoHeight>0){
            float videoAspect = ((float)videoWidth)/videoHeight;
            float playerAspect = ((float)playerWidth)/playerHeight;
            if(videoAspect > playerAspect){
                viewWidth = playerWidth;
                viewHeight = (int)(playerWidth * (1/videoAspect));
            }else{
                viewWidth = (int)(playerHeight * videoAspect);
                viewHeight = playerHeight;
            }
        }
        ViewGroup.LayoutParams lp = player.getLayoutParams();
        lp.height=viewHeight;
        lp.width=viewWidth;
        player.requestLayout();
    }
}
