package com.example.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

public class GetLocation {

	MainActivity mainActivity = null;
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;

	Boolean isFirstLoc = true;
	Boolean startLoc = true;
	LocationClient mLocClient = null;
	LocationMode mCurrentMode = null;
	BitmapDescriptor mCurrentMarker = null;
	Button requestLocButton;
	MyLocationData locData;
	SensorManager sensorManager;
	float x = 0;
	myThread t;
	static Boolean isrun = true;
	float lastZoomLv = 19;

	GetLocation(MainActivity mainActivity, MapView mMapView, BaiduMap mBaiduMap) {
		// TODO Auto-generated constructor stub

		this.mainActivity = mainActivity;
		this.mMapView = mMapView;
		this.mBaiduMap = mBaiduMap;

		sensorManager = (SensorManager) mainActivity
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(new MySensorEventListener(),
				sensor, SensorManager.SENSOR_DELAY_UI);
		
		requestLocButton = (Button) mainActivity
				.findViewById(R.id.locationButton);
		requestLocButton.setText("跟随");
		requestLocButton.setOnClickListener(new btnClickListener());

		mBaiduMap.getUiSettings().setScrollGesturesEnabled(false);
		mBaiduMap.setMaxAndMinZoomLevel(19, 18);
		mBaiduMap.setMyLocationEnabled(true);
		mLocClient = new LocationClient(mainActivity);
		mCurrentMode = LocationMode.FOLLOWING;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		mLocClient.registerLocationListener(new MyLocationListener());

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setNeedDeviceDirect(true);
		mLocClient.setLocOption(option);

		mLocClient.start();

		// t = new myThread();
		// t.start();
	}

	void stop() {
		lastZoomLv = mBaiduMap.getMapStatus().zoom;
		startLoc = true;
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();
		isrun = false;
	}

	void start() {
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(lastZoomLv));
		if (!mLocClient.isStarted())
			mLocClient.start();
		isrun = true;
		t = new myThread();
		t.start();
	}
	
	public class myThread extends Thread {

		public void run() {
			do {
				// System.out.println("我还在");
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mLocClient != null && mLocClient.isStarted())
					mLocClient.requestLocation();
			} while (isrun);
		}
	}

	public class btnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (mCurrentMode) {
			case COMPASS:
				requestLocButton.setText("跟随");
				mCurrentMode = LocationMode.FOLLOWING;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			case FOLLOWING:
				requestLocButton.setText("罗盘");
				mCurrentMode = LocationMode.COMPASS;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			}
		}
	}

	class MySensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (mLocClient != null && mLocClient.isStarted()) {
				float xtemp = event.values[SensorManager.DATA_X];
				float abs = Math.abs(xtemp - x);
				if (abs >= 1) {
					x = xtemp;
					locData = mBaiduMap.getLocationData();
					MyLocationData locData2 = new MyLocationData.Builder()
							.accuracy(locData.accuracy).direction(x)
							.latitude(locData.latitude)
							.longitude(locData.longitude).build();
					mBaiduMap.setMyLocationData(locData2);
					// System.out.println(x + "xxxxxxxxxxxxxxxxxxx");
				}
			}
		}
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null || mMapView == null)
				return;
			locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(x)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			// System.out.println(location.getLocType() + "/" + x + "/"
			// + location.getLatitude() + "/" + location.getLongitude());
			mBaiduMap.setMyLocationData(locData);
		}
	}

}
