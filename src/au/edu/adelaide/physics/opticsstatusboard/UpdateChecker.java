package au.edu.adelaide.physics.opticsstatusboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

public class UpdateChecker extends AsyncTask<URL, Void, String[]>{
	private MainActivity activity;
	private BufferedReader input;
	private String data;
	
	public UpdateChecker(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	protected String[] doInBackground(URL... updateWebsite) {
		String[] output = new String[1];
		output[0] = "";
		
		try {
			input = new BufferedReader((new InputStreamReader(updateWebsite[0].openStream())));
			
			boolean ok = true;
			while (ok) {
				String test = input.readLine();
				
				if (test == null) {
					ok = false;
				} else {
					data += test;
				}
			}
			
			input.close();
			
			Document parsedData = Jsoup.parse(data);
		    Element mainBody = parsedData.body();
		    Elements currentVersion = mainBody.getElementsByTag("p");
		    
			output[0] = currentVersion.get(0).html();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		
		return output;
	}

	protected void onPostExecute(String[] output) {
		int currentVersion;
		String appId = "au.edu.adelaide.physics.opticsstatusboard";
		try {
			currentVersion = activity.getPackageManager().getPackageInfo(appId, 0).versionCode;
			
			if (Integer.parseInt(output[0]) > currentVersion) {
				activity.showToast("New Version Available");
			} else {
				activity.showToast("Currently up to Date");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
