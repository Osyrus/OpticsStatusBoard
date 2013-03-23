package au.edu.adelaide.physics.opticsstatusboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ArrayList<Person> people;
	private ArrayAdapter<Person> peopleAdapter;
	private URL website;
	private boolean networking;
	private ListView peopleList;
	private ImageButton refreshButton;
	private ImageButton inButton;
	private ImageButton outButton;
	private ImageButton confButton;
	private ImageButton lunchButton;
	private ImageButton sickButton;
	private ImageButton vacButton;
	private ImageButton setMessageButton;
	private Button setBackMessageButton;
	private Person user;
	private String username;
	private String password;
	private final int MAX_RETRIES = 3;
	private int retries;
	private String sortMode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        refreshUserData();
        
        user = null;
        networking = false;
        website = null;
        retries = 0;
        
        peopleList = (ListView) findViewById(R.id.peopleList);
        
        try {
			website = new URL("http://www.physics.adelaide.edu.au/cgi-bin/usignin/usignin.cgi");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        people = new ArrayList<Person>(0);
        
        peopleAdapter = new PeopleArrayAdapter(this, people);
        peopleList.setAdapter(peopleAdapter);
        
        refreshList();
        
        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        	
        });
        
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!networking) {
					refreshList();
				}
			}
		});
        
        setMessageButton = (ImageButton) findViewById(R.id.setMessage);
        setMessageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				
			}
		});
        
        setBackMessageButton = (Button) findViewById(R.id.setBackMessage);
        setBackMessageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
        
        inButton = (ImageButton) findViewById(R.id.in_button);
        inButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(1);
			}
		});
        
        outButton = (ImageButton) findViewById(R.id.out_button);
        outButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(0);
			}
		});
        
        confButton = (ImageButton) findViewById(R.id.conf_button);
        confButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(0);
			}
		});
        
        lunchButton = (ImageButton) findViewById(R.id.lunch_button);
        lunchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(0);
			}
		});
        
        sickButton = (ImageButton) findViewById(R.id.sick_button);
        sickButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(0);
			}
		});
        
        vacButton = (ImageButton) findViewById(R.id.vac_button);
        vacButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setStatus(0);
			}
		});
    }
    
    public void refreshList() {
    	refreshUserData();
    	new WebParser(people, peopleAdapter, this, sortMode).execute(website);
    }
    
    public void postData() {
    	new Poster(website, this).execute();
    }
    
    public void refreshUserData() {
    	//Get the saved preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //Retrieve the username and password
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        sortMode = settings.getString("sortMode", "2");
    }
    
    public void setStatus(int status) {
    	refreshUserData();
    	if (username.equals("") || password.equals("")) {
    		Toast.makeText(getApplicationContext(), "No stored Username or Password", Toast.LENGTH_SHORT).show();
    	} else {
    		setStatusButton(status);
    		user.setStatus(status);
    		if (!networking) {
    			retries = MAX_RETRIES;
    			postData();
    		} else {
    			retries = MAX_RETRIES + 1;
    		}
    	}
    }
    
    public void showMessageDialog(int messageType) {
    	String userInput;
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
    	
    	//Make a dialogue box to get the user input
    	
    	switch (messageType) {
    	case 0:
    		user.setMessage(userInput);
    		postData();
    		break;
    	case 1:
    		user.setBackMessage(userInput);
    		postData();
    		break;
    	default:
    		//Shouldn't ever get here...
    		break;
    	}
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
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_settings:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	case R.id.versionButton:
    		PackageInfo packageInfo;
    		try {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	    		Toast.makeText(getApplicationContext(), "Current Version: v" + packageInfo.versionName, Toast.LENGTH_SHORT).show();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    protected void onStop() {
    	super.onStop();
    	
    	//Get the preferences
    	SharedPreferences settings = getPreferences(0);
    	SharedPreferences.Editor editor = settings.edit();
    	
    	//Save any changed data
    	editor.putString("username", username);
    	editor.putString("password", password);
    	editor.putString("sortMode", sortMode);
    	
    	//Commit the changes
    	editor.commit();
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
}








