package au.edu.adelaide.physics.opticsstatusboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ArrayList<Person> people;
	private ArrayAdapter<Person> peopleAdapter;
	private URL website, updateWebsite;
	private boolean networking, canNotify, statusChanged, canVibrate, showNameInList, newVersion;
	private ListView peopleList;
	private ImageButton refreshButton, inButton, outButton, confButton, lunchButton, sickButton, vacButton, setMessageButton;
	private Button setBackMessageButton;
	private Person user;
	private String username, password, sortMode, userInput, webAddress, updateFileURL;
	private final int MAX_RETRIES = 3;
	private int retries, signOutHour, signOutMinute;
	private MenuItem versionButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        refreshUserData();
        
        user = null;
        networking = false;
        website = null;
        retries = 0;
        statusChanged = true;
        newVersion = false;
        
        updateFileURL = "https://dl.dropbox.com/u/11481054/OpticsStatusBoardApp/OpticsStatusBoard.apk";
        
        peopleList = (ListView) findViewById(R.id.peopleList);
        
        new OnAlarmReceiver();
        
        try {
			website = new URL(webAddress);
			updateWebsite = new URL("https://dl.dropbox.com/u/11481054/OpticsStatusBoardApp/current_version.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        people = new ArrayList<Person>(0);
        
        peopleAdapter = new PeopleArrayAdapter(this, people);
        peopleList.setAdapter(peopleAdapter);
        
        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!networking) {
					Context context = parent.getContext();
					Person clickedPerson = people.get(position);

					PersonDetailDialog detailDialog = new PersonDetailDialog(context);
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View personDetailView = inflater.inflate(R.layout.person_detail, null, false);
					detailDialog.setContentView(personDetailView);
					detailDialog.setTitle(clickedPerson.getName());

					ListView personDetailList = (ListView) detailDialog.findViewById(R.id.personDetailList);
					PersonDetailAdapter detailAdapter = new PersonDetailAdapter(context, clickedPerson.getInfoContainer());
					personDetailList.setAdapter(detailAdapter);

					detailDialog.show();
				}
			}
        });
        
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					refreshList();
			}
		});
        
        setMessageButton = (ImageButton) findViewById(R.id.setMessage);
        setMessageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				if (!networking)
					if (user != null)
						showMessageDialog(0);
					else
						showToast("Invalid Username");
			}
		});
        
        setBackMessageButton = (Button) findViewById(R.id.setBackMessage);
        setBackMessageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						showMessageDialog(1);
					else
						showToast("Invalid Username");
			}
		});
        
        inButton = (ImageButton) findViewById(R.id.in_button);
        inButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(1);
					else
						showToast("Invalid Username");
			}
		});
        
        outButton = (ImageButton) findViewById(R.id.out_button);
        outButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(0);
					else
						showToast("Invalid Username");
			}
		});
        
        confButton = (ImageButton) findViewById(R.id.conf_button);
        confButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(0);
					else
						showToast("Invalid Username");
			}
		});
        
        lunchButton = (ImageButton) findViewById(R.id.lunch_button);
        lunchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(0);
					else
						showToast("Invalid Username");
			}
		});
        
        sickButton = (ImageButton) findViewById(R.id.sick_button);
        sickButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(0);
					else
						showToast("Invalid Username");
			}
		});
        
        vacButton = (ImageButton) findViewById(R.id.vac_button);
        vacButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking)
					if (user != null)
						setStatus(0);
					else
						showToast("Invalid Username");
			}
		});
        
        refreshList();
        checkForUpdate();
    }
    
    public void refreshList() {
    	refreshUserData();
    	new WebParser(people, peopleAdapter, this, sortMode).execute(website);
    	
    	if (user != null && statusChanged) {
    		setSignOutAlarm();
    		statusChanged = false;
    	}
    }
    
    public void checkForUpdate() {
    	new UpdateChecker(this).execute(updateWebsite);
    }
    
    public void notifyNewVersion() {
    	showToast("New version available, download link available in menu");
    	
    	versionButton.setTitle("Download New Version");
    	newVersion = true;
    }
    
    public void postData() {
    	refreshUserData();
    	new Poster(website, this).execute();
    }
    
    public void setSignOutTime(int hour, int min) {
    	signOutHour = hour;
    	signOutMinute = min;
    	setUserData();
    	
    	if (user != null) {
    		setSignOutAlarm();
    	}
    }
    
    public void setSignOutAlarm() {
    	AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    	Intent alarmIntent = new Intent(this, OnAlarmReceiver.class);
    	alarmIntent.putExtra("status", getUser().getStatus());
    	alarmIntent.putExtra("canNotify", canNotify);
    	alarmIntent.putExtra("canVibrate", canVibrate);
    	PendingIntent alarmPending = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, signOutHour);
    	cal.set(Calendar.MINUTE, signOutMinute);
    	cal.set(Calendar.SECOND, 0);
    	long trigger = cal.getTimeInMillis();
    	
    	if (trigger <= System.currentTimeMillis()) {
    		trigger += AlarmManager.INTERVAL_DAY;
    	}
