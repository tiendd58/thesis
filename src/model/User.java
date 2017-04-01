package model;

public class User {
	private String id;
	private String rating;
	private String itemID;
	private String time;
	
	public User(String id, String rating, String itemID, String time) {
		this.id = id;
		this.rating = rating;
		this.itemID = itemID;
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public User(User u) {
		id = u.getId();
		itemID = u.getItemID();
		rating = u.getRating();
		time = u.getTime();
	}
	
	
	
	
	
	
}
