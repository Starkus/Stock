package net.starkus.stock.legacy;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LegacyClient {
	
	/*
	 * Client entry for client list.
	 * Keeps track of client debt.
	 */

	private final StringProperty name;
	private final FloatProperty balance;
	
	/*
	 * Default constructor
	 */
	public LegacyClient() {
		this(null, 0f);
	}
	
	/*
	 * Constructor with data input
	 * 
	 * @param name
	 */
	public LegacyClient(String name) {
		this(name, 0f);
	}
	
	// With balance, in case it's needed...
	public LegacyClient(String name, float balance) {
		
		this.name = new SimpleStringProperty(name);
		this.balance = new SimpleFloatProperty(balance);
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
	
	
	public void setBalance(float f) {
		this.balance.set(f);
	}
	
	public float getBalance() {
		return this.balance.get();
	}
	
	public FloatProperty balanceProperty() {
		return this.balance;
	}
}
