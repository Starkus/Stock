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
import javafx.collections.transformation.SortedList;
import net.starkus.stock.util.ProductListAdapter;

public class Purchase extends Transaction {
	
	private final FloatProperty paid;
	private final ReadOnlyFloatWrapper total;
	
	private final ProductList productList;
	
	
	public Purchase() {
		super(TransactionType.PURCHASE);

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
	
	
	public void substractItemsFromStock(SortedList<Product> stock) {
		
		for (Product prod : productList.getProductData()) {
			
			Product prodInStock = BinarySearch.findProductByCode(prod.getCode(), stock);
			prodInStock.setQuantity(prodInStock.getQuantity() - prod.getQuantity());
		}
	}
	
	public void addToStock(SortedList<Product> stock) {
		
		for (Product prod : productList.getProductData()) {
			
			Product prodInStock = BinarySearch.findProductByCode(prod.getCode(), stock);
			prodInStock.setQuantity(prodInStock.getQuantity() + prod.getQuantity());
		}
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
}
