package net.starkus.stock.control;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class DecentContextMenu extends Popup {
	
	private final VBox vbox = new VBox();
	private final Pane pane = new Pane(vbox);
	
	private final int maxEntries = 6;
	
	private final SelectionModel<String> selectionModel;
	private final IntegerProperty hoverIndex = new SimpleIntegerProperty(1);
	
	private final ObservableList<String> entries = FXCollections.observableArrayList();
	private final List<Button> buttonList;
	
	
	public DecentContextMenu() {
		
		pane.getStylesheets().add(DecentContextMenu.class.
				getResource("DecentContextMenu.css").toExternalForm());
		
		getContent().add(pane);
		
		buttonList = new ArrayList<>();
		for (int i=0; i < maxEntries; ++i) {
			
			Button b = new Button("Entry");
			b.getStyleClass().add("item");
			b.setMaxWidth(Double.MAX_VALUE);
			
			final int _i = i;
			b.setOnMouseEntered(e -> {
				hoverIndex.set(_i);
			});
			
			buttonList.add(b);
		}
		
		selectionModel = new SingleSelectionModel<String>() {
			
			@Override
			public void selectPrevious() {
				select((getSelectedIndex() + maxEntries - 1) % maxEntries);
			}
			
			@Override
			public void selectNext() {
				select((getSelectedIndex() + 1) % maxEntries);
			}
			
			@Override
			public void selectLast() {
				select(maxEntries - 1);
			}
			
			@Override
			public void selectFirst() {
				select(0);
			}
			
			@Override
			public void select(String obj) {
				
				if (!entries.contains(obj))
					return;
				
				int i = entries.indexOf(obj);
				setSelectedIndex(i);
				setSelectedItem(obj);
			}
			
			@Override
			public void select(int index) {
				
				if (index < 0 || index >= entries.size())
					return;
				
				setSelectedIndex(index);
				setSelectedItem(getModelItem(index));
			}
			
			@Override
			public boolean isSelected(int index) {
				return getSelectedIndex() == index;
			}

			@Override
			protected int getItemCount() {
				return entries.size();
			}

			@Override
			protected String getModelItem(int index) {
				return entries.get(index);
			}
		};
		
		selectionModel.selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				
				for (int i=0; i < maxEntries; ++i) {
					
					if (i == newValue.intValue()) {
						buttonList.get(i).getStyleClass().add("item-selected");
					}
					else {
						buttonList.get(i).getStyleClass().remove("item-selected");
					}
				}
			}
		});
		
		hoverIndex.addListener((obs, oldValue, newValue) -> {
			selectionModel.select(newValue.intValue());
		});
		
		entries.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
				
				vbox.getChildren().clear();
				
				int entriesSize = Math.min(maxEntries, entries.size());
				
				for (int i=0; i < entriesSize; ++i) {
					
					buttonList.get(i).setText(entries.get(i));
					vbox.getChildren().add(buttonList.get(i));
				}
				
				selectionModel.selectFirst();
			}
			
		});
		
		pane.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.UP) {
				selectionModel.selectPrevious();
				e.consume();
			}
			else if (e.getCode() == KeyCode.DOWN) {
				selectionModel.selectNext();
				e.consume();
			}
		});
	}
	
	public void setOnMouseClicked(EventHandler<? super MouseEvent> event) {
		buttonList.forEach(b -> b.setOnMouseClicked(event));
	}

	public SelectionModel<String> getSelectionModel() {
		return selectionModel;
	}
	
	public ObservableList<String> getEntries() {
		return entries;
	}
	
	public void show(Control ownerNode, Side side) {
		
		double x=0, y=0;
		
		switch (side) {
		case BOTTOM:
			y = ownerNode.getHeight();
			
		case TOP:
			vbox.prefWidthProperty().bind(ownerNode.widthProperty());
			break;
			
		case RIGHT:
			x = ownerNode.getWidth();
			
		case LEFT:
			//pane.setPrefHeight(ownerNode.getHeight());
			break;
		}
		
		javafx.geometry.Point2D screen = ownerNode.localToScreen(x, y);
		
		super.show(ownerNode, screen.getX(), screen.getY());
	}
}
