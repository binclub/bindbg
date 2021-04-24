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
