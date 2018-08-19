package net.starkus.stock.model;

public enum TransactionType {
	SALE("Venta"), PURCHASE("Compra"), PAYMENT("Pago"), LEGACYDEBT("Deuda legado"), @Deprecated PRODUCT("");
	
	
	private final String prettyName;
	
	TransactionType(String name) {
		this.prettyName = name;
	}
	
	public boolean hasProductList() {
		return this == SALE || this == PURCHASE;
	}
	
	public String prettyName() {
		return prettyName;
	}
	
	@Override
	public String toString() {
		return prettyName;
	}
	
	@Deprecated
	public static TransactionType[] publicValues() {
		return new TransactionType[] {
				SALE, PURCHASE, PAYMENT, LEGACYDEBT
		};
	}
}
