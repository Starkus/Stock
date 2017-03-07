package net.starkus.stock.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.util.PasswordUtils;

public class ChangePasswordDialogController extends DialogController {
	
	@FXML
	TextField oldPass;
	@FXML
	TextField newPass1;
	@FXML
	TextField newPass2;
	
	
	@FXML
	void initialize() {
		
		oldPass.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				oldPass.selectAll();
			}
		});
		
		newPass1.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				newPass1.selectAll();
			}
		});
		
		newPass2.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				newPass2.selectAll();
			}
		});
	}
	
	
	boolean isOldPasswordCorrect() {
		
		String p = oldPass.getText();
		p = PasswordUtils.encodePassword(p);
		
		return mainApp.getPassword().equals(p);
	}
	
	boolean doPasswordsMatch() {
		
		String s1 = newPass1.getText();
		String s2 = newPass2.getText();
		
		return s1.equals(s2);
	}
	
	
	@FXML
	private void handleOK() {
		
		if (!isOldPasswordCorrect()) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error de autenticación");
			alert.setHeaderText("Contraseña incorrecta");
			alert.setContentText("Verifique la contraseña vieja ingresada");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
			
			alert.showAndWait();
			return;
		}
		
		if (!doPasswordsMatch()) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Las contraseñas no coinciden");
			alert.setContentText("Vuelva a escribir la contraseña reemplazante.");
			DialogPane pane = alert.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
			
			alert.showAndWait();
			
			newPass1.requestFocus();
		}
		
		String pass = newPass1.getText();
		mainApp.setPassword(PasswordUtils.encodePassword(pass));
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Listo");
		alert.setHeaderText("La contraseña se ha cambiado con éxito.");
		DialogPane pane = alert.getDialogPane();
		pane.getStylesheets().add(getClass().getResource("DarkMetro.css").toExternalForm());
		
		alert.showAndWait();
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
