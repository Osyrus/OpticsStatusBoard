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
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		//Alarm related stuff to get out of the way
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long interval = getInterval(context);
		
		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);
		intent.putExtra("widgetAlarmCall", true);
		// Create a pending intent for the alarm
		PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Update the widget via the service
		context.startService(intent);
		
		// Set the widget update alarm
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
	}
	
	public long getInterval(Context context) {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        long updateInterval = Long.parseLong(settings.getString("widgetUpdateInterval", "7200000"));
        
        return updateInterval;
    }
}
