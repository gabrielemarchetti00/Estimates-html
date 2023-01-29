package it.polimi.tiw.beans;

public class Option {
	private int id;
	private String type;
	private String name;
	private int prodid;
	
	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public int getProduct() {
		return prodid;
	}
	
	public void setId(int i) {
		id = i;
	}
	
	public void setType(String t) {
		type = t;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setProduct(int prod) {
		prodid = prod;
	}
}

