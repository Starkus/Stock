package net.starkus.stock.legacy;

import javax.xml.bind.annotation.XmlRootElement;

import net.starkus.stock.save.ProductListWrapper;

@XmlRootElement(name = "save")
public class LegacySavefileWrapper {
	
	/*
	 * This savefile model contains obsoleted fields to read
	 * them from a save made by a previous version. Once
	 * loaded, it will be later saved in the new format.
	 */

	
	private float cashBox;
	private ProductListWrapper productList;
	private ClientListWrapper clientList;
	private LegacyHistoryWrapper history;
	private String password;
	
	public ProductListWrapper getProducts() {
		return productList;
	}
	
	public void setProducts(ProductListWrapper products) {
		this.productList = products;
	}
	
	
	public ClientListWrapper getClients() {
		return clientList;
	}
	
	public void setClients(ClientListWrapper clients) {
		this.clientList = clients;
	}
	
	
	public LegacyHistoryWrapper getHistory() {
		return history;
	}
	
	public void setHistory(LegacyHistoryWrapper history) {
		this.history = history;
	}
	
	
	public float getCashBox() {
		return cashBox;
	}
	
	public void setCashBox(float n) {
		cashBox = n;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}