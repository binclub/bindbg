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
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
