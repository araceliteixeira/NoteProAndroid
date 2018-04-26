package com.orion.notepro.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dgois on 2018-04-25.
 */

public class Player {

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 500;
    private static final String TAG = Player.class.getName();
    private File mOutputFile;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;
    private PlaybackInfoListener playbackInfoListener;
    private int playedLength = 0;

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        playbackInfoListener = listener;
    }

    public void startPlaying() {

        if (mPlayer != null) {
            seekTo(playedLength);
            mPlayer.start();
        } else {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playedLength = 0;
                    mediaPlayer.seekTo(playedLength);
                    stopUpdatingCallbackWithPosition(true);
                }
            });

            try {
                mPlayer.setDataSource(mOutputFile.getAbsolutePath());
                mPlayer.prepare();
                playbackInfoListener.onDurationChanged(mPlayer.getDuration());
                mPlayer.start();

                if (playbackInfoListener != null) {
                    playbackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
                }
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }
        }
        startUpdatingCallbackWithPosition();
    }

    public void pausePlaying() {
        playedLength = mPlayer.getCurrentPosition();
        mPlayer.pause();
    }

    public void releasePlaying() {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void startRecording(Context context) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mOutputFile = getOutputFile(context);
        mRecorder.setOutputFile(mOutputFile.getAbsoluteFile());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        try{
            mRecorder.stop();
        } catch(RuntimeException stopException){
            Log.w(TAG, "stopRecording: stop after start without content");
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public String getFileName() {
        return mOutputFile.getAbsolutePath();
    }

    public void setInitialAudioFile(File audioFile) {
        this.mOutputFile = audioFile;
    }

    public void releasePlayers() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void seekTo(int position) {
        if (mPlayer != null) {
            Log.i(TAG, String.format("seekTo() %d ms", position));
            mPlayer.seekTo(position);
        }
    }

    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && playbackInfoListener != null) {
                playbackInfoListener.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            int currentPosition = mPlayer.getCurrentPosition();
            if (playbackInfoListener != null) {
                playbackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    private File getOutputFile(Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

        return new File(context.getExternalCacheDir().getAbsolutePath()
                + "/audio_recorder"
                + dateFormat.format(new Date())
                + ".3gp");
    }

}
