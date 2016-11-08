package net.starkus.stock.view;

import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.util.DateUtil;

public class PurchaseDialogController {
	
	private MainApp mainApp;
	
	private ProductList purchase;
	private Stage dialogStage;
	
	
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

	
	private Product productBuffer;
	
	

	public PurchaseDialogController() {
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
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;

    	for (Product p : mainApp.getProductData()) {
    		codeNameField.getEntries().add(p.getName());
    	}
    }
    
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
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
			
			handleOK();
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
		
		// If no code, conclude purchase
		if (c.length() == 0 || c == null) {
			
			handleOK();
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
		
		purchase.setCreationDate(LocalDateTime.now());
		
		System.out.println(DateUtil.format(purchase.getCreationDate()));
		
		dialogStage.close();
	}
}
