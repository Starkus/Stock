package net.starkus.stock.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductBox {

	private static final ObservableList<Product> productList = FXCollections.observableArrayList();
	
	public static ObservableList<Product> getProducts() {
		return productList;
	}
}
