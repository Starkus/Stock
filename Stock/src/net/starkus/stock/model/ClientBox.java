package net.starkus.stock.model;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;


public class ClientBox {
	
	private static final ObservableList<Client> clientList = FXCollections.observableArrayList();
	
	
	public static void init() {
		
		History.getHistory().addListener(new ListChangeListener<Transaction>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Transaction> c) {
				
				clientList.clear();
				getClientsFromHistory(History.getHistory()).forEach(name -> clientList.add(new Client(name)));
			}
		});
	}
	
	static List<String> getClientsFromHistory(ObservableList<Transaction> history) {
		
		ArrayList<String> clients = new ArrayList<>();
		
		for (Transaction t : history) {
			if (t.getCancelled() == true)
				continue;
			
			if (t.getClient() != null && !clients.contains(t.getClient())) {
				clients.add(t.getClient());
			}
		}
		
		return clients;
	}
	
	
	public static ObservableList<Client> getClients() {
		return clientList;
	}
}
