package net.starkus.stock.model;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.starkus.stock.MainApp;
import net.starkus.stock.view.AddStockDialogController;
import net.starkus.stock.view.ChangePasswordDialogController;
import net.starkus.stock.view.ClientOverviewController;
import net.starkus.stock.view.DebtAssignDialogController;
import net.starkus.stock.view.DialogController;
import net.starkus.stock.view.HistoryViewerController;
import net.starkus.stock.view.PasswordDialogController;
import net.starkus.stock.view.ProductEditDialogController;
import net.starkus.stock.view.ProductOverviewController;
import net.starkus.stock.view.PurchaseDialogController;
import net.starkus.stock.view.SetCashDialogController;

/*
 * @author		Starkus <emilianoalejosenega@gmail.com
 * @since		0.4
 */
public class Dialog <T extends DialogController> {
	
	private static MainApp mainApp;
	
	// Here be dialogs! -------------------------------------------------------------
	
	public static final Dialog<AddStockDialogController> addStockDialog =
			new Dialog<AddStockDialogController>		("view/AddStockDialog.fxml").setTitle("Añadir stock");
	
	public static final Dialog<ChangePasswordDialogController> changePasswordDialog =
			new Dialog<ChangePasswordDialogController>	("view/ChangePasswordDialog.fxml").setTitle("Cambiar contraseña");
	
	public static final Dialog<ClientOverviewController> clientOverviewDialog =
			new Dialog<ClientOverviewController>		("view/ClientOverview.fxml").setTitle("Clientes");
	
	public static final Dialog<DebtAssignDialogController> debtAssignDialog =
			new Dialog<DebtAssignDialogController>		("view/DebtAssignDialog.fxml").setTitle("Pago insuficiente");
	
	public static final Dialog<HistoryViewerController> historyViewerDialog = 
			new Dialog<HistoryViewerController>			("view/HistoryViewer.fxml").setTitle("Historial");
	
	public static final Dialog<PasswordDialogController> passwordDialog =
			new Dialog<PasswordDialogController>		("view/PasswordDialog.fxml").setTitle("Contraseña requerida");
	
	public static final Dialog<ProductEditDialogController> productEditDialog = 
			new Dialog<ProductEditDialogController>		("view/ProductEditDialog.fxml").setTitle("Editar producto");
	
	public static final Dialog<ProductOverviewController> productOverviewDialog = 
			new Dialog<ProductOverviewController>		("view/ProductOverview.fxml").setTitle("Productos");
	
	public static final Dialog<PurchaseDialogController> purchaseDialog = 
			new Dialog<PurchaseDialogController>		("view/PurchaseDialog.fxml").setTitle("Nueva compra");
	
	public static final Dialog<SetCashDialogController> setCashDialog =
			new Dialog<SetCashDialogController>			("view/SetCashDialog.fxml").setTitle("Caja");
	
	//-------------------------------------------------------------------------------
	
	private String location;
	
	//private Stage dialogStage;
	
	//private Pane page;
	
	private T controller;
	

	private String title = "- no title -";
	private boolean resizable = true;
	private Modality modality = Modality.WINDOW_MODAL;
	
	
	/*
	 * Sets the reference to the main app used by all the dialogs.
	 */
	public static void setMainApp(MainApp mainApp) {
		Dialog.mainApp = mainApp;
	}
	
	
	/*
	 * Creates a new instance of Dialog.
	 * 
	 * @param location The location to the .fxml sheet.
	 */
	public Dialog(String location) {
		
		this.location = location;
	}
	
	
	/*
	 * Initializes an instance of the dialog and returns the controller.
	 * <p>
	 * Takes care of setting everything up according to the class fields to
	 * show up the dialog.
	 * Note that static field mainApp must be set before calling init() in
	 * any dialog.
	 * 
	 * @return The controller of the created dialog instance.
	 * @throws IOException if fxml sheet is not found.
	 */
	public T init() throws IOException {
		
		if (mainApp == null) {
			
			new NullPointerException("No MainApp set in Dialog class!").printStackTrace();
			return null;
		}
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(location));
		Pane page = (Pane) loader.load();
			
		Stage dialogStage = new Stage();
		dialogStage.setTitle(title);
		dialogStage.initModality(modality);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setResizable(resizable);
		dialogStage.setScene(new Scene(page));
			
		controller = loader.getController();
		controller.setMainApp(mainApp);
		controller.setDialogStage(dialogStage);
		controller.setPage(page);

		return controller;
	}
	
	/*
	 * Returns the controller of the type specified by the class.
	 */
	public T getController() {
		return controller;
	}
	
	/*
	 * Sets the title of the window.
	 * <p>
	 * Defines the title of the stage window. It has to be set before making the
	 * stage visible. For real time changes, use the title property of the Stage.
	 * 
	 * @param title The new title.
	 * @return A reference to itself.
	 */
	public Dialog<T> setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/*
	 * Sets if stage is resizable.
	 * <p>
	 * Defines whether the Stage is resizable or not by the user. Programatically
	 * you may still change the size of the Stage. This is a hint which allows the
	 * implementation to optionally make the Stage resizable by the user. 
	 * 
	 * @param resizable True if resizable.
	 * @return A reference to itself.
	 */
	public Dialog<T> setResizable(boolean resizable) {
		this.resizable = resizable;
		return this;
	}
	
	/*
	 * Set the stage modality.
	 * <p>
	 * Specifies the modality for this stage. This must be done prior to making the
	 * stage visible. The modality is one of: Modality.NONE, Modality.WINDOW_MODAL,
	 * or Modality.APPLICATION_MODAL.
	 * <p>
	 * Defaults to Modality.WINDOW_MODAL.
	 * 
	 * @param modality The new modality.
	 * @return A reference to itself.
	 */
	public Dialog<T> setModality(Modality modality) {
		this.modality = modality;
		return this;
	}
	
}
