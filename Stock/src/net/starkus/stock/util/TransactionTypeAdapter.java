package net.starkus.stock.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.starkus.stock.save.TransactionWrapper.TransactionType;

public class TransactionTypeAdapter extends XmlAdapter<String, TransactionType> {

	@Override
	public TransactionType unmarshal(String v) throws Exception {
		switch (v) {
		case "LEGACYDEBT":
			return TransactionType.LEGACYDEBT;
		case "PAYMENT":
			return TransactionType.PAYMENT;
		case "PURCHASE":
			return TransactionType.PURCHASE;
		}
		
		return null;
	}

	@Override
	public String marshal(TransactionType v) throws Exception {
		return v.toString();
	}

}