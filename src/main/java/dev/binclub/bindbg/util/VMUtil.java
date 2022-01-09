package dev.binclub.bindbg.util;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.ArrayList;
import java.util.List;

public class VMUtil {

	public static List<String> listVirtualMachines() {
		List<String> vms = new ArrayList<>();
		try {
			List<VirtualMachineDescriptor> descriptors = VirtualMachine.list();
			for (VirtualMachineDescriptor descriptor : descriptors) {
				vms.add("(pid: " + descriptor.id() + ") " + descriptor.displayName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return vms;
	}
	
}
