package net.starkus.stock.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.CalendarView;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Sale;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.model.TransactionType;

public class ManageTabController extends DialogController {
	

	private final Image saleIcon = new Image(MainApp.class.getResource("media/sale_icon.png").toExternalForm());
	private final Image purchaseIcon = new Image(MainApp.class.getResource("media/purchase_icon.png").toExternalForm());
	private final Image paymentIcon = new Image(MainApp.class.getResource("media/payment_icon.png").toExternalForm());
	private final Image legacyIcon = new Image(MainApp.class.getResource("media/legacy_icon.png").toExternalForm());
	
	
	private FilteredList<Transaction> filteredTransactionList;
	
	
	@FXML
	private Label transactionAccountNameLabel;
	@FXML
	private Label transactionAccountDebtLabel;
	

	@FXML
	private TableView<Transaction> transactionTable;
	@FXML
	private TableColumn<Transaction, TransactionType> transactionIconColumn;
	@FXML
	private TableColumn<Transaction, String> transactionTabColumn;
	@FXML
	private TableColumn<Transaction, String> transactionBalanceColumn;
	
	
	@FXML
	private CalendarView calendar;
	@FXML
	private Label monthLabel;
	@FXML
	private ImageView monthIncrease;
	@FXML
	private ImageView monthDecrease;
	
	
	private ObjectProperty<Transaction> selectedTransaction = new SimpleObjectProperty<Transaction>();
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Number> productAmmountColumn;
	@FXML
	private TableColumn<Product, String> productSubtotalColumn;
	
	
	@FXML
	private TableView<Client> accountTable;
	@FXML
	private TableColumn<Client, String> accountNameColumn;
	@FXML
	private TableColumn<Client, String> accountBalanceColumn;
	

