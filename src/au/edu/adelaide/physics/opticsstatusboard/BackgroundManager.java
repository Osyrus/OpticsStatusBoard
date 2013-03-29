package au.edu.adelaide.physics.opticsstatusboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class BackgroundManager extends IntentService {
	private ArrayList<Person> people;
	private URL website, updateWebsite;
	private boolean showNameInList, newVersion;
	private Person user;
	@SuppressWarnings("unused")
	private String username, password, sortMode, webAddress, updateFileURL, result;
	public static final int MAX_RETRIES = 3;
	private int retries;
	
	public BackgroundManager() {
		super("backgroundService");
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
//				System.out.println("The user to post for is "+user.getName());
				postData();
				returnData.putString("postResult", result);
			}
			
			//See if a request for a new version check was asked for
			if (data.containsKey("requestVersionCheck")) {
				if (data.getBoolean("requestVersionCheck")) {
					checkForUpdate();
					returnData.putBoolean("newVersion", newVersion);
				}
			}
		}

		//Put the data to be returned into the bundle
		if (getPeople() != null)
			returnData.putParcelableArrayList("people", getPeople());
		if (getUser() != null)
			returnData.putParcelable("user", getUser());
		//Put the bundle into the intent to send off
		returnIntent.putExtras(returnData);
		//Send off the intent
		LocalBroadcastManager.getInstance(this).sendBroadcast(returnIntent);
		
		stopSelf();
	}
	
	public void onCreate() {
		super.onCreate();
		
		website = null;
        retries = 0;
        user = null;
        newVersion = false;
        
        people = new ArrayList<Person>(0);
        
        refreshUserData();
		
		updateFileURL = "https://dl.dropbox.com/u/11481054/OpticsStatusBoardApp/OpticsStatusBoard.apk";
		
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
    
    public void checkForUpdate() {
    	new UpdateChecker(this).check(updateWebsite);
    }
    
    public void postData() {
    	refreshUserData();
    	
    	Poster poster = new Poster(website, this);
    	result = poster.postToWebsite();
    }
    
    public void refreshUserData() {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //Retrieve the username and password
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        sortMode = settings.getString("sortMode", "2");
        webAddress = settings.getString("webAddress", "http://www.physics.adelaide.edu.au/cgi-bin/usignin/usignin.cgi");
        showNameInList = settings.getBoolean("showName", false);
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
