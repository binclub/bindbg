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

package dev.binclub.bindbg.gui;

import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import dev.binclub.bindbg.connection.VmConnection;
import dev.binclub.bindbg.connection.event.VmPauseEvent;
import dev.binclub.bindbg.connection.event.VmResumeEvent;
import dev.binclub.bindbg.event.ThreadSelectedEvent;
import dev.binclub.bindbg.gui.components.ControlBar;
import dev.binclub.bindbg.gui.components.state.StatePanel;
import dev.binclub.bindbg.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
	private final VmConnection vm;
	private final ControlBar controlBar;
	private final StatePanel statePanel;
	private boolean purposefullyClosed;
	
	public MainWindow(VmConnection vm) {
		this.vm = vm;
		
		this.setTitle("BinDbg - %s".formatted(vm));
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout());
		
		controlBar = new ControlBar(vm);
		inner.add(controlBar, BorderLayout.NORTH);
		
		statePanel = new StatePanel(vm);
		inner.add(statePanel, BorderLayout.CENTER);
		
		this.add(inner);
		
		vm.eventManager.subscribe(this, VmResumeEvent.class, (e) -> refresh());
		vm.eventManager.subscribe(this, VmPauseEvent.class, (e) -> refresh());
		vm.eventManager.subscribe(this, ThreadSelectedEvent.class, (e) -> refresh());
		vm.eventManager.subscribe(this, VMDisconnectEvent.class, (e) -> close());
		vm.eventManager.subscribe(this, VMDeathEvent.class, (e) -> close());
		
		refresh();
	}
	
	private void refresh() {
		controlBar.refresh();
		statePanel.refresh();
	}
	
	private void close() {
		System.out.println("Closing");
		
		if (!purposefullyClosed) {
			this.dispose();
			var connWindow = new ConnectionWindow();
			SwingUtils.putWindowInMouseScreen(connWindow, 350);
			connWindow.setVisible(true);
		}
	}
	
	@Override
	public void dispose() {
		purposefullyClosed = true;
		super.dispose();
		vm.disconnect();
	}
}
