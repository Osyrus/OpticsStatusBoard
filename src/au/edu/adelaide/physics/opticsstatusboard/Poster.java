package au.edu.adelaide.physics.opticsstatusboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
		String postData = personUnwrapper(activity.getUser());
		String response = "";
		
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return response;
	}
	
	protected void onPreExecute() {
		activity.setNetworking(true);
	}
	
	protected void onPostExecute(String response) {
		activity.refreshList();
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
