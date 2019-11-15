package com.vladsch.flexmark.util.collection.iteration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Represents an index position in a list and behaves similar to the list.
 * <p>
 * All indexed operations on the position are operations on the list at index relative to this position's index,
 *  so indices may be -ve as long as absolute index is &gt;>0
 * <p>
 * Any insertions/deletions before current position will adjust the current position to reflect the same element.
 * If element at current index is removed then the current index will be invalidated and trying to
 * get the element will throw IllegalStateException.  However, operations relative to the current position are still
 * maintained so access to next/previous elements is available.
 *
 * This makes insert/delete operations around the position easier since you can delete the current element then insert/delete
 * around its position or do it the other way around without concern for adjusting index for the previous operations.
 * Try that with your list or list iterator.
 *
 * Additionally, the PositionList class iterator will iterate over list elements while elements
 * are added or deleted in the list. As long as elements are added right after the current position.
 * This limit is not limited to 1 insertion. Each insert after the current position moves the
 * next element position further down the list. The first insertion is limited to relative position 1
 * from current index. The following can insert anywhere in the previously inserted range.
 * Deletions have no limitations. They can never cause new elements to be inserted after
 * the next element position of the iterator.
 *
 * All this makes list manipulation while traversing it easier to write, debug and understand because
 * all operations are relative to current position instead of a numeric index. In the code you
 * see -1, 0, +1, for previous, current and next access.
 * <p>
 * Since the PositionList adjusts all outstanding positions into its instance,
 * this means that you can get positions in the list at different indices before any
 * modifications are made. If the list is modified by adding/removing elements, all position
 * instances will be invalidated if their element was removed or shifted to point to
 * the the element's new index in the list.
 *
 * Index computations can be a real bug generator, throw inserting, deleting and traversing a list at the
 * same time and you have a beast to debug and maintenance nightmare to look forward to.
 * PositionList and Position instances make all that go away. That's why I wrote these classes
 * so I don't have to write and debug the same error prone code for manipulating lists.
 *
 * @param <T> type of element held in the list
 */
public interface IPosition<T, P extends IPosition<T, P>> {
    /**
     * @return absolute index in list, even if this position is not valid, it will always be [0, list.size()]
     */
    int getIndex();

    /**
     * Internal for changing indices and invalidating positions
     * @param index new index
     * @param isValid false if should be invalidated, if already invalid should ignore true
     */
    void setIndex(int index, boolean isValid);

    /**
     * @param index index relative to current position
     * @return Position representing the index relative to current position,
     *         throws {@link IllegalStateException} if current position is not valid and given index == 0
     *         throws {@link IndexOutOfBoundsException} if requested index results in absolute index &lt;0 or &gt;size() of the list
     *
     *         NOTE: to avoid exceptions test if position has a valid index with isValidIndex()
     */
    P getPosition(int index);

    /**
     * Get previous index position relative to current,
     * returns previous index if it exists even when this position is not valid
     *
     * @return previous valid position relative to this position,
     *         throws {@link IndexOutOfBoundsException} if there is no elements before current position
     */
    P getPrevious();
    /**
     * Get next index position relative to current,
     * returns previous index if it exists even when this position is not valid
     *
     * @return previous valid position relative to this position,
     *         throws {@link IndexOutOfBoundsException} if there is no elements before current position
     */
    P getNext();

    /**
     * @return true if this position was not invalidated by deleting the element at index
     */
    boolean isValid();

    /**
     * @return true if index of this position is between 0 and size() of the list
     */
    boolean isValidIndex();

    /**
     * @return true if this position represents a valid element in the list, ie. isValid() is true and index is at or before last element in list
     */
    boolean isValidPosition();

    /**
     * @return true if getPreviousIndex() will return a value, false if {@link #getPrevious()} will throw an exception
     */
    boolean isPreviousValid();
    /**
     * @return true if getNextIndex() will return a value, false if {@link #getNext()} will throw an exception
     */
    boolean isNextValid();

    /**
     * @return get element at this position, throws {@link IllegalStateException} if current position is not valid or after last element of list
     */
    T get();

    /**
     * @return get element at this position or null if no such thing
     */
    T getOrNull();

    /**
     * Get the requested class or null if element at position cannot be cast to this class
     *
     * @param elementClass class of element desired
     * @param <S>          type of element
     * @return element of type or null
     */
    <S extends T> S getOrNull(Class<S> elementClass);

    /**
     * @param element to which to set the current element in the list
     *                throws IllegalStateException if current index is not valid
     *                <p>
     *                If the current index is after the last element this will add the value to the end of the list without changing any positions at the end of the list
     *                they will now point to a valid element.
     */
    void set(T element);

    /**
     * @return removed current element, throws IllegalStateException if current index is not valid or after last element of list
     */
    T remove();

    /**
     * @param element to insert before current position in the list
     *                throws IllegalStateException if current index is not valid
     * @return true
     */
    boolean add(T element);

    // these are delegated to the original list
    int size();
    boolean isEmpty();
    boolean append(T element);
    void clear(); // will invalidate all indices of the list

    // these add current index to given indices before passing them to original list
    /**
     * Insert element at index
     *
     * @param index relative to this position, absolute index [0, size()], if absolute index == size() then element is added at the end of the list. The latter is also considered an insert at size() index.
     * @return element
     */
    T get(int index);

    /**
     * Insert element at index
     *
     * @param index relative to this position, absolute index [0, size()], if absolute index == size() then element is added at the end of the list. The latter is also considered an insert at size() index.
     * @return element or null if for some reason the index or position are not valid
     */
    @Nullable
    T getOrNull(int index);

    /**
     * Get the requested class or null if element at position cannot be cast to this class
     *
     * @param index        relative to this position, absolute index [0, size()], if absolute index == size() then element is added at the end of the list. The latter is also considered an insert at size() index.
     * @param elementClass class of element desired
     * @param <S>          type of element
     * @return element of type or null
     */
    <S extends T> S getOrNull(int index, Class<S> elementClass);

    /**
     * Set element at given index
     *
     * @param index relative to this position, absolute index [0, size()], if absolute index == size() then element is added at the end of the list. The latter is considered an insert at the index.
     * @return element value at that position before or null if adding at end of list
     */
    T set(int index, T element);
    boolean add(int index, T element);
    boolean addAll(@NotNull Collection<T> elements);
    boolean addAll(int index, @NotNull Collection<T> elements);
    T remove(int index);
    void remove(int startIndex, int endIndex);

    // these return index relative to current index and operate on elements at or after the current index
    // if not found returned position will point to index after the last element. ie. getIndex() == size()
    // test isValidPosition() to see if actual element was found
    P indexOf(T o);
    P indexOf(int index, T o);
    P indexOf(@NotNull Predicate<P> predicate);
    P indexOf(int index, @NotNull Predicate<P> predicate);

    // these return position relative to current index and operate on elements before the current index
    // if not found returned position will point to index after the last element. ie. getIndex() == size()
    // test isValidPosition() to see if actual element was found
    P lastIndexOf(T o);
    P lastIndexOf(@NotNull Predicate<P> predicate);
    P lastIndexOf(int index, T o);
    P lastIndexOf(int index, @NotNull Predicate<P> predicate);
}