package com.advait.saravade.treemapindia;

import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class GPSService extends IntentService implements LocationListener {
	private double lat;
	private double lon;
	private double alt;
	private StringBuilder arrayList;
	private String SP_KEY = "firsttimeuser";
	public GPSService() {
		super("GPSService");
	}

	@Override
	public void onStart(Intent intent, int i)
	{
		String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(provider.contains("gps"))
		{
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)0, (float)10, this);
		}
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onLocationChanged(final Location loc) {
		// TODO Auto-generated method stub
		lat = 35; // Default
		lon = 139;  // Default
		arrayList = null;
		if(loc != null)
		{
			// Now store it in application-wide
    		
    	    
	    Timer timer = new Timer();

	    timer.scheduleAtFixedRate(new TimerTask() {

	        synchronized public void run() {

	        	lat = loc.getLatitude();
	    		lon = loc.getLongitude();
	    		alt = loc.getAltitude();
	    		arrayList.append(lat+", "+lon+", "+alt);
	    		SharedPreferences latlong = getSharedPreferences(SP_KEY, MODE_PRIVATE);
	    	    SharedPreferences.Editor latlongeditor = latlong.edit();
	    		latlongeditor.putString("location", arrayList.toString()).commit();
	            }
	        }, 0, 3000);
		}
		    		
		    		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}