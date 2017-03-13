package net.starkus.stock.model;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.SortedList;
import net.starkus.stock.util.LocalDateTimeAdapter;

public class Purchase extends ProductList {

	private StringProperty client;
	private FloatProperty paid;
	private ObjectProperty<LocalDateTime> creationDate; 
	
	
	public Purchase() {
		super();
		this.creationDate = new SimpleObjectProperty<>(LocalDateTime.now());
		client = new SimpleStringProperty();
		paid = new SimpleFloatProperty();
	}
	
	
	
	public void substractItemsFromStock(SortedList<Product> stock) {
		
		for (Product prod : productList) {
			
			Product prodInStock = BinarySearch.findProductByCode(prod.getCode(), stock);
			prodInStock.setQuantity(prodInStock.getQuantity() - prod.getQuantity());
		}
	}
	
	public void addToStock(SortedList<Product> stock) {
		
		for (Product prod : productList) {
			
			Product prodInStock = BinarySearch.findProductByCode(prod.getCode(), stock);
			prodInStock.setQuantity(prodInStock.getQuantity() + prod.getQuantity());
		}
	}
	
	
	
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getCreationDate() {
		return creationDate.get();
	}
	
	public void setCreationDate(LocalDateTime date) {
		creationDate.set(date);
	}
	
	
	public String getClient() {
		return client.get();
	}
	
	public void setClient(String clientName) {
		client.set(clientName);
	}
	
	
	public float getPaid() {
		return paid.get();
	}
	
	public void setPaid(float f) {
		paid.set(f);
	}
	
	public FloatProperty paidProperty() {
		return paid;
	}
}
