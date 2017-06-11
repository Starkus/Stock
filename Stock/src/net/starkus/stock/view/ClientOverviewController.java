package net.starkus.stock.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.History;
import net.starkus.stock.util.ExceptionUtil;
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
	private SortedList<Client> sortedClientList;
	
	
	public ClientOverviewController() {
	}
	
	@FXML
	private void initialize() {
		
		EventHandler<MouseEvent> ev = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (!event.getButton().equals(MouseButton.PRIMARY))
					return;
				
				if (event.getClickCount() == 2 && clientTable.getSelectionModel().getSelectedIndex() != -1) {
					
					try {
						HistoryViewerController controller = Dialog.historyViewerDialog.init();
						
						controller.setFilterClient(clientTable.getSelectionModel().getSelectedItem().getName());
						
						controller.showAndWait();
						
					} catch (IOException e) {
						
						ExceptionUtil.printStackTrace(e);
					}
				}
			}
		};
		
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColumn.setCellFactory(column -> {
			TableCell<Client, String> cell = new TableCell<Client, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					if (empty || item == null) {
						setText("");
					}
					else {
						setText(item);
					}
					setGraphic(null);
				}
			};
			cell.setOnMouseClicked(ev);
			
			return cell;
		});
		
		balanceColumn.setCellValueFactory(cellData -> cellData.getValue().getObservableBalance());
		balanceColumn.setCellFactory(column -> {
			TableCell<Client, Number> cell = new TableCell<Client, Number>() {
				@Override
				protected void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					
					if (empty || item == null) {
						setText("");
					}
					else {
						setText(item.toString());
					}
					setGraphic(null);
				}
			};
			cell.setOnMouseClicked(ev);
			
			return cell;
		});
		
		clientList = FXCollections.observableArrayList(c -> new Observable[] { c.getTransactions() } );
		
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
					
					ExceptionUtil.printStackTrace(e);
				}
			}
		});
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				filterField.requestFocus();
			}
		});
		
		
		for (String name : Client.getClientsFromHistory(History.getHistory())) {
			if (name != null)
				clientList.add(new Client(name));
		}
		
		filteredClientList = new FilteredList<Client>(clientList);
		sortedClientList = filteredClientList.sorted();

		sortedClientList.comparatorProperty().bind(clientTable.comparatorProperty());

		clientTable.setItems(sortedClientList);
	}
	
	
	@FXML
	void filterByClient() {
		
		List<String> nameList = new ArrayList<>();
		for (Client c : clientList) nameList.add(c.getName());
		List<String> filteredNameList = SearchEngine.filterObjects(filterField.getText(), clientList.listIterator(),
				c -> c.getName());
		
		if (filterField.getText().isEmpty()) {
			filteredClientList.setPredicate(c -> true);
		}
		else {
			filteredClientList.setPredicate(c -> filteredNameList.contains(c.getName()));
		}		
			
		//clientTable.setItems(filteredClientList);
	}
	
	@FXML
	void handleOK() {
		
		dialogStage.close();
	}
	
}
