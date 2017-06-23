package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;

public class DialogController {
	
	@FXML
	protected MainApp mainApp;
	protected Stage dialogStage;
	protected Pane page;
	
	
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
	
	public void setPage(Pane pane) {
		this.page = pane;
	}
	
	public void show() {
		dialogStage.show();
	}
	
	public void showAndWait() {
		dialogStage.showAndWait();
	}

}
