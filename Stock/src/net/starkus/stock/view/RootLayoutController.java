package net.starkus.stock.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import net.starkus.stock.MainApp;
import net.starkus.stock.util.SaveUtil;

public class RootLayoutController {
	
	private MainApp mainApp;
	

	@FXML
	private MenuItem importCmd;
	@FXML
	private MenuItem exportCmd;
	@FXML
	private MenuItem closeCmd;
	
	
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    	closeCmd.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
    }
	
	public void setMainApp(MainApp m) {
		this.mainApp = m;
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
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Stock");
		alert.setHeaderText("About.");
		alert.setContentText("Author: Starkus");
		
		alert.showAndWait();
	}
	
	@FXML
	private void handleClose() {
		System.exit(0);
	}

}
