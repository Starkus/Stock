package net.starkus.stock.legacy;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/*
 * Wrapper to save client list.
 * 
 * @author Starkus
 */

public class ClientListWrapper {

	private List<LegacyClient> clients;
	
	@XmlElement(name = "client")
	public List<LegacyClient> getClients() {
		return clients;
	}
	
	public void setClients(List<LegacyClient> clients) {
		this.clients = clients;
	}
}
