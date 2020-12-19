package dev.binclub.bindbg.gui;

import dev.binclub.bindbg.connection.ConnectionThread;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.connection.event.VmDisconnectEvent;
import dev.binclub.bindbg.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
	private final VmConnection vm;
	private boolean purposefullyClosed;
	
	public MainWindow(VmConnection vm) {
		this.vm = vm;
		
		this.setTitle("BinDbg - %s".formatted(vm));
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		
		var controlBar = new ControlBar(vm);
		inner.add(controlBar, BorderLayout.NORTH);
		
		this.add(inner);
		
		vm.eventManager.subscribe(VmDisconnectEvent.class, (event) -> {
			this.dispose();
			
			if (!purposefullyClosed) {
				var connWindow = new ConnectionWindow();
				SwingUtils.putWindowInMouseScreen(connWindow, 350);
				connWindow.setVisible(true);
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		purposefullyClosed = true;
		vm.disconnect();
	}
}
