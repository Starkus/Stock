package net.starkus.stock.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.LegacyDebt;
import net.starkus.stock.model.Payment;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.save.SaveUtil;

public class HistoryViewerController extends DialogController {
	
	private final String ANY = "Cualquiera";
	private final String NONE = "Nadie";

	@FXML
	private TableView<Transaction> transactionTable;

	@FXML
	private TableColumn<Transaction, String> typeColumn;
	@FXML
	private TableColumn<Transaction, String> clientColumn;
	@FXML
	private TableColumn<Transaction, String> dateColumn;
	@FXML
	private TableColumn<Transaction, Number> balanceColumn;
	
	@FXML
	private TableView<Product> productTable;
	
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Number> productQuantColumn;
	@FXML
	private TableColumn<Product, Number> productPriceColumn;

	@FXML
	private Label paidLabel;
	@FXML
	private Label balanceLabel;
	
	@FXML
	private TextField clientFilterBox;
	
	
	private FilteredList<Transaction> filteredTransactionList;
	//private SortedList<Transaction> sortedTransactionList;

	
	private String transactionClassToStyle(Transaction t) {
		
		if (t.getCancelled()) {
			return "-fx-text-fill:#4d4d4d";
		}
		
		if (t.getClass().equals(Purchase.class)) {
			return "-fx-text-fill:#e88d8d";
		}
		else if (t.getClass().equals(Payment.class)) {
			return "-fx-text-fill:#9ed36b";
		}
		else if (t.getClass().equals(LegacyDebt.class)) {
			return "-fx-text-fill:#c1c1c1";
		}
		else
			return "";
	}
	
	private String transactionClassToString(Transaction t) {
		if (t.getClass().equals(Purchase.class)) {
			return "Compra";
		}
		else if (t.getClass().equals(Payment.class)) {
			return "Pago";
		}
		else if (t.getClass().equals(LegacyDebt.class)) {
			return "Deuda legado";
		}
		else
			return "";
	}
	
	@FXML
	void initialize() {
		
		typeColumn.setCellFactory(column -> {
			return new TableCell<Transaction, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					Transaction t = (Transaction) getTableRow().getItem();
					
					if (empty) {
						setText("");
						setStyle("");
					}
					else {
						setText(transactionClassToString(t));
						setStyle(transactionClassToStyle(t));
					}
					
					setGraphic(null);
				}
			};
		});
		
		clientColumn.setCellFactory(column -> {
			return new TableCell<Transaction, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					Transaction t = (Transaction) getTableRow().getItem();
					
					if (empty) {
						setText("");
						setStyle("");
					} 
					else {
						setText(t.getClient() == null ? "" : t.getClient());
						setStyle(transactionClassToStyle(t));
					}
					
					setGraphic(null);
				}
			};
		});
		
		dateColumn.setCellFactory(column -> {
			return new TableCell<Transaction, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					Transaction t = (Transaction) getTableRow().getItem();
					
					if (empty) {
						setText("");
						setStyle("");
					} 
					else {
						setText(t.formattedDateProperty().get());
						setStyle(transactionClassToStyle(t));
					}
					
					setGraphic(null);
				}
			};
		});
		
		balanceColumn.setCellFactory(column -> {
			return new TableCell<Transaction, Number>() {
				@Override
				protected void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					
					Transaction t = (Transaction) getTableRow().getItem();
					
					if (empty) {
						setText("");
						setStyle("");
					} 
					else {
						setText(Float.toString(t.getBalance()));
						setStyle(transactionClassToStyle(t));
					}
					
					setGraphic(null);
				}
			};
		});
		
		/*
		clientColumn.setCellValueFactory(cellData -> cellData.getValue().clientProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().formattedDateProperty());
		totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
		paidColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());*/
		
		
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		productQuantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
		productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().sellSubtotalProperty());
		
		transactionTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Transaction>() {

			@Override
			public void changed(ObservableValue<? extends Transaction> observable, Transaction oldValue, Transaction newValue) {
				
				if (newValue.getClass().equals(Purchase.class)) {
					productTable.setItems(new ProductListWithTotal(((Purchase) newValue).getProductData()));
					paidLabel.setText(Float.toString(((Purchase) newValue).getPaid()));
					balanceLabel.setText(Float.toString(((Purchase) newValue).getBalance()));
				}
				else {
					productTable.setItems(null);
					paidLabel.setText("");
				}
			}
		});
		
		clientFilterBox.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				filterByClient();
			}
		});
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		filteredTransactionList = mainApp.getHistory().filtered(t -> true);
		//sortedTransactionList = new SortedList<>(filteredTransactionList);
		//sortedTransactionList.comparatorProperty().bind(transactionTable.comparatorProperty());
		
		transactionTable.setItems(filteredTransactionList);
		
		filterByClient();
	}
	
	
	@FXML
	void filterByClient() {
		
		String chosen = clientFilterBox.getText();
		
		if (chosen.isEmpty())
			filteredTransactionList.setPredicate(t -> true);
			
		else if (chosen.equals(ANY)) 
			filteredTransactionList.setPredicate(t -> t.getClient() != null);
		
		else if (chosen.equals(NONE)) 
			filteredTransactionList.setPredicate(t -> t.getClient() == null || t.getClient().isEmpty());
		
		else 
			filteredTransactionList.setPredicate(t -> t.getClient() != null && t.getClient().equals(chosen));
		
		
	}
	
	
	@FXML
	private void handleClientEntered() {
		
		
	}
	
	
	@FXML
	private void nullifyTransaction() {
		
		// Get selected transaction
		Transaction transaction = transactionTable.getSelectionModel().getSelectedItem();
		
		// If already cancelled, return
		if (transaction.getCancelled() == true)
			return;
		
		// If it's a purchase, do purchase specific stuff
		if (transaction.getClass().equals(Purchase.class)) {
			
			Purchase purchase = (Purchase) transaction;
		
			// Put items back in the shelf
			purchase.addToStock(mainApp.getSortedProductData());
		}
			
		// Get money out da bank
		CashBox.substract(transaction.getBalance());
		
		// Flag purchase as canceled
		transaction.cancel();
		
		SaveUtil.saveToFile();
	}
	
	public void setFilterClient(String client) {
		
		clientFilterBox.setText(client);
		filterByClient();
	}
}
