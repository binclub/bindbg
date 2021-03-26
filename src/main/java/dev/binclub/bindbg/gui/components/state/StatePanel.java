package dev.binclub.bindbg.gui.components.state;

import dev.binclub.bindbg.connection.VmConnection;
import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.util.Alignment;

/**
 * Panel to show the state of a selected thread in the current Virtual Machine
 *
 * This includes information like the stack and the bytecode of the methods on the stack
 */
public class StatePanel extends JTabFrame {
	private final BytecodePanel bytecode;
	private final StackFramePanel stack;
	private final LocalVariablePanel variables;
	
	public StatePanel(VmConnection vm) {
		bytecode = new BytecodePanel(vm);
		stack = new StackFramePanel(vm);
		variables = new LocalVariablePanel(vm);
		
		this.setContent(bytecode); // the center content
		this.addTab(stack, "Call Stack", Alignment.SOUTH_WEST);
		this.addTab(variables, "Local Variables", Alignment.SOUTH_EAST);
	}
	
	public void refresh() {
		bytecode.refresh();
		stack.refresh();
	}
}
