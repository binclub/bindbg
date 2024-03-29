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

package dev.binclub.bindbg.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

/**
 * Red = df2e1b
 * Green = 1fa70a
 * Blue = 0f6ae5
 */
public class BinDbgIcons {
	public static final Image openFileIcon = load("openfile", 30, 30);
	public static final Image pauseIcon = load("pause", 30, 30);
	public static final Image pauseAltIcon = load("pause_alt", 30, 30);
	public static final Image restartIcon = load("restart", 30, 30);
	public static final Image resumeIcon = load("resume", 30, 30);
	public static final Image resumeAltIcon = load("resume_alt", 30, 30);
	public static final Image singleStepIcon = load("single_step", 30, 30);
	public static final Image stepJavaIcon = load("step_java", 30, 30);
	public static final Image stepOutIcon = load("step_out", 30, 30);
	public static final Image stepOverIcon = load("step_over", 30, 30);
	public static final Image terminateIcon = load("terminate", 30, 30);
	
	private static Image load(String name, int width, int height) {
		try {
			URL url = BinDbgIcons.class.getResource("/icons/" + name + ".png");
			return resize(ImageIO.read(url), width, height);
		} catch (Exception e) {
			throw new RuntimeException("Error loading BinGait icon '" + name + "'", e);
		}
	}
	
	private static Image resize(Image img, int width, int height) {
		return img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}
}
