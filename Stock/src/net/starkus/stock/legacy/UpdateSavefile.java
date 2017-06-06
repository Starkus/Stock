package net.starkus.stock.legacy;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.LegacyDebt;
import net.starkus.stock.model.Payment;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.model.Transaction;
import net.starkus.stock.save.HistoryWrapper;
import net.starkus.stock.save.ProductListWrapper;
import net.starkus.stock.save.SavefileWrapper;
import net.starkus.stock.save.TransactionWrapper;
import net.starkus.stock.save.TransactionWrapper.TransactionType;

public class UpdateSavefile {
	
	private static File savefile;
	
	
	private static ObservableList<Product> productList = FXCollections.observableArrayList();
	private static ObservableList<Transaction> history = FXCollections.observableArrayList();
	private static String password;
	
	
	
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
					.newInstance(LegacySavefileWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			
			LegacySavefileWrapper wrapper = (LegacySavefileWrapper) um.unmarshal(file);
			
			ProductListWrapper productsWrapper = wrapper.getProducts();
			ClientListWrapper clientsWrapper = wrapper.getClients();
			
			if (productsWrapper != null) {
				productList.clear();
				productList.addAll(productsWrapper.getProducts());
			}
			
			if (clientsWrapper != null) {
				
				for (LegacyClient c : clientsWrapper.getClients()) {
					LegacyDebt ld = new LegacyDebt();
					ld.setClient(c.getName());
					ld.setBalance(-c.getBalance());
					ld.setCreationDate(LocalDateTime.now());
					
					history.add(ld);
				}
			}
			
			CashBox.setCash(wrapper.getCashBox());
			
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
				
				if (t.getClass().equals(LegacyDebt.class)) {
					tw.setType(TransactionType.LEGACYDEBT);
				}
				else if (t.getClass().equals(Payment.class)) {
					tw.setType(TransactionType.PAYMENT);
				}
				else if (t.getClass().equals(Purchase.class)) {
					tw.setType(TransactionType.PURCHASE);
					
					tw.setProducts(((Purchase) t).getProductData());
				}
				
				tw.setClient(t.getClient());
				tw.setCreationDate(t.getCreationDate());
				tw.setBalance(t.getBalance());
				tw.setCancelled(t.getCancelled());
				
				historyWrapper.getHistory().add(tw);
			}
			
			wrapper.setProducts(productsWrapper);
			wrapper.setHistory(historyWrapper);
			wrapper.setCashBox(CashBox.getCash());
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
