package com.example.baidumap;

import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.model.LatLng;

public class MyMapClickListener implements OnMapClickListener{

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		// TODO Auto-generated method stub
		System.out.println(poi.getName() + poi.getPosition());
		return false;
	}
	
}
