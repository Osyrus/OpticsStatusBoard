package au.edu.adelaide.physics.opticsstatusboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class Poster extends AsyncTask<Void, Void, String> {
	private URL website;
	private HttpURLConnection urlCon;
	private PrintWriter out;
	private MainActivity activity;
	
	public Poster(URL website, MainActivity activity) {
		this.website = website;
		this.activity = activity;
	}
	
	protected String doInBackground(Void... voids) {
		Person user = activity.getUser();
		String response = "";
		
		if (user != null) {
			String postData = personUnwrapper(user);

			try {
				urlCon = (HttpURLConnection) website.openConnection();
				urlCon.setDoOutput(true);
				urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlCon.setFixedLengthStreamingMode(postData.getBytes().length);
				urlCon.setInstanceFollowRedirects(false);

				out = new PrintWriter(urlCon.getOutputStream());
				out.print(postData);
				out.close();

				Scanner inStream = new Scanner(urlCon.getInputStream());

				while (inStream.hasNextLine()) {
					response += inStream.nextLine();
				}

//				System.out.println(response);
			} catch (IOException e) {
				return "IOError";
			}
		}
		
		return response;
	}
	
	protected void onPreExecute() {
		activity.setNetworking(true);
	}
	
	protected void onPostExecute(String response) {
		Document parsedData = Jsoup.parse(response);
	    Element mainBody = parsedData.body();
	    
	    //Check for success
	    Elements bElements = mainBody.getElementsByTag("b");
	    Element bCheck = bElements.last();
	    String successCase1 = "No Messages";
	    String successCase2 = "New status was saved successfully";
	    
	    //Check for password invalid
	    Elements formElements = mainBody.getElementsByTag("form");
	    String passwordCheck = formElements.attr("method");
	    
//	    System.out.println(passwordCheck);
	    
	    if (bCheck != null) {
	    	String successCheck = bCheck.html();
	    	if (successCheck.equals(successCase1) || successCheck.equals(successCase2)) {
		    	activity.showToast("Success!");
		    	activity.refreshList();
	    	}
	    } else if (passwordCheck.equals("POST")) {
	    	activity.showToast("Invalid Password");
	    	activity.setRetries(0);
	    	activity.refreshList();
	    } else if (response.equals("IOError")) {
	    	if (activity.getRetries() == 0) {
	    		activity.showToast("Timed out");
	    		activity.refreshList();
	    	} else {
	    		activity.setNetworking(true);
	    		System.out.println("Attempting post again");
	    		activity.decRetries();
	    		activity.postData();
	    	}
	    }
	}
	
	private String personUnwrapper(Person person) {
		String temp = "status=";
		
		switch (person.getStatus()) {
		case 0:
			temp += "in";
			break;
		case 1:
			temp += "out";
			break;
		case 2:
			temp += "conf";
			break;
		case 3:
			temp += "lunch";
			break;
		case 4:
			temp += "sick";
			break;
		case 5:
			temp += "vac";
			break;
		default:
			temp += "out";
		}
		
		temp += "&back=" + person.getBackMessage();
		temp += "&message=" + person.getMessage();
		temp += "&login=" + activity.getUsername();
		temp += "&cmd=status";
		temp += "&password=" + activity.getPassword();
		
		return temp;
	}
}
