package dev.binclub.bindbg.util;

import java.util.regex.Pattern;

public class StringUtils {
	public static String trimStart(String str, int maxChars) {
		final String prefix = "...";
		final int prefixLength = 3;
		
		if (str.length() > maxChars) {
			int cut = str.length() - maxChars + 3;
			return prefix + str.substring(cut);
		}
		return str;
	}
	
	
	private static Pattern nonAlphaNumeric = Pattern.compile("[^a-zA-Z\\d/.:(),]");
	
	// remove all characters that are not alpha numeric, /, ., or :
	public static String escapeNonAlphaNumeric(String str) {
		return nonAlphaNumeric.matcher(str).replaceAll("");
	}
}
