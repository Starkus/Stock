package net.starkus.stock.view;

import java.time.LocalDateTime;
import java.util.Comparator;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.AutoCompleteTextField;
import net.starkus.stock.control.ButtonTableCell;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.ProductTotal;
import net.starkus.stock.model.Sale;
import net.starkus.stock.save.SaveUtil;

public class SellTabController extends DialogController {
	
	
	private Sale currentSale;
	private ProductListWithTotal productList;
	
	private final ObjectProperty<Client> account = new SimpleObjectProperty<Client>(null);
	
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Number> ammountColumn;
	@FXML
	private TableColumn<Product, Number> priceColumn;
	@FXML
	private TableColumn<Product, Number> subtotalColumn;
	@FXML
	private TableColumn<Product, Boolean> actionColumn;
	
	@FXML
	private AutoCompleteTextField addProductField;
	@FXML
	private Button addProductButton;
	
	@FXML
	private Button finishButton;
	@FXML
	private Button clearButton;
	
	@FXML
	private Label totalLabel;
	
	
	@FXML
	private TableView<Product> lowStockTable;
	@FXML
	private TableColumn<Product, String> lowStockNameColumn;
	@FXML
	private TableColumn<Product, Number> lowStockAvailableColumn;
	
	
	@FXML
	private AutoCompleteTextField accountField;
	@FXML
	private TextField paidField;

