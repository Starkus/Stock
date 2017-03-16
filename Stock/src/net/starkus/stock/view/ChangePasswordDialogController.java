package net.starkus.stock.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.model.AlertWrapper;
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
			
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR)
					.setTitle("Error de autenticaci�n")
					.setHeaderText("Contrase�a incorrecta")
					.setContentText("Verifique la contrase�a vieja ingresada");
			
			alert.showAndWait();
			return;
		}
		
		if (!doPasswordsMatch()) {
			
			AlertWrapper alert = new AlertWrapper(AlertType.ERROR)
					.setTitle("Error")
					.setHeaderText("Las contrase�as no coinciden")
					.setContentText("Vuelva a escribir la contrase�a reemplazante.");
			
			alert.showAndWait();
			
			newPass1.requestFocus();
		}
		
		String pass = newPass1.getText();
		mainApp.setPassword(PasswordUtils.encodePassword(pass));
		
		AlertWrapper alert = new AlertWrapper(AlertType.INFORMATION)
				.setTitle("Listo")
				.setHeaderText("La contrase�a se ha cambiado con �xito.");
		
		alert.showAndWait();
		
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
