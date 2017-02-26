package net.starkus.stock.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CashBox {

	private static float cash = 0f;
	private static float sessionBalance = 0f;
	
	private static final StringProperty cashProperty = new SimpleStringProperty();
	private static final StringProperty sessionBalanceProperty = new SimpleStringProperty();
	
	
	public static float getCash() {
		return cash;
	}

	public static void setCash(float n) {
		cash = n;

		cashProperty.set(Float.toString(cash));
	}
	
	public static StringProperty cashProperty() {
		return cashProperty;
	}
	
	
	public static float getSessionBalance() {
		return sessionBalance;
	}
	
	public static StringProperty sessionBalanceProperty() {
		return sessionBalanceProperty;
	}
	
	
	public static void substract(float n) {
		
		cash -= n;
		sessionBalance -= n;

		cashProperty.set(Float.toString(cash));
		
		String balance = Float.toString(sessionBalance);
		if (sessionBalance > 0)
			balance = "+" + balance;
		sessionBalanceProperty.set(balance);
	}
	
	public static void put(float n) {
		
		cash += n;
		sessionBalance += n;

		cashProperty.set(Float.toString(cash));
		sessionBalanceProperty.set(Float.toString(sessionBalance));
	}

}
