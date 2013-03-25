package au.edu.adelaide.physics.opticsstatusboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;

public class ToggleWidget extends AppWidgetProvider {
	private MainActivity activity;
	private String username, password, sortMode, webAddress;
	private Context context;
	private URL website;
	private ArrayList<Person> people;
	private ArrayAdapter<Person> peopleAdapter;
	
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
		this.context = context;
		refreshList();
		
		try {
			website = new URL(webAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshList() {
    	refreshUserData();
//    	new WebParser(people, peopleAdapter, this, sortMode).execute(website);
    }
	
	public void refreshUserData() {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //Retrieve the username and password
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        sortMode = settings.getString("sortMode", "2");
        webAddress = settings.getString("webAddress", webAddress);
    }

	public void setWidgetImage(int status) {
    	activity.findViewById(R.id.in_button_widget).setVisibility(View.GONE);
    	activity.findViewById(R.id.out_button_widget).setVisibility(View.GONE);
//    	activity.findViewById(R.id.conf_button).setVisibility(View.GONE);
//    	activity.findViewById(R.id.lunch_button).setVisibility(View.GONE);
//    	activity.findViewById(R.id.sick_button).setVisibility(View.GONE);
//    	activity.findViewById(R.id.vac_button).setVisibility(View.GONE);
    	
    	switch (status) {
    	case 0:
    		activity.findViewById(R.id.in_button).setVisibility(View.VISIBLE);
    		break;
    	case 1:
    		activity.findViewById(R.id.out_button).setVisibility(View.VISIBLE);
    		break;
    	case 2:
    		activity.findViewById(R.id.conf_button).setVisibility(View.VISIBLE);
    		break;
    	case 3:
    		activity.findViewById(R.id.lunch_button).setVisibility(View.VISIBLE);
    		break;
    	case 4:
    		activity.findViewById(R.id.sick_button).setVisibility(View.VISIBLE);
    		break;
    	case 5:
    		activity.findViewById(R.id.vac_button).setVisibility(View.VISIBLE);
    		break;
    	default:
    		// TODO What should it do in this case?
    	}
    }
}
