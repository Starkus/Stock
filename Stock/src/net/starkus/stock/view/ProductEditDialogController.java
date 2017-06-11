package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.Product;

public class ProductEditDialogController extends DialogController {

	@FXML
	private TextField codeField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField sellPriceField;
	
	
	private Product product;
	private boolean okClicked = false;
	
	
	@FXML
	private void initialize() {
	}
	
	public void setProduct(Product product) {
		this.product = product;
		
		float sell = product.getPrice();
		
		codeField.setText(Long.toString(product.getCode()));
		nameField.setText(product.getName());
		sellPriceField.setText(Float.toString(sell));
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}
	
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			product.setCode(Long.parseLong(codeField.getText()));
			product.setName(nameField.getText());
			product.setPrice(Float.parseFloat(sellPriceField.getText()));
			
			okClicked = true;
			dialogStage.close();
		}
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	private boolean isInputValid() {
		String errorMessage = "";

		if (codeField.getText() == null || codeField.getText().length() == 0) {
			errorMessage += "Inserte un codigo valido!\n";
		}
		if (nameField.getText() == null || nameField.getText().length() == 0) {
			errorMessage += "Inserte un nombre valido!\n";
		}
		if (sellPriceField.getText() == null || sellPriceField.getText().length() == 0) {
			errorMessage += "Inserte un precio de venta!\n";
		}
		
		try {
			String s = sellPriceField.getText();
			
			if (!s.contains(".")) {
				s += ".0";
			}
			
			Float.parseFloat(s);
		
		} catch (NumberFormatException e) {
			errorMessage += "El precio de venta no es valido!\n";
		}
		
		
		if (errorMessage.length() == 0) {
			return true;
		} else {
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido modificar o crear el producto.");
    		alert.setContentText(errorMessage);
    		
    		alert.showAndWait();
    		
    		return false;
    	}
	}
	

	public ProductEditDialogController() {
	}

}
