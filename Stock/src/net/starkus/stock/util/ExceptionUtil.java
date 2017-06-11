package net.starkus.stock.util;

import javafx.scene.control.Alert.AlertType;
import net.starkus.stock.model.AlertWrapper;

public class ExceptionUtil {
	
	
	public static void printStackTrace(Exception e) {
		
		e.printStackTrace();
		
		new AlertWrapper(AlertType.ERROR)
				.setTitle("Ha ocurrido un error.")
				.setHeaderText("Comunique el error a su ingeniero mas cercano.")
				.setContentText(e.getMessage())
				.showAndWait();
	}

}
