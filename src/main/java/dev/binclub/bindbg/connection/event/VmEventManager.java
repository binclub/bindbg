package dev.binclub.bindbg.connection.event;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * This is a garbage collected event manager
 *
 * This means that event subscribers will receive events indefinitely until they are garbage collected
 */
public class VmEventManager {
	private final Map<Class<?>, List<WeakReference<?>[]>> listeners = new HashMap<>();
	
	public <T> void subscribe(Object subscriber, Class<T> clazz, Consumer<T> runnable) {
		var list = listeners.computeIfAbsent(clazz, k -> new ArrayList<>());
		list.add(new WeakReference[] {
			new WeakReference<>(subscriber),
			new WeakReference<>(runnable)
		});
	}
	
	public <T> void dispatch(T event) {
		var hierarchy = new HashSet<Class<?>>(2);
		
		{Class<?> clazz = event.getClass();
		while (clazz != null) {
			hierarchy.add(clazz);
			// Underneath HashSet it just does this anyway, except we will have to box the array into a List
			// Doing it this way should be faster
			//noinspection ManualArrayToCollectionCopy
			for (Class<?> inter : clazz.getInterfaces()) {
				//noinspection UseBulkOperation
				hierarchy.add(inter);
			}
			clazz = clazz.getSuperclass();
		}}
		
		for (Class<?> clazz : hierarchy) {
			var list = listeners.get(clazz);
			
			if (list != null) {
				Class<?> finalClazz = clazz;
				list.removeIf((arr) -> {
					var subscriber = (Object) arr[0].get();
					var runnable = (Consumer<T>) arr[1].get();
					if (subscriber != null && runnable != null) { // has it been garbage collected?
						runnable.accept(event);
						return false;
					} else {
						System.out.println("Unsubscribed " + finalClazz);
						return true;
					}
				});
			}
		}
	}
}
