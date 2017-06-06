package net.starkus.stock.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import net.starkus.stock.MainApp;

public class Client {
	
	/*
	 * Client entry for client list.
	 * Keeps track of client debt.
	 */

	private final StringProperty name;
	
	/*
	 * Default constructor
	 */
	public Client() {
		this(null, 0f);
	}
	
	/*
	 * Constructor with data input
	 * 
	 * @param name
	 */
	public Client(String name) {
		this(name, 0f);
	}
	
	// With balance, in case it's needed...
	public Client(String name, float balance) {
		
		this.name = new SimpleStringProperty(name);
	}
	
	
	public void setName(String n) {
		this.name.set(n);
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	
	public float calculateBalance(MainApp mainApp) {
		
		float bal = 0;
		
		FilteredList<Transaction> filteredTransactionList = new FilteredList<>(mainApp.getHistory());
		filteredTransactionList = mainApp.getHistory().filtered(t -> t.getClient() != null && t.getClient().equals(getName()));
		
		for (int i=0; i < filteredTransactionList.size(); i++) {
			
			Transaction t = filteredTransactionList.get(i);
			
			System.out.println(t.getClient());
			bal += t.getBalance();
		}
		
		return bal;
	}
	
	
	public static List<String> getClientsFromHistory(ObservableList<Transaction> history) {
		
		ArrayList<String> clients = new ArrayList<>();
		
		for (Transaction t : history) {
			if (t.getClient() != null && !clients.contains(t.getClient())) {
				clients.add(t.getClient());
			}
		}
		
		return clients;
	}
}
