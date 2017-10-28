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
	private final FloatProperty price;
	private final FloatProperty quantity;
	private final FloatProperty subtotal;

	/**
	 * Default constructor
	 */
	public Product() {
		this(0, null, -1f);
	}
	
	public Product(long code, String name, float price) {
		this(code, name, price, 0);
	}
	public Product(long code, String name, float price, float quantity) {
		
		this.code = new SimpleLongProperty(code);
		this.name = new SimpleStringProperty(name);

		this.price = new SimpleFloatProperty(price);
		
		this.quantity = new SimpleFloatProperty(quantity);
		
		this.subtotal = new SimpleFloatProperty();
		this.subtotal.bind(Bindings.multiply(this.quantity, this.price));
	}
	
	
	public Product(Product p) {
		
		this(p.getCode(), p.getName(), p.getPrice(), p.getQuantity());
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
	
	
	public void setPrice(float newPrice) {
		this.price.set(newPrice);
	}
	
	public float getPrice() {
		return this.price.get();
	}
	
	public FloatProperty priceProperty() {
		return this.price;
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
	
	
	public float getSubtotal() {
		float s = this.subtotal.get();
		return Math.round(s * 10f) / 10f;
	}
	
	public FloatProperty subtotalProperty() {
		return this.subtotal;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj.getClass() == Product.class)
			return ((Product) obj).getName().equals(this.getName());
		
		return super.equals(obj);
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
		return new Product(code.get(), name.get(), price.get(), quantity.get());
	}

}
