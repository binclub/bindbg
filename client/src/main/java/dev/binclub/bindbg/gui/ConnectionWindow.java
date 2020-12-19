package dev.binclub.bindbg.gui;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ConnectionWindow extends JFrame {
	JTextField ip = new JTextField();
	JTextField port = new JTextField();
	
	public ConnectionWindow() {
		this.setTitle("BinDbg");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,2));
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
