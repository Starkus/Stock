package net.starkus.stock.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/*
 * Wrapper to save client list.
 * 
 * @author Starkus
 */

public class ClientListWrapper {

	private List<Client> clients;
	
	@XmlElement(name = "client")
	public List<Client> getClients() {
		return clients;
	}
	
	public void setClients(List<Client> clients) {
		this.clients = clients;
	}
}
