package net.starkus.stock.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.util.SaveUtil;

public class HistoryViewerController extends DialogController {

	@FXML
	private TableView<Purchase> purchaseTable;

	@FXML
	private TableColumn<Purchase, String> clientColumn;
	@FXML
	private TableColumn<Purchase, String> dateColumn;
	@FXML
	private TableColumn<Purchase, Number> totalColumn;
	@FXML
	private TableColumn<Purchase, Number> paidColumn;
	
	@FXML
	private TableView<Product> productTable;
	
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Number> productQuantColumn;
	
	@FXML
	private ComboBox<String> clientFilterBox;
	
	
	private FilteredList<Purchase> filteredPurchaseList;
	
	
	@FXML
	void initialize() {
		clientColumn.setCellValueFactory(cellData -> cellData.getValue().clientProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateProperty());
		totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
		paidColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());
		
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		productQuantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
		
		purchaseTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Purchase>() {

			@Override
			public void changed(ObservableValue<? extends Purchase> observable, Purchase oldValue, Purchase newValue) {
				productTable.getItems().clear();
				productTable.getItems().addAll(newValue.getProductData());
			}
		});
		
		ObservableList<String> clientOptions = FXCollections.observableArrayList(
				"Cualquiera",
				"Ninguno");
		
		clientFilterBox.setItems(clientOptions);
		clientFilterBox.getSelectionModel().selectFirst();
		
		clientFilterBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				filterByClient();
			}
		});
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		for (Client c : mainApp.getClients()) {
			clientFilterBox.getItems().add(c.getName());
		}
		
		filterByClient();
	}
	
	
	void filterByClient() {
		
		String chosen = clientFilterBox.getSelectionModel().getSelectedItem();
		
		if (chosen.equals("Cualquiera")) 
			filteredPurchaseList = mainApp.getHistory().filtered(p -> true);
		
		else if (chosen.equals("Ninguno")) 
			filteredPurchaseList = mainApp.getHistory().filtered(p -> p.getClient() == null || p.getClient().isEmpty());
		
		else 
			filteredPurchaseList = mainApp.getHistory().filtered(p -> p.getClient() != null && p.getClient().equals(chosen));
		
			
		purchaseTable.setItems(filteredPurchaseList);
	}
	
	
	@FXML
	private void undoPurchase() {
		
		// Get selected purchase
		Purchase purchase = purchaseTable.getSelectionModel().getSelectedItem();
		
		// Put items back in the shelf
		purchase.addToStock(mainApp.getSortedProductData());
		
		// Get money out da bank
		CashBox.substract(purchase.getPaid());
		
		// Look for client and cancel purchase debt
		if (purchase.getClient() != null && !purchase.getClient().isEmpty()) {
			
			for (Client c : mainApp.getClients()) {
				if (c.getName().equals(purchase.getClient())) {
					
					float clientDebt = purchase.getTotal() - purchase.getPaid();
					
					c.substract(clientDebt);
				}
			}
		}
		
		// Delete the purchase
		mainApp.getHistory().remove(purchase);
		
		SaveUtil.saveToFile(mainApp.getSavefile());
	}
	
}
