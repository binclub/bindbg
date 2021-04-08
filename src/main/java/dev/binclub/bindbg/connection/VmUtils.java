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
			if (name.equals(connector.name()))
				return connector;
		}
		throw new IllegalStateException("Couldn't find connector with name '" + name + "'");
	}
}
