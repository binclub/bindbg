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

package dev.binclub.bindbg.gui.components;

import com.sun.jdi.ThreadReference;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.ThreadSelectedEvent;
import dev.binclub.bindbg.gui.BinDbgIcons;
import dev.binclub.bindbg.gui.components.generic.JSimpleButton;

import javax.swing.*;
import java.awt.*;

public class ControlBar extends JPanel {
	private final VmConnection vm;
	
	private final ImageIcon pauseIcon = new ImageIcon(BinDbgIcons.pauseIcon);
	private final ImageIcon pauseAltIcon = new ImageIcon(BinDbgIcons.pauseAltIcon);
	private final ImageIcon resumeIcon = new ImageIcon(BinDbgIcons.resumeIcon);
	private final ImageIcon resumeAltIcon = new ImageIcon(BinDbgIcons.resumeAltIcon);
	
	private final JSimpleButton terminateBtn;
	private final JSimpleButton restartBtn;
	private final JSimpleButton pauseBtn;
	private final JSimpleButton resumeBtn;
	private final JSimpleButton singleStepBtn;
	private final JSimpleButton stepOverBtn;
	private final JSimpleButton stepOutBtn;
	private final JSimpleButton stepJavaBtn;
	
	private final JComboBox<ThreadReference> threadBox = new JComboBox<>();
	
	public ControlBar(VmConnection vm) {
		this.vm = vm;
		this.setLayout(new FlowLayout());
		
		terminateBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.terminateIcon));
		terminateBtn.setToolTipText("Terminate execution");
		terminateBtn.setCallback(() -> vm.exit(1));
		this.add(terminateBtn);
		
		restartBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.restartIcon));
		restartBtn.setToolTipText("Restart execution");
		restartBtn.setCallback(() -> {
			System.out.println("Restart execution");
		});
		this.add(restartBtn);
		
		pauseBtn = new JSimpleButton(pauseIcon);
		pauseBtn.setToolTipText("Pause execution");
		pauseBtn.setCallback(vm::suspend);
		this.add(pauseBtn);
		
		resumeBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.resumeIcon));
		resumeBtn.setToolTipText("Resume execution");
		resumeBtn.setCallback(vm::resume);
		this.add(resumeBtn);
		
		singleStepBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.singleStepIcon));
		singleStepBtn.setToolTipText("Single Step execution");
		singleStepBtn.setCallback(() -> {
			vm.suspend();
			vm.stepInto(vm.debugContext.debuggingThread);
			System.out.println("Single Step execution");
		});
		this.add(singleStepBtn);
		
		stepOverBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.stepOverIcon));
		stepOverBtn.setToolTipText("Step Over execution");
		stepOverBtn.setCallback(() -> {
			System.out.println("Step Over execution");
		});
		this.add(stepOverBtn);
		
		stepOutBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.stepOutIcon));
		stepOutBtn.setToolTipText("Step Out execution");
		stepOutBtn.setCallback(() -> {
			System.out.println("Step Out execution");
		});
		this.add(stepOutBtn);
		
		stepJavaBtn = new JSimpleButton(new ImageIcon(BinDbgIcons.stepJavaIcon));
		stepJavaBtn.setToolTipText("Step Java execution");
		stepJavaBtn.setCallback(() -> {
			System.out.println("Step Java execution");
		});
		this.add(stepJavaBtn);
		
		threadBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof ThreadReference) {
					ThreadReference ref = (ThreadReference) value;
					value = ref.name() + "@" + ref.uniqueID();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		threadBox.addActionListener((e) -> {
			var selected = (ThreadReference) threadBox.getSelectedItem();
			var ctx = vm.debugContext;
			if (ctx.debuggingThread != selected) {
				ctx.debuggingThread = selected;
				// Event will refresh the state panels
				vm.eventManager.dispatch(new ThreadSelectedEvent(selected));
			}
		});
		this.add(threadBox);
	}
	
	public void refresh() {
		if (vm.isSuspended()) {
			pauseBtn.setIcon(pauseAltIcon);
			resumeBtn.setIcon(resumeIcon);
			
			var prevSelected = vm.debugContext.debuggingThread;
			vm.debugContext.debuggingThread = null;
			
			threadBox.setEnabled(true);
			threadBox.removeAllItems();
			for (var thread : vm.threads()) {
				threadBox.addItem(thread);
				
				// This way we will only select the previously selected thread if it is
				// both non null and still a valid thread
				if (thread == prevSelected) {
					threadBox.setSelectedItem(thread);
					vm.debugContext.debuggingThread = thread;
				}
			}
		} else {
			pauseBtn.setIcon(pauseIcon);
			resumeBtn.setIcon(resumeAltIcon);
			
			threadBox.removeAllItems();
			threadBox.setEnabled(false);
		}
	}
}
