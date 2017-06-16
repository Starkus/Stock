package net.starkus.stock.model;

public enum TransactionType {
	SALE("Venta"), PURCHASE("Compra"), PAYMENT("Pago"), LEGACYDEBT("Deuda legado");
	
	
	private final String prettyName;
	
	TransactionType(String name) {
		this.prettyName = name;
	}
	
	public String prettyName() {
		return prettyName;
	}
}
