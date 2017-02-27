package net.starkus.stock;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Product;
import net.starkus.stock.util.PasswordUtils;
import net.starkus.stock.util.SaveUtil;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.view.AddStockDialogController;
import net.starkus.stock.view.ChangePasswordDialogController;
import net.starkus.stock.view.ClientOverviewController;
import net.starkus.stock.view.DebtAssignDialogController;
import net.starkus.stock.view.DialogController;
import net.starkus.stock.view.HomeController;
import net.starkus.stock.view.ProductEditDialogController;
import net.starkus.stock.view.ProductOverviewController;
import net.starkus.stock.view.PurchaseDialogController;
import net.starkus.stock.view.RootLayoutController;
import net.starkus.stock.view.SetCashDialogController;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private ObservableList<Product> productList = FXCollections.observableArrayList();
	private SortedList<Product> sortedProducts;
	
	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	private SortedList<Client> sortedClients;
	
	private ObservableList<ProductList> history = FXCollections.observableArrayList();
	
	private String password;
	
	private File savefile;
	
	private HomeController homeController;
	private DialogController currentDialogController;
	
	
	String css;

	
	
	public MainApp() {
		
		// Sample data
		password = PasswordUtils.encodePassword("tigre");
		
		productList.add(new Product(78600010L, "TicTac Menta", 8, 13));
		productList.add(new Product(78605831L, "TicTac Dupla Frutilla", 8, 13));
		productList.add(new Product(8537023942L, "Xbox 360 Wireless Controller for Windows", 760, 1200));
		productList.add(new Product(790520009944L, "Raid Casa y Jardin", 16, 24));
		
		clientList.add(new Client("Castor", 1337));
		
		sortedProducts = productList.sorted();
		sortedClients = clientList.sorted();
		
		
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
	
	
	public ObservableList<Client> getClients() {
		return clientList;
	}
	
	public SortedList<Client> getSortedClients() {
		return sortedClients;
	}
	
	
	public ObservableList<ProductList> getHistory() {
		return history;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public HomeController getHomeController() {
		return homeController;
	}
	
	public DialogController getCurrentDialogController() {
		return currentDialogController;
	}
	
	public File getSavefile() {
		return savefile;
	}
	
	
	public void setTitle(String s) {
		primaryStage.setTitle(s);
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
			
			homeController = controller;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client showDebtDialog() {
		try {
			// Load up dialog
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/DebtAssignDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Deuda");
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			DebtAssignDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);
			
			dialogStage.showAndWait();
			
			return controller.getClient();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void showSetCashDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SetCashDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Caja");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(false);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			SetCashDialogController controller = loader.getController();
			controller.setMainApp(this);
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showPasswordChangeDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ChangePasswordDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Cambiar contraseña");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(false);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			ChangePasswordDialogController controller = loader.getController();
			controller.setMainApp(this);
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
			
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
			//controller.setDialogStage(dialogStage); dunno what its for
			controller.setMainApp(this);
			
			dialogStage.showAndWait();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showClientOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ClientOverview.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Clientes");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			ClientOverviewController controller = loader.getController();
			
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
			
			SaveUtil.saveToFile(savefile);
			
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
		
		// I'm not sure how much this property can vary, so I try not to be
		// very specific here. It doesn't take long and is done just once so...
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			savefile = new File(System.getenv("APPDATA") + "/Stock/save.xml");
		else
			savefile = new File(System.getProperty("user.home") + "/Stock/save.xml");
		
		// SaveUtil needs a reference to this instance.
		SaveUtil.setMainApp(this);
		
		if (savefile.exists()) {
			SaveUtil.loadFromFile(savefile);
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
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
