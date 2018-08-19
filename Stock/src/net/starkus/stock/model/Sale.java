package net.starkus.stock.model;

import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.starkus.stock.util.ProductListAdapter;

public class Sale extends Transaction {
	
	private final FloatProperty paid;
	private final ReadOnlyFloatWrapper total;
	
	private final ProductList productList;
	
	
	public Sale() {
		super(TransactionType.SALE);

		paid = new SimpleFloatProperty();
		total = new ReadOnlyFloatWrapper();
		
		productList = new ProductList();
		productList.addListener(new ListChangeListener<Product>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Product> c) {
				total.set(productList.getTotal());
			}
		});
		
		total.set(productList.getTotal());
		
		balanceProperty().bind(Bindings.subtract(0, Bindings.subtract(total, paid)));
	}
	

	public Sale(Sale currentSale) {
		this();
		
		client.set(currentSale.getClient());
		paid.set(currentSale.getPaid());
		productList.getProductData().setAll(currentSale.getProductData());
		creationDate.set(currentSale.getCreationDate());
		cancelled.set(currentSale.getCancelled());
	}


	@XmlJavaTypeAdapter(ProductListAdapter.class)
	public ObservableList<Product> getProductData() {
		return productList.getProductData();
	}
	public void setProductData(ObservableList<Product> l) {
		productList.getProductData().clear();
		productList.getProductData().addAll(l);
	}
	
	public void add(Product product) {
		productList.add(product);
	}
	public void addAll(Collection<? extends Product> c) {
		productList.addAll(c);
	}
	
	public void addOrStack(Product toadd) {

		productList.addOrStack(toadd);
	}

	
	public float getPaid() {
		return paid.get();
	}
	public void setPaid(float f) {
		paid.set(f);
	}
	public FloatProperty paidProperty() {
		return paid;
	}

	
	public float getTotal() {
		return total.get();
	}
	public void setTotal(float f) {
		total.set(f);
	}
	public ReadOnlyFloatProperty totalProperty() {
		return total.getReadOnlyProperty();
	}
	
	@Override
	public void setBalance(float f) {
		paid.set(total.get() + f);
	}


	@Override
	public void _do() {
		
		ProductBox.substractFromStock(getProductData());
		
		CashBox.put(getPaid());
	}


	@Override
	public void undo() {
		
		ProductBox.addToStock(getProductData());
		
		CashBox.substract(getPaid());
	}
}
