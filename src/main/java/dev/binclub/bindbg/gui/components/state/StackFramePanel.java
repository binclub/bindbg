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

package dev.binclub.bindbg.gui.components.state;

import com.sun.jdi.StackFrame;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.StackFrameSelectedEvent;
import dev.binclub.bindbg.gui.components.generic.ListBackedListModel;

import javax.swing.*;
import java.awt.*;

import static dev.binclub.bindbg.util.StringUtils.escapeNonAlphaNumeric;
import static javax.swing.JList.VERTICAL;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * Shows the JVM call stack
 */
public class StackFramePanel extends JPanel {
	private final VmConnection vm;
	private final ListModel<StackFrame> callStackModel;
	private final JList<StackFrame> callStacks;
	
	public StackFramePanel(VmConnection vm) {
		this.vm = vm;
		
		this.callStackModel = new ListBackedListModel<>(() -> {
			try {
				var thread = vm.debugContext.debuggingThread;
				if (thread == null || !vm.isSuspended()) return null;
				
				return thread.frames();
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		});
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
		var thread = vm.debugContext.debuggingThread;
		callStacks.setEnabled(thread != null && vm.isSuspended());
	}
}
