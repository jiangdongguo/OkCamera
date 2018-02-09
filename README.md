OkCamera
============   
OkCamera is a Android Camera application development common library,which is a part of project [AndroidFastDevelop](https://github.com/jiangdongguo/AndroidFastDevelop). Using this library,
you can easily achieve lots of functions of Android Camera,such as previewing,capuring JPG,recording mp4,auto focus,and zoom
in/zoom out etc.
 
[中文文档： OkCamera，Android 相机应用开发通用库](http://blog.csdn.net/andrexpert/article/details/79302141)
 
## How to use it?  

### 1.Add to your Android Studio project  

Step 1. Add the JitPack repository to your build file.Add it in your root build.gradle at the end of repositories:  
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```  
Step 2. Add the dependency  
```java
dependencies { 
	    compile 'com.github.jiangdongguo:OkCamera:1.0'
}  
```
### 2. APIs Introduction  
(1) In order to using it correctly,the following two steps must be achieved：  
```java
  CameraHelper mCamHelper = CameraHelper.createCameraHelper();
  mCamHelper.setSurfaceView(mSurfaceView);  
```
   To be attention,mSurfaceView is a Object of SurfaceView.Of cause, if you want to listen to focus,preview or zoom event,you should do like this:  
```java
mCamHelper.setOnCameraHelperListener(new CameraHelper.OnCameraHelperListener() {
          @Override
          public void OnTakePicture(String path, Bitmap bm) {
              // do something...
          }
          @Override
          public void onCameraFocus(boolean success, Camera camera) {
             // do something...
          }
          @Override
          public void onCameraPreview(byte[] data, Camera camera) {
             // do something...
          }

          @Override
          public void onZoomChanged(int maxZoomVaule, int zoomValue) {
             // do something...
          }
     });
```
(2) Capturing JPG Images,ignoring the direction of the problem.  
```java
mCamHelper.takePicture(picPath);  
```
(3) Recording Mp4(Using MediaRecorder),ignoring the direction of the problem.  
```java
mCamHelper.startRecordMp4(videoPath);
mCamHelper.stopRecordMp4();  
```
(4) Camera focus by hander.In face,OkCamera have supported auto focus.  
```java
mCamHelper.cameraFocus();
```
(5) switch resolutions and camera.  
```java
mCamHelper.switchCamera();
mCamHelper.updateResolution(int width, int height);
```


License
-------

    Copyright 2018 Jiangdongguo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
