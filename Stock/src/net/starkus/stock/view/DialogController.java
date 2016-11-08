package net.starkus.stock.view;

import javafx.fxml.FXML;
import net.starkus.stock.MainApp;

public class DialogController {
	
	@FXML
	MainApp mainApp;
	
	
	/**
     * Is called by the main app to give a reference back to itself.
     * 
     * @param mainApp
     */
	public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;
    }
	
	public void onFileLoad() {
	}

}
