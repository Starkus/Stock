package net.starkus.stock.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class History {
	
	
	private static final ObservableList<Transaction> history = FXCollections.observableArrayList(
			t -> new Observable[] { t.cancelledProperty() }
			);
	
	
	public static ObservableList<Transaction> getHistory() {
		return history;
	}

}
