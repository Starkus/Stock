package net.starkus.stock.model;

import java.util.Collection;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.starkus.stock.save.ProductListWrapper;

public class ProductList {
	
	private ObservableList<Product> productList = FXCollections.observableArrayList(p -> new Observable[] { p.subtotalProperty() });

	public ProductList() {}
	
	public ObservableList<Product> getProductData() {
		return productList;
	}
	
	public void add(Product product) {
		productList.add(product);
	}
	public void addAll(Collection<? extends Product> c) {
		productList.addAll(c);
	}
	
	public void addOrStack(Product toadd) {
			
		if (productList.contains(toadd)) {
			
			for (Product sourcep : productList) {
				
				if (sourcep.equals(toadd)) {
					
					sourcep.setQuantity(sourcep.getQuantity() + toadd.getQuantity());
					return;
				}
			}
		}
		else {
			add(toadd);
		}
	}
	
	public boolean contains(Product p) {
		return productList.contains(p);
	}
	
	public void forEach(Consumer<? super Product> action) {
		productList.forEach(action);
	}
	
	public float getTotal() {
		float total = 0;
		
		for (Product p : productList) {
			total += p.getPrice() * p.getQuantity();
		}
		
		return total;
	}
	
	@XmlElement(name = "products")
	public ProductListWrapper getProducts() {
		ProductListWrapper wrapper = new ProductListWrapper();
		wrapper.setProducts(productList);
		
		return wrapper;
	}
	
	public void setProducts(ProductListWrapper wrapper) {
		if (wrapper == null || wrapper.getProducts() == null)
			return;
		
		productList.addAll(wrapper.getProducts());
	}
	
	public void addListener(ListChangeListener<Product> listener) {
		this.productList.addListener(listener);
	}

}
