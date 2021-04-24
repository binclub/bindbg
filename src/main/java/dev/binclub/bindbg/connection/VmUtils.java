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
