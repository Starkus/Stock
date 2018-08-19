package net.starkus.stock.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

public class SearchEngine {
	
	private static String cleanString(String s) {
		/*
		 * Remove accent notations and turn to lower case.
		 */
		s = s.toLowerCase();

		s = s.replace("-", " ");
		s = s.replace("_", " ");
		s = s.replace(",", ".");

		s = s.replace('á', 'a');
		s = s.replace('é', 'e');
		s = s.replace('í', 'i');
		s = s.replace('ó', 'o');
		s = s.replace('ú', 'u');
		
		return s;
	}
	
	public static <T> ArrayList<String> filterObjects(String filter, ListIterator<T> iterator, Function<T, String> function) {
		
		ArrayList<String> searchResults = new ArrayList<>();
		Map<String, Integer> weightedResults = new HashMap<>();
		
		String[] tags = cleanString(filter).split(" ");
		
		// Forward
		while (iterator.hasNext()) {
			
			int score = 0;
			
			String entry = function.apply(iterator.next());
			String cleanEntry = cleanString(entry);
			
			for (String tag : tags) {
				
				if (cleanEntry.startsWith(tag))
					score += 30;
				
				else if (cleanEntry.contains(" " + tag))
					score += 10;
				
				else if (cleanEntry.contains(tag))
					score += 2;
				
				else
					score -= 60;
			}

			if (score > 0) 
				searchResults.add(entry);
			weightedResults.put(entry, score);
		}
		
		searchResults.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				int weight1 = weightedResults.get(o1);
				int weight2 = weightedResults.get(o2);
				return Integer.compare(weight2, weight1);
			}
		});
		
		return searchResults;
	}

}
