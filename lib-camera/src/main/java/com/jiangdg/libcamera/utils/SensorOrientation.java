package com.jiangdg.libcamera.utils;

import android.content.Context;
import android.view.OrientationEventListener;

/** 方向传感器，监听手机方向变化
 *  当手机处于正常垂直方向时，orientation=0
 *  然后，按顺时针方向，orientation的值处于0-360之间
 *
 *  Created by jiangdongguo on 2018-2-1
 */
public class SensorOrientation extends OrientationEventListener{
	private static SensorOrientation mOrientation;
	private OnChangedListener listener;

	private SensorOrientation(Context context) {
		super(context);
	}

	public static SensorOrientation getInstance(Context context) {
		if(mOrientation == null) {
			mOrientation = new SensorOrientation(context);
		}
		return mOrientation;
	}

	public  void startSensorOrientation(OnChangedListener listener){
		this.listener = listener;

		if(mOrientation != null) {
			mOrientation.enable();
		}
	}
	
	public void stopSensorOrientation(){
		if(mOrientation != null) {
			mOrientation.disable();
		}
	}

	@Override
	public void onOrientationChanged(int orientation) {
		if(listener != null) {
			listener.onOrientatonChanged(orientation);
		}
	}

	public interface OnChangedListener {
		void onOrientatonChanged(int orientation);
	}
}
