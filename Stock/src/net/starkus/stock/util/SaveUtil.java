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
import net.starkus.stock.model.ProductListWrapper;
import net.starkus.stock.model.Purchase;
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
			
			if (productsWrapper != null) {
				ObservableList<Product> productList = mainApp.getProductData();
				productList.clear();
				productList.addAll(productsWrapper.getProducts());
			}
				
			if (clientsWrapper != null) {
				ObservableList<Client> clientList = mainApp.getClients();
				clientList.clear();
				clientList.addAll(clientsWrapper.getClients());				
			}
			
			if (historyWrapper != null) {
				ObservableList<Purchase> history = mainApp.getHistory();
				history.clear();
				history.addAll(historyWrapper.getHistory());
			}
			
			CashBox.setCash(wrapper.getCashBox());
			
			String pass = wrapper.getPassword();
			if (pass != null)
				mainApp.setPassword(pass);
			
		
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
		
		if (file.getParentFile().exists() == false) {
			file.getParentFile().mkdirs();
		}
		
		try {
			JAXBContext context = JAXBContext
					.newInstance(SavefileWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Get the stuff from mainApp.
			ObservableList<Product> productList = mainApp.getProductData();
			ObservableList<Client> clientList = mainApp.getClients();
			ObservableList<Purchase> history = mainApp.getHistory();
			
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
			wrapper.setPassword(mainApp.getPassword());
			
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
