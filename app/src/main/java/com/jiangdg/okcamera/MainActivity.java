package com.jiangdg.okcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiangdg.libcamera.CameraSurfaceView;

public class MainActivity extends AppCompatActivity {
    CameraSurfaceView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = (CameraSurfaceView)findViewById(R.id.id_camera);
    }
}
