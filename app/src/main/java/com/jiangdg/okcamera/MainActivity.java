package com.jiangdg.okcamera;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    public void OnTakePicture(String path, Bitmap bm) {
        showMsg(bm==null ? "拍照失败" : path);
    }

    private void showMsg(String msg) {
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
