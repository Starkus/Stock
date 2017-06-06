package net.starkus.stock.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import javafx.collections.ObservableList;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;

public class ProductListAdapter extends XmlAdapter<ObservableList<Product>, ProductList> {

	@Override
	public ProductList unmarshal(ObservableList<Product> v) throws Exception {
		ProductList p = new ProductList();
		p.getProductData().addAll(v);
		return p;
	}

	@Override
	public ObservableList<Product> marshal(ProductList v) throws Exception {
		return v.getProductData();
	}

}
