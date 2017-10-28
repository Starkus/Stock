package net.starkus.stock.save;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.MainApp;
import net.starkus.stock.legacy.UpdateSavefile;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.History;
import net.starkus.stock.model.LegacyDebt;
import net.starkus.stock.model.Payment;
import net.starkus.stock.model.Product;
import net.starkus.stock.model.ProductBox;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.model.Sale;
import net.starkus.stock.model.Transaction;

public class SaveUtil {
	
	private static File savefile;
	private static MainApp mainApp;
	
	
	
	public static void setMainApp(MainApp m) {
		mainApp = m;

		// I'm not sure how much this property can vary, so I try not to be
		// very specific here. It doesn't take long and is done just once so...
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			savefile = new File(System.getenv("APPDATA") + "/Stock/save.xml");
		else
			savefile = new File(System.getProperty("user.home") + "/Stock/save.xml");
		
		if (savefile.exists()) {
			SaveUtil.loadFromFile(savefile);
		}
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
			
			String version = wrapper.getVersion();
			String[] digits = version.split("\\.");
			
			if (version == null ||
					(Integer.parseInt(digits[0]) <= 5 && Integer.parseInt(digits[1]) <= 1)) { // Old savefile
			
				
				System.out.println("Attempting to update old save file...");
				
				// Back up old file
				Files.deleteIfExists(new File(file.getParentFile() + "/save.backup").toPath());
				Files.copy(file.toPath(), new File(file.getParentFile() + "/save.backup").toPath());
				
				// Update savefile
				UpdateSavefile.run(file.getName(), file.getName());
				
				// Run function again and return
				loadFromFile(file);
				
				return;
			}
			
			
			ProductListWrapper productsWrapper = wrapper.getProducts();
			HistoryWrapper historyWrapper = wrapper.getHistory();
			
			if (productsWrapper != null) {
				ObservableList<Product> productList = ProductBox.getProducts();
				productList.clear();
				productList.addAll(productsWrapper.getProducts());
			}			
			
			if (historyWrapper != null) {
				ObservableList<Transaction> history = History.getHistory();
				history.clear();
				//history.addAll(historyWrapper.getHistory());
				for (TransactionWrapper tw : historyWrapper.getHistory()) {
					
					Transaction t = null;
					switch (tw.getType()) {
					case LEGACYDEBT:
						t = new LegacyDebt();
						break;
						
					case PAYMENT:
						t = new Payment();
						break;
						
					case PURCHASE:
						t = new Purchase();
						
						if (tw.getProducts() != null)
							((Purchase) t).addAll(tw.getProducts());
						else
							continue;
						
						break;
						
					case SALE:
						t = new Sale();
						
						if (tw.getProducts() != null)
							((Sale) t).addAll(tw.getProducts());
						else
							continue;
						
						break;
						
					default:
						
						new IOException("Invalid transaction type found on save file!").printStackTrace();
						
						break;
					}
					
					t.setClient(tw.getClient());
					t.setBalance(tw.getBalance());
					t.setCreationDate(tw.getCreationDate());
					t.setCancelled(tw.getCancelled());
					
					history.add(t);
				}
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
	
	
	public static void saveToFile() {
		saveToFile(savefile);
	}
	public static void saveToFile(File file) {
		
		System.out.println("Saving to file...");
		
		if (file.getParentFile().exists() == false) {
			file.getParentFile().mkdirs();
		}
		
		try {
			JAXBContext context = JAXBContext
					.newInstance(SavefileWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Get the stuff from mainApp.
			ObservableList<Product> productList = ProductBox.getProducts();
			ObservableList<Transaction> history = History.getHistory();
			
			// Make the sub-wrappers
			ProductListWrapper productsWrapper = new ProductListWrapper();
			HistoryWrapper historyWrapper = new HistoryWrapper();
			// Main wrapper
			SavefileWrapper wrapper = new SavefileWrapper();
			
			// Fill in the wrappers
			productsWrapper.setProducts(productList);
			//historyWrapper.setHistory(history);
			historyWrapper.setHistory(new ArrayList<TransactionWrapper>());
			for (Transaction t : history) {
				TransactionWrapper tw = new TransactionWrapper();
				
				
				switch (t.getType()) {
				case SALE: case PURCHASE:
					tw.setProducts(((Sale) t).getProductData());
					
				default:
					tw.setType(t.getType());
					
					break;
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
			wrapper.setPassword(mainApp.getPassword());
			
			wrapper.setVersion(MainApp.getVersion());
			
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
