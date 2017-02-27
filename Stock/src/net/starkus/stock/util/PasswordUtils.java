package net.starkus.stock.util;

import java.util.Random;

public class PasswordUtils {

	public static String encodePassword(String s) {
		
		String result = "";
		
		int l = s.length();
		
		Random random = new Random();
		
		for (int i = 0; i < l; i++) {
			
			String substr = "";
			
			int num = (int) s.charAt(i);
			random.setSeed(num*num*8);
			substr += (char) random.nextInt(Integer.MAX_VALUE);
			substr += (char) random.nextInt(Integer.MAX_VALUE);
			substr += (char) random.nextInt(Integer.MAX_VALUE);
			
			boolean where = random.nextBoolean();
			
			if (where) {
				result.concat(substr);
			}
			else {
				result = substr.concat(result);
			}
		}
		
		return result;
	}
}
