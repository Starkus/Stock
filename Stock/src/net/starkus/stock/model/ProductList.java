package net.starkus.stock.model;

import javax.xml.bind.annotation.XmlElement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductList {
	
	protected ObservableList<Product> productList = FXCollections.observableArrayList();

	public ProductList() {}
	
	public ObservableList<Product> getProductData() {
		return productList;
	}
	
	public void add(Product product) {
		productList.add(product);
	}
	
	public float getTotal() {
		float total = 0;
		
		for (Product p : productList) {
			total += p.getSellPrice() * p.getQuantity();
		}
		
		return total;
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
