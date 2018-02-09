package com.jiangdg.libcamera.utils;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.view.Surface;

import java.io.IOException;

/**
 * 录制Mp4工具类
 * Created by jianddongguo on 2018/2/8.
 */
public class MediaRecordUtil {
    private MediaRecorder mMediaReorder;
    private boolean isRecording;
    public static MediaRecordUtil mRecordUtil;

    private MediaRecordUtil() {
    }

    public static MediaRecordUtil getInstance() {
        if (mRecordUtil == null) {
            mRecordUtil = new MediaRecordUtil();
        }
        return mRecordUtil;
    }

    public void startMediaRecorder(final Camera camera, final Surface surface, final RecordParams params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (params == null) {
                    throw new NullPointerException("RecordParams can not be null");
                }
                if (TextUtils.isEmpty(params.getVideoPath())) {
                    throw new NullPointerException("video path can not be null");
                }
                if (mMediaReorder == null) {
                    mMediaReorder = new MediaRecorder();
                } else {
                    mMediaReorder.reset();
                }
                camera.unlock();
                mMediaReorder.setCamera(camera);
                mMediaReorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mMediaReorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                // 判断是前置还是后置,与拍照方向设置一致
                int rotation = (params.getPhoneDegree() == 270 ? 0 : params.getPhoneDegree() + 90);
                if (params.isFrontCamera) {
                    if (rotation == 90) {
                        rotation = 270;
                    } else if (rotation == 270) {
                        rotation = 90;
                    }
                }
                mMediaReorder.setOrientationHint(rotation);

                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                mMediaReorder.setProfile(profile);
                mMediaReorder.setOutputFile(params.videoPath);
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
                // 该方法会自动调用camera.lock
                mMediaReorder.start();

                isRecording = true;
            }
        }).start();
    }

    public void stopMediaRecorder() {
        if (mMediaReorder != null) {
            mMediaReorder.stop();
            mMediaReorder.release();
            mMediaReorder = null;

            isRecording = false;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public static class RecordParams {
        boolean isFrontCamera;
        int phoneDegree;
        String videoPath;

        public boolean isFrontCamera() {
            return isFrontCamera;
        }

        public void setFrontCamera(boolean frontCamera) {
            isFrontCamera = frontCamera;
        }

        public int getPhoneDegree() {
            return phoneDegree;
        }

        public void setPhoneDegree(int phoneDegree) {
            this.phoneDegree = phoneDegree;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }
    }
}
