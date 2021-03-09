package dev.binclub.bindbg.gui;

import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.gui.components.JSimpleButton;
import dev.binclub.bindbg.gui.context.DebugContext;

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
	}
	
	public void refresh() {
		if (vm.isSuspended()) {
			pauseBtn.setIcon(pauseAltIcon);
			resumeBtn.setIcon(resumeIcon);
		} else {
			pauseBtn.setIcon(pauseIcon);
			resumeBtn.setIcon(resumeAltIcon);
		}
	}
}
