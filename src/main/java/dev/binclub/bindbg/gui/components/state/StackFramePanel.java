package dev.binclub.bindbg.gui.components.state;

import com.sun.jdi.StackFrame;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.StackFrameSelectedEvent;

import javax.swing.*;

import java.awt.*;

import static dev.binclub.bindbg.util.StringUtils.escapeNonAlphaNumeric;
import static javax.swing.JList.VERTICAL;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.*;

/**
 * Shows the JVM call stack
 */
public class StackFramePanel extends JPanel {
	private final VmConnection vm;
	private final DefaultListModel<StackFrame> callStackModel;
	private final JList<StackFrame> callStacks;
	
	public StackFramePanel(VmConnection vm) {
		this.vm = vm;
		
		this.callStackModel = new DefaultListModel<>();
		this.callStacks = new JList<>(callStackModel);
		callStacks.setSelectionMode(SINGLE_SELECTION);
		callStacks.setLayoutOrientation(VERTICAL);
		callStacks.setVisibleRowCount(-1);
		callStacks.addListSelectionListener((e) -> {
			var debugContext = vm.debugContext;
			
			var debuggingFrame = callStacks.getSelectedValue();
			if (debuggingFrame != debugContext.debuggingFrame) {
				debugContext.debuggingFrame = debuggingFrame;
				vm.eventManager.dispatch(new StackFrameSelectedEvent(debuggingFrame));
			}
		});
		callStacks.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				var frame = (StackFrame) value;
				var loc = frame.location();
				var methodName = escapeNonAlphaNumeric(loc.method().toString());
				return super.getListCellRendererComponent(list, methodName, index, isSelected, cellHasFocus);
			}
		});
		
		var scrollPane = new JScrollPane(
			callStacks,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	public void refresh() {
		callStackModel.removeAllElements();
		callStacks.setEnabled(false);
		
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
