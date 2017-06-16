package net.starkus.stock.view;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.starkus.stock.model.Admin;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.Sale;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.model.TransactionType;
import net.starkus.stock.save.SaveUtil;

public class HistoryViewerController extends DialogController {
	
	private final String ANY = "Cualquiera";
	private final String NONE = "Nadie";

	@FXML
	private TableView<Transaction> transactionTable;

	@FXML
	private TableColumn<Transaction, TransactionType> typeColumn;
	@FXML
	private TableColumn<Transaction, String> clientColumn;
	@FXML
	private TableColumn<Transaction, LocalDateTime> dateColumn;
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
	@FXML
	private ChoiceBox<DateFilter> dateFilterBox;
	
	@FXML
	private Button nullifyButton;
	
	
	private FilteredList<Transaction> filteredTransactionList;
	private SortedList<Transaction> sortedTransactionList;

	
	private String transactionClassToStyle(Transaction t) {
		
		if (t.getCancelled()) {
			return "-fx-text-fill:#4d4d4d";
		}
		
		switch (t.getType()) {
		case SALE:
			return "-fx-text-fill:#e88d8d";
		
		case PURCHASE:
			return "-fx-text-fill:#3872d1";
		
		case PAYMENT:
			return "-fx-text-fill:#9ed36b";
		
		case LEGACYDEBT:
			return "-fx-text-fill:#c1c1c1";
		}
		
		return "";
	}
	
	
	@FXML
	void initialize() {		
		
		nullifyButton.setDisable(!Admin.getAdmin());
		
		
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		typeColumn.setCellFactory(column -> new TableCell<Transaction, TransactionType>() {
			@Override
			protected void updateItem(TransactionType item, boolean empty) {
				super.updateItem(item, empty);
				
				Transaction t = (Transaction) getTableRow().getItem();
				if (empty || t == null) {
					setText("");
					setStyle("");
				}
				else {
					setText(item.toString());
					setStyle(transactionClassToStyle(t));
				}
				setGraphic(null);
			}
		});
		
		clientColumn.setCellValueFactory(cellData -> cellData.getValue().clientProperty());
		clientColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				
				Transaction t = (Transaction) getTableRow().getItem();
				
				if (empty || t == null) {
					setText("");
					setStyle("");
				} 
				else {
					setText(item);
					setStyle(transactionClassToStyle(t));
				}
				
				setGraphic(null);
			}
		});
		
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());
		dateColumn.setCellFactory(column -> new TableCell<Transaction, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				
				Transaction t = (Transaction) getTableRow().getItem();
				
				if (empty || t == null) {
					setText("");
					setStyle("");
				} 
				else {
					String formatted = String.format("%d/%d/%d - %d:%d:%d", item.getDayOfMonth(), item.getMonthValue(),
							item.getYear(), item.getHour(), item.getMinute(), item.getSecond());
					
					setText(formatted);
					setStyle(transactionClassToStyle(t));
				}
				
				setGraphic(null);
			}
		});
		
		balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
		balanceColumn.setCellFactory(column -> new TableCell<Transaction, Number>() {
			@Override
			protected void updateItem(Number item, boolean empty) {
				super.updateItem(item, empty);
				
				Transaction t = (Transaction) getTableRow().getItem();
				
				if (empty || t == null) {
					setText("");
					setStyle("");
				} 
				else {
					setText(item.toString());
					setStyle(transactionClassToStyle(t));
				}
				
				setGraphic(null);
			}
		});
		
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		productQuantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
		productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
		
		transactionTable.getSortOrder().setAll(Collections.singletonList(dateColumn));
		
		transactionTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Transaction>() {

			@Override
			public void changed(ObservableValue<? extends Transaction> observable, Transaction oldValue, Transaction newValue) {
				
				if (newValue.getType() == TransactionType.SALE || newValue.getType() == TransactionType.PURCHASE) {
					productTable.setItems(new ProductListWithTotal(((Sale) newValue).getProductData()));
					paidLabel.setText(Float.toString(((Sale) newValue).getPaid()));
					balanceLabel.setText(Float.toString(((Sale) newValue).getBalance()));
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

		
		filteredTransactionList = History.getHistory().filtered(t -> true);
		sortedTransactionList = new SortedList<>(filteredTransactionList);
		sortedTransactionList.comparatorProperty().bind(transactionTable.comparatorProperty());
		
		
		List<DateFilter> dateFilters = Arrays.asList(
				new DateFilter("Todo", Period.ofYears(99)),
				new DateFilter("Este mes", Period.ofMonths(1)),
				new DateFilter("Esta semana", Period.ofWeeks(1)),
				new DateFilter("Hoy", Period.ofDays(1))
				);
		dateFilterBox.setItems(FXCollections.observableArrayList(dateFilters));
		dateFilterBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DateFilter>() {

			@Override
			public void changed(ObservableValue<? extends DateFilter> observable, DateFilter oldValue,
					DateFilter newValue) {
				
				filterByClient();
				transactionTable.scrollTo(transactionTable.getItems().size() - 1);
			}
		});
		dateFilterBox.getSelectionModel().select(1);
		
		transactionTable.setItems(sortedTransactionList);
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
		
		filteredTransactionList.setPredicate(filteredTransactionList.getPredicate().and(t -> 
		((Transaction) t).getCreationDate().compareTo(dateFilterBox.getSelectionModel().getSelectedItem().from()) > 0));
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
		
		
		transaction.undo();
		
		
		// Flag purchase as canceled
		transaction.cancel();
		
		SaveUtil.saveToFile();
		
		// Update table
		transactionTable.setItems(sortedTransactionList);
	}
	
	public void setFilterClient(String client) {
		
		clientFilterBox.setText(client);
		filterByClient();
	}
	
	private class DateFilter {
		
		private final TemporalAmount timeAgo;
		private final String displayName;
		
		public DateFilter(String name, TemporalAmount time) {
			this.displayName = name;
			this.timeAgo = time;
		}
		
		public LocalDateTime from() {
			return LocalDateTime.now().minus(timeAgo);
		}
		
		@Override
		public String toString() {
			return displayName;
		}
	}
}
