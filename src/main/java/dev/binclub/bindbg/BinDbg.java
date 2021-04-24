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

package dev.binclub.bindbg;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import dev.binclub.bindbg.gui.ConnectionWindow;
import dev.binclub.bindbg.util.SwingUtils;

public class BinDbg {
	public static void main(String[] args) {
		gui();
	}
	
	private static void gui() {
		System.setProperty("awt.useSystemAAFontSettings", "lcd_hrgb");
		LafManager.install();
		LafManager.installTheme(new DarculaTheme());
		
		var connWindow = new ConnectionWindow();
		SwingUtils.putWindowInMouseScreen(connWindow, 350);
		connWindow.setVisible(true);
	}
}
