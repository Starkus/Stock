package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.util.PasswordUtils;

public class PasswordDialogController extends DialogController {
	
	@FXML
	TextField passwordField;
	
	private boolean passwordCorrect = false;
	
	
	public boolean wasPasswordCorrect() {
		return passwordCorrect;
	}
	
	@FXML
	private void handleOK() {
		
		String pass = passwordField.getText();
		pass = PasswordUtils.encodePassword(pass);
		
		if (!pass.equals(mainApp.getPassword())) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error de autenticación");
			alert.setHeaderText("Contraseña incorrecta");
			alert.setContentText("Verifique la contraseña ingresada.");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
			
			alert.showAndWait();
			return;
		}
		
		passwordCorrect = true;
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		
		dialogStage.close();
	}

}
