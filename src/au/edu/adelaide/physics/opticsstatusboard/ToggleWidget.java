package au.edu.adelaide.physics.opticsstatusboard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ToggleWidget extends AppWidgetProvider {	
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context, ToggleWidget.class);
		int[] allWidgetIds = manager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), BackgroundManager.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
	}
}
