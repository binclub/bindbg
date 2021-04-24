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

import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.util.Alignment;
import dev.binclub.bindbg.connection.VmConnection;

/**
 * Panel to show the state of a selected thread in the current Virtual Machine
 * <p>
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
