package come.user.lezco.adapter

import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import come.user.lezco.R
import java.util.*

/**
 * Created by techintegrity on 15/07/16.
 */
class PickupDropLocationAdapter(internal var activity: Activity, internal var picDrpArray: ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var itemsCount = 0
    private val showLoadingView = false
    internal var OpenSans_Regular: Typeface

    private var onDraoppickupClickListener: OnDraoppickupClickListener? = null

    init {
        OpenSans_Regular = Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Regular_0.ttf")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.drop_pickup_layout, parent, false)
        val dropPickupViewHolder = DropPickupViewHolder(view)
        dropPickupViewHolder.layout_main.setOnClickListener(this)
        return dropPickupViewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as DropPickupViewHolder
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindPickupDropFeedItem(position, holder)
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder)
        }
    }

    private fun bindPickupDropFeedItem(position: Int, holder: DropPickupViewHolder) {
        val DropPickupHashmap = picDrpArray[position]
        holder.txt_location_name.text = DropPickupHashmap["location name"]
        holder.txt_location_name.typeface = OpenSans_Regular
        holder.layout_main.tag = holder
    }

    private fun bindLoadingFeedItem(holder: DropPickupViewHolder) {
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
        return picDrpArray.size
    }

    fun updateItems() {
        itemsCount = picDrpArray.size
        notifyDataSetChanged()
    }

    fun updateBlankItems(locationArray: ArrayList<HashMap<String, String>>) {
        picDrpArray = locationArray
        itemsCount = picDrpArray.size
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        val viewId = v.id
        val holder = v.tag as DropPickupViewHolder
        if (viewId == R.id.layout_main) {
            if (onDraoppickupClickListener != null)
                onDraoppickupClickListener!!.PickupDropClick(holder.adapterPosition)
        }

    }

    inner class DropPickupViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var txt_location_name: TextView
        internal var layout_main: RelativeLayout

        init {
            txt_location_name = view.findViewById(R.id.txt_location_name) as TextView
            layout_main = view.findViewById(R.id.layout_main) as RelativeLayout
        }
    }

    fun setOnDropPickupClickListener(onDraoppickupClickListener: OnDraoppickupClickListener) {
        this.onDraoppickupClickListener = onDraoppickupClickListener
    }

    interface OnDraoppickupClickListener {

        fun PickupDropClick(position: Int)
    }

    companion object {
        private val VIEW_TYPE_DEFAULT = 1
        private val VIEW_TYPE_LOADER = 2
    }
}
