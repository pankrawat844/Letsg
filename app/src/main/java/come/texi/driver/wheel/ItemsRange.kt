package come.texi.driver.wheel

class ItemsRange
/**
 * Constructor
 * @param first the number of first item
 * @param count the count of items
 */
@JvmOverloads constructor(// First item number
    /**
     * Gets number of  first item
     * @return the number of the first item
     */
    val first: Int = 0, // Items count
    /**
     * Get items count
     * @return the count of items
     */
    val count: Int = 0
) {

    /**
     * Gets number of last item
     * @return the number of last item
     */
    val last: Int
        get() = first + count - 1

    /**
     * Tests whether item is contained by range
     * @param index the item number
     * @return true if item is contained
     */
    operator fun contains(index: Int): Boolean {
        return index >= first && index <= last
    }
}
/**
 * Default constructor. Creates an empty range
 */

