package net.starkus.stock.save;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.starkus.stock.model.Product;
import net.starkus.stock.model.TransactionType;
import net.starkus.stock.util.LocalDateTimeAdapter;
import net.starkus.stock.util.TransactionTypeAdapter;;



public class TransactionWrapper {	
	
	private TransactionType type;
	private String client;
	private float balance;
	private List<Product> products;
	private LocalDateTime creationDate;
	private boolean cancelled;
	
	
	@XmlJavaTypeAdapter(TransactionTypeAdapter.class)
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType tt) {
		type = tt;
	}
	
	public String getClient() {
		return client;
	}
	public void setClient(String s) {
		client = s;
	}
	
	public float getBalance() {
		return balance;
	}
	public void setBalance(float f) {
		balance = f;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> l) {
		products = l;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(LocalDateTime d) {
		creationDate = d;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
