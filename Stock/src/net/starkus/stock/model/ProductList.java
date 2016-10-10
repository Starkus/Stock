package net.starkus.stock.model;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import net.starkus.stock.util.LocalDateTimeAdapter;

public class ProductList {
	
	private ObservableList<Product> productList = FXCollections.observableArrayList();
	private ObjectProperty<LocalDateTime> creationDate;

	public ProductList() {
		this.creationDate = new SimpleObjectProperty<>(LocalDateTime.now());
	}
	
	public ObservableList<Product> getProductData() {
		return productList;
	}
	
	public void add(Product product) {
		productList.add(product);
	}
	
	public float getTotal(boolean sell) {
		float total = 0;
		
		for (Product p : productList) {
			if (sell)
				total += p.getSellPrice() * p.getQuantity();
			else
				total += p.getBuyPrice() * p.getQuantity();
		}
		
		return total;
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
	
	@XmlElement(name = "products")
	public ProductListWrapper getProducts() {
		ProductListWrapper wrapper = new ProductListWrapper();
		wrapper.setProducts(productList);
		
		return wrapper;
	}
	
	public void setProducts(ProductListWrapper wrapper) {
		if (wrapper == null || wrapper.getProducts() == null)
			return;
		
		productList.addAll(wrapper.getProducts());
	}

}
