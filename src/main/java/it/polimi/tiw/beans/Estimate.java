package it.polimi.tiw.beans;

public class Estimate {
	private int id;
	private float price;
	private int client;
	private int worker;
	private int product;
	private int assigned;

	public int getId() {
		return id;
	}

	public float getPrice() {
		return price;
	}
	
	public int getClient() {
		return client;
	}
	
	public int getWorker() {
		return worker;
	}
	
	public int getProduct() {
		return product;
	}
	
	public int getAssigned() {
		return assigned;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setPrice(float p) {
		price = p;
	}
	
	public void setClient(int c) {
		client = c;
	}
	
	public void setWorker(int w) {
		worker = w;
	}
	
	public void setProduct(int prod) {
		product = prod;
	}
	
	public void setAssigned(int ass) {
		assigned = ass;
	}


}