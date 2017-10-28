package net.starkus.stock.control;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ButtonTableCell<S> extends TableCell<S, Boolean> {
	
	protected Button button = new Button();
	
	
	public ButtonTableCell() {
		init();
	}
	
	protected void init() {}
	
	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		
		if (!empty) {
			setGraphic(button);
		}
	}

}
