package net.starkus.stock.view;

import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.BinarySearch;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.util.DateUtil;

public class AddStockDialogController {
	
	
	private MainApp mainApp;
	
	private ProductList productList;
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
	private Button OKButton;
	
	@FXML
	private AutoCompleteTextField codeNameField;
	@FXML
	private TextField quantField;
	@FXML
	private TextField priceField;
	
	
	private Product productBuffer;

	
	public AddStockDialogController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	productList = new ProductList();
    	
    	productTable.setItems(new ProductListWithTotal(productList.getProductData()));
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().buyPriceProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtColumn.setCellValueFactory(cellData -> cellData.getValue().buySubtotalProperty());
    	
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
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;

    	for (Product p : mainApp.getProductData()) {
    		codeNameField.getEntries().add(p.getName());
    	}
    }
    
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	@FXML
	private void handleCodeEntered() {
		
		String text = codeNameField.getText();
		long code = -1;
		Product product = null;
		
		
		// If no text, focus OK button
		if (text.length() == 0 || text == null) {
			
			//OKButton.requestFocus();
			handleOK();
			return;
		}
		
		
		// If entry, use entry
		if (codeNameField.getResults().size() > 0) {
			codeNameField.setText(codeNameField.getResults().getFirst());
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
		
		priceField.setText(Float.toString(product.getBuyPrice()));
		
		quantField.requestFocus();
	}
	
	@FXML
	private void handleQuantEntered() {
		
		if (quantField.getText().length() == 0)
			quantField.setText("1");
		
		priceField.requestFocus();
	}
	
	@FXML
	private void handleAddItem() {
		
		String q = quantField.getText();
		String p = priceField.getText();
		
		int quant = 1;
		if (q.length() != 0 && q != null)
			quant = Integer.parseInt(q);
		
		float price = -1;
		if (p.length() != 0 && p != null)
			price = Float.parseFloat(p);
		
		// If the product is not found
		if (productBuffer == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Producto no encontrado!");
			alert.setContentText("Agregue el producto para continuar.");
			
			alert.showAndWait();
		
		} else {
		
			Product productToAdd = productBuffer.copy();
			productToAdd.setQuantity(quant);
			productToAdd.setBuyPrice(price);
			
			productList.add(productToAdd);
		}
		
		priceField.setText("");
		quantField.setText("");
		
		codeNameField.requestFocus();
		codeNameField.selectAll();
		return;
	}
	
	@FXML
	private void handleCancel() {
		productList = null;
		dialogStage.close();
	}
	
	@FXML
	private void handleOK() {
		
		productList.setCreationDate(LocalDateTime.now());
		
		System.out.println(DateUtil.format(productList.getCreationDate()));
		
		dialogStage.close();
	}
	
	@FXML
	private void handleClean() {
		
	}
	
	public ProductList getProductList() {
		return productList;
	}

}
