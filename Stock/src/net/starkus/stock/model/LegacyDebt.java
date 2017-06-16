package net.starkus.stock.model;

public class LegacyDebt extends Transaction {

	public LegacyDebt() {
		super(TransactionType.LEGACYDEBT);
	}

	@Override
	public void _do() {
	}

	@Override
	public void undo() {
	}
}
