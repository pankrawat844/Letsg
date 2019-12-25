package come.user.lezco.wheel

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
abstract class AbstractWheelTextAdapter
/**
 * Constructor
 * @param context the current context
 * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
 * @param itemTextResource the resource ID for a text view in the item layout
 */
@JvmOverloads protected constructor(// Current context
        protected var context: Context, // Items resources
        /**
         * Gets resource Id for items views
         * @return the item resource Id
         */
        /**
         * Sets resource Id for items views
         * @param itemResourceId the resource Id to set
         */
        var itemResource: Int = TEXT_VIEW_ITEM_RESOURCE,
        /**
         * Gets resource Id for text view in item layout
         * @return the item text resource Id
         */
        /**
         * Sets resource Id for text view in item layout
         * @param itemTextResourceId the item text resource Id to set
         */
        var itemTextResource: Int = NO_RESOURCE) : AbstractWheelAdapter() {

    // Text settings
    /**
     * Gets text color
     * @return the text color
     */
    /**
     * Sets text color
     * @param textColor the text color to set
     */
    var textColor = DEFAULT_TEXT_COLOR
    /**
     * Gets text size
     * @return the text size
     */
    /**
     * Sets text size
     * @param textSize the text size to set
     */
    var textSize = DEFAULT_TEXT_SIZE
    // Layout inflater
    protected var inflater: LayoutInflater

    // Empty items resources
    /**
     * Gets resource Id for empty items views
     * @return the empty item resource Id
     */
    /**
     * Sets resource Id for empty items views
     * @param emptyItemResourceId the empty item resource Id to set
     */
    var emptyItemResource: Int = 0

    init {

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    /**
     * Returns text for specified item
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract fun getItemText(index: Int): CharSequence?

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     * @param view the text view to be configured
     */
    protected open fun configureTextView(view: TextView) {
        view.setTextColor(textColor)
        view.gravity = Gravity.CENTER
        view.textSize = textSize.toFloat()
        view.setLines(1)
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    /**
     * Loads a text view from view
     * @param view the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private fun getTextView(view: View, textResource: Int): TextView? {
        var text: TextView? = null
        try {
            if (textResource == NO_RESOURCE && view is TextView) {
                text = view
            } else if (textResource != NO_RESOURCE) {
                text = view.findViewById(textResource) as TextView
            }
        } catch (e: ClassCastException) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView")
            throw IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a TextView", e)
        }

        return text
    }

    /**
     * Loads view from resources
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private fun getView(resource: Int, parent: ViewGroup): View? {
        when (resource) {
            NO_RESOURCE -> return null
            TEXT_VIEW_ITEM_RESOURCE -> return TextView(context)
            else -> return inflater.inflate(resource, parent, false)
        }
    }

    companion object {

        /** Text view resource. Used as a default view for adapter.  */
        val TEXT_VIEW_ITEM_RESOURCE = -1

        /** No resource constant.  */
        protected val NO_RESOURCE = 0

        /** Default text color  */
        val DEFAULT_TEXT_COLOR = -0xefeff0

        /** Default text color  */
        val LABEL_COLOR = -0x8fff90

        /** Default text size  */
        val DEFAULT_TEXT_SIZE = 24

        fun getItem(abstractWheelTextAdapter: AbstractWheelTextAdapter, index: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            if (index >= 0 && index < abstractWheelTextAdapter.itemsCount) {
                if (convertView == null) {
                    convertView = abstractWheelTextAdapter.getView(abstractWheelTextAdapter.itemResource, parent)
                }
                val textView = abstractWheelTextAdapter.getTextView(convertView!!, abstractWheelTextAdapter.itemTextResource)
                if (textView != null) {
                    var text: CharSequence? = abstractWheelTextAdapter.getItemText(index)
                    if (text == null) {
                        text = ""
                    }
                    textView.text = text

                    if (abstractWheelTextAdapter.itemResource == TEXT_VIEW_ITEM_RESOURCE) {
                        abstractWheelTextAdapter.configureTextView(textView)
                    }
                }
                return convertView
            }
            return null
        }

        fun getEmptyItem(abstractWheelTextAdapter: AbstractWheelTextAdapter, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            if (convertView == null) {
                convertView = abstractWheelTextAdapter.getView(abstractWheelTextAdapter.emptyItemResource, parent)
            }
            if (abstractWheelTextAdapter.emptyItemResource == TEXT_VIEW_ITEM_RESOURCE && convertView is TextView) {
                abstractWheelTextAdapter.configureTextView((convertView as TextView?)!!)
            }

            return convertView
        }
    }
}
/**
 * Constructor
 * @param context the current context
 */
/**
 * Constructor
 * @param context the current context
 * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
 */

