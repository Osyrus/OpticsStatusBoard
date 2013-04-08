package au.edu.adelaide.physics.opticsstatusboard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class ToggleWidget extends AppWidgetProvider {	
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);

		// Update the widget via the service
		context.startService(intent);
	}
}
