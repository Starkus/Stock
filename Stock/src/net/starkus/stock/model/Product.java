package net.starkus.stock.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product implements Comparable<Product> {

	private final LongProperty code;
	private final StringProperty name;
	private final FloatProperty buyPrice;
	private final FloatProperty sellPrice;
	private final FloatProperty quantity;
	private final FloatProperty buySubtotal;
	private final FloatProperty sellSubtotal;

	/**
	 * Default constructor
	 */
	public Product() {
		this(0, null, 2, 3.4f);
	}
	
	/*
	 * Constructor with data input
	 * 
	 * @param code
	 * @param name
	 * @param buyPrice
	 * @param sellPrice
	 */
	public Product(long code, String name, float buyPrice, float sellPrice) {
		this(code, name, buyPrice, sellPrice, 0);
	}
	public Product(long code, String name, float buyPrice, float sellPrice, float quantity) {
		
		this.code = new SimpleLongProperty(code);
		this.name = new SimpleStringProperty(name);

		this.buyPrice = new SimpleFloatProperty(buyPrice);
		this.sellPrice = new SimpleFloatProperty(sellPrice);
		
		this.quantity = new SimpleFloatProperty(quantity);
		
		this.buySubtotal = new SimpleFloatProperty();
		this.buySubtotal.bind(Bindings.multiply(this.quantity, this.buyPrice));
		
		this.sellSubtotal = new SimpleFloatProperty();
		this.sellSubtotal.bind(Bindings.multiply(this.quantity, this.sellPrice));
	}
	
	
	public void setCode(long n) {
		this.code.set(n);
	}
	
	public long getCode() {
		return this.code.get();
	}
	
	public LongProperty codeProperty() {
		return this.code;
	}
	
	
	public void setName(String newName) {
		this.name.set(newName);
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	
	public void setBuyPrice(float newPrice) {
		this.buyPrice.set(newPrice);
	}
	
	public float getBuyPrice() {
		return this.buyPrice.get();
	}
	
	public FloatProperty buyPriceProperty() {
		return this.buyPrice;
	}
	
	
	public void setSellPrice(float newPrice) {
		this.sellPrice.set(newPrice);
	}
	public void setSellPriceFlat(float n) {
		this.setSellPrice(n); 
	}
	
	public void setSellPriceMult(float mult) {
		this.sellPrice.set(getBuyPrice() * mult);
	}
	
	public float getSellPrice() {
		return this.sellPrice.get();
	}
	
	public FloatProperty sellPriceProperty() {
		return this.sellPrice;
	}
	
	
	public void setQuantity(float q) {
		this.quantity.set(q);
	}
	
	public float getQuantity() {
		return this.quantity.get();
	}
	
	public FloatProperty quantityProperty() {
		return this.quantity;
	}

	
	public float getBuySubtotal() {
		return this.buySubtotal.get();
	}
	
	public FloatProperty buySubtotalProperty() {
		return this.buySubtotal;
	}
	
	
	public float getSellSubtotal() {
		float s = this.sellSubtotal.get();
		return Math.round(s * 10f) / 10f;
	}
	
	public FloatProperty sellSubtotalProperty() {
		return this.sellSubtotal;
	}

	@Override
	public int compareTo(Product o) {
		if (o.getCode() > this.getCode())
			return -1;
		
		if (o.getCode() < this.getCode())
			return 1;
		
		return 0;
	}
	
	public Product copy() {
		return new Product(code.get(), name.get(), buyPrice.get(), sellPrice.get(), quantity.get());
	}

}
