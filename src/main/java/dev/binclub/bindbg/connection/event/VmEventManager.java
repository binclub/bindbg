package dev.binclub.bindbg.connection.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This is a garbage collected event manager
 *
 * This means that event subscribers will receive events indefinitely until they are garbage collected
 */
public class VmEventManager {
	private Map<Class, List<Object[]>> listeners = new HashMap<>();
	
	public <T> void subscribe(Object subscriber, Class<T> clazz, Consumer<T> runnable) {
		var list = listeners.computeIfAbsent(clazz, k -> new ArrayList<>());
		list.add(new Object[] {
			new WeakReference<>(subscriber),
			new WeakReference<>(runnable)
		});
	}
	
	public void dispatch(Object event) {
		Class clazz = event.getClass();
		while (clazz != null) {
			var list = listeners.get(clazz);
			
			if (list != null) {
				list.removeIf((arr) -> {
					var subscriber = ((WeakReference<Object>) arr[0]).get();
					if (subscriber != null) { // has it been garbage collected?
						var runnable = ((WeakReference<Consumer>) arr[1]).get();
						if (runnable != null) runnable.accept(event);
						return false;
					}
					else {
						System.out.println("Unsubscribed " + subscriber + ((WeakReference<Consumer>) arr[1]).get());
						return true;
					}
				});
			}
			
			clazz = clazz.getSuperclass();
		}
	}
}
