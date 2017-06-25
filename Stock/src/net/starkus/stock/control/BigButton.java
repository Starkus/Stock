package net.starkus.stock.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BigButton extends Button {
	
	private static PseudoClass TRANSPARENT_PSEUDO_CLASS = PseudoClass.getPseudoClass("transparent");
	
	final BooleanProperty transparent = new BooleanPropertyBase(false) {
		
		protected void invalidated() {
			pseudoClassStateChanged(TRANSPARENT_PSEUDO_CLASS, get());
		};
		
		@Override
		public String getName() {
			return "transparent";
		}
		
		@Override
		public Object getBean() {
			return BigButton.this;
		}
	};
	
	
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

	public void setTransparent(boolean f) {
		transparent.set(f);
	}
}
