package com.jiangdg.okcamera;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jiangdg.libcamera.CameraHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements CameraHelper.OnCameraHelperListener{
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private SurfaceView mCameraView;
    private CameraHelper mCamHelper;
    private float lastDistance;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mCameraView = (SurfaceView)findViewById(R.id.id_camera);
        mCamHelper = CameraHelper.createCameraHelper();
        mCamHelper.setSurfaceView(mCameraView);
        mCamHelper.setOnCameraHelperListener(this);
    }

    public void onSwitchCamera(View view) {
        mCamHelper.switchCamera();
    }

    public void onTakePitureClick(View view) {
        String picPath = ROOT_PATH + File.separator
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))
                + ".jpg";
        mCamHelper.takePicture(picPath);
    }

    public void onRecordMp4(View view) {
        String videoPath = ROOT_PATH + File.separator
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))
                + ".mp4";
        if(! isRecording) {
            mCamHelper.startRecordMp4(videoPath);
            showMsg("正在录制");
        } else {
            mCamHelper.stopRecordMp4();
            showMsg("录制结束");
        }
        isRecording = !isRecording;
    }

    @Override
    public void OnTakePicture(String path, Bitmap bm) {
        showMsg(bm==null ? "拍照失败" : path);
    }

    @Override
    public void onCameraFocus(boolean success, Camera camera) {
        showMsg(success ? "对焦成功" : "对焦失败");
    }

    @Override
    public void onCameraPreview(byte[] data, Camera camera) {
//        Log.i("debug","预览数据，data.length="+data.length);
    }

    @Override
    public void onZoomChanged(int maxZoomVaule, int zoomValue) {
        Log.i("debug","当前焦距="+zoomValue+"；最大焦距="+maxZoomVaule);
    }

    // 单指触摸，对焦
    // 双指触摸，缩放焦距
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            mCamHelper.cameraFocus();
            return true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                lastDistance = getFingersDistance(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float newDistance = getFingersDistance(event);
                if (newDistance > lastDistance) {
                    // 当getFingerSpace()逐渐变大，手指张开动作
                    mCamHelper.setZoom(true);
                } else if (newDistance < lastDistance) {
                    // 当getFingerSpace()逐渐变小，手指缩拢动作
                    mCamHelper.setZoom(false);
                }
                break;
        }
        return true;
    }

    // 根据三角形公：求平方根
    // 计算两指触摸的距离
    private float getFingersDistance(MotionEvent event) {
        float xDistance = event.getX(0) - event.getY(1);
        float yDistance = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    private void showMsg(String msg) {
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
