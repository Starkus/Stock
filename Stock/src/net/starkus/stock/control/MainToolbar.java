package net.starkus.stock.control;

import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import net.starkus.stock.MainApp;
import net.starkus.stock.model.Admin;
import net.starkus.stock.model.CashBox;
import net.starkus.stock.model.Dialog;
import net.starkus.stock.model.History;
import net.starkus.stock.model.Payment;
import net.starkus.stock.model.Purchase;
import net.starkus.stock.model.Sale;
import net.starkus.stock.save.SaveUtil;
import net.starkus.stock.util.ExceptionUtil;
import net.starkus.stock.view.AddStockDialogController;
import net.starkus.stock.view.PasswordDialogController;
import net.starkus.stock.view.PaymentDialogController;
import net.starkus.stock.view.SaleDialogController;

public class MainToolbar extends ToolBar {
	
	final AdminButton adminButton;
	final BigButton purchaseButton;
	final BigButton saleButton;
	final BigButton paymentButton;
	
	final Pane leftSpacer, rightSpacer;
	
	final HBox leftItems;
	final SearchBox searchBox;
	
	
	public MainToolbar() {
		super();
		
		// Admin mode toggle button
		adminButton = new AdminButton();
		
		// New payment icon
		Image image = new Image(MainApp.class.getResource("media/payment_icon.png").toExternalForm());
		purchaseButton = new BigButton(image, "Compra");
		purchaseButton.setOnAction(e -> handleNewPurchase());
		
		// New sale button
		image = new Image(MainApp.class.getResource("media/sale_icon.png").toExternalForm());
		saleButton = new BigButton(image, "Venta");
		saleButton.setDefaultButton(true);
		saleButton.setPrefWidth(85);
		saleButton.setPrefHeight(85);
		saleButton.setOnAction(e -> handleNewSale());
		
		// New payment icon
		image = new Image(MainApp.class.getResource("media/payment_icon.png").toExternalForm());
		paymentButton = new BigButton(image, "Pago");
		paymentButton.setOnAction(e -> handleNewPayment());
		
		// Spacers
		leftSpacer = new Pane();
		HBox.setHgrow(leftSpacer, Priority.SOMETIMES);
		rightSpacer = new Pane();
		HBox.setHgrow(rightSpacer, Priority.SOMETIMES);
		
		// Search box
		searchBox = new SearchBox();
		searchBox.setPrefWidth(300);
		
		// Custom left items
		leftItems = new HBox(5);
		leftItems.setPrefWidth(300);
		leftItems.setAlignment(Pos.CENTER_LEFT);
		
		
		List<Node> items = getItems();
		
		items.add(leftItems);
		
		items.add(leftSpacer);
		
		items.add(purchaseButton);
		items.add(saleButton);
		items.add(paymentButton);
		
		items.add(rightSpacer);
		
		items.add(searchBox);
	}
	
	public TextField getSearchField() {
		return searchBox.getTextField();
	}
	
	public ObservableList<Node> getLeftItems() {
		return leftItems.getChildren();
	}

	
	private class AdminButton extends AnimatedToggleButton {
		
		public AdminButton() {
			super();
			
			setAlignment(Pos.BOTTOM_CENTER);
			setContentDisplay(ContentDisplay.TOP);
			
			getStyleClass().add("big-button");
			
			setPrefHeight(30);
			setPrefWidth(70);
	    	
	    	selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue == true) {
						try {
							PasswordDialogController cont = Dialog.passwordDialog.init();
							cont.showAndWait();
							
							if (!cont.wasPasswordCorrect()) {
								setSelected(false);
							}
							else {
								Admin.setAdmin(true);
							}
							
						} catch (IOException e) {
							setSelected(false); // Extra security?
							ExceptionUtil.printStackTrace(e);
						}
					}
					else {
						Admin.setAdmin(false);
					}
				}
			});
		}
	}

	private void handleNewSale() {

		try {
			SaleDialogController controller = Dialog.saleDialog.init();
			controller.showAndWait();

			Sale sale = controller.getSale();

			if (sale != null) {

				History.getHistory().add(sale);

				sale._do();

				SaveUtil.saveToFile();
			}
		} catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
		}
	}

	private void handleNewPayment() {

		try {
			PaymentDialogController controller = Dialog.paymentDialogController.init();
			controller.showAndWait();

			Payment payment = controller.getPayment();

			if (payment != null) {

				History.getHistory().add(payment);

				CashBox.put(payment.getBalance());

				SaveUtil.saveToFile();
			}
		} catch (IOException e) {

			ExceptionUtil.printStackTrace(e);
		}
	}
	
private void handleNewPurchase() {
		
		try {
			AddStockDialogController controller = Dialog.addStockDialog.init();
			controller.showAndWait();
			
			Purchase purchase = controller.getPurchase();
	    	
	    	if (purchase != null) {
	    		purchase._do();
	    		
	    		History.getHistory().add(purchase);
	        	
	        	SaveUtil.saveToFile();
	    	}
		}
		catch (IOException e) {
			
			ExceptionUtil.printStackTrace(e);
		}
	}
}
