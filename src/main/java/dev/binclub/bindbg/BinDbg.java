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
