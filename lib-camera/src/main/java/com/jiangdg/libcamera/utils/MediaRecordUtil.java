package com.jiangdg.libcamera.utils;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.Surface;

import java.io.IOException;

/**
 * 录制Mp4工具类
 * Created by jianddongguo on 2018/2/8.
 */
public class MediaRecordUtil {
    private static MediaRecorder mMediaReorder;

    public static void startMediaRecorder(final Camera camera, final Surface surface, final String videoPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaReorder == null) {
                    mMediaReorder = new MediaRecorder();
                } else {
                    mMediaReorder.reset();
                }
                camera.unlock();
                mMediaReorder.setCamera(camera);
                mMediaReorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mMediaReorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mMediaReorder.setOrientationHint(90);
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                mMediaReorder.setProfile(profile);
                mMediaReorder.setOutputFile(videoPath);
                mMediaReorder.setPreviewDisplay(surface);
                try {
                    mMediaReorder.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    stopMediaRecorder();
                } catch (IOException e) {
                    e.printStackTrace();
                    stopMediaRecorder();
                }
                mMediaReorder.start();
            }
        }).start();
    }

    public static void stopMediaRecorder() {
        if (mMediaReorder != null) {
            mMediaReorder.stop();
            mMediaReorder.release();
            mMediaReorder = null;
        }
    }
}
