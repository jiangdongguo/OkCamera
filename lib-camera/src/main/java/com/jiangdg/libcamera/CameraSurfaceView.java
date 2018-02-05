package com.jiangdg.libcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mCamOperator = CameraOperatorImpl.getInstance(context);
        // 为SurfaceHolder注册事件监听器
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("CameraSurfaceView","--------surfaceCreated--------");
        mCamOperator.createCamera();
        mCamOperator.setParameters(null);
        mCamOperator.setSurfaceHolder(holder);
        mCamOperator.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("CameraSurfaceView","--------surfaceChanged--------");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("CameraSurfaceView","--------surfaceDestroyed--------");
        mCamOperator.stopPreview();
        mCamOperator.destoryCamera();
    }
}
