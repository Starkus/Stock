package net.starkus.stock.model;

import java.util.Collection;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class ProductBox {

	private static final ObservableList<Product> stockList = FXCollections.observableArrayList();
	
	private static final SortedList<Product> sortedStock = stockList.sorted(new Comparator<Product>() {
		@Override
		public int compare(Product o1, Product o2) {
			return Long.compare(o1.getCode(), o2.getCode());
		}
	});
	
	public static ObservableList<Product> getProducts() {
		return stockList;
	}
	
	public static void substractFromStock(Collection<? extends Product> productList) {
		productList.forEach(p -> {
			Product stockProduct = BinarySearch.findProductByCode(p.getCode(), sortedStock);
			stockProduct.setQuantity(stockProduct.getQuantity() - p.getQuantity());
		});
	}
	
	public static void addToStock(Collection<? extends Product> productList) {
		productList.forEach(p -> {
			Product stockProduct = BinarySearch.findProductByCode(p.getCode(), sortedStock);
			stockProduct.setQuantity(stockProduct.getQuantity() - p.getQuantity());
		});
	}
}
