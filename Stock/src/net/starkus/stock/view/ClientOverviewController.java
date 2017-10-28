package net.starkus.stock.view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import net.starkus.stock.MainApp;
import net.starkus.stock.control.MainToolbar;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.util.SearchEngine;

public class ClientOverviewController extends DialogController {
	
	@FXML
	private TableView<Client> clientTable;
	@FXML
	private TableColumn<Client, String> nameColumn;
	@FXML
	private TableColumn<Client, Number> balanceColumn;
	
	@FXML
	private MainToolbar toolBar;

	private TextField searchField;
	
	private FilteredList<Client> filteredClientList;
	private SortedList<Client> sortedClientList;
	
	
	@FXML
	private void initialize() {
		
		searchField = toolBar.getSearchField();
		
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				filterByClient();
			}
		});
		
		searchField.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				mainApp.getRootLayout().selectTab(1);
			}
		});
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				searchField.requestFocus();
			}
		});
		
		
		filteredClientList = new FilteredList<Client>(ClientBox.getClients());
		sortedClientList = filteredClientList.sorted();

		sortedClientList.comparatorProperty().bind(clientTable.comparatorProperty());

		clientTable.setItems(sortedClientList);
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		mainApp.getRootLayout().getSearchFields().add(searchField);
		
		EventHandler<MouseEvent> ev = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (!event.getButton().equals(MouseButton.PRIMARY))
					return;
				
				if (event.getClickCount() == 2 && clientTable.getSelectionModel().getSelectedIndex() != -1) {
					
					int index = RootLayoutController.MANAGE_TAB_INDEX;
					
					String name = clientTable.getSelectionModel().getSelectedItem().getName();
					
					mainApp.getRootLayout().selectTab(index);
					mainApp.getRootLayout().getSearchFields().get(index).setText(name);
				}
			}
		};

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColumn.setCellFactory(column -> {
			SimpleTableCell<Client, String> cell = new SimpleTableCell<Client, String>();
			cell.setOnMouseClicked(ev);
			
			return cell;
		});

		balanceColumn.setCellValueFactory(cellData -> cellData.getValue().getObservableBalance());
		balanceColumn.setCellFactory(column -> {
			SimpleTableCell<Client, Number> cell = new SimpleTableCell<Client, Number>();
			cell.setOnMouseClicked(ev);
			
			return cell;
		});
	}
	
	
	@FXML
	void filterByClient() {
		
		List<String> nameList = new ArrayList<>();
		
		ClientBox.getClients().forEach(c -> nameList.add(c.getName()));
		
		List<String> filteredNameList = SearchEngine.filterObjects(searchField.getText(), ClientBox.getClients().listIterator(),
				c -> c.getName());
		
		if (searchField.getText().isEmpty()) {
			filteredClientList.setPredicate(c -> true);
		}
		else {
			filteredClientList.setPredicate(c -> filteredNameList.contains(c.getName()));
		}
	}
	
	@FXML
	void handleOK() {
		
		dialogStage.close();
	}
	
	
	private class SimpleTableCell<S, T> extends TableCell<S, T> {
		@Override
		protected void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			
			if (empty || item == null) {
				setText("");
			}
			else {
				setText(item.toString());
			}
			setGraphic(null);
		}
	}
}
