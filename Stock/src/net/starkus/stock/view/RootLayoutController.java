package net.starkus.stock.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.util.ExceptionUtil;

public class RootLayoutController {
	
	private MainApp mainApp;
	private Stage primaryStage;
	private AnchorPane rootPage;
	
	private BorderPane borderPane;
	

	@FXML
	private Tab productsTab;
	@FXML
	private Tab historyTab;
	@FXML
	private Tab clientsTab;
	
	@FXML
	private MenuItem importCmd;
	@FXML
	private MenuItem exportCmd;
	@FXML
	private MenuItem closeCmd;

	@FXML
	private MenuItem setCashCmd;
	
	
	@FXML
	private HBox adminButtonBox;


	public static final int productsTabIndex = 0;
	public static final int historyTabIndex = 1;
	public static final int clientsTabIndex = 2;

	private final List<TextField> searchFields = new ArrayList<>();
	
	private boolean lastAltKey = false;
	
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	/*
    	Admin.adminProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setCashCmd.setDisable(!newValue);
				addStockCmd.setDisable(!newValue);
			}
		});
    	*/
    	/*
    	CashBox.sessionBalanceProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setBalanceFieldTextColor();
			}
		});*/
    }
    
    /*
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
    */
    
	public void setMainApp(MainApp m) {
		this.mainApp = m;
		
		loadUpTab("view/ProductOverview.fxml", productsTab);
    	loadUpTab("view/HistoryViewer.fxml", historyTab);
    	loadUpTab("view/ClientOverview.fxml", clientsTab);
	}
	
	private void loadMenuBar() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MenuBar.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			
			((MenuBarController) loader.getController()).setMainApp(mainApp);
			
			borderPane.setTop(pane);
		} catch (IOException e) {
			ExceptionUtil.printStackTrace(e);
		}
	}

    private void loadUpTab(String resource, Tab tab) {
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(resource));
			AnchorPane pane = (AnchorPane) loader.load();
			
			((DialogController) loader.getController()).setMainApp(mainApp);
			
			tab.setContent(pane);
		
		} catch (IOException e) {
			ExceptionUtil.printStackTrace(e);
		}
    }
    
    
    public void selectTab(int index) {
    	productsTab.getTabPane().getSelectionModel().select(index);
    }
    
	
	public SingleSelectionModel<Tab> getTabSelectionModel() {
		return productsTab.getTabPane().getSelectionModel();
	}
    
    public void setPrimaryStage(Stage stage) {
		primaryStage = stage;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void setRootPage(AnchorPane page) {
		rootPage = page;
    	borderPane = (BorderPane) rootPage.getChildren().get(0);
    	
    	rootPage.setOnKeyPressed(e -> {
    		lastAltKey = e.getCode() == KeyCode.ALT;
    	});
    	
    	rootPage.setOnKeyReleased(e -> {
    		if (e.getCode() == KeyCode.ALT && lastAltKey) {
    			if (borderPane.getTop() == null) {
    				loadMenuBar();
    				AnchorPane.setTopAnchor(adminButtonBox, 64.0);
    			}
    			else {
    				borderPane.setTop(null);
    				AnchorPane.setTopAnchor(adminButtonBox, 32.0);
    			}
    		}
    	});
	}
	
	public AnchorPane getRootPage() {
		return rootPage;
	}
	
	public List<TextField> getSearchFields() {
		return searchFields;
	}
	
	/*
	@FXML
    private void handleNewSale() {
    	
    	try {
	    	SaleDialogController controller = Dialog.saleDialog.init();
	    	controller.showAndWait();
	    	
	    	Sale sale = controller.getSale();
	    	
	    	if (sale != null) {
	    		
	    		History.getHistory().add(sale);
	    		
	    		sale._do();
	        	
	        	SaveUtil.saveToFile();
	    	}
    	}
	    catch (IOException e) {
	    	
	    	ExceptionUtil.printStackTrace(e);
	    }
    }
    
    @FXML
    private void handleNewPayment() {
    	
    	try {
	    	PaymentDialogController controller = Dialog.paymentDialogController.init();
	    	controller.showAndWait();
	    	
	    	Payment payment = controller.getPayment();
	    	
	    	if (payment != null) {
	    		
	    		History.getHistory().add(payment);
	    		
	    		CashBox.put(payment.getBalance());
	        	
	        	SaveUtil.saveToFile();
	    	}
    	}
	    catch (IOException e) {
	    	
	    	ExceptionUtil.printStackTrace(e);
	    }
    }*/
}
