package com.jiangdg.libcamera.widget;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.jiangdg.libcamera.CameraHelper;

import java.util.List;

/** 自定义SurfaceView类
 * Created by jiangdongguo on 2018/2/5.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public CameraSurfaceView(Context context) {
        super(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
