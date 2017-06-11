package net.starkus.stock.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Admin {
	
	private static final BooleanProperty adminMode = new SimpleBooleanProperty(false);
	
	
	public static void setAdmin(boolean b) {
		adminMode.set(b);
	}
	
	public static boolean getAdmin() {
		return adminMode.get();
	}
	
	public static BooleanProperty adminProperty() {
		return adminMode;
	}

}
