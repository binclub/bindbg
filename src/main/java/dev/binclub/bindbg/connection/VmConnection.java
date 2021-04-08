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

package dev.binclub.bindbg.connection;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
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
	private final AtomicBoolean dead = new AtomicBoolean(false);
	private Field versionInfoField;
	
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
			return true;
		}
		return false;
	}
	
	public boolean suspend() {
		if (!isSuspended()) {
			vm.suspend();
			eventManager.dispatch(new VmPauseEvent());
			return true;
		}
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

	public Iterable<Event> pollEvents(long timeout) {
		try {
			return vm.eventQueue().remove(timeout);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	
	public boolean isDead() {
		if (!dead.get()) {
			try {
				// need to prevent cache
				if (versionInfoField == null) {
					versionInfoField = Class.forName("com.sun.tools.jdi.VirtualMachineImpl")
						.getDeclaredField("versionInfo");
					versionInfoField.setAccessible(true);
				}
				Object old = versionInfoField.get(vm);
				versionInfoField.set(vm, null);
				
				vm.name();
				
				versionInfoField.set(vm, old);
			} catch (VMDisconnectedException e) {
				dead.set(true);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return false;
	}
	
	public void disconnect() {
		if (dead.compareAndSet(false, true)) {
			try {
				vm.dispose();
			} catch (VMDisconnectedException ignored) {
				// Already disconnected
			} catch (Throwable t) {
				// Couldn't disconnect for whatever reason
				dead.set(false);
				throw new RuntimeException(t);
			}
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
		
		private final Object inner;
		
		private VMState(Object inner) {
			this.inner = inner;
		}
		
		public static VMState get(VirtualMachine vm) {
			try {
				return new VMState(stateGet.invoke(vm));
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
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
