package net.starkus.stock;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.starkus.stock.legacy.UpdateSavefile;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.PasswordUtils;
import net.starkus.stock.view.DialogController;
import net.starkus.stock.view.HomeController;
import net.starkus.stock.view.RootLayoutController;

public class MainApp extends Application {
	
	private RootLayoutController rootLayoutController;
	
	private String password;
	
	private HomeController homeController;
	private DialogController currentDialogController;

	
	
	public MainApp() {
		
		// Sample data
		password = PasswordUtils.encodePassword("tigre");
		
		ProductBox.getProducts().add(new Product(12345679L, "Samsung Galaxy S6", 13000, 20000));
		
		//clientMap.put("Castor", new Client("Castor", 1));
		
		//sortedProducts = productList.sorted();
		
		Dialog.setMainApp(this);
	}
	
	
	/*
	 * Returns the data as an observable list of Products.
	 * 
	 * @return ObservableList<Product>
	 */
	@Deprecated
	public ObservableList<Product> getProductData() {
		return ProductBox.getProducts();
	}
	
	
	@Deprecated
	public ObservableList<Transaction> getHistory() {
		return History.getHistory();
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
		
		SaveUtil.setMainApp(this);
		
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
		
		for (int i=0; i < args.length; i++) {
			
			String s = args[i];
			
			if (s.equals("-updatefile")) {
				
				System.out.println("Updating savefile...");
				
				UpdateSavefile.run(args[i+1], args[i+2]);
				
				System.out.println("Success.");
				return;
			}
		}
		
		System.out.println("Launching app...");
		
		launch(args);
	}
	
	public static String getVersion() {
		return "0.5";
	}
}
