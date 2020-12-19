package dev.binclub.bindbg.gui.components;

import javax.swing.*;

public class JSimpleButton extends JButton {
	public JSimpleButton() {
	}
	
	public JSimpleButton(Icon icon) {
		super(icon);
	}
	
	public JSimpleButton(String text) {
		super(text);
	}
	
	public JSimpleButton(Action a) {
		super(a);
	}
	
	public JSimpleButton(String text, Icon icon) {
		super(text, icon);
	}
	
	public void setCallback(Runnable callback) {
		this.addActionListener((l) -> {
			callback.run();
		});
	}
}
