package net.starkus.stock.model;


public class CashBox {
	
	// TODO - Kinda get this working and push out version 0.1 or 0.01!
	
	private static float cash = 0f;
	private static float sessionBalance = 0f;
	
	
	public static float getCash() {
		
		return cash;
	}
	
	public static float getSessionBalance() {
		return sessionBalance;
	}
	
	public static void setCash(float n) {
		
		cash = n;
	}
	
	public static void substract(float n) {
		
		setCash(getCash() - n);
		sessionBalance -= n;
	}
	
	public static void put(float n) {
		
		setCash(getCash() + n);
		sessionBalance += n;
	}

}
