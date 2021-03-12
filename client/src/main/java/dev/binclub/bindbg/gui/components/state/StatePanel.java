package dev.binclub.bindbg.gui.components.state;

import dev.binclub.bindbg.connection.VmConnection;

import javax.swing.*;

/**
 * Panel to show the state of a selected thread in the current Virtual Machine
 *
 * This includes information like the stack and the bytecode of the methods on the stack
 */
public class StatePanel extends JSplitPane {
	private final BytecodePanel bytecode;
	private final StackPanel stack;
	
	public StatePanel(VmConnection vm) {
		bytecode = new BytecodePanel(vm);
		stack = new StackPanel(vm);
		this.add(bytecode);
		this.add(stack);
		this.setDividerLocation(0.75);
	}
	
	public void refresh() {
		bytecode.refresh();
		stack.refresh();
	}
}
