package com.jiangdg.libcamera.operation;

import android.view.SurfaceHolder;

/**相机操作接口
 * Created by jiangdongguo on 2018/2/5.
 */

public interface ICameraOperator {
    void setOnCameraListener(CameraOperatorImpl.OnCameraListener listener);
    // 创建Camera
    void createCamera();
    // 销毁Camera
    void destoryCamera();
    // 配置Camera参数
    void setParameters(CameraParameters pData);
    // 开始Camera预览
    void startPreview();
    // 停止Camera预览
    void stopPreview();
    // 操作Surface句柄
    void setSurfaceHolder(SurfaceHolder holder);
    // 切换前后置摄像头
    void switchCamera();
    // 切换分辨率
    void updateResolution(int width,int height);
    // 开启方向传感器
    void startOrientationSensor();
    // 关闭方向传感器
    void stopOrientationSensor();
}
