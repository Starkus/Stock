package net.starkus.stock.view;

import java.io.IOException;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
//import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.util.DateUtil;
import net.starkus.stock.util.ExceptionUtil;

public class AddStockDialogController extends DialogController {
	
	
	private Purchase productList;
	
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> quantColumn;
	@FXML
	private TableColumn<Product, Number> subtotalColumn;
	
	@FXML
	private Button OKButton;
	
	@FXML
	private AutoCompleteTextField codeNameField;
	@FXML
	private TextField quantField;
	@FXML
	private TextField priceField;
	@FXML
	private TextField subtotalField;
	

	
	public AddStockDialogController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	productList = new Purchase();
    	
    	productTable.setItems(new ProductListWithTotal(productList.getProductData()));
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtotalColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
    	
    	codeNameField.setAutoProc(true);
    	
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
		    	codeNameField.requestFocus();
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
    
    private Product findProduct(String text) {
    	
    	Product product = null;
    	
    	try {
			long code = Long.parseLong(text);
			
			product = BinarySearch.findProductByCode(code, ProductBox.getProducts().sorted());
		}
		catch (NumberFormatException e) {
			
			for (Product p : ProductBox.getProducts()) {
				if (text.equals(p.getName())) {
					
					product = p;
					//code = p.getCode();
					
					break;
				}
			}
		}
    	
    	return product;
    }
	
	@FXML
	private void handleCodeEntered() {
		
		String text = codeNameField.getText();
		
		
		// If no text, focus OK button
		if (text.length() == 0 || text == null) {
			
			//OKButton.requestFocus();
			handleOK();
			return;
		}
		
		
		// If entry, use entry
		if (codeNameField.getResults().size() > 0) {
			codeNameField.setText(codeNameField.getResults().get(0));
		}
		
		quantField.requestFocus();
	}
	
	@FXML
	private void handleAddItem() {
		
		String q = quantField.getText();
		
		float quant = 1;
		if (q.length() != 0 && q != null && !q.isEmpty())
			quant = Float.parseFloat(q);
		
		Product product = findProduct(codeNameField.getText());
		
		// If the product is not found
		if (product == null) {
			
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR)
					.setTitle("ERROR")
					.setHeaderText("Producto no encontrado!")
					.setContentText("Agregue el producto para continuar.");
			
			alert.showAndWait();
		
		} else {
		
			Product productToAdd = product.copy();
			productToAdd.setQuantity(quant);
			
			productList.add(productToAdd);
		}
		
		quantField.setText("");
		
		codeNameField.requestFocus();
		codeNameField.setText("");
		return;
	}
	
	@FXML
	private void handleCancel() {
		productList = null;
		dialogStage.close();
	}
	
	@FXML
	private void handleOK() {
		
		PasswordDialogController controller;
		try {
			controller = Dialog.passwordDialog.init();
		}
		catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
			return;
		}
		
		controller.showAndWait();
		
		if (!controller.wasPasswordCorrect()) {
			return;
		}
		
		productList.setCreationDate(LocalDateTime.now());
		
		System.out.println(DateUtil.format(productList.getCreationDate()));
		
		dialogStage.close();
	}
	
	@FXML
	private void handleClean() {
		
	}
	
	public Purchase getProductList() {
		return productList;
	}

}
