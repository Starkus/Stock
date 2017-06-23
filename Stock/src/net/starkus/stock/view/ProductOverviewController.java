package net.starkus.stock.view;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.starkus.stock.MainApp;
import net.starkus.stock.control.BigButton;
import net.starkus.stock.control.MainToolbar;
import net.starkus.stock.model.AlertWrapper;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.ExceptionUtil;
import net.starkus.stock.util.SearchEngine;

public class ProductOverviewController extends DialogController {
	
	@FXML
	private TableView<Product> stockTable;
	@FXML
	private TableColumn<Product, Number> codeColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Number> priceColumn;
	@FXML
	private TableColumn<Product, Number> quanColumn;

	@FXML
	private MainToolbar toolBar;
	
	private BigButton addButton, editButton, eraseButton;
	
	
	private FilteredList<Product> filteredProductList;
	private SortedList<Product> sortedProductList;
	
	private ContextMenu contextMenu;
	
	
	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
	public ProductOverviewController() {
	}
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    void initialize() {
    	
    	initToolbar();
		
    	
    	stockTable.setPlaceholder(new Label("Nada por aquí"));
    	
    	EventHandler<MouseEvent> mouseEvent = event -> {
    		if (!event.getButton().equals(MouseButton.PRIMARY))
    			return;
    		
    		if (event.getClickCount() == 2 && (stockTable.getSelectionModel().getSelectedIndex() != -1)) {
    			handleEditProduct();
    		}
    	};
    	
    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	codeColumn.setCellFactory(column -> {
    		TableCell<Product, Number> cell = new TableCell<Product, Number>() {
    			@Override
    			protected void updateItem(Number item, boolean empty) {
    				setText((empty || item==null) ? "" : item.toString()); 
    			}
    		};
    		cell.setOnMouseClicked(mouseEvent);
    		return cell;
    	});
    	
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	nameColumn.setCellFactory(column -> {
    		TableCell<Product, String> cell = new TableCell<Product, String>() {
    			@Override
    			protected void updateItem(String item, boolean empty) {
    				setText((empty || item==null) ? "" : item); 
    			}
    		};
    		cell.setOnMouseClicked(mouseEvent);
    		return cell;
    	});
    	
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
    	priceColumn.setCellFactory(column -> {
    		TableCell<Product, Number> cell = new TableCell<Product, Number>() {
    			@Override
    			protected void updateItem(Number item, boolean empty) {
    				setText((empty || item==null) ? "" : item.toString()); 
    			}
    		};
    		cell.setOnMouseClicked(mouseEvent);
    		return cell;
    	});
    	
    	quanColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
    	quanColumn.setCellFactory(column -> {
    		TableCell<Product, Number> cell = new TableCell<Product, Number>() {
    			@Override
    			protected void updateItem(Number item, boolean empty) {
    				setText((empty || item==null) ? "" : item.toString()); 
    			}
    		};
    		cell.setOnMouseClicked(mouseEvent);
    		return cell;
    	});
    	
    	setUpContextMenu();
    	
    	toolBar.getSearchField().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				filter(newValue);
			}
		});
    	
    	filteredProductList = ProductBox.getProducts().filtered(p -> true);
    	sortedProductList = filteredProductList.sorted();
    	sortedProductList.comparatorProperty().bind(stockTable.comparatorProperty());
    	
    	stockTable.setItems(sortedProductList);
    	stockTable.getSortOrder().setAll(Collections.singletonList(nameColumn));
    }
    
    void initToolbar() {
    	
    	Image image = new Image(MainApp.class.getResource("media/add_icon.png").toExternalForm());
    	addButton = new BigButton(image, "Nuevo");
    	addButton.setOnAction(e -> handleNewProduct());
    	
    	image = new Image(MainApp.class.getResource("media/edit_icon.png").toExternalForm());
    	editButton = new BigButton(image, "Editar");
    	editButton.setOnAction(e -> handleEditProduct());
    	
    	image = new Image(MainApp.class.getResource("media/erase_icon.png").toExternalForm());
    	eraseButton = new BigButton(image, "Borrar");
    	eraseButton.setOnAction(e -> handleDeleteProduct());
    	
    	toolBar.getLeftItems().add(addButton);
    	toolBar.getLeftItems().add(editButton);
    	toolBar.getLeftItems().add(eraseButton);
    }
    
    @Override
    public void setMainApp(MainApp mainApp) {
    	super.setMainApp(mainApp);
    	
		mainApp.getRootLayout().getSearchFields().add(toolBar.getSearchField());
    }
    
    
    private void setUpContextMenu() {
    	
    	contextMenu = new ContextMenu();
    	
    	MenuItem mi1 = new MenuItem("Nuevo...");
    	mi1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				handleNewProduct();
			}
		});
    	contextMenu.getItems().add(mi1);
    	
    	MenuItem mi2 = new MenuItem("Editar...");
    	mi2.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleEditProduct();
			}
		});
    	contextMenu.getItems().add(mi2);
    	
    	MenuItem mi3 = new MenuItem("Borrar");
    	mi3.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				handleDeleteProduct();
			}
		});
    	contextMenu.getItems().add(mi3);
    	
    	

    	/*
    	 * Make the contextMenu visible when right-clicking an item.
    	 */
    	stockTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY) {
					
					contextMenu.show(stockTable, event.getScreenX(), event.getScreenY());
				}
				else {
					
					contextMenu.hide();
				}
			}
		});
    	
    	/*
    	 * Hide it!
    	 */
    	contextMenu.setAutoHide(true);
    }
    
    
    private void filter(String s) {

    	List<String> results = SearchEngine.filterObjects(s, ProductBox.getProducts().listIterator(), p -> ((Product) p).getName());
    	
    	// Ignore case
    	filteredProductList.setPredicate(p -> results.contains(p.getName()));
    }
    
    @FXML
    private void handleFilter() {
    	filter(toolBar.getSearchField().getText());
    }
    
    @FXML
    private void handleNewProduct() {
    	Product tempProduct = new Product();
    	
    	ProductEditDialogController controller;
		try {
			controller = Dialog.productEditDialog.init();
		}
		catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
			return;
		}
		
		controller.setProduct(tempProduct);
		
		controller.showAndWait();
    	
    	if (controller.isOkClicked()) {
    		ProductBox.getProducts().add(tempProduct);
    		
    		SaveUtil.saveToFile();
    	}
    }
    
    @FXML
    private void handleEditProduct() {
    	Product selectedProduct = stockTable.getSelectionModel().getSelectedItem();
    	if (selectedProduct != null) {
    		//boolean okClicked = mainApp.showProductEditDialog(selectedProduct);
    		
    		ProductEditDialogController controller;
			try {
				controller = Dialog.productEditDialog.init();
			}
			catch (IOException e) {
				ExceptionUtil.printStackTrace(e);
				return;
			}
    		
			controller.setProduct(selectedProduct);
			
			controller.showAndWait();
			
			SaveUtil.saveToFile();
			
    	} else {
    		// Nothing selected
    		AlertWrapper alert = new AlertWrapper(AlertType.WARNING)
    				.setTitle("No hay seleccion")
    				.setHeaderText("No hay un producto seleccionado.")
    				.setContentText("Seleccione un producto para editar.");
    		
    		alert.showAndWait();
    	}
    }
    
    @FXML
    private void handleDeleteProduct() {
    	Product selected = stockTable.getSelectionModel().getSelectedItem();
    	
    	if (selected != null) {
    		
    		AlertWrapper alert = new AlertWrapper(AlertType.CONFIRMATION)
    				.setTitle("Confirmar")
    				.setHeaderText("¿Seguro que desea eliminar \"" + selected.getName() + "\" ?");
			
			alert.showAndWait();
    		
			if (alert.getResult() == ButtonType.OK) {
				ProductBox.getProducts().remove(selected);
				
				SaveUtil.saveToFile();
			}
    	
    	} else {
    		// Nothing selected
    		AlertWrapper alert = new AlertWrapper(AlertType.WARNING)
		    		.setTitle("No hay seleccion")
		    		.setHeaderText("No hay un producto seleccionado.")
		    		.setContentText("Seleccione un producto para eliminar.");
    		
    		alert.showAndWait();
    	}
    }

}
