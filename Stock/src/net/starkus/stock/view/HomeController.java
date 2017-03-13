package net.starkus.stock.view;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Purchase;
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
	
	@FXML
	private AutoCompleteTextField filterField;
	
	
	private FilteredList<Product> filteredProductList;
	
	private ContextMenu contextMenu;
	
	
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
    	
    	filterField.setAutoProc(true);
    	
    	setUpContextMenu();
    }
    
    /**
     * Is called by the main app to give a reference back to itself.
     * 
     * @param mainApp
     */
    @Override
    public void setMainApp(MainApp mainApp) {
    	super.setMainApp(mainApp);
    	
    	for (Product p : mainApp.getProductData()) {
    		filterField.getEntries().add(p.getName());
    	}
    	
    	// Initialize filter list and set table items
    	handleFilter();
    	
    	RootLayoutController root = mainApp.getRootLayout();
    	root.getNewCmd().setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleNewProduct();
			}
		});
    	
    	root.getEditCmd().setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleEditProduct();
			}
		});
    	
    	root.getDeleteCmd().setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleDeleteProduct();
			}
		});
    }
    
    
    
    private void setUpContextMenu() {
    	
    	contextMenu = new ContextMenu();
    	
    	MenuItem mi1 = new MenuItem("Nuevo...");
    	mi1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				handleNewProduct();
			}
		});
    	contextMenu.getItems().add(mi1);
    	
    	MenuItem mi2 = new MenuItem("Editar...");
    	mi2.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleEditProduct();
			}
		});
    	contextMenu.getItems().add(mi2);
    	
    	MenuItem mi3 = new MenuItem("Borrar");
    	mi3.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleDeleteProduct();
			}
		});
    	contextMenu.getItems().add(mi3);
    	
    	

    	/*
    	 * Make the contextMenu visible when right-clicking an item.
    	 */
    	stockTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY) {
					
					contextMenu.show(stockTable, event.getScreenX(), event.getScreenY());
				}
				else {
					
					contextMenu.hide();
				}
			}
		});
    	
    	/*
    	 * Hide it!
    	 */
    	contextMenu.setAutoHide(true);
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
    private void handleFilter() {
    	
    	// Ignore case
    	filteredProductList = mainApp.getProductData().filtered(p ->
    		p.getName().toLowerCase().contains(filterField.getText().toLowerCase()));
    	
    	stockTable.setItems(filteredProductList);
    }
    
    @FXML
    private void handleNewPurchase() {
    	
    	try {
	    	PurchaseDialogController controller = Dialog.purchaseDialog.init();
	    	controller.showAndWait();
	    	
	    	Purchase purchase = controller.getPurchase();
	    	
	    	if (purchase != null) {
	    		
	    		mainApp.getHistory().add(purchase);
	    		purchase.substractItemsFromStock(mainApp.getSortedProductData());
	    		
	    		CashBox.put(purchase.getTotal());
	        	
	        	SaveUtil.saveToFile(mainApp.getSavefile());
	    	}
    	}
	    catch (IOException e) {
	    	
	    	e.printStackTrace();
	    }
    }
    
    @FXML
    private void handleEditClients() {
    	
    	try {
			Dialog.clientOverviewDialog.init().showAndWait();
	    	
	    	SaveUtil.saveToFile(mainApp.getSavefile());
			
		} catch (IOException e) {

			e.printStackTrace();
		}
    }
    
    @FXML
    private void handleNewProduct() {
    	Product tempProduct = new Product();
    	
    	ProductEditDialogController controller;
		try {
			controller = Dialog.productEditDialog.init();
		}
		catch (IOException e) {

			e.printStackTrace();
			return;
		}
		
		controller.setProduct(tempProduct);
		
		controller.showAndWait();
    	
    	if (controller.isOkClicked()) {
    		mainApp.getProductData().add(tempProduct);
    	}
    }
    
    @FXML
    private void handleEditProduct() {
    	Product selectedProduct = stockTable.getSelectionModel().getSelectedItem();
    	if (selectedProduct != null) {
    		//boolean okClicked = mainApp.showProductEditDialog(selectedProduct);
    		
    		ProductEditDialogController controller;
			try {
				controller = Dialog.productEditDialog.init();
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
    		
			controller.setProduct(selectedProduct);
			
			controller.showAndWait();
			
    	} else {
    		// Nothing selected
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("No hay seleccion");
    		alert.setHeaderText("No hay un producto seleccionado.");
    		alert.setContentText("Seleccione un producto para editar.");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
    		
    		alert.showAndWait();
    	}
    }
    
    @FXML
    private void handleDeleteProduct() {
    	int selectedIndex = stockTable.getSelectionModel().getSelectedIndex();
    	
    	if (selectedIndex >= 0) {
    		
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Confirmar");
    		alert.setHeaderText("¿Seguro que desea eliminar el producto seleccionado?");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
			
			alert.showAndWait();
    		
			if (alert.getResult() == ButtonType.OK)
				mainApp.getProductData().remove(selectedIndex);
    	
    	} else {
    		// Nothing selected
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("No hay seleccion");
    		alert.setHeaderText("No hay un producto seleccionado.");
    		alert.setContentText("Seleccione un producto para eliminar.");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
    		
    		alert.showAndWait();
    	}
    }

}
