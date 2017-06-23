package net.starkus.stock.model;

import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Transaction implements Comparable<Transaction> {
	
	protected final ObjectProperty<TransactionType> type;
	
	protected final StringProperty client;
	protected FloatProperty balance;
	
	protected final BooleanProperty cancelled;
	
	protected final ObjectProperty<LocalDateTime> creationDate;
	
	
	public Transaction(TransactionType transactionType) {
		
		type = new SimpleObjectProperty<TransactionType>(transactionType);

		creationDate = new SimpleObjectProperty<>(null);
		client = new SimpleStringProperty();
		balance = new SimpleFloatProperty();
		
		cancelled = new SimpleBooleanProperty(false);
	}
	

	public abstract void _do();
	public abstract void undo();
	
	
	@Override
	public int compareTo(Transaction o) {
		return getClient().compareTo(o.getClient());
	}

	
	public TransactionType getType() {
		return type.get();
	}
	public void setType(TransactionType t) {
		type.set(t);
	}
	public ObjectProperty<TransactionType> typeProperty() {
		return type;
	}
	
	public String getClient() {
		return client.get();
	}
	public void setClient(String name) {
		client.set(name);
	}
	public StringProperty clientProperty() {
		return client;
	}
	
	public void cancel() {
		cancelled.set(true);
	}
	public boolean getCancelled() {
		return cancelled.get();
	}
	public void setCancelled(boolean b) {
		cancelled.set(b);
	}
	public BooleanProperty cancelledProperty() {
		return cancelled;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate.get();
	}
	public void setCreationDate(LocalDateTime date) {
		creationDate.set(date);
	}
	public ObjectProperty<LocalDateTime> creationDateProperty() {
		return creationDate;
	}
	
	public float getBalance() {
		return balance.get();
	}
	public FloatProperty balanceProperty() {
		return balance;
	}
	public void setBalance(float f) {
		balance.set(f);
	}
}
