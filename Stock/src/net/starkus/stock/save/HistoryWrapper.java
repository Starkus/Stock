package net.starkus.stock.save;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name = "history")
public class HistoryWrapper {
	
	private List<TransactionWrapper> history;
	
	@XmlElement(name = "transaction")
	public List<TransactionWrapper> getHistory() {
		return history;
	}
	
	public void setHistory(List<TransactionWrapper> history) {
		this.history = history;
	}

}
