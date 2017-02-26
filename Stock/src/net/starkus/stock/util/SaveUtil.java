package net.starkus.stock.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.Client;
import net.starkus.stock.model.ClientListWrapper;
import net.starkus.stock.model.HistoryWrapper;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductList;
import net.starkus.stock.model.ProductListWrapper;
import net.starkus.stock.model.SavefileWrapper;

public class SaveUtil {
	
	private static MainApp mainApp;
	
	
	public static void setMainApp(MainApp m) {
		mainApp = m;
	}
	

	public static void loadFromFile(File file) {
		try  {
			JAXBContext context = JAXBContext
					.newInstance(SavefileWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			SavefileWrapper wrapper = (SavefileWrapper) um.unmarshal(file);
			
			ProductListWrapper productsWrapper = wrapper.getProducts();
			ClientListWrapper clientsWrapper = wrapper.getClients();
			HistoryWrapper historyWrapper = wrapper.getHistory();
			
			ObservableList<Product> productList = mainApp.getProductData();
			ObservableList<ProductList> history = mainApp.getHistory();
			ObservableList<Client> clientList = mainApp.getClients();
			
			productList.clear();
			productList.addAll(productsWrapper.getProducts());
			
			clientList.clear();
			clientList.addAll(clientsWrapper.getClients());
			
			history.clear();
			history.addAll(historyWrapper.getHistory());
			
			CashBox.setCash(wrapper.getCashBox());
			
		
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido leer los datos.");
    		alert.setContentText("Imposible leer archivo:\n" + file.getAbsolutePath());
    		
    		e.printStackTrace();
    		
    		alert.showAndWait();
		}
	}
	
	public static void saveToFile(File file) {
		try {
			JAXBContext context = JAXBContext
					.newInstance(SavefileWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Get the stuff from mainApp.
			ObservableList<Product> productList = mainApp.getProductData();
			ObservableList<Client> clientList = mainApp.getClients();
			ObservableList<ProductList> history = mainApp.getHistory();
			
			// Make the sub-wrappers
			ProductListWrapper productsWrapper = new ProductListWrapper();
			ClientListWrapper clientsWrapper = new ClientListWrapper();
			HistoryWrapper historyWrapper = new HistoryWrapper();
			// Main wrapper
			SavefileWrapper wrapper = new SavefileWrapper();
			
			// Fill in the wrappers
			productsWrapper.setProducts(productList);
			clientsWrapper.setClients(clientList);
			historyWrapper.setHistory(history);
			
			wrapper.setProducts(productsWrapper);
			wrapper.setClients(clientsWrapper);
			wrapper.setHistory(historyWrapper);
			wrapper.setCashBox(CashBox.getCash());
			
			m.marshal(wrapper, file);
			
		
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error!");
    		alert.setHeaderText("No se ha podido guardar los datos.");
    		alert.setContentText("Imposible escribir el archivo:\n" + file.getAbsolutePath());
    		
    		e.printStackTrace();
    		
    		alert.showAndWait();
		}
	}

}
