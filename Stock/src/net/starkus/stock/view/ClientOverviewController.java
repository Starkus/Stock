package net.starkus.stock.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.util.SearchEngine;

public class ClientOverviewController extends DialogController {
	
	@FXML
	private TableView<Client> clientTable;
	@FXML
	private TableColumn<Client, String> nameColumn;
	@FXML
	private TableColumn<Client, Number> balanceColumn;

	@FXML
	private TextField filterField;

	private ObservableList<Client> clientList; 
	private FilteredList<Client> filteredClientList;
	
	
	public ClientOverviewController() {
	}
	
	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		
		balanceColumn.setCellFactory(column -> {
			return new TableCell<Client, Number>() {
				@Override
				protected void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					
					Client client = (Client) getTableRow().getItem();
					
					if (empty || client == null || client.getName().isEmpty()) {
						setText("");
					}
					else {
						setText(Float.toString(client.calculateBalance(mainApp)));
					}
					
					setGraphic(null);
				}
			};
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
		
		clientList = FXCollections.observableArrayList();
		
		filterField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				filterByClient();
			}
		});
		
		filterField.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				try {
					HistoryViewerController controller = Dialog.historyViewerDialog.init();
					
					String client = clientTable.getItems().get(0).getName();
					controller.setFilterClient(client);
					
					controller.showAndWait();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				filterField.requestFocus();
			}
		});
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		for (String name : Client.getClientsFromHistory(mainApp.getHistory())) {
			if (name != null)
				clientList.add(new Client(name));
		}
		
		filteredClientList = new FilteredList<Client>(clientList);

		clientTable.setItems(filteredClientList);
	}
	
	@FXML
	void filterByClient() {
		
		List<String> nameList = new ArrayList<>();
		for (Client c : clientList) {nameList.add(c.getName()); System.out.println(c.getName());}
		List<String> filteredNameList = SearchEngine.filterList(filterField.getText(), nameList);
		
		if (filterField.getText().isEmpty()) {
			filteredClientList = clientList.filtered(c -> true);
		}
		else {
			filteredClientList = clientList.filtered(c -> filteredNameList.contains(c.getName()));
		}		
			
		clientTable.setItems(filteredClientList);
	}
	
	@FXML
	void handleOK() {
		
		dialogStage.close();
	}
	
}
