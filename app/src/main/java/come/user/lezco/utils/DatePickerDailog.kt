package come.user.lezco.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout.LayoutParams
import android.widget.TextView
import come.user.lezco.wheel.ArrayWheelAdapter
import come.user.lezco.wheel.NumericWheelAdapter
import come.user.lezco.wheel.OnWheelChangedListener
import come.user.lezco.wheel.WheelView
import java.util.*

class DatePickerDailog(
    private val Mcontex: Context, calendar: Calendar,
    dtp: DatePickerListner
) : Dialog(Mcontex) {

    private val NoOfYear = 40

    init {
        val lytmain = LinearLayout(Mcontex)
        lytmain.orientation = LinearLayout.VERTICAL
        val lytdate = LinearLayout(Mcontex)
        val lytbutton = LinearLayout(Mcontex)

        val btnset = Button(Mcontex)
        val btncancel = Button(Mcontex)

        btnset.text = "Set"
        btncancel.text = "Cancel"

        val month = WheelView(Mcontex)
        val year = WheelView(Mcontex)
        val day = WheelView(Mcontex)

        lytdate.addView(
            day, LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f
            )
        )
        lytdate.addView(
            month, LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f
            )
        )
        lytdate.addView(
            year, LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        lytbutton.addView(
            btnset, LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        )

        lytbutton.addView(
            btncancel, LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
        )
        lytbutton.setPadding(5, 5, 5, 5)
        lytmain.addView(lytdate)
        lytmain.addView(lytbutton)

        setContentView(lytmain)

        window!!.setLayout(
            LayoutParams.FILL_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        val listener = object : OnWheelChangedListener {
            override fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int) {
                updateDays(year, month, day)
            }
        }

        // month
        val curMonth = calendar.get(Calendar.MONTH)
        val months = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        month.viewAdapter = DateArrayAdapter(Mcontex, months, curMonth)

        month.currentItem = curMonth
        month.addChangingListener(listener)

        val cal = Calendar.getInstance()
        // year
        val curYear = calendar.get(Calendar.YEAR)
        val Year = cal.get(Calendar.YEAR)


        year.viewAdapter = DateNumericAdapter(
            Mcontex, Year - NoOfYear,
            Year + NoOfYear, NoOfYear
        )
        year.currentItem = curYear - (Year - NoOfYear)
        year.addChangingListener(listener)

        // day
        updateDays(year, month, day)
        day.currentItem = calendar.get(Calendar.DAY_OF_MONTH) - 1

        btnset.setOnClickListener {
            // TODO Auto-generated method stub
            val c = updateDays(year, month, day)
            dtp.OnDoneButton(this@DatePickerDailog, c)
        }
        btncancel.setOnClickListener { dtp.OnCancelButton(this@DatePickerDailog) }

    }

    internal fun updateDays(year: WheelView, month: WheelView, day: WheelView): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(
            Calendar.YEAR,
            calendar.get(Calendar.YEAR) + (year.currentItem - NoOfYear)
        )
        calendar.set(Calendar.MONTH, month.currentItem)

        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        day.viewAdapter = DateNumericAdapter(
            Mcontex, 1, maxDays, calendar
                .get(Calendar.DAY_OF_MONTH) - 1
        )
        val curDay = Math.min(maxDays, day.currentItem + 1)
        day.setCurrentItem(curDay - 1, true)
        calendar.set(Calendar.DAY_OF_MONTH, curDay)
        return calendar

    }

    inner class DateNumericAdapter(
        context: Context, minValue: Int, maxValue: Int,
        internal var currentValue: Int
    ) : NumericWheelAdapter(context, minValue, maxValue) {
        override fun getItem(index: Int, convertView: View, parent: ViewGroup): View {
            currentItem = index
            return getItem(this, index, convertView, parent)!!
        }

        internal var currentItem: Int = 0

        init {
            textSize = 20
        }

        override fun configureTextView(view: TextView) {
            super.configureTextView(view)
            if (currentItem == currentValue) {
                view.setTextColor(-0xffff10)
            }
            view.setTypeface(null, Typeface.BOLD)
        }

//        override fun getItem(index: Int, cachedView: View?, parent: ViewGroup): View? {
//            currentItem = index
//            return AbstractWheelTextAdapter.getItem(super, index, cachedView, parent)
//        }
    }

    private inner class DateArrayAdapter(
        context: Context,
        items: Array<String>,
        internal var currentValue: Int
    ) : ArrayWheelAdapter<String>(context, items) {
        override fun getItem(index: Int, convertView: View, parent: ViewGroup): View {
            currentItem = index
            return Companion.getItem(this, index, convertView, parent)!!
        }

        internal var currentItem: Int = 0

        init {
            textSize = 20
        }

        override fun configureTextView(view: TextView) {
            super.configureTextView(view)
            if (currentItem == currentValue) {
                view.setTextColor(-0xffff10)
            }
            view.setTypeface(null, Typeface.BOLD)
        }

        /*override fun getItem(index: Int, cachedView: View?, parent: ViewGroup): View? {
            currentItem = index
            return Companion.getItem(super, index, cachedView, parent)
        }*/
    }

    interface DatePickerListner {
        fun OnDoneButton(datedialog: Dialog, c: Calendar)

        fun OnCancelButton(datedialog: Dialog)
    }
}
