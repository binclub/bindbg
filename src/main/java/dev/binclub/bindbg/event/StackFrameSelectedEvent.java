package dev.binclub.bindbg.event;

import com.sun.jdi.StackFrame;

public class StackFrameSelectedEvent {
	public final StackFrame stackFrame;
	
	public StackFrameSelectedEvent(StackFrame stackFrame) {
		this.stackFrame = stackFrame;
	}
}
