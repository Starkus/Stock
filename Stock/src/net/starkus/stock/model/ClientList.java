package net.starkus.stock.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientList {
	
	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	
	
	public ClientList() {
	}
	
	public ObservableList<Client> getClients() {
		return clientList;
	}
	
	public void add(Client client) {
		clientList.add(client);
	}
	/*
	@XmlElement(name = "clients")
	public ClientListWrapper getClientsWrapper() {
		ClientListWrapper wrapper = new ClientListWrapper();
		wrapper.setClients(clientList);
		
		return wrapper;
	}
	
	public void setClientsFromWrapper(ClientListWrapper wrapper) {
		if (wrapper == null || wrapper.getClients() == null)
			return;
		
		clientList.addAll(wrapper.getClients());
	}*/
}
