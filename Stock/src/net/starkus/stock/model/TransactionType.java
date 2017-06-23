package net.starkus.stock.model;

public enum TransactionType {
	SALE("Venta"), PURCHASE("Compra"), PAYMENT("Pago"), LEGACYDEBT("Deuda legado"), PRODUCT("");
	
	
	private final String prettyName;
	
	TransactionType(String name) {
		this.prettyName = name;
	}
	
	public String prettyName() {
		return prettyName;
	}
	
	@Override
	public String toString() {
		return prettyName;
	}
	
	public static TransactionType[] publicValues() {
		return new TransactionType[] {
				SALE, PURCHASE, PAYMENT, LEGACYDEBT
		};
	}
}
