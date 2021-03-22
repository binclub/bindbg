package dev.binclub.bindbg.connection;

public class ConnectionThread extends Thread {
	private final VmConnection vm;
	
	/// Is the connection active?
	public boolean active = true;
	
	public ConnectionThread (VmConnection vm) {
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
				for (var event : vm.pollEvents()) {
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
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				this.active = false;
				break;
			}
		}
	}
}
