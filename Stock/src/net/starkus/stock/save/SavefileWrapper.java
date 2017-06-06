package net.starkus.stock.save;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "save")
public class SavefileWrapper {
	
	private float cashBox;
	private ProductListWrapper productList;
	private HistoryWrapper history;
	private String password;
	private String version;
	
	public ProductListWrapper getProducts() {
		return productList;
	}
	
	public void setProducts(ProductListWrapper products) {
		this.productList = products;
	}
	
	
	public HistoryWrapper getHistory() {
		return history;
	}
	
	public void setHistory(HistoryWrapper history) {
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
	
	public void setVersion(String s) {
		version = s;
	}
	public String getVersion() {
		return version;
	}

}
