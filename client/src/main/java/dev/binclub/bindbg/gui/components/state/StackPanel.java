package dev.binclub.bindbg.gui.components.state;

import com.sun.jdi.StackFrame;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.gui.context.DebugContext;

import javax.swing.*;

/**
 * Shows the JVM stack including the call stack, the data stack and the local variables
 */
public class StackPanel extends JPanel {
	private final VmConnection vm;
	private final JPanel callStacks;
	private final StackFrame selectedFrame = null;
	
	public StackPanel(VmConnection vm) {
		this.vm = vm;
		
		this.callStacks = new JPanel();
		callStacks.setLayout(new BoxLayout(callStacks, BoxLayout.Y_AXIS));
		this.add(new JScrollPane(callStacks));
	}
	
	public void refresh() {
		try {
			var thread = vm.debugContext.debuggingThread;
			if (thread == null) return;
			var frames = thread.frames();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static class CallStackEntry extends JTextPane {
		private final StackFrame frame;
		
		private CallStackEntry(StackFrame frame) {
			this.frame = frame;
			this.setEditable(false);
			
			try {
				var loc = frame.location();
				String name = loc.declaringType().sourceName();
				// remove all characters that are not alpha numeric, /, ., or :
				name = name.replaceAll("[^a-zA-Z\\d/.:]", "");
				this.setText(name);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}
}
