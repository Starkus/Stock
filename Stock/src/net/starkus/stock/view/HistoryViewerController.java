package net.starkus.stock.view;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.BigButton;
import net.starkus.stock.control.MainToolbar;
import net.starkus.stock.model.Admin;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.model.History;
import net.starkus.stock.model.ProductTransaction;
import net.starkus.stock.model.Sale;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.model.TransactionType;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.SearchEngine;

public class HistoryViewerController extends DialogController {
	
	private final String ANY = "Cualquiera";
	private final String NONE = "Nadie";

	@FXML
	private TreeTableView<Transaction> transactionTable;

	@FXML
	private TreeTableColumn<Transaction, TransactionType> typeColumn;
	@FXML
	private TreeTableColumn<Transaction, String> clientColumn;
	@FXML
	private TreeTableColumn<Transaction, LocalDateTime> dateColumn;
	@FXML
	private TreeTableColumn<Transaction, Number> balanceColumn;
	
	@FXML
	private MainToolbar toolBar;
	
	private ChoiceBox<DateFilter> dateFilterBox;
	private BigButton nullifyButton;
	private TextField searchField;
	
	
	private final List<TreeItem<Transaction>> treeTransactionList = new ArrayList<>();
	private FilteredList<Transaction> filteredTransactionList;
	private SortedList<Transaction> sortedTransactionList;

	
	private String transactionClassToStyle(Transaction t) {
		
		if (t.getCancelled()) {
			return "-fx-text-fill:#4d4d4d";
		}
		
		if (t.getType() != null) switch (t.getType()) {
		case SALE:
			return "-fx-text-fill:#e88d8d";
		
		case PURCHASE:
			return "-fx-text-fill:#3872d1";
		
		case PAYMENT:
			return "-fx-text-fill:#9ed36b";
		
		case LEGACYDEBT:
			return "-fx-text-fill:#c1c1c1";
			
		case PRODUCT:
			return "-fx-text-fill:#d1d1d1";
		}
		
		return "";
	}
	
	
	private class StyledTreeTableCell<S extends Transaction, T> extends TreeTableCell<S, T> {
		@Override
		protected void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			
			Transaction t = (Transaction) getTreeTableRow().getItem();
			
