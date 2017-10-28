package net.starkus.stock.model;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.MainApp;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class AlertWrapper {
	
	private Alert actualAlert;
	
	public AlertWrapper(AlertType type) {
		
		actualAlert = new Alert(type);
		
		DialogPane pane = actualAlert.getDialogPane();
		pane.getStylesheets().add(MainApp.class.getResource("Flatus_alert.css").toExternalForm());
	}
	
	
	
	public AlertWrapper setTitle(String s) {
		actualAlert.setTitle(s);
		return this;
	}
	
	public AlertWrapper setHeaderText(String s) {
		actualAlert.setHeaderText(s);
		return this;
	}
	
	public AlertWrapper setContentText(String s) {
		actualAlert.setContentText(s);
		return this;
	}
	
	
	
	public void show() {
		actualAlert.show();
	}
	
	public Optional<ButtonType> showAndWait() {
		return actualAlert.showAndWait();
	}
	
	public ButtonType getResult() {
		return actualAlert.getResult();
	}

}
