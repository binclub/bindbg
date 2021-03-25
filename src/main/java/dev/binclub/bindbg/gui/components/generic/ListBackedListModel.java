package dev.binclub.bindbg.gui.components.generic;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ListBackedListModel<T> extends AbstractListModel<T> {
	private final Supplier<List<T>> provider;
	private List<T> last;
	private int lastSize;
	
	public ListBackedListModel(Supplier<List<T>> provider) {
		this.provider = provider;
	}
	
	private List<T> get() {
		var list = provider.get();
		if (list == null) {
			fireIntervalRemoved(this, 0, lastSize);
			last = null;
			lastSize = 0;
			return Collections.emptyList();
		}
		var listSize = list.size();
		if (list != last || listSize != lastSize) {
			int diff = listSize - lastSize;
			if (diff > 0) {
				fireContentsChanged(this, 0, lastSize);
				fireIntervalAdded(this, lastSize, listSize);
			} else if (diff < 0) {
				fireContentsChanged(this, 0, listSize);
				fireIntervalRemoved(this, listSize, lastSize);
			} else {
				fireContentsChanged(this, 0, listSize);
			}
		}
		last = list;
		lastSize = listSize;
		return list;
	}
	
	@Override
	public int getSize() {
		return get().size();
	}
	
	@Override
	public T getElementAt(int index) {
		return get().get(index);
	}
}