	private Predicate<Transaction> accountPredicate;
	private Predicate<Transaction> datePredicate;
	
	
	@FXML
	void initialize() {
		
		setUpHistoryPanel();
		
		setUpTransactionPanel();
		
		setUpFilterPanel();
	
	}
	
	
	
	
	private void setUpHistoryPanel() {
		
		transactionIconColumn.setCellValueFactory(cell -> cell.getValue().typeProperty());
		transactionIconColumn.setCellFactory(column -> new TableCell<Transaction, TransactionType>() {
			@Override
			protected void updateItem(TransactionType item, boolean empty) {
				
				if (empty || item == null) {
					setGraphic(null);
					return;
				}
				
				switch (item) {
				case SALE:
					setGraphic(new ImageView(saleIcon));
					break;
					
				case PURCHASE:
					setGraphic(new ImageView(purchaseIcon));
					break;
					
				case PAYMENT:
					setGraphic(new ImageView(paymentIcon));
					break;
					
				case LEGACYDEBT:
					setGraphic(new ImageView(legacyIcon));
					break;
					
				default:
					setGraphic(null);
					break;
				}
			}
		});
		
		transactionTabColumn.setCellValueFactory(cell -> cell.getValue().clientProperty());
		transactionTabColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				
				setPrefHeight(55);
				
				
				Transaction t = (Transaction) getTableRow().getItem();
				
				if (empty || t == null) {
					setText("");
					setGraphic(null);
					
					return;
				}
				
				if (item == null || item.isEmpty())
					setText("");
				
				else
					setText(item);
				
				setText(getText() + "                                                                                                                                                                  ");
				setTextOverrun(OverrunStyle.CLIP);
				
				Label dateLabel = new Label();
				LocalDateTime date = t.getCreationDate();
				dateLabel.setText(String.format("%d/%d/%d - %d:%d:%d", date.getDayOfMonth(), date.getMonthValue(),
						date.getYear(), date.getHour(), date.getMinute(), date.getSecond()));
				
				dateLabel.prefWidthProperty().bind(this.widthProperty());
				
				setGraphic(dateLabel);
				setContentDisplay(ContentDisplay.BOTTOM);
				
				
				if (t.getCancelled()) {
					getTableRow().getStyleClass().add("table-row-void");
				}
				else {
					getTableRow().getStyleClass().remove("table-row-void");
				}
			}
		});
		
		transactionBalanceColumn.setCellValueFactory(cell -> Bindings.format("%.1f", cell.getValue().balanceProperty()));
		
		transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, previous, selected) -> {
			
			selectedTransaction.set(selected);
		});
	}
	
	
	
	
	private void setUpTransactionPanel() {
		
		selectedTransaction.addListener((obs, oldT, newT) -> {
			
			if (newT == null)
				return;
			
			if (newT.getClient() == null || newT.getClient().isEmpty()) {
				transactionAccountNameLabel.setText("?");
				transactionAccountDebtLabel.setText("?");
				
				return;
			}
			
			Client c = ClientBox.getClients().filtered(a -> a.getName().equals(newT.getClient())).get(0);
			
			transactionAccountNameLabel.setText(newT.getClient());
			transactionAccountDebtLabel.setText(Float.toString(c.calculateBalance()));
		});
		
		selectedTransaction.addListener((obs, oldTransaction, newTransaction) -> {
			
			if (newTransaction != null && newTransaction.getType().hasProductList())
				productTable.setItems(((Sale) newTransaction).getProductData());
			
			else
				productTable.setItems(null);
			
		});
		
		productNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		productAmmountColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty());
		productSubtotalColumn.setCellValueFactory(cell -> Bindings.format("%.1f", cell.getValue().subtotalProperty()));
	}
	
	
	
	
	private void setUpFilterPanel() {

		/* Initialize predicates */
		accountPredicate = t -> true;
		datePredicate = t -> t.getCreationDate().toLocalDate().equals(calendar.getDatePicked());
		
		filteredTransactionList = History.getHistory().filtered(datePredicate.and(accountPredicate));
		transactionTable.setItems(filteredTransactionList);
		
		
		calendar.datePickedProperty().addListener((obs, oldDate, newDate) -> {
			
			if (newDate == null) {
				datePredicate = (t -> true);
			}
			else {		
				datePredicate = (t -> t.getCreationDate().toLocalDate().equals(newDate));
			}
			
			filteredTransactionList.setPredicate(datePredicate.and(accountPredicate));
		});

		monthIncrease.setOnMouseClicked(e -> calendar.setMonthShown(calendar.getMonthShown().plusMonths(1)));
		monthDecrease.setOnMouseClicked(e -> calendar.setMonthShown(calendar.getMonthShown().minusMonths(1)));
		
		calendar.monthShownProperty().addListener((obs, oldDate, newDate) -> {
			String text = newDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase();
			
			int year = newDate.getYear();
			
			if (newDate.getYear() != LocalDate.now().getYear()) {
				text += " " + Integer.toString(year);
			}
			
			monthLabel.setText(text);
		});
		
		
		accountTable.setItems(ClientBox.getClients().sorted(new Comparator<Client>() {
			
			@Override
			public int compare(Client o1, Client o2) {
				return o1.getName().compareTo(o2.getName());
			}
		}));

		accountNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		accountBalanceColumn.setCellValueFactory(cell -> Bindings.format("%.1f", cell.getValue().getObservableBalance()));
		
		accountTable.setRowFactory(t -> new TableRow<Client>() {
			@Override
			public void updateItem(Client client, boolean empty) {
				super.updateItem(client, empty);
				
				if (empty || client == null) 
					return;
				
				accountTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
					
					if (selected != null && client.getName().equals(selected.getName())) {
						
						getStyleClass().add("table-row-filter");
						
						// FIXME this is messy af
						setOnMouseReleased(e -> setOnMousePressed(f -> setOnMouseReleased(g -> {
							accountTable.getSelectionModel().clearSelection();
						})));
					}
					else {
						getStyleClass().remove("table-row-filter");
						
						setOnMousePressed(null);
						setOnMouseReleased(null);
					}
				});
			}
		});
		
		accountTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			
			if (selected == null)
				accountPredicate = (t -> true);
			else
				accountPredicate = (t -> selected.getName().equals(t.getClient()));
				
			filteredTransactionList.setPredicate(datePredicate.and(accountPredicate));
		});
	}
}
