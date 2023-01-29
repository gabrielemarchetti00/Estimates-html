package it.polimi.tiw.beans;

public class Product {
	private int id;
	private String image;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getName() {
		return name;
	}
	
	
	public void setId(int i) {
		id = i;
	}
	
	public void setImage(String img) {
		image = img;
	}
	
	public void setName(String n) {
		name = n;
	}
}