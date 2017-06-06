package net.starkus.stock.view;

import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.starkus.stock.model.Payment;

public class PaymentDialogController extends DialogController {

	@FXML
	private TextField clientField;

	@FXML
	private TextField ammountField;
	
	private Payment payment;
	
	
	public Payment getPayment() {
		return payment;
	}
	
	
	@FXML
	private void handleOK() {
		
		payment = new Payment();
		
		String clientEntered = clientField.getText();
		payment.setClient(clientEntered);
		
		
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
