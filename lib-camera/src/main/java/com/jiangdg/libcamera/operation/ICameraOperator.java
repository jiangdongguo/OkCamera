package com.jiangdg.libcamera.operation;

/**相机操作接口
 * Created by jiangdongguo on 2018/2/5.
 */

public interface ICameraOperator {
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
    // 切换前后置摄像头
    void switchCamera();
    // 切换分辨率
    void updateResolution(int width,int height);
    // 方向传感器
    void startOrientationSenor();
}
