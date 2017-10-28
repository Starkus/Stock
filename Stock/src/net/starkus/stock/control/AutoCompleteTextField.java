package net.starkus.stock.control;


import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.TextField;
import net.starkus.stock.util.SearchEngine;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * JavaFX ContextMenu is a piece of shit, so I made my own from scratch.
 * @author Starkus
 */
public class AutoCompleteTextField extends TextField
{
	/** The existing autocomplete entries. */
	private final List<String> entries;
	private final List<String> searchResult;
	
	/** The popup used to select an entry. */
	private DecentContextMenu entriesPopup;
	
	private final ObjectProperty<EventHandler<ActionEvent>> secondOnAction;
	
	
	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		
		super();
		
		entries = new ArrayList<>();
		searchResult = new ArrayList<>();
		
		entriesPopup = new DecentContextMenu();
		
		AutoCompleteTextField this_ = this;
		textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				
				if (getText().length() == 0) {

					searchResult.clear();
					entriesPopup.hide();
				}
				else {
					
					searchResult.clear();
					searchResult.addAll(SearchEngine.filterObjects(newValue, entries.listIterator(), s -> s));
					
					if (entries.size() > 0)
					{
						populatePopup();
						if (!entriesPopup.isShowing())
							entriesPopup.show(this_, Side.BOTTOM);
						
					} else
					{
						searchResult.clear();
						entriesPopup.hide();
					}
				}
			}
		});
		
		secondOnAction = new SimpleObjectProperty<>();
		
		setOnAction(e -> {
			
			if (entriesPopup.getEntries().isEmpty() || entriesPopup.isShowing() == false) {
				
				if (secondOnAction.get() != null) {
					secondOnAction.get().handle(e);
				}
			}
			else {
				
				String selected = entriesPopup.getSelectionModel().getSelectedItem();

				if (selected != null) {
					setText(selected);
					positionCaret(getLength());
					selectAll();
					
					entriesPopup.hide();
				}
			}
		});
		
		entriesPopup.setOnMouseClicked(e -> {
			
			String selected = entriesPopup.getSelectionModel().getSelectedItem();
			
			if (selected != null) {
				setText(selected);
				positionCaret(getLength());
				selectAll();
				
				entriesPopup.hide();
			}
		});

		/*
		 * Hide popup when out of focus.
		 */
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
				entriesPopup.hide();
			}
		});
		
	}

	/**
	 * Get the existing set of autocomplete entries.
	 * @return The existing autocomplete entries.
	 */
	public List<String> getEntries() { return entries; }
	
	public DecentContextMenu getPopup() {
		return entriesPopup;
	}
	
	public List<String> getResults() {
		return searchResult;
	}
	
	public void setSecondOnAction(EventHandler<ActionEvent> e) {
		secondOnAction.set(e);
	}


	/**
	 * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
	 * @param searchResult The set of matching strings.
	 */
	private void populatePopup() {
		
		entriesPopup.getEntries().setAll(searchResult);
	}
}