package dev.binclub.bindbg.gui.components;

import com.sun.jdi.ThreadReference;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.gui.BingaitIcons;
import dev.binclub.bindbg.gui.components.generic.JSimpleButton;

import javax.swing.*;
import java.awt.*;

public class ControlBar extends JPanel {
	private final VmConnection vm;
	
	private final ImageIcon pauseIcon = new ImageIcon(BingaitIcons.pauseIcon);
	private final ImageIcon pauseAltIcon = new ImageIcon(BingaitIcons.pauseAltIcon);
	private final ImageIcon resumeIcon = new ImageIcon(BingaitIcons.resumeIcon);
	private final ImageIcon resumeAltIcon = new ImageIcon(BingaitIcons.resumeAltIcon);
	
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
		
		terminateBtn = new JSimpleButton(new ImageIcon(BingaitIcons.terminateIcon));
		terminateBtn.setToolTipText("Terminate execution");
		terminateBtn.setCallback(() -> vm.exit(1));
		this.add(terminateBtn);
		
		restartBtn = new JSimpleButton(new ImageIcon(BingaitIcons.restartIcon));
		restartBtn.setToolTipText("Restart execution");
		restartBtn.setCallback(() -> {
			System.out.println("Restart execution");
		});
		this.add(restartBtn);
		
		pauseBtn = new JSimpleButton(pauseIcon);
		pauseBtn.setToolTipText("Pause execution");
		pauseBtn.setCallback(vm::suspend);
		this.add(pauseBtn);
		
		resumeBtn = new JSimpleButton(new ImageIcon(BingaitIcons.resumeIcon));
		resumeBtn.setToolTipText("Resume execution");
		resumeBtn.setCallback(vm::resume);
		this.add(resumeBtn);
		
		singleStepBtn = new JSimpleButton(new ImageIcon(BingaitIcons.singleStepIcon));
		singleStepBtn.setToolTipText("Single Step execution");
		singleStepBtn.setCallback(() -> {
			vm.suspend();
			vm.stepInto(vm.debugContext.debuggingThread);
			System.out.println("Single Step execution");
		});
		this.add(singleStepBtn);
		
		stepOverBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepOverIcon));
		stepOverBtn.setToolTipText("Step Over execution");
		stepOverBtn.setCallback(() -> {
			System.out.println("Step Over execution");
		});
		this.add(stepOverBtn);
		
		stepOutBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepOutIcon));
		stepOutBtn.setToolTipText("Step Out execution");
		stepOutBtn.setCallback(() -> {
			System.out.println("Step Out execution");
		});
		this.add(stepOutBtn);
		
		stepJavaBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepJavaIcon));
		stepJavaBtn.setToolTipText("Step Java execution");
		stepJavaBtn.setCallback(() -> {
			System.out.println("Step Java execution");
		});
		this.add(stepJavaBtn);
		
		threadBox.setRenderer(new DefaultListCellRenderer () {
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
				refresh();
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
			
			threadBox.setEnabled(false);
			threadBox.removeAllItems();
		}
	}
}
