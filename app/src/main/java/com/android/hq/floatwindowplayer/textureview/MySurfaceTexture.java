package com.android.hq.floatwindowplayer.textureview;

import android.graphics.SurfaceTexture;

public class MySurfaceTexture extends SurfaceTexture {
    public MySurfaceTexture(int texName) {
        super(texName);
        init();
    }

    private void init() {
        super.detachFromGLContext();
    }
}
