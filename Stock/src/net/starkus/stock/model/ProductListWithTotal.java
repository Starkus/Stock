package net.starkus.stock.model;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

public class ProductListWithTotal extends TransformationList<Product, Product>{
	
	private Product total;

	public ProductListWithTotal(ObservableList<? extends Product> source) {
		super(source);
		
		total = new ProductTotal(source);
	}

	@Override
	protected void sourceChanged(Change<? extends Product> c) {
		fireChange(c);
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
		// FIXME
		//if (getSource().size() == 0)
		//	return 0;
		return getSource().size() + 1;
	}
	
	@Override
	public boolean remove(Object o) {
		return getSource().remove(o);
	}

}
