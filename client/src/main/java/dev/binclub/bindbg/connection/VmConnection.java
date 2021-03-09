package dev.binclub.bindbg.connection;

import com.sun.jdi.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;
import dev.binclub.bindbg.connection.event.VmEventManager;
import dev.binclub.bindbg.connection.event.VmPauseEvent;
import dev.binclub.bindbg.connection.event.VmResumeEvent;
import dev.binclub.bindbg.gui.context.DebugContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.binclub.bindbg.connection.VmUtils.connect;

public class VmConnection {
	public final ConnectionThread conThread;
	public final VmEventManager eventManager;
	public final DebugContext debugContext;
	
	private final VirtualMachine vm;
	private final AtomicBoolean suspended = new AtomicBoolean(false);
	
	public VmConnection(String host, int port) throws IOException, IllegalConnectorArgumentsException {
		vm = connect(host, port);
		debugContext = new DebugContext();
		conThread = new ConnectionThread(this);
		eventManager = new VmEventManager();
	}
	
	public String name() {
		return vm.name();
	}
	
	public String description() {
		return vm.description();
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	public boolean resume() {
		if (suspended.compareAndSet(true, false)) {
			vm.resume();
			eventManager.dispatch(new VmResumeEvent());
			return true;
		}
		return false;
	}
	
	public boolean suspend() {
		if (suspended.compareAndSet(false, true)) {
			vm.suspend();
			eventManager.dispatch(new VmPauseEvent());
			return true;
		}
		return false;
	}
	
	public boolean isSuspended() {
		return suspended.get();
	}
	
	public void stepInto(ThreadReference thread) {
		suspend();
		var req = vm.eventRequestManager().createStepRequest(thread, StepRequest.STEP_MIN, StepRequest.STEP_INTO);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL); // suspend all threads when the step is triggered
		req.enable();
		thread.resume();
	}
	
	public void exit(int code) {
		vm.exit(code);
	}
	
	public Iterable<Event> pollEvents() {
		try {
			return vm.eventQueue().remove(0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	
	private Boolean dead = Boolean.FALSE;
	private Field versionInfoField;
	
	public boolean isDead() {
		synchronized (dead) {
			if (!dead) {
				try {
					// need to prevent cache
					if (versionInfoField == null) {
						versionInfoField = Class.forName("com.sun.tools.jdi.VirtualMachineImpl").getDeclaredField("versionInfo");
						versionInfoField.setAccessible(true);
					}
					Object old = versionInfoField.get(vm);
					versionInfoField.set(vm, null);
					
					vm.name();
					
					versionInfoField.set(vm, old);
				} catch (VMDisconnectedException e) {
					dead = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			return dead;
		}
	}
	
	public void disconnect() {
		synchronized (dead) {
			try {
				vm.dispose();
			} catch (VMDisconnectedException ignored) {}
			dead = true;
		}
	}
}
