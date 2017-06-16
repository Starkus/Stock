package net.starkus.stock.model;

public class Purchase extends Sale {

	public Purchase() {
		super();
		
		this.type.set(TransactionType.PURCHASE);
	}
	
	@Override
	public void _do() {

		CashBox.substract(getTotal());
		
		ProductBox.addToStock(getProductData());
	}
	
	@Override
	public void undo() {

		CashBox.put(getTotal());
		
		ProductBox.substractFromStock(getProductData());
	}
	
	@Override
	public void setBalance(float f) {}
}
