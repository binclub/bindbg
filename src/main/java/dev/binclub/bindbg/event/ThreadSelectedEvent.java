package dev.binclub.bindbg.event;

import com.sun.jdi.ThreadReference;

/**
 * A new thread was selected to be debugged
 */
public class ThreadSelectedEvent {
	public final ThreadReference debuggingThread;
	
	public ThreadSelectedEvent(ThreadReference debuggingThread) {
		this.debuggingThread = debuggingThread;
	}
}
