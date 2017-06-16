package net.starkus.stock.legacy;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.LegacyDebt;
import net.starkus.stock.model.Payment;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Sale;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.model.TransactionType;
import net.starkus.stock.save.HistoryWrapper;
import net.starkus.stock.save.ProductListWrapper;
import net.starkus.stock.save.SavefileWrapper;
import net.starkus.stock.save.TransactionWrapper;

public class UpdateSavefile {
	
	private static File savefile;
	
	
	private static ObservableList<Product> productList = FXCollections.observableArrayList();
	private static ObservableList<Transaction> history = FXCollections.observableArrayList();
	private static String password;
	private static float cash;
	
	
	
	public static void init() {

		// I'm not sure how much this property can vary, so I try not to be
		// very specific here. It doesn't take long and is done just once so...
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			savefile = new File(System.getenv("APPDATA") + "/Stock/save.xml");
		else
			savefile = new File(System.getProperty("user.home") + "/Stock/save.xml");
	}
	

	public static void loadFromFile() {
		loadFromFile(savefile);
	}
	public static void loadFromFile(File file) {
		try  {
			JAXBContext context = JAXBContext
					.newInstance(SavefileWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			SavefileWrapper wrapper = (SavefileWrapper) um.unmarshal(file);
			
			
			ProductListWrapper productsWrapper = wrapper.getProducts();
			HistoryWrapper historyWrapper = wrapper.getHistory();
			
			if (productsWrapper != null) {
				productList.clear();
				productList.addAll(productsWrapper.getProducts());
			}			
			
			if (historyWrapper != null) {
				history.clear();

				for (TransactionWrapper tw : historyWrapper.getHistory()) {
					
					Transaction t = null;
					switch (tw.getType()) {
					case LEGACYDEBT:
						t = new LegacyDebt();
						break;
						
					case PAYMENT:
						t = new Payment();
						break;
						
					case PURCHASE: case SALE:
						t = new Sale();
						
						if (tw.getProducts() != null)
							((Sale) t).addAll(tw.getProducts());
						else
							continue;
						
						break;
					}
					
					t.setClient(tw.getClient());
					t.setBalance(tw.getBalance());
					t.setCreationDate(tw.getCreationDate());
					t.setCancelled(tw.getCancelled());
					
					history.add(t);
				}
			}
			
			cash = wrapper.getCashBox();
			
			password = wrapper.getPassword();
			
		
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

	public static void saveToFile() {
		saveToFile(savefile);
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
			
			// Make the sub-wrappers
			ProductListWrapper productsWrapper = new ProductListWrapper();
			HistoryWrapper historyWrapper = new HistoryWrapper();
			// Main wrapper
			SavefileWrapper wrapper = new SavefileWrapper();
			
			// Fill in the wrappers
			productsWrapper.setProducts(productList);
			
			historyWrapper.setHistory(new ArrayList<TransactionWrapper>());
			for (Transaction t : history) {
				TransactionWrapper tw = new TransactionWrapper();
				
				tw.setType(t.getType());
					
				if (t.getType() == TransactionType.SALE || t.getType() == TransactionType.PURCHASE)
					tw.setProducts(((Sale) t).getProductData());
				
				tw.setClient(t.getClient());
				tw.setCreationDate(t.getCreationDate());
				tw.setBalance(t.getBalance());
				tw.setCancelled(t.getCancelled());
				
				historyWrapper.getHistory().add(tw);
			}
			
			wrapper.setProducts(productsWrapper);
			wrapper.setHistory(historyWrapper);
			wrapper.setCashBox(cash);
			wrapper.setPassword(password);
			
			wrapper.setVersion(MainApp.getVersion());
			
			m.marshal(wrapper, file);
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void run(String old, String new_) {
		init();
		loadFromFile(new File(savefile.getParent() + "/" + old));
		saveToFile(new File(savefile.getParent() + "/" + new_));
	}

}
