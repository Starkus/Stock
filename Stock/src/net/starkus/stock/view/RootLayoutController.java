package net.starkus.stock.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import net.starkus.stock.MainApp;

public class RootLayoutController {
	
	private MainApp mainApp;
	
	public void setMainApp(MainApp m) {
		this.mainApp = m;
	}
	
	@FXML
	private void handleNew() {
		mainApp.getProductData().clear();
		mainApp.setProductFilePath(null);
	}
	
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		if (file != null) {
			mainApp.loadProductDataFromFile(file);
		}
	}
	
	@FXML
	private void handleSave() {
		File productFile = mainApp.getProductFilePath();
		if (productFile != null) {
			mainApp.saveProductDataToFile(productFile);
		} else {
			handleSaveAs();
		}
	}
	
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
		
		if (file != null) {
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			mainApp.saveProductDataToFile(file);
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
