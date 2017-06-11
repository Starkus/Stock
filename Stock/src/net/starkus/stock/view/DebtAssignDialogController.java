package net.starkus.stock.view;

import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.History;

public class DebtAssignDialogController extends DialogController {
	
	@FXML
	private AutoCompleteTextField clientField;
	
	
	private List<String> clientList;
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
	}
	
	void populateEntries() {
		
		clientList = Client.getClientsFromHistory(History.getHistory());
		clientField.getEntries().addAll(clientList);
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
			
			System.out.println("Client \"" + client + "\" created.");
		}
	}
	
	
	@FXML
	private void handleOK() {
		
		client = clientField.getText();
		
		// If given client isn't found
		if (!clientList.contains(client)) {
			// Ask user if he wants to create it
			confirmClient();
		}
		
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		
		client = null;
		
		dialogStage.close();
	}
}
