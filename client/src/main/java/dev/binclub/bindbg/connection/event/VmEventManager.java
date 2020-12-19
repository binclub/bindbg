package dev.binclub.bindbg.connection.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class VmEventManager {
	private Map<Class, List<Consumer>> listeners = new HashMap<>();
	
	public <T> void subscribe(Class<T> clazz, Consumer<T> runnable) {
		List<Consumer> list = listeners.computeIfAbsent(clazz, k -> new ArrayList<>());
		list.add(runnable);
	}
	
	public void dispatch(Object event) {
		Class clazz = event.getClass();
		while (clazz != null) {
			List<Consumer> list = listeners.get(clazz);
			if (list != null) {
				for (Consumer runnable : list) {
					runnable.accept(event);
				}
			}
			
			clazz = clazz.getSuperclass();
		}
	}
}
