/**
 *   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion 
 *   Centre InterActive)
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.esco.portal.groups.grouper;

import java.util.Iterator;


/**
 * Merge 2 iterators.
 * @author GIP RECIA - A. Deman
 * 10 mars 08
 *
 */
@SuppressWarnings("unchecked")
public class MergedIterators implements Iterator {

	/** The first iterator to merge. */
	private Iterator firstIter;

	/** The second iterator to merge. */
	private Iterator secondIter;

	/** Flag used to determine the current iterator. */
	private boolean useFirstIter;

	/**
	 * Constructor for MergedIterators.
	 * @param firstIter The first iterator to merge.
	 * @param secondIter The second iterator to merge.
	 */
	protected MergedIterators(final Iterator firstIter, final Iterator secondIter) {
		this.firstIter = firstIter;
		this.secondIter = secondIter;
		useFirstIter = firstIter.hasNext();
	}

	/**
	 * Tests if there is a next element.
	 * @return True if there is a next element.
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (firstIter.hasNext()) {
			return true;
		}
		return secondIter.hasNext();
	}

	/**
	 * Gives the next element.
	 * @return The next element.
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (useFirstIter) {
			final Object o = firstIter.next();
			if (!firstIter.hasNext()) {
				useFirstIter = false;
			}
			return o;
		}
		return secondIter.next();
	}

	/**
	 * Unsupported operation.
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}