			if (empty || t == null || item == null) {
				setStyle("");
				setText("");
			}
			else {
				setStyle(transactionClassToStyle(t));
				setText(item.toString());
			}
		}
	}
	
	
	
	
	@FXML
	void initialize() {
		
		searchField = toolBar.getSearchField();
		
		initToolbar();
		
		
		nullifyButton.disableProperty().bind(Bindings.not(Admin.adminProperty()));
		
		
		
		filteredTransactionList = History.getHistory().filtered(t -> true);
		sortedTransactionList = new SortedList<>(filteredTransactionList);
		
		// FIXME
		sortedTransactionList.addListener(new ListChangeListener<Transaction>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Transaction> c) {
				treeTransactionList.clear();
				sortedTransactionList.forEach(t -> {
					if (t == null)
						return;
					
					TreeItem<Transaction> item = new TreeItem<Transaction>(t);
					if (t.getType() == TransactionType.SALE || t.getType() == TransactionType.PURCHASE) {
						
						((Sale) t).getProductData().forEach(p -> {
							
							ProductTransaction prodT = new ProductTransaction();
							prodT.setClient("    " + p.getName());
							prodT.setQuantity(p.getQuantity());
							prodT.setSubtotal(p.getSubtotal());
							
							TreeItem<Transaction> tp = new TreeItem<Transaction>(prodT);
							item.getChildren().add(tp);							
						});
					}
					
					treeTransactionList.add(item);
				});
				transactionTable.getRoot().getChildren().setAll(treeTransactionList);
			}
		});

		
		TreeItem<Transaction> root = new TreeItem<>();
		root.getChildren().setAll(treeTransactionList);
		root.setExpanded(true);
		transactionTable.setRoot(root);
		
		
		typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
		typeColumn.setCellFactory(column -> new StyledTreeTableCell<Transaction, TransactionType>());
		
		clientColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("client"));
		clientColumn.setCellFactory(column -> new StyledTreeTableCell<Transaction, String>());
		
		dateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("creationDate"));
		dateColumn.setCellFactory(column -> new StyledTreeTableCell<Transaction, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime item, boolean empty) {
				super.updateItem(item, empty);
				
				Transaction t = (Transaction) getTreeTableRow().getItem();
				
				if (t != null && getTreeTableRow().getItem().getType() == TransactionType.PRODUCT) {
					ProductTransaction pt = (ProductTransaction) t;
					String formatted = String.format("    Cantidad: %.1f         Subtotal: %.1f", pt.getQuantity(), pt.getSubtotal());
					
					setText(formatted);
					
					return;
				}
				
				if (empty || t == null || item == null) {
					setText("");
				} 
				else {
					String formatted = String.format("%d/%d/%d - %d:%d:%d", item.getDayOfMonth(), item.getMonthValue(),
							item.getYear(), item.getHour(), item.getMinute(), item.getSecond());
					
					setText(formatted);
				}
				
				setGraphic(null);
			}
		});
		
		balanceColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("balance"));
		balanceColumn.setCellFactory(column -> new StyledTreeTableCell<Transaction, Number>());
		
		transactionTable.getSortOrder().setAll(Collections.singletonList(dateColumn));
		
		searchField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				filterByClient();
			}
		});
		
		dateFilterBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DateFilter>() {

			@Override
			public void changed(ObservableValue<? extends DateFilter> observable, DateFilter oldValue,
					DateFilter newValue) {
				
				filterByClient();
				transactionTable.scrollTo(transactionTable.getExpandedItemCount() - 1);
			}
		});
		dateFilterBox.getSelectionModel().select(1);
		transactionTable.scrollTo(transactionTable.getExpandedItemCount() - 1);
		
		
		filterByClient();
	}
	
	void initToolbar() {
		
		Image image = new Image(MainApp.class.getResource("media/erase_icon.png").toExternalForm());
		nullifyButton = new BigButton(image, "Anular");
		nullifyButton.setOnAction(e -> nullifyTransaction());
		nullifyButton.disableProperty().bind(Bindings.not(Admin.adminProperty()));
		
		DateFilter[] dateFilters = new DateFilter[] {
				new DateFilter("Todo", Period.ofYears(99)),
				new DateFilter("Este mes", Period.ofMonths(1)),
				new DateFilter("Esta semana", Period.ofWeeks(1)),
				new DateFilter("Hoy", Period.ofDays(1))
		};
		dateFilterBox = new ChoiceBox<>(FXCollections.observableArrayList(dateFilters));
		dateFilterBox.setPrefSize(150, 30);
		
		String[] types = new String[] {
				"Todos",
				"Venta",
				"Pago",
				"Compra",
				"Deuda legado"
		};
		ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(types));
		typeChoiceBox.getSelectionModel().select(0);
		typeChoiceBox.setPrefSize(150, 30);
		
		VBox vBox = new VBox(5);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.getChildren().add(dateFilterBox);
		vBox.getChildren().add(typeChoiceBox);

		toolBar.getLeftItems().add(nullifyButton);
		toolBar.getLeftItems().add(vBox);
	}
	
	@Override
	public void setMainApp(MainApp mainApp) {
		super.setMainApp(mainApp);
		
		mainApp.getRootLayout().getSearchFields().add(searchField);
	}
	
	
	@FXML
	void filterByClient() {
		
		String chosen = searchField.getText();
		
		if (chosen.isEmpty())
			filteredTransactionList.setPredicate(t -> true);
			
		else if (chosen.equals(ANY)) 
			filteredTransactionList.setPredicate(t -> t.getClient() != null);
		
		else if (chosen.equals(NONE)) 
			filteredTransactionList.setPredicate(t -> t.getClient() == null || t.getClient().isEmpty());
		
		else 
			filteredTransactionList.setPredicate(t -> {
				
				if (t.getClient() == null)
					return false;
				
				List<String> results = SearchEngine.filterObjects(chosen, ClientBox.getClients().listIterator(), c -> c.getName());
				
				return results.contains(t.getClient());
			});
		
		filteredTransactionList.setPredicate(filteredTransactionList.getPredicate().and(t -> 
		((Transaction) t).getCreationDate().compareTo(dateFilterBox.getSelectionModel().getSelectedItem().from()) > 0));
	}
	
	
	@FXML
	private void handleClientEntered() {
		
		
	}
	
	
	@FXML
	private void nullifyTransaction() {
		
		// Get selected transaction
		Transaction transaction = transactionTable.getSelectionModel().getSelectedItem().getValue();
		
		// If already cancelled, return
		if (transaction.getCancelled() == true)
			return;
		
		
		transaction.undo();
		
		
		// Flag purchase as canceled
		transaction.cancel();
		
		SaveUtil.saveToFile();
		
		// Update table
		//transactionTable.setItems(sortedTransactionList);
	}
	
	public void setFilterClient(String client) {
		
		searchField.setText(client);
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
