package net.starkus.stock.model;


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

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.javafx.event.EventUtil;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField
{
	/** The existing autocomplete entries. */
	private final SortedSet<String> entries;
	/** Sorry I need this externally.      */
	private final LinkedList<String> searchResult;
	/** The popup used to select an entry. */
	private ContextMenu entriesPopup;
	
	private boolean autoProc = false;
	private boolean keyPressedOnThisField = false;
	
	
	
	private String cleanString(String s) {
		/*
		 * Remove accent notations and turn to lower case.
		 */
		s = s.toLowerCase();

		s = s.replace("-", "");
		s = s.replace("_", "");
		s = s.replace(",", ".");

		s = s.replace('á', 'a');
		s = s.replace('é', 'e');
		s = s.replace('í', 'i');
		s = s.replace('ó', 'o');
		s = s.replace('ú', 'u');
		
		return s;
	}
	

	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		
		super();
		
		entries = new TreeSet<>();
		searchResult = new LinkedList<>();
		entriesPopup = new ContextMenu();
		
		textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				
				searchResult.clear();
				
				if (getText().length() == 0)
				{
					entriesPopup.hide();
				} else
				{
					// Can't add them right away, cause non-cap-sensitive checking.
					//searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
					String cText = cleanString(getText());
					
					for (String e : entries) {
						
						String cEntry = cleanString(e);
						
						if (cEntry.startsWith(cText) || cEntry.contains(" " + cText))
							searchResult.add(e);
					}
					
					for (String e : entries) {
						
						String cEntry = cleanString(e);
						
						if (cEntry.contains(cText.replace(" ", "")) && !searchResult.contains(e))
							searchResult.add(e);
					}
					
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
	public SortedSet<String> getEntries() { return entries; }
	
	public ContextMenu getPopup() {
		return entriesPopup;
	}
	
	public LinkedList<String> getResults() {
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