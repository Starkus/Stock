package net.starkus.stock.model;

import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

public class ProductTotal extends Product {

	public ProductTotal(ObservableList<? extends Product> items) {
		this.nameProperty().set("TOTAL:");
		this.sellPriceProperty().set(0);
		
		this.sellSubtotalProperty().bind(Bindings.createObjectBinding(() -> 
				items.stream().collect(Collectors.summingDouble(Product::getSellSubtotal)), items));
	}

}
