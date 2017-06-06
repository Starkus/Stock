package net.starkus.stock.legacy;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name = "history")
public class LegacyHistoryWrapper {
	
	private List<LegacyPurchase> history;
	
	@XmlElement(name = "purchase")
	public List<LegacyPurchase> getHistory() {
		return history;
	}
	
	public void setHistory(List<LegacyPurchase> history) {
		this.history = history;
	}

}
