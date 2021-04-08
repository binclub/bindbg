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

public class ConnectionThread extends Thread {
	private final VmConnection vm;
	
	/// Is the connection active?
	public boolean active = true;
	
	public ConnectionThread(VmConnection vm) {
		super("%s VM Connection".formatted(vm.toString()));
		this.vm = vm;
		this.setDaemon(true);
		this.start();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (!active) break;
				
				// We can still have some queued events even if the vm is dead
				for (var event : vm.pollEvents(1000)) {
					vm.eventManager.dispatch(event);
				}
				
				if (vm.isDead()) {
					active = false;
					break;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			if (Thread.interrupted()) {
				this.active = false;
				break;
			}
		}
	}
}
