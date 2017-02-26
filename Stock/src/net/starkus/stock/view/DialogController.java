package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;

public class DialogController {
	
	@FXML
	protected MainApp mainApp;
	protected Stage dialogStage;
	
	
	/**
     * Is called by the main app to give a reference back to itself.
     * 
     * @param mainApp
     */
	public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;
    }
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void onFileLoad() {
	}

}
