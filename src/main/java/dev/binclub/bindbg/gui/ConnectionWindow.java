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

import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class ConnectionWindow extends JFrame {
	private final JTextField ip = new JTextField();
	private final JTextField port = new JTextField();
	
	public ConnectionWindow() {
		this.setTitle("BinDbg");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("ip"));
		panel.add(new JLabel("port"));
		panel.add(ip);
		panel.add(port);
		add(panel, BorderLayout.NORTH);
		
		var connectBtn = new JButton("Connect");
		connectBtn.addActionListener(e -> {
			VmConnection connection = null;
			try {
				connection = new VmConnection(ip.getText().strip(), Integer.parseInt(port.getText().strip()));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			if (connection != null) {
				ConnectionWindow.this.setVisible(false);
				ConnectionWindow.this.dispose();
				
				var mainWindow = new MainWindow(connection);
				SwingUtils.putWindowInMouseScreen(mainWindow, 500);
				mainWindow.setVisible(true);
			}
		});
		this.add(connectBtn, BorderLayout.SOUTH);
		
		ip.addActionListener(e -> connectBtn.doClick());
		port.addActionListener(e -> connectBtn.doClick());
	}
}
