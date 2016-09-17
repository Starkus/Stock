package net.starkus.stock.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class Purchase {
	
	private ObservableList<Product> productList = FXCollections.observableArrayList();

	public Purchase() {
		// TODO Auto-generated constructor stub
	}
	
	public ObservableList<Product> getProductData() {
		return productList;
	}
	
	public void add(Product product) {
		productList.add(product);
	}
	
	public void substractItemsFromStock(SortedList<Product> stock) {
		
		for (Product prod : productList) {
			
			Product prodInStock = BinarySearch.findProductByCode(prod.getCode(), stock);
			prodInStock.setQuantity(prodInStock.getQuantity() - prod.getQuantity());
		}
	}

}
