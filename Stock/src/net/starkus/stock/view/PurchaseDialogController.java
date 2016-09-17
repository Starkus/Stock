package net.starkus.stock.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Purchase;

public class PurchaseDialogController {
	
	private MainApp mainApp;
	
	private Purchase purchase;
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
	private TextField codeField;
	@FXML
	private TextField quantField;
	@FXML
	private TextField subtField;
	

	public PurchaseDialogController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	purchase = new Purchase();
    	
    	productTable.setItems(purchase.getProductData());
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getSellPrice() * cellData.getValue().getQuantity()));
    	
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
		    	codeField.requestFocus();
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
    }
    
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public Purchase getPurchase() {
		return purchase;
	}
	
	
	@FXML
	private void handleCodeEntered() {
		quantField.requestFocus();
	}
	
	@FXML
	private void handleAddItem() {

		String c = codeField.getText();
		
		// If no code, do nothing
		if (c.length() == 0 || c == null)
			return;
		
		long code = Long.parseLong(c);
		
		String q = quantField.getText();
		
		int quant = 1;
		if (q.length() != 0 && q != null)
			quant = Integer.parseInt(q);
		
		Product prod = BinarySearch.findProductByCode(code, mainApp.getSortedProductData());
		
		// If the product is not found
		if (prod == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Producto no encontrado!");
			alert.setContentText("Agregue el producto para continuar.");
			
			alert.showAndWait();
		
		} else {
		
			Product productToAdd = prod.copy();
			productToAdd.setQuantity(quant);
			
			purchase.add(productToAdd);
		}
		
		quantField.setText("");
		
		codeField.requestFocus();
		codeField.selectAll();
		return;
	}
	
	@FXML
	private void handleCancel() {
		purchase = null;
		dialogStage.close();
	}
	
	@FXML
	private void handleOK() {
		dialogStage.close();
	}
}
