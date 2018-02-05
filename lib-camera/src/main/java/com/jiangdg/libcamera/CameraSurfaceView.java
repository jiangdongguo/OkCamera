package com.jiangdg.libcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.jiangdg.libcamera.operation.CameraOperatorImpl;
import com.jiangdg.libcamera.operation.ICameraOperator;

/** 自定义SurfaceView类
 * Created by jiangdongguo on 2018/2/5.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private ICameraOperator mCamOperator;

    public CameraSurfaceView(Context context) {
        super(context);
        mCamOperator = CameraOperatorImpl.getInstance(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamOperator = CameraOperatorImpl.getInstance(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamOperator.createCamera();
        mCamOperator.setParameters(null);
        mCamOperator.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamOperator.stopPreview();
        mCamOperator.destoryCamera();
    }
}
