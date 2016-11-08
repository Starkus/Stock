package net.starkus.stock.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name = "history")
public class HistoryWrapper {
	
	private List<ProductList> history;
	
	@XmlElement(name = "purchase")
	public List<ProductList> getHistory() {
		return history;
	}
	
	public void setHistory(List<ProductList> history) {
		this.history = history;
	}

}
