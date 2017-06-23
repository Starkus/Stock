package net.starkus.stock.view;

import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.AutoCompleteTextField;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.ClientBox;

public class DebtAssignDialogController extends DialogController {
	
	@FXML
	private AutoCompleteTextField clientField;
	@FXML
	private Label currentDebtLabel;
	
	
	String client;
	
	
	public DebtAssignDialogController() {
	}
	
	@FXML
	private void initialize() {
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				clientField.requestFocus();
			}
		});
		
		clientField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				if (ClientBox.getClients().contains(newValue)) {
					float balance = new Client(newValue).calculateBalance();
					
					currentDebtLabel.setText("Deuda actual: " + Float.toString(balance));
				}
			}
		});
	}
	
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
	
	
	public String getClient() {
		return client;
	}
	
	
	@FXML
	private void handleClientEntered() {
		
		String text = clientField.getText();
		
		// If no text, do nothing
		if (text.length() == 0 || text == null) {
			
			return;
		}
		
		// Everything else checked, just procceed
		handleOK();
	}
	
	
	void confirmClient() {
	
		AlertWrapper alert = new AlertWrapper(AlertType.CONFIRMATION)
				.setTitle("No encontrado")
				.setHeaderText("El cliente no coincide con ningun otro!")
				.setContentText("Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		// If OK is clicked
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Create client with entered name
			// Reference it in "client" variable
			client = clientField.getText();
			
			dialogStage.close();
		}
	}
	
	
	@FXML
	private void handleOK() {
		
		client = clientField.getText();
		
		// If given client isn't found
		if (!ClientBox.getClients().contains(client)) {
			// Ask user if he wants to create it
			confirmClient();
		}
		else {
			
			dialogStage.close();
		}
	}
	
	@FXML
	private void handleCancel() {
		
		client = null;
		
		dialogStage.close();
	}
}
