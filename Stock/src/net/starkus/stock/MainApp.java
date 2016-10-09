package net.starkus.stock;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.starkus.stock.model.HistoryWrapper;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductListWrapper;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.view.AddStockDialogController;
import net.starkus.stock.view.HomeController;
import net.starkus.stock.view.ProductEditDialogController;
import net.starkus.stock.view.ProductOverviewController;
import net.starkus.stock.view.PurchaseDialogController;
import net.starkus.stock.view.RootLayoutController;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private ObservableList<Product> productList = FXCollections.observableArrayList();
	private SortedList<Product> sortedProducts;
	
	private ObservableList<ProductList> history = FXCollections.observableArrayList();

	
	
	public MainApp() {
		
		// Sample data
		productList.add(new Product(78600010L, "TicTac Menta", 8, 13));
		productList.add(new Product(78605831L, "TicTac Dupla Frutilla", 8, 13));
		productList.add(new Product(8537023942L, "Xbox 360 Wireless Controller for Windows", 760, 1200));
		productList.add(new Product(790520009944L, "Raid Casa y Jardin", 16, 24));
		
		sortedProducts = productList.sorted();
	}
	
	
	/*
	 * Returns the data as an observable list of Products.
	 * 
	 * @return ObservableList<Product>
	 */
	public ObservableList<Product> getProductData() {
		return productList;
	}
	
	public SortedList<Product> getSortedProductData() {
		return sortedProducts;
	}
	
	public ObservableList<ProductList> getHistory() {
		return history;
	}
	
	public void showHome() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Home.fxml"));
			AnchorPane home = (AnchorPane) loader.load();
			
			rootLayout.setCenter(home);
			
			// Give the controller access to the main app
			HomeController controller = loader.getController();
			controller.setMainApp(this);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showProductOverview() {
		try {
			// Load product overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductOverview.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Productos");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Give the controller access to the main app
			ProductOverviewController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);
			
			dialogStage.showAndWait();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean showProductEditDialog(Product product) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Editar producto");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Set the product into the controller.
			ProductEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(product);
			
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public ProductList showPurchaseDialog() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PurchaseDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Compra");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Uhh... lets see here...
			PurchaseDialogController controller = loader.getController();
			controller.setMainApp(this);
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
			
			history.add(controller.getPurchase());
			saveHistoryToFile(getHistoryFilePath());
			
			return controller.getPurchase();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public ProductList showAddStockDialog() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/AddStockDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Agregar stock");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Uhh... lets see here...
			AddStockDialogController controller = loader.getController();
			controller.setMainApp(this);
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
			
			//history.add(controller.getPurchase());
			//saveHistoryToFile(getHistoryFilePath());
			
			return controller.getProductList();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Stock");
		
		File file = new File(System.getenv("APPDATA") + "/Stock/history.xml");
		setHistoryFilePath(file);
		
		if (file.exists()) {
			loadHistoryFromFile(file);
		}
		
		Preferences prefs = Preferences.systemNodeForPackage(MainApp.class);
		if (prefs.get("cash", "NULL") == "NULL") {
			prefs.putFloat("cash", 10000.0f);
		}
		
		initRootLayout();
		
		showHome();
		
	}
	
	public void initRootLayout() {
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			primaryStage.show();
			
			setExitPrompt(scene);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		File file = getProductFilePath();
		if (file != null) {
			loadProductDataFromFile(file);
		}
	}
	
	private void setExitPrompt(Scene scene) {
		
		scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Salir");
				alert.setHeaderText("Estas seguro de que queres salir?");
				alert.setContentText("Se perderan los cambios no guardados.");
				
				ButtonType guardar_y_salir = new ButtonType("Guardar y salir");
				ButtonType salir = new ButtonType("Salir");
				ButtonType cancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
				
				alert.getButtonTypes().setAll(guardar_y_salir, salir, cancelar);

				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == salir){
					// Go ahead.
					
				} else if (result.get() == guardar_y_salir){
					// Save and let the event close the app
					saveProductDataToFile(getProductFilePath());
					saveHistoryToFile(getHistoryFilePath());
					
				} else {
					// Vanish the close event
				    event.consume();
				}
			}
		});
	}
	

	
	public File getProductFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	public void setProductFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			
			primaryStage.setTitle("Stock - " + file.getName());
		} else {
			prefs.remove("filePath");
			
			primaryStage.setTitle("Stock");
		}
	}
	
	public File getHistoryFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePathHistory", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	public void setHistoryFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePathHistory", file.getPath());
		} else {
			prefs.remove("filePathHistory");
		}
	}
	
	
	public void loadProductDataFromFile(File file) {
		try  {
			JAXBContext context = JAXBContext
					.newInstance(ProductListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			ProductListWrapper wrapper = (ProductListWrapper) um.unmarshal(file);
			
			productList.clear();
			productList.addAll(wrapper.getProducts());
			
			setProductFilePath(file);
		
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido leer los datos.");
    		alert.setContentText("Imposible leer archivo:\n" + file.getAbsolutePath());
    		
    		alert.showAndWait();
		}
	}
	
	public void saveProductDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext
					.newInstance(ProductListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			ProductListWrapper wrapper = new ProductListWrapper();
			wrapper.setProducts(productList);
			
			m.marshal(wrapper, file);
			
			setProductFilePath(file);
		
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido guardar los datos.");
    		alert.setContentText("Imposible escribir el archivo:\n" + file.getAbsolutePath());
    		
    		alert.showAndWait();
		}
	}
	
	public void loadHistoryFromFile(File file) {
		try  {
			JAXBContext context = JAXBContext
					.newInstance(HistoryWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			HistoryWrapper wrapper = (HistoryWrapper) um.unmarshal(file);
			
			history.clear();
			history.addAll(wrapper.getHistory());
			
			setHistoryFilePath(file);
		
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido leer el historial.");
    		alert.setContentText("Imposible leer archivo:\n" + file.getAbsolutePath());
    		
    		alert.showAndWait();
		}
	}
	
	public void saveHistoryToFile(File file) {
		try {
			JAXBContext context = JAXBContext
					.newInstance(HistoryWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			HistoryWrapper wrapper = new HistoryWrapper();
			wrapper.setHistory(history);
			
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			
			if (!file.exists())
				file.createNewFile();
			
			m.marshal(wrapper, file);
			
			setHistoryFilePath(file);
			
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText("No se ha podido guardar el historial.");
    		alert.setContentText("Imposible escribir el archivo:\n" + file.getAbsolutePath());
    		
    		alert.showAndWait();
		}
	}
	
	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
