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

public class Poster {
	private URL website;
	private HttpURLConnection urlCon;
	private PrintWriter out;
	private int retries;
	private Person user;
	private BackgroundManager manager;
	private String result;
	
	public Poster(URL website, BackgroundManager manager) {
		this.website = website;
		user = manager.getUser();
		this.manager = manager;
	}
	
	public String postToWebsite() {
		preExecute();
		
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
				response = "IOError";
			}
		}
		
		postExecute(response);
		return result;
	}
	
	private void preExecute() {
//		activity.setNetworking(true);
//		activity.disableRefreshButton();
	}
	
	private void postExecute(String response) {
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
	    		result = "Success!";
		    	manager.refreshList();
	    	}
	    } else if (passwordCheck.equals("POST")) {
	    	result = "Invalid Password";
	    	retries = 0;
	    	manager.refreshList();
	    } else if (response.equals("IOError")) {
	    	if (retries == 0) {
	    		result = "Timed out";
	    		manager.refreshList();
	    	} else {
//	    		activity.setNetworking(true);
//	    		System.out.println("Attempting post again");
	    		retries -= 1;
	    		manager.postData();
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
		temp += "&login=" + manager.getUsername();
		temp += "&cmd=status";
		temp += "&password=" + manager.getPassword();
		
		return temp;
	}
}
