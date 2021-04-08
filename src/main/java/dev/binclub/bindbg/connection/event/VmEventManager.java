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

package dev.binclub.bindbg.connection.event;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * This is a garbage collected event manager
 * <p>
 * This means that event subscribers will receive events indefinitely until they are garbage collected
 */
public class VmEventManager {
	private final Map<Class<?>, List<WeakReference<?>[]>> listeners = new HashMap<>();
	
	public <T> void subscribe(Object subscriber, Class<T> clazz, Consumer<T> runnable) {
		var list = listeners.computeIfAbsent(clazz, k -> new ArrayList<>());
		list.add(new WeakReference[]{
			new WeakReference<>(subscriber),
			new WeakReference<>(runnable)
		});
	}
	
	public <T> void dispatch(T event) {
		var hierarchy = new HashSet<Class<?>>(2);
		
		{
			Class<?> clazz = event.getClass();
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
			}
		}
		
		for (Class<?> clazz : hierarchy) {
			var list = listeners.get(clazz);
			
			if (list != null) {
				list.removeIf((arr) -> {
					var subscriber = (Object) arr[0].get();
					//noinspection unchecked
					var runnable = (Consumer<T>) arr[1].get();
					if (subscriber != null && runnable != null) { // has it been garbage collected?
						runnable.accept(event);
						return false;
					} else {
						System.out.println("Unsubscribed " + clazz);
						return true;
					}
				});
			}
		}
	}
}
