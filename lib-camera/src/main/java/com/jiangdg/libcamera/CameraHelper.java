package com.jiangdg.libcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jiangdg.libcamera.utils.CameraUtil;
import com.jiangdg.libcamera.utils.MediaRecordUtil;
import com.jiangdg.libcamera.utils.SensorAccelerator;
import com.jiangdg.libcamera.utils.SensorOrientation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 相机操作实现类
 * Created by jiangdongguo on 2018/2/5.
 */

public class CameraHelper implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraHelper";
    private final MediaRecordUtil mRecordUtil;
    private int width = 1280;
    private int height = 720;
    private Camera mCamera;
    private static CameraHelper mCameraHelper;
    private WeakReference<SurfaceView> mSurfaceViewRf;
    private SurfaceHolder mHolder;
    private OnCameraHelperListener mHelperListener;
    private SensorOrientation mOriSensor;
    private SensorAccelerator mAccelerSensor;
    private int mPhoneDegree;
    private boolean isFrontCamera = false;

    private CameraHelper() {
        mRecordUtil = MediaRecordUtil.getInstance();
    }

    public static CameraHelper createCameraHelper() {
        if (mCameraHelper == null) {
            mCameraHelper = new CameraHelper();
        }
        return mCameraHelper;
    }

    public interface OnCameraHelperListener {
        // 拍照
        void OnTakePicture(String path, Bitmap bm);

        // 对焦
        void onCameraFocus(boolean success, Camera camera);

        // 预览
        void onCameraPreview(byte[] data, Camera camera);

        // 变焦
        void onZoomChanged(int maxZoomVaule, int zoomValue);
    }

    public void setOnCameraHelperListener(OnCameraHelperListener listener) {
        this.mHelperListener = listener;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null)
            return;
        if (mHelperListener != null) {
            mHelperListener.onCameraPreview(data, camera);
        }
        // 调用 setPreviewCallbackWithBuffer方法获取预览图像流
        // 需要使用addCallbackBuffer进行数据回调，否则，不会有数据
        if (mCamera != null) {
            mCamera.addCallbackBuffer(data);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createCamera();
        startPreview();
        startOrientationSensor();
        startAcceleratorSensor();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopOrientationSensor();
        stopAcceleratorSensor();
        stopPreview();
        destoryCamera();
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        mSurfaceViewRf = new WeakReference<>(surfaceView);
        mHolder = mSurfaceViewRf.get().getHolder();
        mHolder.addCallback(this);
    }

    public void takePicture(final String path) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (mHelperListener != null) {
                    // 如果data=null,说明拍照失败
                    if (data != null) {
                        File file = new File(path);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            // 对图片进行旋转
                            // 前置摄像头需要对垂直方向做变换，否则照片是颠倒的
                            int rotation = (mPhoneDegree == 270 ? 0 : mPhoneDegree + 90);
                            if (isFrontCamera) {
                                if (rotation == 90) {
                                    rotation = 270;
                                } else if (rotation == 270) {
                                    rotation = 90;
                                }
                            }
                            Matrix matrix = new Matrix();
                            matrix.setRotate(rotation);
                            Bitmap rotateBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                            rotateBmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            // 回传结果
                            mHelperListener.OnTakePicture(path, rotateBmp);
                            // 重新预览
                            stopPreview();
                            startPreview();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "拍照失败：请确保路径是否正确，或者存储权限");
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                bitmap.recycle();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private void createCamera() {
        try {
            // 初始化资源
            if (mCamera != null) {
                stopPreview();
                destoryCamera();
            }
            // 实例化Camera(前、后置摄像头)
            if (!isFrontCamera) {
                mCamera = Camera.open();
            } else {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                int numofCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numofCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCamera = Camera.open(i);
                    }
                }
            }
            setParameters();
        } catch (Exception e) {
            Log.e(TAG, "创建Camera失败：" + e.getMessage());
        }
    }

    private void setParameters() {
        if (mCamera == null) {
            Log.w(TAG, "mCamera=null,请确保是否创建了Camera");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        // 预览分辨率，默认640x480
        parameters.setPreviewSize(width, height);
        // 预览颜色格式，默认NV21
        parameters.setPreviewFormat(ImageFormat.NV21);
        // 自动对焦
        if (CameraUtil.isSupportFocusAuto(parameters)) {
            parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        // 图片格式，默认JPEG
        parameters.setPictureFormat(ImageFormat.JPEG);
        // 图片尺寸，与预览尺寸一致
        parameters.setPictureSize(width, height);
        // 图片质量，默认最好
        parameters.setJpegQuality(100);
        // 图片缩略图质量
        parameters.setJpegThumbnailQuality(100);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private void destoryCamera() {
        if (mCamera == null)
            return;
        mCamera.release();
        mCamera = null;
    }

    private void startPreview() {
        if (mHolder != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCamera.startPreview();
        // 开启预览，自动对焦一次
        mCamera.autoFocus(null);
        // 注册预览回调接口,缓存大小为一帧图像所占字节数
        // 即，(width * height * 每个像素所占bit数)/8
        int previewFormat = mCamera.getParameters().getPreviewFormat();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        int bufferSize = previewSize.width * previewSize.height *
                ImageFormat.getBitsPerPixel(previewFormat) / 8;
        mCamera.addCallbackBuffer(new byte[bufferSize]);
        mCamera.setPreviewCallbackWithBuffer(this);
    }

    private void stopPreview() {
        if (mCamera == null)
            return;
        try {
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(null);
            mCamera.setPreviewCallbackWithBuffer(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Camera对焦
    public void cameraFocus() {
        if (mCamera == null || mRecordUtil.isRecording())
            return;
        Camera.Parameters parameter = mCamera.getParameters();
        if (CameraUtil.isSupportFocusAuto(parameter)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (mHelperListener != null) {
                        mHelperListener.onCameraFocus(success, camera);
                    }
                }
            });
        }
    }

    // Camera前后置切换
    public void switchCamera() {
        stopPreview();
        destoryCamera();

        isFrontCamera = !isFrontCamera;
        createCamera();
        startPreview();

    }

    // Camera分辨率切换
    public void updateResolution(int width, int height) {
        this.width = width;
        this.height = height;
        stopPreview();
        destoryCamera();
        createCamera();
        startPreview();
    }

    public List<Camera.Size> getPreviewSizes() {
        if(mCamera == null)
            return null;
        Camera.Parameters param = mCamera.getParameters();
        if(param != null) {
            return param.getSupportedPreviewSizes();
        }
        return  null;
    }

    public void startRecordMp4(String videoPath) {
        MediaRecordUtil.RecordParams params = new MediaRecordUtil.RecordParams();
        params.setFrontCamera(isFrontCamera);
        params.setPhoneDegree(mPhoneDegree);
        params.setVideoPath(videoPath);
        mRecordUtil.startMediaRecorder(mCamera, mSurfaceViewRf.get().getHolder().getSurface(), params);
    }

    public void stopRecordMp4() {
        mRecordUtil.stopMediaRecorder();
    }

    // 变焦增大，inZoomIn = true
    // 变焦缩小，inZoomIn = false
    public void setZoom(boolean isZoomIn) {
        if (!isSupportZoom() || isFrontCamera) {
            Log.w("dddd", "(前)摄像头不支持变焦");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        int maxZoom = parameters.getMaxZoom();
        int curZoom = parameters.getZoom();

        if (isZoomIn && curZoom < maxZoom) {
            curZoom++;
        } else if (curZoom > 0) {
            curZoom--;
        }
        parameters.setZoom(curZoom);
        mCamera.setParameters(parameters);
        if (mHelperListener != null) {
            mHelperListener.onZoomChanged(maxZoom, curZoom);
        }
    }

    private boolean isSupportZoom() {
        boolean isSupport = false;
        if (mCamera.getParameters().isZoomSupported()) {
            isSupport = true;
        }
        return isSupport;
    }

    private void startOrientationSensor() {
        mOriSensor = SensorOrientation.getInstance(mSurfaceViewRf.get().getContext());
        mOriSensor.startSensorOrientation(new SensorOrientation.OnChangedListener() {
            @Override
            public void onOrientatonChanged(int orientation) {
                // 假定某个范围，确定手机当前方向
                // mPhoneDegree = 0,正常垂直方向
                // mPhoneDegree = 90,向右水平方向 ...
                int rotate = 0;
                if ((orientation >= 0 && orientation <= 45) || (orientation > 315)) {
                    rotate = 0;
                } else if (orientation > 45 && orientation <= 135) {
                    rotate = 90;
                } else if (orientation > 135 && orientation <= 225) {
                    rotate = 180;
                } else if (orientation > 225 && orientation <= 315) {
                    rotate = 270;
                } else {
                    rotate = 0;
                }
                if (rotate == orientation)
                    return;
                mPhoneDegree = rotate;
                Log.i(TAG, "手机方向角度：" + mPhoneDegree);
            }
        });
        mOriSensor.enable();
    }

    private void stopOrientationSensor() {
        if (mOriSensor != null) {
            mOriSensor.disable();
        }
    }

    private void startAcceleratorSensor() {
        mAccelerSensor = SensorAccelerator.getSensorInstance();
        mAccelerSensor.startSensorAccelerometer(mSurfaceViewRf.get().getContext(), new SensorAccelerator.OnSensorChangedResult() {
            @Override
            public void onMoving(int x, int y, int z) {

            }

            @Override
            public void onStopped() {
                // 开始对焦
                cameraFocus();
            }
        });
    }

    private void stopAcceleratorSensor() {
        if (mAccelerSensor != null) {
            mAccelerSensor.stopSensorAccelerometer();
        }
    }
}
