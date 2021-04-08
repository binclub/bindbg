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

package dev.binclub.bindbg.gui.components.generic;

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
		this.addActionListener((l) -> callback.run());
	}
}
