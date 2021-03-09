package dev.binclub.bindbg.gui;

import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.connection.event.VmPauseEvent;
import dev.binclub.bindbg.connection.event.VmResumeEvent;
import dev.binclub.bindbg.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
	private final VmConnection vm;
	private boolean purposefullyClosed;
	private final ControlBar controlBar;
	private final BytecodePanel bytecodePanel;
	
	public MainWindow(VmConnection vm) {
		this.vm = vm;
		
		this.setTitle("BinDbg - %s".formatted(vm));
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		
		controlBar = new ControlBar(vm);
		inner.add(controlBar, BorderLayout.NORTH);
		
		bytecodePanel = new BytecodePanel(vm);
		inner.add(bytecodePanel, BorderLayout.CENTER);
		
		this.add(inner);
		
		vm.eventManager.subscribe(this, VmResumeEvent.class, (e) -> refresh());
		vm.eventManager.subscribe(this, VmPauseEvent.class, (e) -> refresh());
		vm.eventManager.subscribe(this, VMDisconnectEvent.class, (e) -> close());
		vm.eventManager.subscribe(this, VMDeathEvent.class, (e) -> close());
		
		refresh();
	}
	
	private void refresh() {
		controlBar.refresh();
		bytecodePanel.refresh();
	}
	
	private void close() {
		System.out.println("Closing");
		this.dispose();
		
		if (!purposefullyClosed) {
			var connWindow = new ConnectionWindow();
			SwingUtils.putWindowInMouseScreen(connWindow, 350);
			connWindow.setVisible(true);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		purposefullyClosed = true;
		vm.disconnect();
	}
}
