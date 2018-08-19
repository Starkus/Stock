package net.starkus.stock.model;

import javafx.beans.property.FloatProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public class ProductListWithTotal extends TransformationList<Product, Product>{
	
	private Product total;
	
	
	public ProductListWithTotal(ObservableList<? extends Product> source) {
		super(source);
		
		total = new ProductTotal(source);
	}
	
	
	public float getTotal() {
		return total.getSubtotal();
	}
	
	public FloatProperty totalProperty() {
		return total.subtotalProperty();
	}

	@Override
	public int getSourceIndex(int index) {
		if (index < getSource().size())
			return index;
		
		return -1;
	}

	@Override
	public Product get(int index) {
		if (index < getSource().size()) {
			return getSource().get(index);
		} else if (index == getSource().size()) {
			return total;
		} else throw new ArrayIndexOutOfBoundsException(index);
	}

	@Override
	public int size() {
		return getSource().size() + 1;
	}

	@Override
	public void clear() {
		getSource().clear();
	}

	@Override
	protected void sourceChanged(Change<? extends Product> c) {
		fireChange(c);
	}
	
	@Override
	public boolean remove(Object o) {
		return getSource().remove(o);
	}
}
