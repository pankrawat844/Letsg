package come.texi.driver.wheel

import android.content.Context
import android.view.View
import android.view.ViewGroup

class AdapterWheel
/**
 * Constructor
 * @param context the current context
 * @param adapter the source adapter
 */
(context: Context, // Source adapter
 /**
  * Gets original adapter
  * @return the original adapter
  */
 val adapter: WheelAdapter) : AbstractWheelTextAdapter(context) {
    override fun getItem(index: Int, convertView: View, parent: ViewGroup): View? {
        return null
    }

    override val itemsCount: Int
        get() = adapter.itemsCount

    override fun getItemText(index: Int): CharSequence {
        return adapter.getItem(index)
    }

}

