package net.starkus.stock.view;

import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.starkus.stock.model.AutoCompleteTextField;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
//import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWithTotal;
import net.starkus.stock.model.Purchase;

public class AddStockDialogController extends DialogController {
	
	
	private Purchase productList;
	
	private Product currentProduct;
	
	
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> quantColumn;
	@FXML
	private TableColumn<Product, Number> subtotalColumn;
	
	@FXML
	private Button OKButton;
	
	@FXML
	private AutoCompleteTextField codeNameField;
	@FXML
	private TextField quantField;
	@FXML
	private TextField priceField;
	@FXML
	private TextField subtotalField;
	

	
	public AddStockDialogController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	productList = new Purchase();
    	
    	productTable.setItems(new ProductListWithTotal(productList.getProductData()));
    	
    	ProductBox.getProducts().forEach(p -> codeNameField.getEntries().add(p.getName()));
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	quantColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	subtotalColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty());
    	
    	codeNameField.setAutoProc(true);
    	
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
		    	codeNameField.requestFocus();
			}
		});
    	
    	currentProduct = new Product();

    	
    	codeNameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == false)
					readCodeName();
			}
		});
    	
    	quantField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == false)
					readQuantity();
			}
		});
    	
    	priceField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == false)
					readPrice();
			}
		});

    	subtotalField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == false)
					readPrice();
			}
		});
    	
    	quantField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (quantField.getText().isEmpty())
					return;
				
				quantField.setStyle("");
				
				try {
					float quant = Float.parseFloat(newValue);
					subtotalField.setText(Float.toString(quant * currentProduct.getPrice()));
					
				} catch (NumberFormatException e) {
					quantField.setStyle("-fx-text-fill: red");
					//ExceptionUtil.printStackTrace(e);
				}
			}
		});
    	
    	priceField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (priceField.isFocused() == false)
					return;
				
				if (priceField.getText().isEmpty())
					return;
				
				priceField.setStyle("");
				
				try {
					float price = Float.parseFloat(newValue);
					subtotalField.setText(Float.toString(price * currentProduct.getQuantity()));
					
				} catch (NumberFormatException e) {
					priceField.setStyle("-fx-text-fill: red");
					//ExceptionUtil.printStackTrace(e);
				}
			}
		});
    	
    	subtotalField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (subtotalField.isFocused() == false)
					return;
				
				if (subtotalField.getText().isEmpty())
					return;
				
				subtotalField.setStyle("");
				
				try {
					float price = Float.parseFloat(newValue);
					priceField.setText(Float.toString(price / currentProduct.getQuantity()));
					
				} catch (NumberFormatException e) {
					subtotalField.setStyle("-fx-text-fill: red");
					//ExceptionUtil.printStackTrace(e);
				}
			}
		});
    }
    
    private boolean readCodeName() {
    	
    	String text = codeNameField.getText();
		
		// If no text, focus OK button
		if (text.length() == 0 || text == null) {
			
			//OKButton.requestFocus();
			handleOK();
			return true;
		}
		
		
		// If number entered, look up product by code.
		if (codeNameField.getText().matches("-?\\d+(\\.\\d+)?")) {
			long code = Long.parseLong(codeNameField.getText());
			ProductBox.getProducts().forEach(p -> {
				if (p.getCode() == code) {
					codeNameField.setText(p.getName());
					return;
				}
			});
		}
		
		
		// If entry, use entry
		if (codeNameField.getResults().size() > 0) {
			codeNameField.setText(codeNameField.getResults().get(0));
		}
		
		currentProduct.setName(codeNameField.getText());
		
		return false;
    }
    
    private boolean readQuantity() {
    	String q = quantField.getText();
		
		if (q.length() != 0 && q != null && !q.isEmpty())
			currentProduct.setQuantity(Float.parseFloat(q));
		else
			return true;
		
		return false;
    }
    
    private boolean readPrice() {
    	String p = priceField.getText();
		
		if (p.length() != 0 && p != null && !p.isEmpty())
			currentProduct.setPrice(Float.parseFloat(p));
		else
			return true;
    	
    	return false;
    }
	
	@FXML
	private void handleCodeEntered() {
		
		if (readCodeName())
			return;
		
		quantField.requestFocus();
	}
	
	@FXML
	private void handleQuantityEntered() {
		
		if (readQuantity())
			return;
		
		priceField.requestFocus();
		
	}
	
	@FXML
	private void handlePriceEntered() {
		
		if (readPrice())
			return;
		
		handleAddItem();
	}
	
	@FXML
	private void handleSubtotalEntered() {
		
		handlePriceEntered();
	}
	
	@FXML
	private void handleAddItem() {
		
		productList.add(currentProduct);
		currentProduct = new Product();
		
		codeNameField.setText("");
		quantField.setText("");
		priceField.setText("");
		subtotalField.setText("");
		
		codeNameField.requestFocus();
	}
	
	@FXML
	private void handleCancel() {
		productList = null;
		dialogStage.close();
	}
	
	@FXML
	private void handleOK() {
		
		productList.setCreationDate(LocalDateTime.now());
		
		dialogStage.close();
	}
	
	@FXML
	private void handleClean() {
		
	}
	
	public Purchase getProductList() {
		return productList;
	}

}
