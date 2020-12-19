package dev.binclub.bindbg.gui;

import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.gui.components.JSimpleButton;

import javax.swing.*;
import java.awt.*;

public class ControlBar extends JPanel {
	private final VmConnection vm;
	
	public ControlBar(VmConnection vm) {
		this.vm = vm;
		this.setLayout(new FlowLayout());
		
		var terminateBtn = new JSimpleButton(new ImageIcon(BingaitIcons.terminateIcon));
		terminateBtn.setToolTipText("Terminate execution");
		terminateBtn.setCallback(() -> vm.exit(1));
		this.add(terminateBtn);
		
		var restartBtn = new JSimpleButton(new ImageIcon(BingaitIcons.restartIcon));
		restartBtn.setToolTipText("Restart execution");
		restartBtn.setCallback(() -> {
			System.out.println("Restart execution");
		});
		this.add(restartBtn);
		
		var pauseBtn = new JSimpleButton(new ImageIcon(BingaitIcons.pauseIcon));
		pauseBtn.setToolTipText("Pause execution");
		pauseBtn.setCallback(vm::suspend);
		this.add(pauseBtn);
		
		var resumeBtn = new JSimpleButton(new ImageIcon(BingaitIcons.resumeIcon));
		resumeBtn.setToolTipText("Resume execution");
		resumeBtn.setCallback(vm::resume);
		this.add(resumeBtn);
		
		var singleStepBtn = new JSimpleButton(new ImageIcon(BingaitIcons.singleStepIcon));
		singleStepBtn.setToolTipText("Single Step execution");
		singleStepBtn.setCallback(() -> {
			System.out.println("Single Step execution");
		});
		this.add(singleStepBtn);
		
		var stepOverBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepOverIcon));
		stepOverBtn.setToolTipText("Step Over execution");
		stepOverBtn.setCallback(() -> {
			System.out.println("Step Over execution");
		});
		this.add(stepOverBtn);
		
		var stepOutBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepOutIcon));
		stepOutBtn.setToolTipText("Step Out execution");
		stepOutBtn.setCallback(() -> {
			System.out.println("Step Out execution");
		});
		this.add(stepOutBtn);
		
		var stepJavaBtn = new JSimpleButton(new ImageIcon(BingaitIcons.stepJavaIcon));
		stepJavaBtn.setToolTipText("Step Java execution");
		stepJavaBtn.setCallback(() -> {
			System.out.println("Step Java execution");
		});
		this.add(stepJavaBtn);
	}
}
