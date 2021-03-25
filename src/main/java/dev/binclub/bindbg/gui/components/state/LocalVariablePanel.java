package dev.binclub.bindbg.gui.components.state;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.StackFrameSelectedEvent;

import javax.swing.*;

import java.awt.*;

import static dev.binclub.bindbg.util.StringUtils.escapeNonAlphaNumeric;
import static javax.swing.JList.VERTICAL;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * Shows the local variables of the selected stack frame
 */
public class LocalVariablePanel extends JPanel {
	private final VmConnection vm;
	private final DefaultListModel<LocalVariable> variableModel;
	private final JList<LocalVariable> variables;
	
	public LocalVariablePanel(VmConnection vm) {
		this.vm = vm;
		
		this.variableModel = new DefaultListModel<>();
		this.variables = new JList<>(variableModel);
		variables.setEnabled(false);
		variables.setSelectionMode(SINGLE_SELECTION);
		variables.setLayoutOrientation(VERTICAL);
		variables.setVisibleRowCount(-1);
		variables.addListSelectionListener((e) -> {
		});
		variables.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				var frame = (StackFrame) value;
				var loc = frame.location();
				var methodName = escapeNonAlphaNumeric(loc.method().toString());
				return super.getListCellRendererComponent(list, methodName, index, isSelected, cellHasFocus);
			}
		});
		
		var scrollPane = new JScrollPane(
			variables,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
	}
}
