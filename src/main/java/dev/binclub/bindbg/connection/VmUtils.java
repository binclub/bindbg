package dev.binclub.bindbg.connection;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.*;

import java.util.*;

public class VmUtils {
	static VirtualMachine connect(Connector conn, Map<String,? extends Connector.Argument> arguments) throws Throwable {
		if (conn instanceof AttachingConnector) {
			var aConn = (AttachingConnector) conn;
			return aConn.attach(arguments);
		} else if (conn instanceof LaunchingConnector) {
			var lConn = (LaunchingConnector) conn;
			return lConn.launch(arguments);
		} else {
			throw new UnsupportedOperationException("Connector " + conn.getClass());
		}
	}
	
	public static List<Connector> connectors() {
		List<Connector> connectors = new ArrayList();
		var manager = Bootstrap.virtualMachineManager();
		connectors.addAll(manager.attachingConnectors());
		connectors.addAll(manager.launchingConnectors());
		return connectors;
	}
}
