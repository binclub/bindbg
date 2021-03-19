package dev.binclub.bindbg.connection;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import java.io.IOException;

public class VmUtils {
	static VirtualMachine connect(String host, int port) throws IOException, IllegalConnectorArgumentsException {
		var connector = getConnector("com.sun.jdi.SocketAttach");
		
		var args = connector.defaultArguments();
		args.get("hostname").setValue(host);
		args.get("port").setValue(Integer.toString(port));
		
		return connector.attach(args);
	}
	
	static VirtualMachine connect(int pid) throws IOException, IllegalConnectorArgumentsException {
		var connector = getConnector("com.sun.jdi.ProcessAttach");
		
		var args = connector.defaultArguments();
		args.get("pid").setValue(Integer.toString(pid));
		
		return connector.attach(args);
	}
	
	static AttachingConnector getConnector(String name) {
		var vmManager = Bootstrap.virtualMachineManager();
		
		for (var connector : vmManager.attachingConnectors()) {
			if(name.equals(connector.name()))
				return connector;
		}
		throw new IllegalStateException("Couldn't find connector with name '" + name + "'");
	}
}
