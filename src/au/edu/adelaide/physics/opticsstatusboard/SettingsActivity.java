package au.edu.adelaide.physics.opticsstatusboard;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
//		System.out.println("Preference change detected");
		if (key.equals("locationEnabled")) {
//			System.out.println("Location enabled setting change detected");
			boolean enabled = settings.getBoolean("locationEnabled", false);
			setLocationProximityService(enabled, this);
		}
	}
	
	public static void setLocationProximityService(boolean enable, Context context) {
    	//Get the location manager
    	LocationManager location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	
    	//Set the position and radius for the alert (perhaps read these from preferences later)
    	double lat = -34.91883;
    	double lon = 138.60444;
    	float r = 250;
    	
    	//Set up the pending intent to fire off that will start the background manager
    	Intent locationIntent = new Intent(context.getApplicationContext(), BackgroundManager.class);
		PendingIntent locPendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	//Create the proximity alert
		if (enable)
			location.addProximityAlert(lat, lon, r, -1, locPendingIntent);
		else
			location.removeProximityAlert(locPendingIntent);
    }
}
