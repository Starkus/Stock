package net.starkus.stock.control;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BigButton extends Button {
	
	public BigButton() {
		super();

		setAlignment(Pos.BOTTOM_CENTER);
		setContentDisplay(ContentDisplay.TOP);
		
		getStyleClass().add("big-button");
		
		setPrefHeight(70);
		setPrefWidth(70);
	}
	
	public BigButton(Image image, String label) {
		this();
		
		setText(label);
		setGraphic(new ImageView(image));
	}

}
