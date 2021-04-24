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

package dev.binclub.bindbg.gui.components.state;

import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.StackFrameSelectedEvent;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;

public class BytecodePanel extends JPanel {
	private final VmConnection vm;
	
	public BytecodePanel(VmConnection vm) {
		this.vm = vm;
		vm.eventManager.subscribe(this, StackFrameSelectedEvent.class, (e) -> refresh());
	}
	
	public void refresh() {
		// We only refresh the bytecode panel in instances where the VM is suspended
		// Otherwise it would be too costly to constantly redraw
		if (!vm.isSuspended()) return;
		
		try {
			var debuggingFrame = vm.debugContext.debuggingFrame;
			if (debuggingFrame == null) return;
			var loc = debuggingFrame.location();
			var clazz = loc.declaringType();
			var method = loc.method();
			/*var cp = ConstantPoolParser.INSTANCE.parse(
				new DataInputStream(new ByteArrayInputStream(clazz.constantPool())),
				clazz.constantPoolCount()
			);
			var version = new ClassVersion(clazz.minorVersion(), clazz.majorVersion());*/
			var bytes = method.bytecodes();
			var dbgFile = new File(clazz.name() + ".class");
			System.out.println(dbgFile.getAbsolutePath());
			try (var fs = new FileOutputStream(dbgFile)) {
				fs.write(bytes);
			}
			System.out.println("Wrote " + clazz.name() + "." + method.name() + " (" + bytes.length + " bytes)");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
