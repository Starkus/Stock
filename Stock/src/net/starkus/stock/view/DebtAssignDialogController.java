package net.starkus.stock.view;

import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.Client;

public class DebtAssignDialogController extends DialogController {
	
	@FXML
	private AutoCompleteTextField clientField;
	
	
	private Client client;
	
	
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
		
		for (Client c : mainApp.getClients()) {
			clientField.getEntries().add(c.getName());
		}
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		populateEntries();
	}
	
	
	public Client getClient() {
		return client;
	}
	
	
	void findSpecifiedClient() {
		
		String name = clientField.getText();
		
		for (Client c : mainApp.getSortedClients()) {
			if (c.getName().equals(name)) {
				client = c;
				break;
			}
		}
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
	
	
	void confirmCreatingNewClient() {
	
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("No encontrado");
		alert.setHeaderText("Cliente no encontrado!");
		alert.setContentText("Desea crearlo ahora?");
		DialogPane pane = alert.getDialogPane();
		pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
		
		Optional<ButtonType> result = alert.showAndWait();
		// If OK is clicked
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Create client with entered name
			Client n = new Client(clientField.getText());
			mainApp.getClients().add(n);
			// Reference it in "client" variable
			client = n;
			
			System.out.println("Client \"" + n.getName() + "\" created.");
		}
	}
	
	
	@FXML
	private void handleOK() {
		
		findSpecifiedClient();
		
		// If given client isn't found
		if (client == null) {
			// Ask user if he wants to create it
			confirmCreatingNewClient();
		}
		
		// By now if user created it, the new client is referenced in "client" var
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		
		client = null;
		
		dialogStage.close();
	}
}
