package au.edu.adelaide.physics.opticsstatusboard;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class OnAlarmReceiver extends BroadcastReceiver {
	private final long[] vibratePattern = {0, 200, 0, 200, 0, 200};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int status = intent.getIntExtra("status", 1);
		boolean canNotify = intent.getBooleanExtra("canNotify", false);
		boolean canVibrate = intent.getBooleanExtra("canVibrate", true);
		
		if (status == 0 && canNotify) {
			NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
			nBuilder.setSmallIcon(R.drawable.ic_launcher);
			nBuilder.setContentTitle("Signout Notification");
			nBuilder.setContentText("Should you be signed out?");
			if (canVibrate)
				nBuilder.setVibrate(vibratePattern);

			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, nBuilder.build());
		}
	}
}
