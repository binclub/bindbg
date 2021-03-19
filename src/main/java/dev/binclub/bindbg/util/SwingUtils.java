package dev.binclub.bindbg.util;

import java.awt.*;

public class SwingUtils {
	public static void putWindowInMouseScreen(Window window, int height) {
		var activeScreen = SwingUtils.findActiveScreen();
		window.setBounds(activeScreen);
		float aspect = 1920f / 1080f;
		window.setSize((int) (height * aspect), height);
	}
	
	public static Rectangle findActiveScreen() {
		return screenAtPoint(MouseInfo.getPointerInfo().getLocation());
	}
	
	public static Rectangle screenAtPoint(Point point) {
		var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		for (GraphicsDevice device : ge.getScreenDevices()) {
			var bounds = device.getDefaultConfiguration().getBounds();
			if (bounds.contains(point)) {
				return bounds;
			}
		}
		
		// First available monitor
		return ge.getScreenDevices()[0].getDefaultConfiguration().getBounds();
	}
}
