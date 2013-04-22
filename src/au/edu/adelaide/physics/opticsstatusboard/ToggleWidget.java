package au.edu.adelaide.physics.opticsstatusboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ToggleWidget extends AppWidgetProvider {
	public void onEnabled(Context context) {
		//Alarm related stuff to get out of the way
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long interval = getInterval(context);

		// Build the intent for the alarm
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);
		intent.putExtra("widgetAlarmCall", true);
		
		// Create a pending intent for the alarm
		PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Set the widget update alarm
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
	}
	
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);

		// Update the widget via the service
		context.startService(intent);
	}
	
	public long getInterval(Context context) {
    	// Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        // Pull the desired interval from the settings
        long updateInterval = Long.parseLong(settings.getString("widgetUpdateInterval", "7200000"));
        
        // Return the desired interval between widget updates
        return updateInterval;
    }
	
	public void onDisabled(Context context) {
		// Alarm related stuff to get out of the way
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// Build the intent for the alarm
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);
		intent.putExtra("widgetAlarmCall", true);
		
		// Create a pending intent for the alarm
		PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Cancel the existing alarm with the matching pending intent
		am.cancel(pendingIntent);
	}
}
