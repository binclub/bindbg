package dev.binclub.bindbg.connection;

import com.sun.jdi.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;
import dev.binclub.bindbg.connection.event.VmEventManager;
import dev.binclub.bindbg.connection.event.VmPauseEvent;
import dev.binclub.bindbg.connection.event.VmResumeEvent;
import dev.binclub.bindbg.gui.context.DebugContext;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.binclub.bindbg.connection.VmUtils.connect;

public class VmConnection {
	public final ConnectionThread conThread;
	public final VmEventManager eventManager;
	public final DebugContext debugContext;
	
	private final VirtualMachine vm;
	private final VMState state;
	
	public VmConnection(String host, int port) throws IOException, IllegalConnectorArgumentsException {
		vm = connect(host, port);
		state = VMState.get(vm);
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
	
	public List<ThreadReference> threads() {
		return vm.allThreads();
	}
	
	public boolean resume() {
		if (isSuspended()) {
			vm.resume();
			eventManager.dispatch(new VmResumeEvent());
			System.out.println("Resumed");
			return true;
		}
		System.out.println("Not Resumed");
		return false;
	}
	
	public boolean suspend() {
		if (isSuspended()) {
			vm.suspend();
			eventManager.dispatch(new VmPauseEvent());
			System.out.println("Paused");
			return true;
		}
		System.out.println("Not Paused");
		return false;
	}
	
	public boolean isSuspended() {
		return state.isSuspended();
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
	
	/**
	 * Wrapper class for the private com.sun.tools.jdi.VMState class
	 */
	private static class VMState {
		private static MethodHandle stateGet;
		private static MethodHandle isSuspendedMeth;
		
		static {
			try {
				var lookup = MethodHandles.lookup();
				var vmImpl = Class.forName("com.sun.tools.jdi.VirtualMachineImpl");
				var stateCls = Class.forName("com.sun.tools.jdi.VMState");
				
				var stateField = vmImpl.getDeclaredField("state");
				stateField.setAccessible(true);
				stateGet = lookup.unreflectGetter(stateField);
				
				var isSuspended = stateCls.getDeclaredMethod("isSuspended");
				isSuspended.setAccessible(true);
				isSuspendedMeth = lookup.unreflect(isSuspended);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		public static VMState get(VirtualMachine vm) {
			try {
				return new VMState(stateGet.invoke(vm));
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}
		
		private final Object inner;
		
		private VMState(Object inner) {
			this.inner = inner;
		}
		
		public boolean isSuspended() {
			try {
				return (boolean) isSuspendedMeth.invoke(inner);
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}
	}
}
