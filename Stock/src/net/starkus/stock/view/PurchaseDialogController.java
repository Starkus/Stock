package net.starkus.stock.view;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.util.ExceptionUtil;

public class PurchaseDialogController extends DialogController {
	
	private Purchase purchase;
	
	
	private FloatProperty total;
	private FloatProperty paying;
	private FloatProperty debt;
	
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> priceColumn;
	@FXML
	private TableColumn<Product, Number> quantColumn;
	@FXML
	private TableColumn<Product, Number> subtColumn;
	
	
	@FXML
	private AutoCompleteTextField codeNameField;
	@FXML
	private TextField quantField;
	@FXML
	private TextField subtField;
	
	@FXML
	private TextField totalField;
	@FXML
	private TextField payingField;
	@FXML
	private TextField debtField;

	
	private Product productToAdd;
	
	private ProductListWithTotal productList;
	
	

	public PurchaseDialogController() {
		total = new SimpleFloatProperty();
		paying = new SimpleFloatProperty();
		debt = new SimpleFloatProperty();
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	purchase = new Purchase();
    	
    	ObservableList<Product> items = purchase.getProductData();
    	
    	productList = new ProductListWithTotal(items);
    	productTable.setItems(productList);
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
    	
    	formatLastRow();
    	
    	
    	codeNameField.setAutoProc(true);
    	
    	
    	total.bind(Bindings.createObjectBinding(() -> 
    			items.stream().collect(Collectors.summingDouble(Product::getSubtotal)), items));
    	totalField.textProperty().bind(Bindings.convert(total));
    	
    	NumberFormat nf = NumberFormat.getInstance(Locale.US);
    	Bindings.bindBidirectional(payingField.textProperty(), paying, new NumberStringConverter(nf));
    	
    	debt.bind(Bindings.subtract(total, paying));
    	debtField.textProperty().bind(Bindings.convert(debt));
    	
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				codeNameField.requestFocus();
			}
		});
    	
    	quantField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (quantField.isFocused())
					handleQuantEntered();
			}
		});
    	
    	subtField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (subtField.isFocused())
					handleSubtotalEntered();
			}
		});
    	
    	
    	productTable.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
					
					Product sel = productTable.getSelectionModel().getSelectedItem();
					productList.remove(sel);
				}
			}
		});
    }
    
    private void formatLastRow() {
    	// FIXME - not working properly.
    	// row factory just sets a CSS pseudoclass on the total row, for styling it differently:
		productTable.setRowFactory(row -> new TableRow<Product>() {
			@Override
			public void updateItem(Product item, boolean empty) {
				super.updateItem(item, empty);
				
				setStyle("");
				
				if (item == null || empty) {
				} else {
					
					if (item.getQuantity() != 1) {
						for (int i=0; i < getChildren().size(); i++) {
							((Labeled) getChildren().get(i)).setStyle("-fx-font-weight: bolder");
						}
					}
				}
			}
		});
    }
    
    /**
     * Is called by the main app to give a reference back to itself.
     * 
     * @param mainApp
     */
    @Override
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;

    	for (Product p : ProductBox.getProducts()) {
    		codeNameField.getEntries().add(p.getName());
    	}
    }
	
	public Purchase getPurchase() {
		return purchase;
	}
	
	
	private String pickClient() {
		
		DebtAssignDialogController controller;
		
		try {
			controller = Dialog.debtAssignDialog.init();
		}
		catch (IOException e) {
			ExceptionUtil.printStackTrace(e);
			return null;
		}
		
		controller.showAndWait();
		
		return controller.getClient();
	}
	
	
	@FXML
	private void handleCodeEntered() {
		
		
		// If entry, use entry
		if (codeNameField.getResults().size() > 0) {
			codeNameField.setText(codeNameField.getResults().get(0));
		}
		
		String text = codeNameField.getText();
		long code = -1;
		Product product = null;
		
		// If no text, conclude operation
		if (text.length() == 0 || text == null) {
			
			System.out.println(codeNameField.isFocused());
			
			payingField.requestFocus();
			return;
		}
		
		try {
			code = Long.parseLong(text);
			
			product = BinarySearch.findProductByCode(code, ProductBox.getProducts().sorted());
		}
		catch (NumberFormatException e) {
			
			for (Product p : ProductBox.getProducts()) {
				if (text.equals(p.getName())) {
					
					product = p;
					code = p.getCode();
					
					break;
				}
			}
		}
		
		if (product == null)
			return;
		
		productToAdd = product.copy();
		productToAdd.setQuantity(0);
		
		quantField.requestFocus();
	}
	
	@FXML
	private void handleQuantEntered() {
		
		String q = quantField.getText();
		
		float quant = 1;
		if (q.length() != 0 && q != null) {
			try {
				quantField.setStyle("-fx-background-color: white;");
				quant = Float.parseFloat(q);
			}
			catch (NumberFormatException e) {
				quantField.setStyle("-fx-background-color: #ff7070;");
			}
		}
		
		productToAdd.setQuantity(quant);
		
		float subt = productToAdd.getSubtotal();
		subtField.setText(String.format("%.2f", subt));
		
		//handleAddItem();
		
	}
	
	@FXML
	private void handleSubtotalEntered() {
		
		String s = subtField.getText();
		
		float subt = 0;
		if (s.length() != 0 && s != null) {
			try {
				subtField.setStyle("-fx-background-color: white;");
				subt = Float.parseFloat(s);
			}
			catch (NumberFormatException e) {
				subtField.setStyle("-fx-background-color: #ff7070;");
			}
		}
		
		float quant = subt / productToAdd.getPrice();
		
		productToAdd.setQuantity(Math.round(quant*100f) / 100f);
		
		quantField.setText(String.format("%.2f", quant));
		
		//handleAddItem();
	}
	
	@FXML
	private void handleAddItem() {
		
		if (productToAdd.getQuantity() == 0)
			productToAdd.setQuantity(1);

		String c = codeNameField.getText();
		
		// If no code, focus on paying field
		if (c.length() == 0 || c == null) {
			
			payingField.requestFocus();
			return;
		}
		
		// If the product is not found
		if (productToAdd == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Producto no encontrado!");
			alert.setContentText("Introduzca un producto existente.");
			
			alert.showAndWait();
		
		} else {
			
			purchase.add(productToAdd.copy());
		}
		
		// Update paying field to be equal to the new total.
		payingField.setText(Float.toString(total.get()));
		
		quantField.setText("");

		codeNameField.setText("");
		codeNameField.requestFocus();
		
		return;
	}
	
	@FXML
	private void handleCancel() {
		purchase = null;
		dialogStage.close();
	}
	
	@FXML
	private void handleOK() {
		
		if (debt.get() > 0) {
			
			String debtor = pickClient();
			
			if (debtor == null)
				return;
			
			purchase.setClient(debtor);
			
		}
		else if (debt.get() < 0) {
			
			AlertWrapper alert = new AlertWrapper(AlertType.CONFIRMATION);
			alert.setTitle("Vuelto");
			alert.setHeaderText("El excedente es de $" + Float.toString(debt.get()));
			
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.CANCEL)
				return;
			
			// Clean out excedents
			payingField.setText(totalField.getText());
		}	
		
		purchase.setCreationDate(LocalDateTime.now());
		purchase.setPaid(paying.get());
		
		dialogStage.close();
	}
}
