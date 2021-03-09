package dev.binclub.bindbg.gui;

import dev.binclub.bincode.parsing.ConstantPoolParser;
import dev.binclub.bincode.types.ClassVersion;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.connection.event.VmPauseEvent;
import dev.binclub.bindbg.gui.context.DebugContext;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BytecodePanel extends JPanel {
	private final VmConnection vm;
	
	public BytecodePanel(VmConnection vm) {
		this.vm = vm;
	}
	
	public void refresh() {
		// We only refresh the bytecode panel in instances where the VM is suspended
		// Otherwise it would be too costly to constantly redraw
		if (!vm.isSuspended()) return;
		
		try {
			DebugContext context = vm.debugContext;
			if (context.debuggingThread == null) return;
			var loc = context.debuggingThread.frame(0).location();
			var clazz = loc.declaringType();
			var method = loc.method();
			var cp = ConstantPoolParser.INSTANCE.parse(
				new DataInputStream(new ByteArrayInputStream(clazz.constantPool())),
				clazz.constantPoolCount()
			);
			var version = new ClassVersion(clazz.minorVersion(), clazz.majorVersion());
			var bytes = method.bytecodes();
			try (var fs = new FileOutputStream(new File(clazz.name() + ".class"))) {
				fs.write(bytes);
			}
			//System.out.println(bytecode);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
