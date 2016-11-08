package net.starkus.stock.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.util.SaveUtil;

public class HomeController extends DialogController {
	
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

	@FXML
	private Label cashField;
	@FXML
	private Label sesionBalanceField;
	
	
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
    void initialize() {
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty());
    	quanColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

    	cashField.textProperty().bind(CashBox.cashProperty());
    	sesionBalanceField.textProperty().bind(CashBox.sessionBalanceProperty());
    	
    	CashBox.sessionBalanceProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setBalanceFieldTextColor();
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
    	super.setMainApp(mainApp);
    	
    	stockTable.setItems(mainApp.getProductData());
    }
    
    private void setBalanceFieldTextColor() {

    	float balance = CashBox.getSessionBalance();
    	
    	if (balance == 0f) {
        	sesionBalanceField.setTextFill(null);
    	}
    	if (balance < 0f) {
        	sesionBalanceField.setTextFill(Color.RED);
    	}
    	else {
        	sesionBalanceField.setTextFill(Color.LIGHTGREEN);
    	}
    	
    }
    
    @FXML
    private void handleNewPurchase() {
    	ProductList purchase = mainApp.showPurchaseDialog();
    	
    	if (purchase != null) {
    		purchase.substractItemsFromStock(mainApp.getSortedProductData());
    		
    		CashBox.put(purchase.getTotal(true));
        	
        	SaveUtil.saveToFile(mainApp.getSavefile());
    	}
    }
    
    @FXML
    private void handleAddStock() {
    	ProductList purchase = mainApp.showAddStockDialog();
    	
    	if (purchase != null) {
    		purchase.addToStock(mainApp.getSortedProductData());
    		
    		CashBox.substract(purchase.getTotal(false));
        	
        	SaveUtil.saveToFile(mainApp.getSavefile());
    	}
    }
    
    @FXML
    private void handleEditProducts() {
    	mainApp.showProductOverview();
    	
    	SaveUtil.saveToFile(mainApp.getSavefile());
    }

}
