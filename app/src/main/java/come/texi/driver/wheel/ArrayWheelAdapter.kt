package come.texi.driver.wheel

import android.content.Context
import android.view.View
import android.view.ViewGroup

open class ArrayWheelAdapter<T>
/**
 * Constructor
 * @param context the current context
 * @param items the items
 */
    (
    context: Context, // items
    private val items: Array<T>
)//setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
    : AbstractWheelTextAdapter(context) {
    override fun getItem(index: Int, convertView: View, parent: ViewGroup): View? {
        return null
    }

    override val itemsCount: Int
        get() = items.size

    public override fun getItemText(index: Int): CharSequence? {
        if (index >= 0 && index < items.size) {
            val item = items[index]
            return if (item is CharSequence) {
                item
            } else item.toString()
        }
        return null
    }
}

