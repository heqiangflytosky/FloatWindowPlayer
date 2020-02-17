package com.android.hq.floatwindowplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class MediaPlayerManager {
    private MediaPlayer mMediaPlayer;
    private MediaPlayerManager() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private static class SingletonHolder{
        private static MediaPlayerManager INSTANCE = new MediaPlayerManager();
    }

    public static MediaPlayerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
