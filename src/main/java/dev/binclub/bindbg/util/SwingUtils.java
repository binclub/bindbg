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

import java.awt.*;

public class SwingUtils {
	public static void putWindowInMouseScreen(Window window, int height) {
		var activeScreen = SwingUtils.findActiveScreen();
		if (activeScreen != null) {
			window.setBounds(activeScreen);
			float aspect = 1920f / 1080f;
			window.setSize((int) (height * aspect), height);
		}
	}
	
	public static Rectangle findActiveScreen() {
		return screenAtPoint(MouseInfo.getPointerInfo().getLocation());
	}
	
	public static Rectangle screenAtPoint(Point point) {
		var screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getScreenDevices();
		
		for (GraphicsDevice device : screenDevices) {
			var bounds = device.getDefaultConfiguration().getBounds();
			if (bounds.contains(point)) {
				return bounds;
			}
		}
		
		if (screenDevices.length > 0) {
			// First available monitor
			return screenDevices[0].getDefaultConfiguration().getBounds();
		}
		return null;
	}
}
