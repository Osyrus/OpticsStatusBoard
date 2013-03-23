package au.edu.adelaide.physics.opticsstatusboard;

public class InfoContainer {
	private String title;
	private String contents;
	private int infoType;
	
	public InfoContainer(String title, String contents, int infoType) {
		this.title = title;
		this.contents = contents;
		this.infoType = infoType;
	}
	
	public String getTitle() {
		return title;
	}
	public String getContents() {
		return contents;
	}
	public int getInfoType() {
		return infoType;
	}
}
