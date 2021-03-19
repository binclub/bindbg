package dev.binclub.bindbg.gui.components.state;

import com.sun.jdi.StackFrame;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.util.StringUtils;

import javax.swing.*;

import java.awt.*;

import static dev.binclub.bindbg.util.StringUtils.escapeNonAlphaNumeric;
import static javax.swing.JList.VERTICAL;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.*;

/**
 * Shows the JVM stack including the call stack, the data stack and the local variables
 */
public class StackPanel extends JPanel {
	private final VmConnection vm;
	private final DefaultListModel<StackFrame> callStackModel;
	private final JList<StackFrame> callStacks;
	
	public StackPanel(VmConnection vm) {
		this.vm = vm;
		
		this.callStackModel = new DefaultListModel<>();
		this.callStacks = new JList<>(callStackModel);
		callStacks.setSelectionMode(SINGLE_SELECTION);
		callStacks.setLayoutOrientation(VERTICAL);
		callStacks.setVisibleRowCount(-1);
		callStacks.addListSelectionListener((e) ->
            vm.debugContext.debuggingFrame = callStacks.getSelectedValue()
		);
		callStacks.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				var frame = (StackFrame) value;
				var loc = frame.location();
				var methodName = escapeNonAlphaNumeric(loc.method().toString());
				return super.getListCellRendererComponent(list, methodName, index, isSelected, cellHasFocus);
			}
		});
		
		var scrollPane = new JScrollPane(callStacks);
		scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(750, 150));
		this.add(scrollPane);
	}
	
	public void refresh() {
		callStacks.setEnabled(false);
		callStackModel.removeAllElements();
		
		try {
			var thread = vm.debugContext.debuggingThread;
			if (thread == null || !vm.isSuspended()) return;
			
			var prevSelectedFrame = vm.debugContext.debuggingFrame;
			vm.debugContext.debuggingFrame = null;
			
			var frames = thread.frames();
			for (StackFrame frame : frames) {
				callStackModel.addElement(frame);
				
				if (frame == prevSelectedFrame) {
					vm.debugContext.debuggingFrame = frame;
					callStacks.setSelectedValue(frame, true);
				}
			}
			callStacks.setEnabled(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
