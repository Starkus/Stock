package net.starkus.stock.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name = "history")
public class HistoryWrapper {
	
	private List<Purchase> history;
	
	@XmlElement(name = "purchase")
	public List<Purchase> getHistory() {
		return history;
	}
	
	public void setHistory(List<Purchase> history) {
		this.history = history;
	}

}
