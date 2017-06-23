package net.starkus.stock.control;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.starkus.stock.MainApp;


public class SearchBox extends HBox {
	
	
	private Image clear, noclear;
	private TextField textField;

	
	public SearchBox() {
		super();
		
		this.setAlignment(Pos.CENTER);
		
		Image image = new Image(MainApp.class.getResource("media/search_icon.png").toExternalForm());
		ImageView glassIcon = new ImageView(image);
		
		getChildren().add(glassIcon);
		
		
		TextField field = new TextField();
		field.setPromptText("Buscar");
		field.getStyleClass().add("search-field");
		field.setMaxHeight(30);
		field.setMinHeight(30);
		setHgrow(field, Priority.SOMETIMES);
		
		getChildren().add(field);
		textField = field;
		

		clear = new Image(MainApp.class.getResource("media/search_clear_icon.png").toExternalForm());
		noclear = new Image(MainApp.class.getResource("media/search_clear_no_icon.png").toExternalForm());
		
		ImageView clearIcon = new ImageView(noclear);
		field.textProperty().addListener((ob, o, n) -> clearIcon.setImage(n.isEmpty() ? noclear : clear));
		clearIcon.setOnMouseClicked((m) -> field.setText(""));
		
		getChildren().add(clearIcon);
	}
	
	
	public TextField getTextField() {
		return textField;
	}
}
