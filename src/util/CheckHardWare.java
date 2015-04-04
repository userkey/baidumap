package util;

import android.content.Context;
import android.location.LocationManager;

public final class CheckHardWare {
	
	private boolean isGpsEnable(Context mContext) {
		LocationManager locationManager = ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
