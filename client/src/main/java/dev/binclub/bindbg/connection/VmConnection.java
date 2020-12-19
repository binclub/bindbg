package dev.binclub.bindbg.connection;

import com.sun.jdi.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import dev.binclub.bindbg.connection.event.VmEventManager;

import java.io.IOException;
import java.lang.reflect.Field;

import static dev.binclub.bindbg.connection.VmUtils.connect;

public class VmConnection {
	private final VirtualMachine vm;
	public final ConnectionThread conThread;
	public final VmEventManager eventManager;
	
	public VmConnection(String host, int port) throws IOException, IllegalConnectorArgumentsException {
		vm = connect(host, port);
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
	
	public void resume() {
		vm.resume();
	}
	
	public void suspend() {
		vm.suspend();
	}
	
	public void exit(int code) {
		vm.exit(code);
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
