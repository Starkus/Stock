package net.starkus.stock.view;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.Product;

public class ProductOverviewController {
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> buyPriceColumn;
	@FXML
	private TableColumn<Product, Number> sellPriceColumn;
	
	
	@FXML
	private Label codeLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label buyPriceLabel;
	@FXML
	private Label sellPriceLabel;
	
	@FXML
	private AutoCompleteTextField filterField;
	
	
	private MainApp mainApp;

	private Stage dialogStage;
	
	
	private FilteredList<Product> filteredProductList;
	
	
	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
	public ProductOverviewController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	buyPriceColumn.setCellValueFactory(cellData -> cellData.getValue().buyPriceProperty());
    	sellPriceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty());
    	
    	showProductDetails(null);
    	
    	productTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showProductDetails(newValue));
    	
    	productTable.setOnMouseClicked(event -> {
    		if (!event.getButton().equals(MouseButton.PRIMARY))
    			return;
    		
    		if (event.getClickCount() == 2 && (productTable.getSelectionModel().getSelectedIndex() != -1)) {
    			handleEditProduct();
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
    	
    	handleFilter();

    	for (Product p : mainApp.getProductData()) {
    		filterField.getEntries().add(p.getName());
    	}
    }
    
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
    
    /**
     * Fills detail table
     * 
     * @param product or null
     */
    private void showProductDetails(Product product) {
    	if (product != null) {
    		// Fill the labels with info from Product object.
    		buyPriceLabel.setText(Float.toString(product.getBuyPrice()));
    		sellPriceLabel.setText(Float.toString(product.getSellPrice()));
    	
    	} else {
    		
    		buyPriceLabel.setText("");
    		sellPriceLabel.setText("");
    	}
    }
    
    @FXML
    private void handleFilter() {
    	
    	// Filter ignoring case
    	filteredProductList = mainApp.getProductData().filtered(p -> p.getName().toLowerCase().contains(filterField.getText().toLowerCase()));
    	productTable.setItems(filteredProductList);
    }
    
    @FXML
    private void handleDeleteProduct() {
    	int selectedIndex = productTable.getSelectionModel().getSelectedIndex();
    	
    	if (selectedIndex >= 0) {
    		productTable.getItems().remove(selectedIndex);
    	
    	} else {
    		// Nothing selected
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("No hay seleccion");
    		alert.setHeaderText("No hay un producto seleccionado.");
    		alert.setContentText("Seleccione un producto para eliminar.");
    		
    		alert.showAndWait();
    	}
    }
    
    @FXML
    private void handleNewProduct() {
    	Product tempProduct = new Product();
    	boolean okClicked = mainApp.showProductEditDialog(tempProduct);
    	if (okClicked) {
    		mainApp.getProductData().add(tempProduct);
    	}
    }
    
    @FXML
    private void handleEditProduct() {
    	Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
    	if (selectedProduct != null) {
    		boolean okClicked = mainApp.showProductEditDialog(selectedProduct);
    		if (okClicked) {
    			showProductDetails(selectedProduct);
        		
        		mainApp.setSomethingChanged(true);
    		}
    	} else {
    		// Nothing selected
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("No hay seleccion");
    		alert.setHeaderText("No hay un producto seleccionado.");
    		alert.setContentText("Seleccione un producto para editar.");
    		
    		alert.showAndWait();
    	}
    }

}
