package net.starkus.stock.view;

import java.io.IOException;

import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.util.SaveUtil;

public class ClientOverviewController extends DialogController {
	
	@FXML
	private TableView<Client> clientTable;
	@FXML
	private TableColumn<Client, String> nameColumn;
	@FXML
	private TableColumn<Client, Number> balanceColumn;

	@FXML
	private AutoCompleteTextField filterField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField balanceField;
	
	private Client selectedClient;

	private FilteredList<Client> filteredClientList;
	
	
	public ClientOverviewController() {
	}
	
	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
		
		clientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
				updateFields(newValue));
		
		clientTable.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					
					AlertWrapper alert = new AlertWrapper(AlertType.CONFIRMATION)
							.setTitle("Eliminar cliente")
							.setHeaderText("¿Seguro que desea eliminar el cliente seleccionado?");
					
					alert.showAndWait();
					
					if (alert.getResult() == ButtonType.OK)
						clientTable.getItems().remove(clientTable.getSelectionModel().getSelectedIndex());
				}
			}
		});
		
		clientTable.setOnMouseClicked(event -> {
			if (!event.getButton().equals(MouseButton.PRIMARY))
				return;
			
			if (event.getClickCount() == 2 && clientTable.getSelectionModel().getSelectedIndex() != -1) {
				
				try {
					HistoryViewerController controller = Dialog.historyViewerDialog.init();
					
					String client = clientTable.getSelectionModel().getSelectedItem().getName();
					controller.setFilterClient(client);
					
					controller.showAndWait();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		filterField.setAutoProc(true);
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);

		clientTable.setItems(mainApp.getClients());
		
		for (Client c : mainApp.getClients()) {
			filterField.getEntries().add(c.getName());
		}
	}
	
	void updateFields(Client newClient) {
		
		selectedClient = newClient;
		
		nameField.setText(newClient.getName());
		balanceField.setText(Float.toString(newClient.getBalance()));
	}
	
	@FXML
	void filterByClient() {
		
		if (filterField.getText().isEmpty()) {
			filteredClientList.clear();
			filteredClientList.addAll(mainApp.getClients());
		}
		else {
			filteredClientList = mainApp.getClients().filtered(c -> filterField.getResults().contains(c.getName()));
		}		
			
		clientTable.setItems(filteredClientList);
	}
	
	@FXML
	private void updateClientData() {
		
		String name = nameField.getText();
		String bstr = balanceField.getText();
		
		if (name.isEmpty() || bstr.isEmpty()) {
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR)
					.setTitle("ERROR")
					.setHeaderText("Campos vacíos")
					.setContentText("Uno o mas campos estan vacíos. Por favor complete.");
			
			alert.showAndWait();
			return;
		}
		
		try {
			
			float balance = Float.parseFloat(bstr);
			
			selectedClient.setName(name);
			selectedClient.setBalance(balance);
		}
		catch (NumberFormatException e) {
			
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR)
					.setTitle("Numero invalido!")
					.setHeaderText("Error de sintaxis")
					.setContentText("No se ha podido interpretar el numero del balance. Por favor verifique.");
			
			alert.showAndWait();
			return;
		}
		
		SaveUtil.saveToFile(mainApp.getSavefile());
		
		AlertWrapper alert = new AlertWrapper(AlertType.INFORMATION)
				.setTitle("Listo!")
				.setContentText("El cliente se ha modificado correctamente.");
		
		alert.showAndWait();
	}
	
	@FXML
	void handleAddClient() {
		
		Client n = new Client("");

		mainApp.getClients().add(n);
		clientTable.getSelectionModel().select(n);
		
		nameField.requestFocus();
	}
	
	@FXML
	void handleOK() {
		
		dialogStage.close();
	}
	
}
