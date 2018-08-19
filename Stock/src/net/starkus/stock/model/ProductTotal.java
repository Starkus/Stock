package net.starkus.stock.model;

import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.LongProperty;
import javafx.collections.ObservableList;

public class ProductTotal extends Product {

	public ProductTotal(ObservableList<? extends Product> items) {
		this.nameProperty().set("TOTAL:");
		
		this.subtotalProperty().bind(Bindings.createObjectBinding(() -> 
				items.stream().collect(Collectors.summingDouble(Product::getSubtotal)), items));
	}

	
	@Override
	public FloatProperty quantityProperty() {
		return null;
	}
	
	@Override
	public FloatProperty priceProperty() {
		return null;
	}
	
	@Override
	public LongProperty codeProperty() {
		return null;
	}
}
