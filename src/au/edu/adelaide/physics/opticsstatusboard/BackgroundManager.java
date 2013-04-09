package au.edu.adelaide.physics.opticsstatusboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class BackgroundManager extends IntentService {
	private ArrayList<Person> people;
	private URL website, updateWebsite;
	private boolean showNameInList, newVersion, locationEnabled, locationNotification, locationVibrate;
	private Person user;
	private String username, password, sortMode, webAddress;
	public static final int MAX_RETRIES = 3;
	private int retries;
	private final long[] vibratePattern = {0, 200, 0, 200, 0, 200};
	
	public BackgroundManager() {
		super("BackgroundService");
	}
		
	@Override
	protected void onHandleIntent (Intent intent) {
		Bundle data = intent.getExtras();
		
		//Update the list of people and get the user
		refreshList();
		
		//Set up the objects for returning data
		Intent returnIntent = new Intent("BackgroundRefresh");
		Bundle returnData = new Bundle();
		
		//If the service has been sent an updated user, post the changes to the sign in page
		if (data != null) {
			if (data.containsKey("updatedUser")) {
				user = (Person) data.getParcelable("updatedUser");
				String result = postData();
				returnData.putString("postResult", result);
			}
			
			//See if a request for a new version check was asked for
			if (data.containsKey("requestVersionCheck")) {
				if (data.getBoolean("requestVersionCheck")) {
					checkForUpdate();
					returnData.putBoolean("newVersion", newVersion);
				}
			}
			
			//Check if a widget has requested a sign in/out
			if (data.containsKey("widgetSignIn")) {
				if (data.getBoolean("widgetSignIn"))
					user.setStatus(0);
				else
					user.setStatus(1);
				
				postData();
			}
			
			//Check if this intent was fired by the location proximity alert
			if (data.containsKey(LocationManager.KEY_PROXIMITY_ENTERING) && locationEnabled) {
				boolean entered = data.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
				
				if (entered)
					user.setStatus(0);
				else
					user.setStatus(1);
				
				if (locationNotification)
					createLocationNotification(entered);
				
				postData();
			}
		}
		
		//Refresh the widget
		updateWidget();
		
		//Put the data to be returned into the bundle
		if (getPeople() != null)
			returnData.putParcelableArrayList("people", getPeople());
		if (getUser() != null)
			returnData.putParcelable("user", getUser());
		//Put the bundle into the intent to send off
		returnIntent.putExtras(returnData);
		//Send off the intent
		LocalBroadcastManager.getInstance(this).sendBroadcast(returnIntent);
	}
	
	private void createLocationNotification(boolean signin) {
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setSmallIcon(R.drawable.ic_launcher);
		
		if (signin) {
			nBuilder.setContentTitle("Location Based Signin");
			nBuilder.setContentText("You are in proximity of the university and have thus been signed in.");
		} else {
			nBuilder.setContentTitle("Location Based Signout");
			nBuilder.setContentText("You are out of proximity of the university and have thus been signed out.");
		}
		
		if (locationVibrate)
			nBuilder.setVibrate(vibratePattern);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, nBuilder.build());
	}
	
	public void onCreate() {
		super.onCreate();
		
		website = null;
        retries = 0;
        user = null;
        newVersion = false;
        
        people = new ArrayList<Person>(0);
        
        refreshUserData();
		
		try {
			website = new URL(webAddress);
			updateWebsite = new URL("https://dl.dropbox.com/u/11481054/OpticsStatusBoardApp/current_version.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
    public void refreshList() {
    	refreshUserData();
    	
    	WebParser parser = new WebParser(people, this, sortMode);
    	parser.parseWebsite(website);
    	user = parser.fetchUser();
;    	people = parser.fetchPeople();
    }
    
    private void updateWidget() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		ComponentName myWidget = new ComponentName(this.getApplicationContext(), ToggleWidget.class);
		RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.toggle_widget);

		// Register an onClickListener to sign in
		if (user.getStatus() > 0) { 
			Intent signInIntent = new Intent(this.getApplicationContext(), BackgroundManager.class);
			signInIntent.putExtra("widgetSignIn", true);
			PendingIntent pendingSignInIntent = PendingIntent.getService(getApplicationContext(), 0, signInIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.outButtonW, pendingSignInIntent);
			remoteViews.setOnClickPendingIntent(R.id.vacButtonW, pendingSignInIntent);
			remoteViews.setOnClickPendingIntent(R.id.confButtonW, pendingSignInIntent);
			remoteViews.setOnClickPendingIntent(R.id.lunchButtonW, pendingSignInIntent);
			remoteViews.setOnClickPendingIntent(R.id.sickButtonW, pendingSignInIntent);
		} else {
			//And to sign out
			Intent signOutIntent = new Intent(this.getApplicationContext(), BackgroundManager.class);
			signOutIntent.putExtra("widgetSignIn", false);
			PendingIntent pendingSignOutIntent = PendingIntent.getService(getApplicationContext(), 0, signOutIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.inButtonW, pendingSignOutIntent);
		}

		setWidgetImage(user.getStatus(), remoteViews);
		setWidgetCounter(remoteViews);
		
		appWidgetManager.updateAppWidget(myWidget, remoteViews);
    }
    
    private void setWidgetCounter(RemoteViews views) {
    	Person current;
    	int counter = 0;
    	
    	for (int i = 0; i < people.size(); i++) {
    		current = people.get(i);
    		if (current.getStatus() == 0)
    			counter++;
    	}
    	
    	views.setTextViewText(R.id.inCounterW, "In: "+Integer.toString(counter));
    }
    
    private void setWidgetImage(int status, RemoteViews views) {
    	views.setViewVisibility(R.id.inButtonW, View.GONE);
    	views.setViewVisibility(R.id.outButtonW, View.GONE);
    	views.setViewVisibility(R.id.confButtonW, View.GONE);
    	views.setViewVisibility(R.id.lunchButtonW, View.GONE);
    	views.setViewVisibility(R.id.sickButtonW, View.GONE);
    	views.setViewVisibility(R.id.vacButtonW, View.GONE);
    	
    	switch (status) {
    	case 0:
    		views.setViewVisibility(R.id.inButtonW, View.VISIBLE);
    		break;
    	case 1:
    		views.setViewVisibility(R.id.outButtonW, View.VISIBLE);
    		break;
    	case 2:
    		views.setViewVisibility(R.id.confButtonW, View.VISIBLE);
    		break;
    	case 3:
    		views.setViewVisibility(R.id.lunchButtonW, View.VISIBLE);
    		break;
    	case 4:
    		views.setViewVisibility(R.id.sickButtonW, View.VISIBLE);
    		break;
    	case 5:
    		views.setViewVisibility(R.id.vacButtonW, View.VISIBLE);
    		break;
    	default:
    		// TODO What should it do in this case?
    	}
    }
    
    private void checkForUpdate() {
    	new UpdateChecker(this).check(updateWebsite);
    }
    
    public String postData() {
    	refreshUserData();
    	
    	Poster poster = new Poster(website, this);
    	String result = poster.postToWebsite();
    	
    	return result;
    }
    
    private void refreshUserData() {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //Retrieve the required settings
        username = settings.getString("username", "").trim();
        password = settings.getString("password", "").trim();
        sortMode = settings.getString("sortMode", "2");
        webAddress = settings.getString("webAddress", "http://www.physics.adelaide.edu.au/cgi-bin/usignin/usignin.cgi");
        showNameInList = settings.getBoolean("showName", false);
        locationEnabled = settings.getBoolean("locationEnabled", false);
        locationNotification = settings.getBoolean("locationNotification", false);
        locationVibrate = settings.getBoolean("locationVibrate", false);
    }
    
    public void notifyNewVersion(boolean newVersion) {
    	this.newVersion = newVersion;
    }
	
	public void onDestroy () {
		super.onDestroy();
	}
	
	public void showToast(String data) {
    	Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
    }
    public int getRetries() {
    	return retries;
    }
    public void decRetries() {
    	retries -= 1;
    }
    public void setRetries(int retries) {
    	this.retries = retries;
    }
    public boolean canShowNameInList() {
    	return showNameInList;
    }
    public String getUsername() {
    	return username;
    }
    public String getPassword() {
    	return password;
    }
    public Person getUser() {
    	return user;
    }
    public ArrayList<Person> getPeople() {
    	return people;
    }
}
