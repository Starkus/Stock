package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.util.PasswordUtils;

public class SetCashDialogController extends DialogController {

	@FXML
	private TextField passwordField;
	@FXML
	private TextField cashField;
	
	@FXML
	void initialize() {
	
		cashField.setText(Float.toString(CashBox.getCash()));
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
			
			alert.showAndWait();
			return;
		}
		
		try {
			float cash = Float.parseFloat(cashField.getText());
			CashBox.setCash(cash);
			
			dialogStage.close();
			return;
			
		} catch (NumberFormatException e) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Numero invalido!");
			alert.setHeaderText("Error de sintaxis");
			alert.setContentText("No se ha podido interpretar el numero del balance. Por favor verifique.");
			
			alert.showAndWait();
			
			cashField.requestFocus();
			cashField.selectAll();
			
			return;
		}
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
}
