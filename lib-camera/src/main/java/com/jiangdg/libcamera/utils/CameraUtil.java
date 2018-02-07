package com.jiangdg.libcamera.utils;

import android.hardware.Camera;

import java.util.List;

/** Camera工具类
 * Created by jianddongguo on 2018/2/7.
 */

public class CameraUtil {
    public static  boolean isSupportFocusAuto(Camera.Parameters p) {
        boolean isSupport = false;
        List<String> modes = p.getSupportedFocusModes();
        for (String mode : modes) {
            if(mode.equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                isSupport = true;
                break;
            }
        }
        return isSupport;
    }
}
