package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;

public class HomeController {

	private MainApp mainApp;
	
	@FXML
	private TableView<Product> stockTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> priceColumn;
	@FXML
	private TableColumn<Product, Number> quanColumn;
	
	
	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
	public HomeController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty());
    	quanColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    }
    
    /**
     * Is called by the main app to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;
    	
    	stockTable.setItems(mainApp.getProductData());
    }
    
    @FXML
    private void handleNewPurchase() {
    	ProductList purchase = mainApp.showPurchaseDialog();
    	
    	if (purchase != null) {
    		purchase.substractItemsFromStock(mainApp.getSortedProductData());
    	}
    }
    
    @FXML
    private void handleAddStock() {
    	ProductList purchase = mainApp.showAddStockDialog();
    	
    	if (purchase != null) {
    		purchase.addToStock(mainApp.getSortedProductData());
    	}
    }
    
    @FXML
    private void handleEditProducts() {
    	mainApp.showProductOverview();
    }

}
