package come.user.lezco.wheel

/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Numeric Wheel adapter.
 */
open class NumericWheelAdapter
/**
 * Constructor
 * @param context the current context
 * @param minValue the wheel min value
 * @param maxValue the wheel max value
 * @param format the format string
 */
@JvmOverloads constructor(
    context: Context, // Values
    private val minValue: Int = DEFAULT_MIN_VALUE,
    private val maxValue: Int = DEFAULT_MAX_VALUE, // format
    private val format: String? = null
) : AbstractWheelTextAdapter(context) {
    override fun getItem(index: Int, convertView: View, parent: ViewGroup): View? {
        return null
    }

    override val itemsCount: Int
        get() = maxValue - minValue + 1

    public override fun getItemText(index: Int): CharSequence? {
        if (index >= 0 && index < itemsCount) {
            val value = minValue + index
            return if (format != null) String.format(format, value) else Integer.toString(value)
        }
        return null
    }

    companion object {

        /** The default min value  */
        val DEFAULT_MAX_VALUE = 9

        /** The default max value  */
        private val DEFAULT_MIN_VALUE = 0
    }
}
/**
 * Constructor
 * @param context the current context
 */
/**
 * Constructor
 * @param context the current context
 * @param minValue the wheel min value
 * @param maxValue the wheel max value
 */

