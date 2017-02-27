package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.Client;

public class ClientOverviewController extends DialogController {
	
	@FXML
	private TableView<Client> clientTable;
	@FXML
	private TableColumn<Client, String> nameColumn;
	@FXML
	private TableColumn<Client, Number> balanceColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField balanceField;
	
	private Client selectedClient;
	
	
	public ClientOverviewController() {
	}
	
	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
		
		clientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
				updateFields(newValue));
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);

		clientTable.setItems(mainApp.getClients());
	}
	
	void updateFields(Client newClient) {
		
		selectedClient = newClient;
		
		nameField.setText(newClient.getName());
		balanceField.setText(Float.toString(newClient.getBalance()));
	}
	
	@FXML
	private void updateClientData() {
		
		String name = nameField.getText();
		String bstr = balanceField.getText();
		
		if (name.isEmpty() || bstr.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Campos vacíos");
			alert.setContentText("Uno o mas campos estan vacíos. Por favor complete.");
			
			alert.showAndWait();
			return;
		}
		
		try {
			
			float balance = Float.parseFloat(bstr);
			
			selectedClient.setName(name);
			selectedClient.setBalance(balance);
		}
		catch (NumberFormatException e) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Numero invalido!");
			alert.setHeaderText("Error de sintaxis");
			alert.setContentText("No se ha podido interpretar el numero del balance. Por favor verifique.");
			
			alert.showAndWait();
			return;
		}
	}
	
	@FXML
	void handleAddClient() {
		
		Client n = new Client("");

		mainApp.getClients().add(n);
		clientTable.getSelectionModel().select(n);
		
		nameField.requestFocus();
	}
	
}
