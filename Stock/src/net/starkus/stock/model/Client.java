package net.starkus.stock.model;

import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;

public class Client {
	
	/*
	 * Client entry for client list.
	 * Keeps track of client debt.
	 */

	private final StringProperty name;
	private final FilteredList<Transaction> transactions;
	private final FilteredList<Transaction> validTransactions;
	
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

		this.transactions = History.getHistory().filtered(t -> t.getClient() != null && t.getClient().equals(getName()));
		this.validTransactions = transactions.filtered(t -> t.getCancelled() == false);
	}
	
	
	public void setName(String n) {
		this.name.set(n);
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public FilteredList<Transaction> getTransactions() {
		return transactions;
	}
	
	public ObservableValue<Number> getObservableBalance() {
		return Bindings.createObjectBinding(() -> 
				this.validTransactions.stream().collect(Collectors.summingDouble(Transaction::getBalance)), validTransactions);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	
	public float calculateBalance() {
		
		float bal = 0;
		
		FilteredList<Transaction> filteredTransactionList = new FilteredList<>(History.getHistory());
		filteredTransactionList = validTransactions;
		
		for (int i=0; i < filteredTransactionList.size(); i++) {
			
			Transaction t = filteredTransactionList.get(i);
			
			bal += t.getBalance();
		}
		
		return bal;
	}
}
