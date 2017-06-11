package net.starkus.stock.model;

public class Payment extends Transaction {
	
	public Payment() {
		super(TransactionType.PAYMENT);
	}
}
