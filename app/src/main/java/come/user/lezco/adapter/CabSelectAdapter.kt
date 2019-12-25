package come.user.lezco.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

import come.user.lezco.R
import come.user.lezco.utils.CabDetails
import come.user.lezco.utils.Url
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class CabSelectAdapter(list1: ArrayList<CabDetails>, var distance: Float, var totalTime: Int) :
    RecyclerView.Adapter<CabSelectAdapter.ViewHolder>() {
    lateinit var list: ArrayList<CabDetails>
    lateinit var DayNight: String
    internal var car_rate: String? = null
    internal var fromintailrate: String? = null
    internal var ride_time_rate: String? = "0"
    internal var currentTime = SimpleDateFormat("HH:mm")
    internal var FirstKm: Float = 0.toFloat()

    private lateinit var context: Context

    init {
        this.list = list1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_view_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cabDetails = list[position]

        Picasso.with(context).load(Url.carImageUrl + cabDetails.icon).into(holder.itemView.img)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
//        val txtCarName = itemView!!.findViewById(R.id.car_name) as TextView
//        val txt_car_header = itemView!!.findViewById(R.id.txt_car_header) as TextView
//        val txt_currency = itemView!!.findViewById(R.id.txt_currency) as TextView
//        val img = itemView!!.findViewById(R.id.img) as ImageView
//        val txt_far_breakup = itemView!!.findViewById(R.id.txt_far_breakup) as TextView
//        val txt_book = itemView!!.findViewById(R.id.txt_book) as TextView
//        val txt_cancel = itemView!!.findViewById(R.id.txt_cancel) as TextView
//        val txt_car_descriptin = itemView!!.findViewById(R.id.txt_car_descriptin) as TextView
//        val txt_first_price = itemView!!.findViewById(R.id.txt_first_price) as TextView
//        val txt_first_km = itemView!!.findViewById(R.id.txt_first_km) as TextView
//        val txt_sec_pric = itemView!!.findViewById(R.id.txt_sec_pric) as TextView
//        val txt_sec_km = itemView!!.findViewById(R.id.txt_sec_km) as TextView
//        val txt_thd_price = itemView!!.findViewById(R.id.txt_thd_price) as TextView
//        val layout_one = itemView!!.findViewById(R.id.layout_one) as RelativeLayout
//        val layout_two = itemView!!.findViewById(R.id.layout_two) as RelativeLayout
//        val layout_three = itemView!!.findViewById(R.id.layout_three) as RelativeLayout
//        val txt_total_price = itemView!!.findViewById(R.id.txt_total_price) as TextView
//        val txt_cash = itemView!!.findViewById(R.id.txt_cash) as TextView
//        val spinner_person = itemView!!.findViewById(R.id.spinner_person) as Spinner
//        val txt_first_currency = itemView!!.findViewById(R.id.txt_first_currency) as TextView
//        val txt_secound_currency = itemView!!.findViewById(R.id.txt_secound_currency) as TextView
//        val txt_thd_currency = itemView!!.findViewById(R.id.txt_thd_currency) as TextView
//        val layout_timming = itemView!!.findViewById(R.id.layout_timming) as LinearLayout
//        val layout_far_breakup = itemView!!.findViewById(R.id.layout_far_breakup) as RelativeLayout
//        val txt_specailChr_note = itemView!!.findViewById(R.id.txt_specailChr_note) as TextView
//        val dropdown = itemView!!.findViewById(R.id.dropdown) as ImageView
//        val car_content = itemView!!.findViewById(R.id.car_content) as ConstraintLayout
//        val rate = itemView!!.findViewById(R.id.rate) as TextView

    }
}