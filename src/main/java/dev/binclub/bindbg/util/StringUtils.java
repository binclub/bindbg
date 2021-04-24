/*
 * This file is part of BinDbg.
 *
 * BinDbg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BinDbg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BinDbg.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.binclub.bindbg.util;

import java.util.regex.Pattern;

public class StringUtils {
	private static final Pattern nonAlphaNumeric = Pattern.compile("[^a-zA-Z\\d/.:(),]");
	
	public static String trimStart(String str, int maxChars) {
		final String prefix = "...";
		final int prefixLength = 3;
		
		if (str.length() > maxChars) {
			int cut = str.length() - maxChars + 3;
			return prefix + str.substring(cut);
		}
		return str;
	}
	
	// remove all characters that are not alpha numeric, /, ., or :
	public static String escapeNonAlphaNumeric(String str) {
		return nonAlphaNumeric.matcher(str).replaceAll("");
	}
}
