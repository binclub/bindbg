package dev.binclub.bindbg.gui.components.state;

import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.event.StackFrameSelectedEvent;

import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JPanel;

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
