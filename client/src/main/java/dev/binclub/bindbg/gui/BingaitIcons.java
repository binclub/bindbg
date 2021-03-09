package dev.binclub.bindbg.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

/**
 * Red = df2e1b
 * Green = 1fa70a
 * Blue = 0f6ae5
 */
public class BingaitIcons {
	public static Image openFileIcon = load("openfile", 30, 30);
	public static Image pauseIcon = load("pause", 30, 30);
	public static Image pauseAltIcon = load("pause_alt", 30, 30);
	public static Image restartIcon = load("restart", 30, 30);
	public static Image resumeIcon = load("resume", 30, 30);
	public static Image resumeAltIcon = load("resume_alt", 30, 30);
	public static Image singleStepIcon = load("single_step", 30, 30);
	public static Image stepJavaIcon = load("step_java", 30, 30);
	public static Image stepOutIcon = load("step_out", 30, 30);
	public static Image stepOverIcon = load("step_over", 30, 30);
	public static Image terminateIcon = load("terminate", 30, 30);
	
	private static Image load(String name, int width, int height) {
		try {
			URL url = BingaitIcons.class.getResource("/icons/" + name + ".png");
			return resize(ImageIO.read(url), width, height);
		} catch (Exception e) {
			throw new RuntimeException("Error loading BinGait icon '" + name + "'", e);
		}
	}
	
	private static Image resize(Image img, int width, int height) {
		return img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}
}
