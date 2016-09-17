package net.starkus.stock.model;

import javafx.collections.transformation.SortedList;

public class BinarySearch {

	public static Product findProductByCode(long code, SortedList<Product> list) {
		int size = list.size();
		return findProductByCode(code, list, size/2, size/2, 0);
	}
	private static Product findProductByCode(long code, SortedList<Product> list, int index, int step, int notFoundCount) {
		
		if (notFoundCount > 4 || index < 0 || index >= list.size())
			return null;
		
		Product current = list.get(index);
		
		if (current.getCode() == code)
			return current;
		
		step /= 2;
		if (step < 1) {
			step = 1;
			notFoundCount += 1;
		}
		
		if (current.getCode() > code)
			index -= step;
		else
			index += step;
		
		return findProductByCode(code, list, index, step, notFoundCount);
	}

}
