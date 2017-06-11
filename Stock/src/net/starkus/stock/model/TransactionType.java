package net.starkus.stock.model;

public enum TransactionType {
	PURCHASE("Compra"), PAYMENT("Pago"), LEGACYDEBT("Deuda legado");
	
	
	private final String name;
	
	TransactionType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
