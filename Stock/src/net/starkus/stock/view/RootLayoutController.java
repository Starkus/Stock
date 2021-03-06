package net.starkus.stock.view;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.Admin;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.ExceptionUtil;

public class RootLayoutController {
	
	private MainApp mainApp;
	private Stage primaryStage;
	private BorderPane rootPage;
	

	@FXML
	private MenuItem importCmd;
	@FXML
	private MenuItem exportCmd;
	@FXML
	private MenuItem closeCmd;

	@FXML
	private MenuItem newCmd;
	@FXML
	private MenuItem editCmd;
	@FXML
	private MenuItem dupliCmd;
	@FXML
	private MenuItem deleteCmd;

	@FXML
	private MenuItem setCashCmd;
	@FXML
	private MenuItem addStockCmd;
	
	
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    	closeCmd.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
    	
    	
    	Admin.adminProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setCashCmd.setDisable(!newValue);
				addStockCmd.setDisable(!newValue);
			}
		});
    }
	
	public void setMainApp(MainApp m) {
		this.mainApp = m;
	}
	
	public void setPrimaryStage(Stage stage) {
		primaryStage = stage;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void setRootPage(BorderPane page) {
		rootPage = page;
	}
	
	public BorderPane getRootPage() {
		return rootPage;
	}
	
	private File saveLoadDirectory() {
		
		String dir = System.getProperty("user.home");
		
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			dir += "\\Documents";
		
		return new File(dir);
	}
	
	@FXML
	private void handleImport() {
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setInitialDirectory(saveLoadDirectory());
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		if (file != null) {
			SaveUtil.loadFromFile(file);
			
			mainApp.getHomeController().onFileLoad();
		}
	}
	
	@FXML
	private void handleExport() {
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setInitialDirectory(saveLoadDirectory());
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
		
		if (file != null) {
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			SaveUtil.saveToFile(file);
		}
	}
	
	@FXML
	private void handleAbout() {
		AlertWrapper alert = new AlertWrapper(AlertType.INFORMATION)
				.setTitle("Stock")
				.setHeaderText("Stock v" + MainApp.getVersion())
				.setContentText("Author: Starkus");
		
		alert.showAndWait();
	}
	
	@FXML
	private void handleClose() {
		System.exit(0);
	}
	
	@FXML
	private void handleChangePassword() {
		
		try {
			Dialog.changePasswordDialog.init().showAndWait();
			SaveUtil.saveToFile();
		}
		catch (IOException e) {
			ExceptionUtil.printStackTrace(e);
		}
		
	}

	@FXML
	private void handleSetCash() {

		try {
			Dialog.setCashDialog.init().showAndWait();
		}
		catch (IOException e) {
			
			ExceptionUtil.printStackTrace(e);
			return;
		}
		SaveUtil.saveToFile();
	}
	
	@FXML
	private void handleAddStock() {
		
		try {
			AddStockDialogController controller = Dialog.addStockDialog.init();
			controller.showAndWait();
			
			Purchase purchase = controller.getPurchase();
	    	
	    	if (purchase != null) {
	    		purchase._do();
	    		
	    		History.getHistory().add(purchase);
	        	
	        	SaveUtil.saveToFile();
	    	}
		}
		catch (IOException e) {
			
			ExceptionUtil.printStackTrace(e);
		}
	}
	
	@FXML
	private void handleOpenClients() {
		
		try {
			Dialog.clientOverviewDialog.init().showAndWait();
		} 
		catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
		}
	}
	
	@FXML
	private void handleOpenHistory() {
		
		try {
			Dialog.historyViewerDialog.init().showAndWait();
		} 
		catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
		}
	}
	
	
	public MenuItem getNewCmd() {
		return newCmd;
	}
	public MenuItem getEditCmd() {
		return editCmd;
	}
	public MenuItem getDuplicateCmd() {
		return dupliCmd;
	}
	public MenuItem getDeleteCmd() {
		return deleteCmd;
	}
}
