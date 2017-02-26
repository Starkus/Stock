package net.starkus.stock.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.model.Product;

public class ProductEditDialogController extends DialogController {

	@FXML
	private TextField codeField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField buyPriceField;
	@FXML
	private TextField sellPriceFlatField;
	@FXML
	private TextField sellPriceMultField;
	
	
	private Product product;
	private boolean okClicked = false;
	
	
	@FXML
	private void initialize() {
	}
	
	public void setProduct(Product product) {
		this.product = product;
		
		float buy = product.getBuyPrice();
		float sell = product.getSellPrice();
		
		codeField.setText(Long.toString(product.getCode()));
		nameField.setText(product.getName());
		buyPriceField.setText(Float.toString(buy));
		sellPriceFlatField.setText(Float.toString(sell));
		sellPriceMultField.setText(Float.toString(sell / buy));
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}
	
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			product.setCode(Long.parseLong(codeField.getText()));
			product.setName(nameField.getText());
			product.setBuyPrice(Float.parseFloat(buyPriceField.getText()));
			product.setSellPriceFlat(Float.parseFloat(sellPriceFlatField.getText()));
			
			okClicked = true;
			dialogStage.close();
		}
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	@FXML
	private void handleChangeBuyPrice() {
		
		String buy = buyPriceField.getText();
		
		if (buy.length() != 0) {
			
			if (!buy.contains(".")) {
				buy += ".0";
			}
			
			try {
				float sell = Float.parseFloat(buy) * Float.parseFloat(sellPriceMultField.getText());
				sellPriceFlatField.setText(Float.toString(sell));
			} catch (NumberFormatException e) {
				sellPriceFlatField.setText("?");
			}
		}
	}
	
	@FXML
	private void handleChangeFlatSellPrice() {
		
		String flat = sellPriceFlatField.getText();
		
		if (flat.length() != 0) {
			
			if (!flat.contains(".")) {
				flat += ".0";
			}
			
			try {
				float mult = Float.parseFloat(flat) / Float.parseFloat(buyPriceField.getText());
				sellPriceMultField.setText(Float.toString(mult));
			} catch (NumberFormatException e) {
				sellPriceMultField.setText("?");
			}
		}
	}
	
	@FXML
	private void handleChangeMultSellPrice() {
		
		String mult = sellPriceMultField.getText();
		
		if (mult.length() != 0) {
			
			if (!mult.contains(".")) {
				mult += ".0";
			}
			
			try {
				float flat = Float.parseFloat(mult) * Float.parseFloat(buyPriceField.getText());
				sellPriceFlatField.setText(Float.toString(flat));
			} catch (NumberFormatException e) {
				sellPriceFlatField.setText("?");
			}
		}
	}
	
	private boolean isInputValid() {
		String errorMessage = "";

		if (codeField.getText() == null || codeField.getText().length() == 0) {
			errorMessage += "Inserte un codigo valido!\n";
		}
		if (nameField.getText() == null || nameField.getText().length() == 0) {
			errorMessage += "Inserte un nombre valido!\n";
		}
		if (buyPriceField.getText() == null || buyPriceField.getText().length() == 0) {
			errorMessage += "Inserte un precio de compra!\n";
		}
		if (sellPriceFlatField.getText() == null || sellPriceFlatField.getText().length() == 0) {
			errorMessage += "Inserte un precio de venta!\n";
		}
		
		try {
			String s = buyPriceField.getText();
			
			if (!s.contains(".")) {
				s += ".0";
			}
			
			Float.parseFloat(s);
		
		} catch (NumberFormatException e) {
			errorMessage += "El precio de compra no es valido!\n";
		}
		
		try {
			String s = sellPriceFlatField.getText();
			
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
			Alert alert = new Alert(AlertType.ERROR);
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
