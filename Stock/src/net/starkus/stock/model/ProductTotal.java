package net.starkus.stock.model;

import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

public class ProductTotal extends Product {

	public ProductTotal(ObservableList<? extends Product> items) {
		this.nameProperty().set("TOTAL:");
		this.buyPriceProperty().set(0);
		this.sellPriceProperty().set(0);

		this.buySubtotalProperty().bind(Bindings.createObjectBinding(() -> 
				items.stream().collect(Collectors.summingDouble(Product::getBuySubtotal)), items));
		
		this.sellSubtotalProperty().bind(Bindings.createObjectBinding(() -> 
				items.stream().collect(Collectors.summingDouble(Product::getSellSubtotal)), items));
	}

}
