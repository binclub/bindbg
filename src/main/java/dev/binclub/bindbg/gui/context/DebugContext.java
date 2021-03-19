package dev.binclub.bindbg.gui.context;

import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;

public class DebugContext {
	public ThreadReference debuggingThread;
	public StackFrame debuggingFrame;
}
