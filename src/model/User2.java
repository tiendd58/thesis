package model;

public class User2 {
	private String id;
	private String rating;
	private String itemID;
	private String categoryID;
	
	public User2(String id, String rating, String itemID, String categoryID) {
		this.id = id;
		this.rating = rating;
		this.itemID = itemID;
		this.categoryID = categoryID;
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

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public User2(User2 u) {
		id = u.getId();
		itemID = u.getItemID();
		rating = u.getRating();
		categoryID = u.getCategoryID();
	}
	
	
	
	
	
	
}