	@FXML
	private Label clientNameLabel;
	@FXML
	private Label currentDebtLabel;
	@FXML
	private Label newDebtLabel;
	
	
	@FXML
	void initialize() {
		
		currentSale = new Sale();
		
		
		initProductTable();
		
		
		addProductButton.setOnAction(e -> handleProductEntered());
		addProductField.setSecondOnAction(addProductButton.getOnAction());
		ProductBox.getProducts().forEach(p -> addProductField.getEntries().add(p.getName()));
		
		
		productList.totalProperty().addListener((observable, oldValue, newValue) -> {
			totalLabel.setText("$" + newValue.toString());
			
			updateAccountInfo();
		});
		
		
		accountField.textProperty().addListener((observable, oldValue, newValue) -> {
			handleClientEntered();
		});
		ClientBox.getClients().forEach(c -> accountField.getEntries().add(c.getName()));
		
		finishButton.setOnAction(e -> handleFinish());
		clearButton.setOnAction(e -> handleClear());
		
		
		initLowStockTable();
		
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				addProductField.requestFocus();
			}
		});
		
		
		Tooltip.install(accountField, new Tooltip("A tooltip"));
		
		paidField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					if (!newValue.isEmpty()) {
						float paid = Float.parseFloat(newValue);
					
						currentSale.setPaid(paid);
					}
						
					updateAccountInfo();
					
					paidField.setStyle("-fx-background-image: url(/net/starkus/stock/media/paid_field.png);");
				}
				catch (NumberFormatException e) {
					currentSale.setPaid(Float.MAX_VALUE);
					
					paidField.setStyle("-fx-background-image: url(/net/starkus/stock/media/paid_field.png);"
							+ "-fx-border-color: red;");
				}
			}
		});
		
		account.addListener(new ChangeListener<Client>() {

			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				updateAccountInfo();
			}
		});
	}
	
	
	private void initProductTable() {
		productList = new ProductListWithTotal(currentSale.getProductData());
		productTable.setItems(productList);

		productNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
		
		ammountColumn.setCellValueFactory(p -> p.getValue().quantityProperty());
		
		priceColumn.setCellValueFactory(p -> p.getValue().priceProperty());
		
		subtotalColumn.setCellValueFactory(p -> p.getValue().subtotalProperty());
		subtotalColumn.setCellFactory(column -> new TotalTableCell<>());
		
		actionColumn.setCellFactory(column -> new ButtonTableCell<Product>() {
			@Override
			protected void init() {
				
				button.setGraphic(new ImageView(MainApp.class.
						getResource("media/delete_row_icon.png").toExternalForm()));
				
				button.setStyle(
						  "-fx-background-color: transparent;"
						+ "-fx-background-insets: 0;");
				
				button.setOnAction(e -> {
					
					Product p = (Product) getTableRow().getItem();
					
					if (p != null)
						productList.remove(p);
				});
			}
			
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				
				Product p = (Product) getTableRow().getItem();
				
				if (p == null || p.getClass() == ProductTotal.class)
					setGraphic(null);
			}
		});
	}
	
	private void initLowStockTable() {
		lowStockTable.setItems(ProductBox.getProducts().sorted(new Comparator<Product>() {
			
			@Override
			public int compare(Product o1, Product o2) {
				return Float.compare(o1.getQuantity(), o2.getQuantity());
			}
		}));

		lowStockTable.setRowFactory(table -> {
			TableRow<Product> row = new TableRow<>();
			
			Product p = row.getItem();
			
			if (p != null && p.getQuantity() <= 0)
				row.getStyleClass().add("table-row-danger");
			
			return row;
		});
		
		lowStockNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
		lowStockAvailableColumn.setCellValueFactory(p -> p.getValue().quantityProperty());
		
		lowStockAvailableColumn.setCellFactory(column -> new TableCell<Product, Number>() {
			@Override
			protected void updateItem(Number item, boolean empty) {
				
				Product p = (Product) getTableRow().getItem();
				
				if (empty || p == null)
					return;
				
				setText(item.toString());
				
				if (item.intValue() <= 3)
					getTableRow().getStyleClass().add("table-row-danger");
				else
					getTableRow().getStyleClass().remove("table-row-danger");
			}
		});
	}
	
	
	private void addProduct(Product p) {
		
		currentSale.addOrStack(p);
	}
	
	private Product getProductFromCode(long code) {
		return BinarySearch.findProductByCode(code, ProductBox.getProducts().sorted());
	}
	
	private Product getProductFromName(String name) {
		
		for (Product p : ProductBox.getProducts()) {
			if (p.getName().equals(name))
				return p;
		}
		
		return null;
	}
	
	
	private void handleProductEntered() {
		
		addProductField.requestFocus();
		
		Product product = null;
		String text = addProductField.getText();
		
		try {
			long code = Long.parseLong(text);
			
			Product found = getProductFromCode(code);
			
			if (found != null)
				product = new Product(found);
		}
		catch (NumberFormatException e) {
			
			Product found = getProductFromName(text);
			if (found != null)
				product = new Product(found);
		}
		
		if (product != null) {
			product.setQuantity(1);
			addProduct(product);
		}
	}
	
	private void handleClientEntered() {
		
		String text = accountField.getText();
		
		account.set(null);
		
		ClientBox.getClients().forEach(c -> {
			
			if (c.getName().equals(text)) {
				account.set(c);
			}
		});
	}
	
	private void handleFinish() {
		
		Sale shippingSale = new Sale(currentSale);
		
		shippingSale.setCreationDate(LocalDateTime.now());
		if (account.get() != null)
			shippingSale.setClient(account.get().getName());
		
		History.getHistory().add(shippingSale);
		
		shippingSale._do();
		
		
		handleClear();
		
		
		SaveUtil.saveToFile();
	}
	
	private void handleClear() {
		
		currentSale.getProductData().clear();
		currentSale.setClient("");
		currentSale.setCreationDate(null);
		currentSale.setCancelled(false);
		
		addProductField.setText("");
		accountField.setText("");
		paidField.setText("");
		
		updateAccountInfo();
	}
	
	private void updateAccountInfo() {
		if (account.get() != null) {
			clientNameLabel.setText(account.get().getName());
			currentDebtLabel.setText( 
				"$" + account.get().getObservableBalance().getValue().toString());

			double balance = (currentSale.getPaid() - productList.getTotal());
			if (paidField.getText().isEmpty())
				balance = 0;
			
			newDebtLabel.setText(String.format("%.2f", account.get().getObservableBalance().getValue().floatValue() - balance));
		}
	}

	
	
	private class TotalTableCell<T extends Number> extends TableCell<Product, T> {
		
		@Override
		protected void updateItem(T item, boolean empty) {
			
			Product p = (Product) getTableRow().getItem();
			
			if (empty || p == null) {
				setText("");
				return;
			}
			
			String prefix = item.getClass().equals(Float.class) ? "$" : "";
			setText(prefix + String.format("%.1f", item.floatValue()));
			
			if (p.getClass().equals(ProductTotal.class)) {
				getStyleClass().add("cell-total");
			}
			else {
				getStyleClass().remove("cell-total");
			}
		}
	};
}
