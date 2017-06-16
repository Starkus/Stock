package net.starkus.stock.model;

public class Payment extends Transaction {
	
	public Payment() {
		super(TransactionType.PAYMENT);
	}

	@Override
	public void _do() {
		
		CashBox.put(getBalance());
	}

	@Override
	public void undo() {
		
		CashBox.substract(getBalance());
	}
}
