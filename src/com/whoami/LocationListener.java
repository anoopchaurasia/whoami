package com.whoami;

import java.text.ParseException;

import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationListener implements android.location.LocationListener{
	private MainService service = null;
	public LocationListener(Service s) {
		this.service = (MainService) s;
		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) service.getSystemService(context);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);
		crta.setAltitudeRequired(false);
		crta.setBearingRequired(false);
		crta.setCostAllowed(true);
		crta.setPowerRequirement(Criteria.POWER_LOW);
		// String provider = locationManager.getBestProvider(crta, true);

		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		try {
			service.updateWithNewLocation(location);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		locationManager.requestLocationUpdates(provider, 10, 0, this);
	
	}
	
	@Override
	public void onLocationChanged(Location location) {
		try {
			service.updateWithNewLocation(location);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		try {
			service.updateWithNewLocation(null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void setService(MainService mainService) {
		this.service = mainService;
	}
}
