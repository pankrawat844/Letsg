package come.texi.driver.adapter

import android.app.Activity
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import come.texi.driver.R
import come.texi.driver.utils.CabDetails
import come.texi.driver.utils.CircleTransform
import come.texi.driver.utils.Url
import java.util.*

/**
 * Created by techintegrity on 11/07/16.
 */
class CabDetailAdapter(internal var activity: Activity, internal var cabDetailsArrayList: ArrayList<CabDetails>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var itemsCount = 0
    private val showLoadingView = false

    private var onCabDetailClickListener: OnCabDetailClickListener? = null

    internal var Roboto_Regular: Typeface

    init {
        Roboto_Regular = Typeface.createFromAsset(activity.assets, "fonts/Roboto-Regular.ttf")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(activity).inflate(R.layout.cabdetail_list_layout, parent, false)
        val cabDetViewHol = CabDetailViewHolder(view)
        cabDetViewHol.layout_tab.setOnClickListener(this)
        return cabDetViewHol
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as CabDetailViewHolder
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindCabDetailFeedItem(position, holder)
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder)
        }
    }

    private fun bindCabDetailFeedItem(position: Int, holder: CabDetailViewHolder) {

        val cabDetails = cabDetailsArrayList[position]

        if (cabDetails.getiIsSelected())
            holder.img_selected_indicator.visibility = View.VISIBLE
        else
            holder.img_selected_indicator.visibility = View.INVISIBLE
        Log.d("Car Icon", "Car Icon = " + Url.carImageUrl + cabDetails.icon)
        if (cabDetails.icon != "") {
            val iconUri = Url.carImageUrl + cabDetails.icon
            Picasso.with(activity)
                    .load(iconUri)
                    .placeholder(R.drawable.truck_icon)
                    .transform(CircleTransform())
                    .into(holder.img_car_icon)

        } else {
            Picasso.with(activity)
                    .load(R.drawable.truck_icon)
                    .placeholder(R.drawable.truck_icon)
                    .transform(CircleTransform())
                    .into(holder.img_car_icon)
        }

        holder.txt_car_type.text = cabDetails.cartype
        holder.txt_car_type.typeface = Roboto_Regular

        holder.layout_tab.tag = holder
        if (cabDetails.fixPrice != "" && cabDetails.areaId != "") {
            holder.img_fix_rate_icon.visibility = View.VISIBLE
        } else {
            holder.img_fix_rate_icon.visibility = View.GONE
        }

    }

    private fun bindLoadingFeedItem(holder: CabDetailViewHolder) {

        println("BindLoadingFeedItem >>>>>")

    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoadingView && position == 0) {
            VIEW_TYPE_LOADER
        } else {
            VIEW_TYPE_DEFAULT
        }
    }

    override fun getItemCount(): Int {
        return cabDetailsArrayList.size
    }

    fun updateItems() {
        itemsCount = cabDetailsArrayList.size
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {

        val viwId = v.id
        val holder = v.tag as CabDetailViewHolder
        if (viwId == R.id.layout_tab) {
            if (onCabDetailClickListener != null)
                onCabDetailClickListener!!.CarDetailTab(holder.adapterPosition)
        }

    }

    inner class CabDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var img_selected_indicator: ImageView
        internal var img_car_icon: ImageView
        internal var img_fix_rate_icon: ImageView
        internal var txt_car_type: TextView
        internal var layout_tab: RelativeLayout

        init {

            img_selected_indicator = view.findViewById(R.id.img_selected_indicator) as ImageView
            img_car_icon = view.findViewById(R.id.img_car_icon) as ImageView
            img_fix_rate_icon = view.findViewById(R.id.img_fix_rate_icon) as ImageView
            txt_car_type = view.findViewById(R.id.txt_car_type) as TextView
            layout_tab = view.findViewById(R.id.layout_tab) as RelativeLayout
        }
    }

    fun setOnCabDetailItemClickListener(onCabDetailClickListener: OnCabDetailClickListener) {
        this.onCabDetailClickListener = onCabDetailClickListener
    }

    interface OnCabDetailClickListener {
        fun CarDetailTab(position: Int)
    }

    companion object {
        private val VIEW_TYPE_DEFAULT = 1
        private val VIEW_TYPE_LOADER = 2
    }
}
