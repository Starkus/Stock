package net.starkus.stock;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.util.PasswordUtils;
import net.starkus.stock.util.SaveUtil;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.view.DialogController;
import net.starkus.stock.view.HomeController;
import net.starkus.stock.view.RootLayoutController;

public class MainApp extends Application {
	
	private RootLayoutController rootLayoutController;
	
	private ObservableList<Product> productList = FXCollections.observableArrayList();
	private SortedList<Product> sortedProducts;
	
	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	private SortedList<Client> sortedClients;
	
	private ObservableList<Purchase> history = FXCollections.observableArrayList();
	
	private String password;
	
	private File savefile;
	
	private HomeController homeController;
	private DialogController currentDialogController;

	
	
	public MainApp() {
		
		// Sample data
		password = PasswordUtils.encodePassword("tigre");
		
		productList.add(new Product(78600010L, "TicTac Menta", 8, 13));
		productList.add(new Product(78605831L, "TicTac Dupla Frutilla", 8, 13));
		productList.add(new Product(8537023942L, "Xbox 360 Wireless Controller for Windows", 760, 1200));
		productList.add(new Product(790520009944L, "Raid Casa y Jardin", 16, 24));
		
		clientList.add(new Client("Castor", 1));
		
		sortedProducts = productList.sorted();
		sortedClients = clientList.sorted();
		
		Dialog.setMainApp(this);
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
	
	
	public ObservableList<Purchase> getHistory() {
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
		rootLayoutController.getPrimaryStage().setTitle(s);
	}
	
	
	public void showHome() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Home.fxml"));
			AnchorPane home = (AnchorPane) loader.load();
			
			rootLayoutController.getRootPage().setCenter(home);
			
			// Give the controller access to the main app
			HomeController controller = loader.getController();
			controller.setMainApp(this);
			
			homeController = controller;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		
		initRootLayout(primaryStage);

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
		
		showHome();
		
	}
	
	public void initRootLayout(Stage primaryStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			BorderPane page = (BorderPane) loader.load();
			page.getStylesheets().add(MainApp.class.getResource("DarkMetro.css").toExternalForm());
			
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Stock");
			
			rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this);
			rootLayoutController.setPrimaryStage(primaryStage);
			rootLayoutController.setRootPage(page);
			
			primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toExternalForm()));
			
			primaryStage.show();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	
	public RootLayoutController getRootLayout() {
		return rootLayoutController;
	}
	
	public Stage getPrimaryStage() {
		return rootLayoutController.getPrimaryStage();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
