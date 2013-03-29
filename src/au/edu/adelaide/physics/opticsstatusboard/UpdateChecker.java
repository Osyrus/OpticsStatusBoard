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

public class UpdateChecker {
	private BackgroundManager manager;
	private BufferedReader input;
	private String data;
	
	public UpdateChecker(BackgroundManager manager) {
		this.manager = manager;
	}

	public void check(URL updateWebsite) {
		String[] output = new String[1];
		output[0] = "";
		
		try {
			input = new BufferedReader((new InputStreamReader(updateWebsite.openStream())));
			
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
		
		postExecute(output);
	}

	private void postExecute(String[] output) {
		int currentVersion;
		String appId = "au.edu.adelaide.physics.opticsstatusboard";
		try {
			currentVersion = manager.getPackageManager().getPackageInfo(appId, 0).versionCode;
			
			if (Integer.parseInt(output[0]) > currentVersion) {
				manager.notifyNewVersion(true);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
