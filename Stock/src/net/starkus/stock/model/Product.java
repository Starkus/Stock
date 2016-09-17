package net.starkus.stock.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product implements Comparable<Product> {

	private final LongProperty code;
	private final StringProperty name;
	private final FloatProperty buyPrice;
	private final FloatProperty sellPrice;
	private final IntegerProperty quantity;

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
	public Product(long code, String name, float buyPrice, float sellPrice, int available) {
		
		this.code = new SimpleLongProperty(code);
		this.name = new SimpleStringProperty(name);

		this.buyPrice = new SimpleFloatProperty(buyPrice);
		this.sellPrice = new SimpleFloatProperty(sellPrice);
		
		this.quantity = new SimpleIntegerProperty(available);
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
	
	
	public void setQuantity(int n) {
		this.quantity.set(n);
	}
	
	public int getQuantity() {
		return this.quantity.get();
	}
	
	public IntegerProperty quantityProperty() {
		return this.quantity;
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
