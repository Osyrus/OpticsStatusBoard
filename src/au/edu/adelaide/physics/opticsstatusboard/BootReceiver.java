package au.edu.adelaide.physics.opticsstatusboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
	private boolean locationEnabled = false;
		
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			refreshUserData(context);
			SettingsActivity.setLocationProximityService(locationEnabled, context);
		}
	}
	
	private void refreshUserData(Context context) {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        
        locationEnabled = settings.getBoolean("reminderEnabled", false);
    }
}
