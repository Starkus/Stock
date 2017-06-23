package net.starkus.stock.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.AutoCompleteTextField;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.model.Payment;

public class PaymentDialogController extends DialogController {

	@FXML
	private AutoCompleteTextField clientField;

	@FXML
	private TextField ammountField;
	
	private Payment payment;
	
	
	private List<String> clientList;
	
	
	void populateEntries() {
		
		List<String> entries = clientField.getEntries();
		
		entries.clear();
		ClientBox.getClients().forEach(c -> entries.add(c.getName()));
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		populateEntries();
	}
	
	
	public Payment getPayment() {
		return payment;
	}
	
	
	boolean confirmClient() {
		
		AlertWrapper alert = new AlertWrapper(AlertType.CONFIRMATION)
				.setTitle("No encontrado")
				.setHeaderText("El cliente no coincide con ningun otro!")
				.setContentText("Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		// If OK is clicked
		if (result.isPresent() && result.get() == ButtonType.OK) {
			
			payment.setClient(clientField.getText());
			return true;
		}
		
		return false;
	}
	
	
	@FXML
	private void handleClientEntered() {
		
		ammountField.requestFocus();
	}
	
	@FXML
	private void handleOK() {
		
		payment = new Payment();
		
		payment.setClient(clientField.getText());
		
		if (!clientList.contains(payment.getClient())) {
			
			if (!confirmClient()) {
				return;
			}
		}
		
		
		String ammountStr = ammountField.getText();
		
		float ammount = 0;
		if (ammountStr.length() != 0 && ammountStr != null) {
			try {
				ammountField.setStyle("-fx-background-color: white;");
				ammount = Float.parseFloat(ammountStr);
			}
			catch (NumberFormatException e) {
				ammountField.setStyle("-fx-background-color: #ff7070;");
			}
		}
		
		payment.setBalance(ammount);
		payment.setCreationDate(LocalDateTime.now());
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		
		dialogStage.close();
	}
}
