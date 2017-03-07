package net.starkus.stock.view;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Labeled;
import javafx.util.converter.NumberStringConverter;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWithTotal;

public class PurchaseDialogController extends DialogController {
	
	private ProductList purchase;
	
	
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

	
	private Product productBuffer;
	
	

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
    	purchase = new ProductList();
    	
    	ObservableList<Product> items = purchase.getProductData();
    	productTable.setItems(new ProductListWithTotal(items));
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtColumn.setCellValueFactory(cellData -> cellData.getValue().sellSubtotalProperty());
    	
    	formatLastRow();
    	
    	
    	codeNameField.setAutoProc(true);
    	
    	
    	total.bind(Bindings.createObjectBinding(() -> 
    			items.stream().collect(Collectors.summingDouble(Product::getSellSubtotal)), items));
    	totalField.textProperty().bind(Bindings.convert(total));
    	
    	Bindings.bindBidirectional(payingField.textProperty(), paying, new NumberStringConverter());
    	
    	debt.bind(Bindings.subtract(total, paying));
    	debtField.textProperty().bind(Bindings.convert(debt));
    	
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				codeNameField.requestFocus();
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
				
				if (item == null || empty) {
					setStyle("");
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

    	for (Product p : mainApp.getProductData()) {
    		codeNameField.getEntries().add(p.getName());
    	}
    }
	
	public ProductList getPurchase() {
		return purchase;
	}
	
	
	@FXML
	private void handleCodeEntered() {
		
		
		// If entry, use entry
		if (codeNameField.getResults().size() > 0) {
			codeNameField.setText(codeNameField.getResults().getFirst());
		}
		
		String text = codeNameField.getText();
		long code = -1;
		Product product = null;
		
		// If no text, conclude operation
		if (text.length() == 0 || text == null) {
			
			payingField.requestFocus();
			return;
		}
		
		try {
			code = Long.parseLong(text);
			
			product = BinarySearch.findProductByCode(code, mainApp.getSortedProductData());
		}
		catch (NumberFormatException e) {
			
			for (Product p : mainApp.getProductData()) {
				if (text.equals(p.getName())) {
					
					product = p;
					code = p.getCode();
					
					break;
				}
			}
		}
		
		productBuffer = product;
		
		quantField.requestFocus();
	}
	
	@FXML
	private void handleAddItem() {

		String c = codeNameField.getText();
		
		// If no code, focus on paying field
		if (c.length() == 0 || c == null) {
			
			payingField.requestFocus();
			return;
		}
		
		
		String q = quantField.getText();
		
		int quant = 1;
		if (q.length() != 0 && q != null)
			quant = Integer.parseInt(q);
		
		// If the product is not found
		if (productBuffer == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Producto no encontrado!");
			alert.setContentText("Introduzca un producto existente.");
			
			alert.showAndWait();
		
		} else {
		
			Product productToAdd = productBuffer.copy();
			productToAdd.setQuantity(quant);
			
			purchase.add(productToAdd);
		}
		
		// Update paying field to be equal to the new total.
		payingField.setText(Float.toString(total.get()));
		
		quantField.setText("");
		
		codeNameField.requestFocus();
		codeNameField.setText("");
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
			
			Client debtor = mainApp.showDebtDialog();
			
			if (debtor == null)
				return;
			
			debtor.add(debt.get());
		}
		else if (debt.get() < 0) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Vuelto");
			alert.setHeaderText("El excedente es de $" + Float.toString(debt.get()));
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
			
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.CANCEL)
				return;
			
			// Clean out excedents
			payingField.setText(totalField.getText());
		}	
		
		purchase.setCreationDate(LocalDateTime.now());
		
		dialogStage.close();
	}
}
