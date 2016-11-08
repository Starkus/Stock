package net.starkus.stock.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "save")
public class SavefileWrapper {
	
	private float cashBox;
	private ProductListWrapper productList;
	private HistoryWrapper history;
	
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

}
