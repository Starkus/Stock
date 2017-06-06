package net.starkus.stock.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchEngine {
	
	private static String cleanString(String s) {
		/*
		 * Remove accent notations and turn to lower case.
		 */
		s = s.toLowerCase();

		s = s.replace("-", "");
		s = s.replace("_", "");
		s = s.replace(",", ".");

		s = s.replace('�', 'a');
		s = s.replace('�', 'e');
		s = s.replace('�', 'i');
		s = s.replace('�', 'o');
		s = s.replace('�', 'u');
		
		return s;
	}
	
	public static List<String> filterList(String filter, Collection<String> entries) {
		
		List<String> searchResults = new ArrayList<>();
		
		String cText = cleanString(filter);
		
		for (String e : entries) {
			
			String cEntry = cleanString(e);
			
			if (cEntry.startsWith(cText) || cEntry.contains(" " + cText))
				searchResults.add(e);
		}
		
		for (String e : entries) {
			
			String cEntry = cleanString(e);
			
			if (cEntry.contains(cText.replace(" ", "")) && !searchResults.contains(e))
				searchResults.add(e);
		}
		
		return searchResults;
	}

}
