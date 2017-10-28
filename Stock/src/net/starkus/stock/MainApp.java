package net.starkus.stock;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.starkus.stock.legacy.UpdateSavefile;
import net.starkus.stock.model.ClientBox;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.PasswordUtils;
import net.starkus.stock.view.DialogController;
import net.starkus.stock.view.ProductOverviewController;
import net.starkus.stock.view.RootLayoutController;

public class MainApp extends Application {
	
	private RootLayoutController rootLayoutController;
	
	private String password;
	
	private ProductOverviewController homeController;
	private DialogController currentDialogController;

	
	
	public MainApp() {
		
		// Sample data
		password = PasswordUtils.encodePassword("tigre");
		
		ProductBox.getProducts().add(new Product(12345679L, "Samsung Galaxy S6", 13000, 20000));
		ClientBox.init();
		
		Dialog.setMainApp(this);
	}
	
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public ProductOverviewController getHomeController() {
		return homeController;
	}
	
	public DialogController getCurrentDialogController() {
		return currentDialogController;
	}
	
	
	public void setTitle(String s) {
		rootLayoutController.getPrimaryStage().setTitle(s);
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		
		SaveUtil.setMainApp(this);
		
		initRootLayout(primaryStage);
		
	}
	
	public void initRootLayout(Stage primaryStage) {
		
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("net.starkus.stock.locale.Locale");
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/RootLayout.fxml"), bundle);
			AnchorPane page = (AnchorPane) loader.load();

			page.getStylesheets().add(MainApp.class.getResource("Flatus.css").toExternalForm());
			page.getStylesheets().add(MainApp.class.getResource("Flatus_table.css").toExternalForm());
			page.getStylesheets().add(MainApp.class.getResource("Flatus_custom.css").toExternalForm());
			
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Stock");
			primaryStage.setMinWidth(700);
			primaryStage.setMinHeight(450);
			
			rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this);
			rootLayoutController.setPrimaryStage(primaryStage);
			rootLayoutController.setRootPage(page);
			
			primaryStage.getIcons().add(new Image(getClass().getResource("media/icon.png").toExternalForm()));
			
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
		return "0.6";
	}
}
