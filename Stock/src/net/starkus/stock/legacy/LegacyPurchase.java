package net.starkus.stock.legacy;


import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.util.LocalDateTimeAdapter;



public class LegacyPurchase extends ProductList {

	private final StringProperty client;
	private final FloatProperty paid;
	private final ObjectProperty<LocalDateTime> creationDate; 
	
	private final StringProperty formattedDate;
	private final FloatProperty total;
	
	
	public LegacyPurchase() {
		super();
		creationDate = new SimpleObjectProperty<>(null);
		client = new SimpleStringProperty();
		paid = new SimpleFloatProperty();
		
		formattedDate = new SimpleStringProperty();
		total = new SimpleFloatProperty();
		
		creationDate.addListener(new ChangeListener<LocalDateTime>() {

			@Override
			public void changed(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue,
					LocalDateTime n) {
				
				String value = String.format("%d/%d/%d - %d:%d:%d", n.getDayOfMonth(), n.getMonthValue(), n.getYear(),
						n.getHour(), n.getMinute(), n.getSecond());
				
				formattedDate.set(value);
			}
		});
		
		productList.addListener(new ListChangeListener<Product>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Product> c) {
				total.set(getTotal());
			}
		});
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
	
	public ObjectProperty<LocalDateTime> creationDateProperty() {
		return creationDate;
	}
	
	public StringProperty formattedDateProperty() {
		return formattedDate;
	}
	
	public FloatProperty totalProperty() {
		return total;
	}
	
	
	public String getClient() {
		return client.get();
	}
	
	public void setClient(String clientName) {
		client.set(clientName);
	}
	
	public StringProperty clientProperty() {
		return client;
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
