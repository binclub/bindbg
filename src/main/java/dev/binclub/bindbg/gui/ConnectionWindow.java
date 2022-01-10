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
import dev.binclub.bindbg.connection.VmUtils;
import dev.binclub.bindbg.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.text.NumberFormat;
import com.sun.jdi.connect.*;

public class ConnectionWindow extends JFrame {
	public ConnectionWindow() {
		this.setTitle("BinDbg");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		var tabPane = new JTabbedPane();
		for (var conn : VmUtils.connectors()) {
			var panel = new ConnectorPanel(conn);
			tabPane.addTab(panel.name(), null, panel, conn.description());
		}
		add(tabPane, BorderLayout.NORTH);
		
		var connectBtn = new JButton("Connect");
		connectBtn.addActionListener(e -> {
			var connector = (ConnectorPanel) tabPane.getSelectedComponent();
			
			VmConnection connection = null;
			try {
				connection = new VmConnection(connector.connector, connector.arguments);
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
		var showVMs = new JButton("Show running VM's");
		showVMs.addActionListener(e -> {
			JFrame runningVMs = new JFrame("JVMs:");
			runningVMs.setLayout(new BorderLayout());
			JTextArea jta = new JTextArea();
			JScrollPane sp = new JScrollPane(jta);
			jta.setEditable(false);
			String content = "";
			for(String index : VmUtils.listVirtualMachines()) {
				content = new StringBuilder().append(content).append(index).toString();
				if(!index.equals(VmUtils.listVirtualMachines().get(VmUtils.listVirtualMachines().size() - 1))) {
					content = new StringBuilder().append(content).append(System.lineSeparator()).toString();
				}
			}
			jta.setText(content);

			runningVMs.getContentPane().add(sp, BorderLayout.CENTER);
			runningVMs.setSize(400, (int) (runningVMs.preferredSize().height * 1.5));
			runningVMs.setLocationRelativeTo(null);
			runningVMs.setVisible(true);
		});
		this.add(showVMs, BorderLayout.EAST);
		this.add(connectBtn, BorderLayout.SOUTH);
	}
	
	static class ConnectorPanel extends JPanel {
		public Connector connector;
		public Map<String,? extends Connector.Argument> arguments;
		
		public ConnectorPanel(Connector connector) {
			this.connector = connector;
			this.arguments = connector.defaultArguments();
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			var desc = new JLabel(connector.description());
			this.add(desc);
			
			var argPanel = new JPanel(new SpringLayout());
			var numArgs = 0;
			
			for (Connector.Argument arg : arguments.values()) {
				var argName = new JLabel(arg.name(), JLabel.TRAILING);
				if (arg.mustSpecify()) {
					argName.setForeground(Color.red);
				}
				argPanel.add(argName);
				
				if (arg instanceof Connector.BooleanArgument) {
					var barg = (Connector.BooleanArgument) arg;
					var field = new JCheckBox();
					field.setSelected(barg.booleanValue());
					field.addActionListener((e) -> {
						barg.setValue(((Boolean) field.isSelected()).toString());
					});
					argName.setLabelFor(field);
					argPanel.add(field);
				} else if (arg instanceof Connector.IntegerArgument) {
					var iarg = (Connector.IntegerArgument) arg;
					var field = new JFormattedTextField(NumberFormat.getNumberInstance());
					field.setValue(iarg.intValue());
					field.addActionListener((e) -> {
						iarg.setValue(field.getValue().toString());
					});
					argName.setLabelFor(field);
					argPanel.add(field);
				} else if (arg instanceof Connector.SelectedArgument) {
					var sarg = (Connector.SelectedArgument) arg;
					var field = new JComboBox(sarg.choices().toArray());
					field.setSelectedItem(sarg.value());
					field.addActionListener((e) -> {
						sarg.setValue((String) field.getSelectedItem());
					});
					argName.setLabelFor(field);
					argPanel.add(field);
				} else if (arg instanceof Connector.StringArgument) {
					var sarg = (Connector.StringArgument) arg;
					var field = new JTextField(sarg.value());
					field.addActionListener((e) -> {
						sarg.setValue(field.getText());
					});
					var fieldPan = new JPanel();
					argName.setLabelFor(field);
					argPanel.add(field);
				} else {
					throw new UnsupportedOperationException("Argument " + arg.getClass());
				}
				numArgs += 1;
			}
			this.add(argPanel);
			SpringUtilities.makeCompactGrid(argPanel, numArgs, 2, 6, 6, 6, 6);
		}
		
		public String name() {
			// names returned by the connector are fully qualified by default
			// this is used to prevent conflicts but we can ignore this
			var name = connector.name();
			return name.substring(name.lastIndexOf('.') + 1);
		}
	}
}
