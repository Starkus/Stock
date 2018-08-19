package net.starkus.stock.control;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimatedToggleButton extends ToggleButton {

	final Color backgroundOff = Color.rgb(0xbd, 0xc3, 0xc7);
	final Color backgroundOn = Color.rgb(0x20, 0x24, 0x3d);
	
	final Color handleOff = Color.rgb(0x7f, 0x8c, 0x8d);
	final Color handleOn = Color.rgb(0xf8, 0xb1, 0x52);
	
	private Timeline turnOn, turnOff;
	private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<Color>(backgroundOff);
	private final ObjectProperty<Color> handleColor = new SimpleObjectProperty<Color>(handleOff);

	private final StringProperty colorStringProperty = createColorStringProperty(backgroundColor);
	private final StringProperty handleColorStringProperty = createColorStringProperty(handleColor);
	
	private final FloatProperty handlePositionProperty = new SimpleFloatProperty(-0.5f);
	
	private final Region handle = new Region();
	
	
	
	public AnimatedToggleButton() {
		super();
		
		createTimelines();
		bindAnimations();
		
		selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == true)
					turnOn.play();
				else
					turnOff.play();
			}
		});
		
		handle.setMaxSize(20, 20);
		
		setGraphic(handle);
		handle.translateXProperty().bind(Bindings.multiply(Bindings.subtract(widthProperty(), heightProperty()), handlePositionProperty));
	}
	
	private void createTimelines() {
		
		final double duration = 0.1;
		
		turnOn = new Timeline(
    			new KeyFrame(Duration.seconds(0),			new KeyValue(backgroundColor, backgroundOff)),
    			new KeyFrame(Duration.seconds(0),			new KeyValue(handleColor, handleOff)),
    			new KeyFrame(Duration.seconds(0),			new KeyValue(handlePositionProperty, -0.5f)),
    			
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(backgroundColor, backgroundOn)),
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(handleColor, handleOn)),
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(handlePositionProperty,  0.5f))
    	);

		turnOff = new Timeline(
    			new KeyFrame(Duration.seconds(0),			new KeyValue(backgroundColor, backgroundOn)),
    			new KeyFrame(Duration.seconds(0),			new KeyValue(handleColor, handleOn)),
    			new KeyFrame(Duration.seconds(0),			new KeyValue(handlePositionProperty,  0.5f)),
    			
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(backgroundColor, backgroundOff)),
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(handleColor, handleOff)),
    			new KeyFrame(Duration.seconds(duration),	new KeyValue(handlePositionProperty, -0.5f))
    	);
	}
	
	private void bindAnimations() {
		
		styleProperty().bind(
				new SimpleStringProperty("-fx-background-color: ")
				.concat(colorStringProperty)
				.concat(";")
		);
		
		handle.styleProperty().bind(
				new SimpleStringProperty("-fx-background-radius: 20px; -fx-background-color: ")
				.concat(handleColorStringProperty)
				.concat(";")
		);
	}
	
	private StringProperty createColorStringProperty(ObjectProperty<Color> observableColor) {
		
		final StringProperty prop = new SimpleStringProperty();
		setColorStringFromColor(prop, observableColor);
		
		observableColor.addListener(new ChangeListener<Color>() {

			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				setColorStringFromColor(prop, observableColor);
			}
		});
		
		return prop;
	}
	
	private void setColorStringFromColor(StringProperty colorStringProperty, ObjectProperty<Color> color) {
        colorStringProperty.set(
                "rgba("
                        + ((int) (color.get().getRed()   * 255)) + ","
                        + ((int) (color.get().getGreen() * 255)) + ","
                        + ((int) (color.get().getBlue()  * 255)) + ","
                        + color.get().getOpacity() +
                ")"
        );
    }
}
