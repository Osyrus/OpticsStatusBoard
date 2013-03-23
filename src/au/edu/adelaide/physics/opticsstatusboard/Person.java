package au.edu.adelaide.physics.opticsstatusboard;

public class Person {
	private String firstName;
	private int status;
	private String lastName;
	private boolean hasMessage;
	private String mob;
	private String email;
	private String backMessage;
	private String message;
	private String username;
	
	public Person(String firstName, String lastName, String username, boolean hasMessage, String mob, String email, int status, String backMessage, String message) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.hasMessage = hasMessage;
		this.setMob(mob);
		this.setEmail(email);
		this.status = status;
		this.setBackMessage(backMessage);
		this.setMessage(message);
		this.username = username;
	}
	public Person(String firstName, int status) {
		this.firstName = firstName;
		this.status = status;
	}
	
	public String getVerboseStatus() {
		switch (status) {
			case 0:
				return "In";
			case 1:
				return "Not in";
			case 2:
				return "At a conference";
			case 3:
				return "Out to lunch";
			case 4:
				return "Sick";
			case 5:
				return "On vacation";
			default:
				return "Unknown";
		}
	}
	
	public boolean equals(Person other) {
		if (other.getStatus() != status)
			return false;
		if (!other.getBackMessage().equals(backMessage))
			return false;
		if (!other.getMessage().equals(message))
			return false;
		return true;
	}
	
	public InfoContainer[] getInfoContainer() {
		InfoContainer[] info = new InfoContainer[5];
		
		info[0] = new InfoContainer("Status", getVerboseStatus(), 0);
		info[1] = new InfoContainer("Phone #", getMob(), 1);
		info[2] = new InfoContainer("Email", getEmail(), 2);
		info[3] = new InfoContainer("Back", getBackMessage(), 0);
		info[4] = new InfoContainer("Message", getMessage(), 0);
		
		return info;
	}
	
	public String getName() {
		return firstName + " " + lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public int getStatus() {
		return status;
	}
	public String getUsername() {
		return username;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean hasMessage() {
		return hasMessage;
	}
	public String getMob() {
		return mob;
	}
	public void setMob(String mob) {
		this.mob = mob;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBackMessage() {
		return backMessage;
	}
	public void setBackMessage(String backMessage) {
		this.backMessage = backMessage;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
