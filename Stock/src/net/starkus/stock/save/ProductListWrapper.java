package net.starkus.stock.save;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import net.starkus.stock.model.Product;

/**
 * Wrapper to save product database.
 * 
 * @author starkus
 *
 */

//@XmlRootElement(name = "save")
public class ProductListWrapper {

	private List<Product> products;
	
	@XmlElement(name = "product")
	public List<Product> getProducts() {
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}

}