//    	System.out.println("Trigger time in ms: "+trigger);
    	
    	alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, trigger, AlarmManager.INTERVAL_DAY, alarmPending);
    }
    
    public int[] getSignOutTime() {
    	int[] out = {signOutHour, signOutMinute};
    	return out;
    }
    
    public void refreshUserData() {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //Retrieve the username and password
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        sortMode = settings.getString("sortMode", "2");
        webAddress = settings.getString("webAddress", "http://www.physics.adelaide.edu.au/cgi-bin/usignin/usignin.cgi");
        canNotify = settings.getBoolean("notificationEnabled", false);
        canVibrate = settings.getBoolean("canVibrate", true);
        showNameInList = settings.getBoolean("showName", false);
        signOutHour = settings.getInt("signOutHour", 18);
        signOutMinute = settings.getInt("signOutMin", 0);
    }
    
    public void setStatus(int status) {
    	refreshUserData();
    	setStatusButton(status);
    	user.setStatus(status);
    	if (!networking) {
    		retries = MAX_RETRIES;
    		postData();
    	} else {
    		retries = MAX_RETRIES + 1;
    	}
    	statusChanged = true;
    }
    
    public void showMessageDialog(final int messageType) {
    	refreshUserData();
    	String title;
    	
    	switch (messageType) {
    	case 0:
    		userInput = user.getMessage();
    		title = "Enter your message";
    		break;
    	case 1:
    		userInput = user.getBackMessage();
    		title = "Enter when you will be back";
    		break;
    	default:
    		userInput = "";
    		title = "How did you even get here?";
    		break;
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(title);

    	// Set up the input
    	final EditText input = new EditText(this);
    	input.setText(userInput);
    	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    	builder.setView(input);

    	// Set up the buttons
    	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        userInput = input.getText().toString();
    	        
    	        switch (messageType) {
    	    	case 0:
    	    		user.setMessage(userInput);
    	    		if (!networking) {
    	    			retries = MAX_RETRIES;
    	    			postData();
    	    		} else {
    	    			retries = MAX_RETRIES + 1;
    	    		}
    	    		break;
    	    	case 1:
    	    		user.setBackMessage(userInput);
    	    		if (!networking) {
    	    			retries = MAX_RETRIES;
    	    			postData();
    	    		} else {
    	    			retries = MAX_RETRIES + 1;
    	    		}
    	    		break;
    	    	default:
    	    		//Shouldn't ever get here...
    	    		break;
    	    	}
    	    }
    	});
    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});

    	builder.show();
    }
    
    public void showToast(String data) {
    	Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
    }
    
    public void setStatusButton(int status) {
    	findViewById(R.id.in_button).setVisibility(View.GONE);
    	findViewById(R.id.out_button).setVisibility(View.GONE);
    	findViewById(R.id.conf_button).setVisibility(View.GONE);
    	findViewById(R.id.lunch_button).setVisibility(View.GONE);
    	findViewById(R.id.sick_button).setVisibility(View.GONE);
    	findViewById(R.id.vac_button).setVisibility(View.GONE);
    	
    	switch (status) {
    	case 0:
    		findViewById(R.id.in_button).setVisibility(View.VISIBLE);
    		break;
    	case 1:
    		findViewById(R.id.out_button).setVisibility(View.VISIBLE);
    		break;
    	case 2:
    		findViewById(R.id.conf_button).setVisibility(View.VISIBLE);
    		break;
    	case 3:
    		findViewById(R.id.lunch_button).setVisibility(View.VISIBLE);
    		break;
    	case 4:
    		findViewById(R.id.sick_button).setVisibility(View.VISIBLE);
    		break;
    	case 5:
    		findViewById(R.id.vac_button).setVisibility(View.VISIBLE);
    		break;
    	default:
    		// TODO What should it do in this case?
    	}
    }
    
    public void disableRefreshButton() {
		findViewById(R.id.refresh_button).setVisibility(View.GONE);
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }
    
    public void enableRefreshButton() {
		findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);
		findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        versionButton = menu.findItem(R.id.versionButton);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_settings:
    		setNetworking(false);
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	case R.id.versionButton:
    		if (!newVersion) {
    			checkForUpdate();
    		} else {
    			Intent appDownload = new Intent(Intent.ACTION_VIEW);
    			appDownload.setData(Uri.parse(updateFileURL));
    			startActivity(appDownload);
    		}
    		return true;
    	case R.id.setStatusOption:
    		AlertDialog.Builder statusDialog = new AlertDialog.Builder(this);
        	
        	statusDialog.setSingleChoiceItems(R.array.statusOptions, user.getStatus(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int selectedStatus) {
					setStatus(selectedStatus);
					dialog.dismiss();
				}
			});
        	
        	statusDialog.setTitle(R.string.statusOption);
        	statusDialog.show();
        	
        	return true;
    	case R.id.setSignOutTime:
    		refreshUserData();
    		TimePickerDialog timePicker = new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {
    			@Override
    			public void onTimeSet(TimePicker view, int hour, int min) {
    				setSignOutTime(hour, min);
    			}
    		}, signOutHour, signOutMinute, false);
    		
    		timePicker.setTitle(R.string.signOutTime);
    		timePicker.show();
    		
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void setUserData() {
    	//Get the preferences
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
    	SharedPreferences.Editor editor = settings.edit();
    	
    	//Save any changed data
    	editor.putString("webAddress", webAddress);
    	editor.putInt("signOutHour", signOutHour);
    	editor.putInt("signOutMin", signOutMinute);
    	
    	//Commit the changes
    	editor.commit();
    }
    
    protected void onStop() {
    	super.onStop();
    	
    	setUserData();
    }
    
    public String getUsername() {
    	return username;
    }
    public String getPassword() {
    	return password;
    }
    public void setUser(Person person) {
    	user = person;
    }
    public void setNetworking(boolean networking) {
    	this.networking = networking;
    }
    public boolean isNetworking() {
    	return networking;
    }
    public Person getUser() {
    	return user;
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
    public ArrayList<Person> getPeople() {
    	return people;
    }
    public boolean canShowNameInList() {
    	return showNameInList;
    }
}









