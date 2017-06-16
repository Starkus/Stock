package net.starkus.stock.save;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.starkus.stock.model.TransactionType;

public class TransactionTypeAdapter extends XmlAdapter<String, TransactionType> {

	@Override
	public TransactionType unmarshal(String v) throws Exception {
		
		return TransactionType.valueOf(v);
	}

	@Override
	public String marshal(TransactionType v) throws Exception {
		
		return v.name();
	}

}