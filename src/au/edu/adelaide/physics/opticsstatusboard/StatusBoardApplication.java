package au.edu.adelaide.physics.opticsstatusboard;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "",
				mailTo = "blokart11@gmail.com",
				mode = ReportingInteractionMode.TOAST,
				resToastText = R.string.crashToastText)
public class StatusBoardApplication extends Application {
	public void onCreate() {
		super.onCreate();
		
		ACRA.init(this);
	}
}
