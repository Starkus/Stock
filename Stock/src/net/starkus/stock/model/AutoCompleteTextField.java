package net.starkus.stock.model;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sun.javafx.event.EventUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.starkus.stock.util.SearchEngine;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField
{
	/** The existing autocomplete entries. */
	private final List<String> entries;
	
	private final List<String> searchResult;
	/** The popup used to select an entry. */
	private ContextMenu entriesPopup;
	
	private boolean autoProc = false;
	private boolean keyPressedOnThisField = false;
	
	
	
	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		
		super();
		
		entries = new ArrayList<>();
		searchResult = new LinkedList<>();
		entriesPopup = new ContextMenu();
		
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
						{
							entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
						}
						
					} else
					{
						searchResult.clear();
						entriesPopup.hide();
					}
				}
			}
		});
		
		entriesPopup.setAutoHide(true);
		entriesPopup.setConsumeAutoHidingEvents(false);
		//entriesPopup.setOnAction(this.getOnAction());

		/*
		 * Hide popup when out of focus.
		 */
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
				entriesPopup.hide();
				keyPressedOnThisField = false;
			}
		});
		
		this.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				keyPressedOnThisField = true;
			}
			
		});
		
		this.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if (autoProc && event.getCode() == KeyCode.ENTER && keyPressedOnThisField) {
					EventUtil.fireEvent(new ActionEvent(), event.getTarget());
				}
			}
		});
		
	}

	/**
	 * Get the existing set of autocomplete entries.
	 * @return The existing autocomplete entries.
	 */
	public List<String> getEntries() { return entries; }
	
	public ContextMenu getPopup() {
		return entriesPopup;
	}
	
	public List<String> getResults() {
		return searchResult;
	}
	
	public void setAutoProc(boolean b) {
		autoProc = b;
	}

	/**
	 * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
	 * @param searchResult The set of matching strings.
	 */
	private void populatePopup() {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		// If you'd like more entries, modify this line.
		int maxEntries = 10;
		int count = Math.min(searchResult.size(), maxEntries);
		for (int i = 0; i < count; i++)
		{
			final String result = searchResult.get(i);
			Label entryLabel = new Label(result);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			item.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent actionEvent) {
					setText(result);
					selectAll();
					entriesPopup.hide();
					
					//if (autoProc)
					//	getOnAction().handle(new ActionEvent());
				}
			});
			
			menuItems.add(item);
		}
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);

	}
}