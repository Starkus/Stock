package net.starkus.stock.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class ProductTransaction extends Transaction {
	
	/*
	 * This class is solely a model to display transaction products
	 * in the transaction table.
	 */
	
	private final FloatProperty quantity;
	private final FloatProperty subtotal;

	public ProductTransaction() {
		super(TransactionType.PRODUCT);
		
		quantity = new SimpleFloatProperty();
		subtotal = new SimpleFloatProperty();
	}

	@Override
	public void _do() {
	}

	@Override
	public void undo() {
	}

	public float getQuantity() {
		return quantity.get();
	}
	public void setQuantity(float value) {
		quantity.set(value);
	}
	public FloatProperty quantityProperty() {
		return quantity;
	}

	public float getSubtotal() {
		return subtotal.get();
	}
	public void setSubtotal(float value) {
		subtotal.set(value);
	}
	public FloatProperty subtotalProperty() {
		return subtotal;
	}

	@Override
	public FloatProperty balanceProperty() {
		return null;
	}
}
