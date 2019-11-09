package come.texi.driver.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.squareup.picasso.Picasso
import come.texi.driver.R
import come.texi.driver.utils.AllTripFeed
import come.texi.driver.utils.CircleTransform
import come.texi.driver.utils.Url
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by techintegrity on 13/07/16.
 */

class AllTripAdapter(internal var activity: Activity, internal var TripArray: ArrayList<AllTripFeed>) : Adapter<ViewHolder>(), View.OnClickListener,
    Parcelable {
    private var itemsCount = 0
    private val showLoadingView = false

    internal var OpenSans_Regular: Typeface
    internal var OpenSans_Semi_Bold: Typeface
    internal var OpenSans_Light: Typeface

    private var onAllTripClickListener: OnAllTripClickListener? = null

    constructor(parcel: Parcel) : this(
        TODO("activity"),
        TODO("TripArray")
    ) {
        itemsCount = parcel.readInt()
    }

    init {

        OpenSans_Regular = Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Regular_0.ttf")
        OpenSans_Semi_Bold = Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Semibold_0.ttf")
        OpenSans_Light = Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Light_0.ttf")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(activity).inflate(R.layout.alltrip_list_layout, parent, false)
        val allTrpViewHol = AllTripViewHolder(view)
        allTrpViewHol.layout_footer_detail.setOnClickListener(this)
        allTrpViewHol.layout_all_trip.setOnClickListener(this)
        allTrpViewHol.layout_footer_detail.setOnClickListener(this)
        allTrpViewHol.layout_detail.setOnClickListener(this)
        return allTrpViewHol
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val holder = viewHolder as AllTripViewHolder
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindCabDetailFeedItem(position, holder)
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder)
        }
    }

    private fun bindCabDetailFeedItem(position: Int, holder: AllTripViewHolder) {

        holder.txt_current_booking.typeface = OpenSans_Semi_Bold
        holder.txt_trip_date.typeface = OpenSans_Regular
        holder.txt_pickup_address.typeface = OpenSans_Light
        holder.txt_drop_address.typeface = OpenSans_Light
        holder.txt_booking_id.typeface = OpenSans_Semi_Bold
        holder.txt_truct_type.typeface = OpenSans_Semi_Bold
        holder.txt_booking_id_val.typeface = OpenSans_Regular
        holder.txt_truct_type_val.typeface = OpenSans_Regular

        val allTripFeed = TripArray[position]

        Log.d("Status", "Status = " + allTripFeed.status)
        if (allTripFeed.status == "1" || allTripFeed.status == "5") {
            holder.txt_current_booking.text = activity.resources.getString(R.string.pending)
            Picasso.with(activity)
                    .load(R.drawable.status_pending)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.GONE
            holder.img_driver_image.visibility = View.GONE
            holder.img_cancle_status.visibility = View.GONE
            holder.layout_detail.visibility = View.VISIBLE

        } else if (allTripFeed.status == "3") {
            holder.txt_current_booking.text = activity.resources.getString(R.string.accepted)
            Picasso.with(activity)
                    .load(R.drawable.status_accepted)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.VISIBLE
            holder.img_driver_image.visibility = View.VISIBLE
            holder.img_cancle_status.visibility = View.GONE

            holder.layout_detail.visibility = View.VISIBLE

            try {
                val DrvObj = JSONObject(allTripFeed.driverDetail)
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl + DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(CircleTransform())
                        .into(holder.img_driver_image)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val drvParams = RelativeLayout.LayoutParams(activity.resources.getDimension(R.dimen.height_50).toInt(), activity.resources.getDimension(R.dimen.height_50).toInt())
            drvParams.setMargins(0, 0, 0, 0)
            holder.img_driver_image.layoutParams = drvParams

        } else if (allTripFeed.status == "4") {
            holder.txt_current_booking.text = activity.resources.getString(R.string.user_cancelled)
            Picasso.with(activity)
                    .load(R.drawable.status_user_cancelled)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.VISIBLE
            //holder.7mm.setVisibility(View.VISIBLE);
            holder.img_cancle_status.visibility = View.VISIBLE
            holder.img_driver_image.visibility = View.GONE

            holder.layout_detail.visibility = View.VISIBLE


            //            if(!allTripFeed.getDriverDetail().equals("null")) {
            //                try {
            //                    JSONObject DrvObj = new JSONObject(allTripFeed.getDriverDetail());
            //                    Picasso.with(activity)
            //                            .load(Uri.parse(DrvObj.getString("image")))
            //                            .placeholder(R.drawable.avatar_placeholder)
            //                            .transform(new CircleTransform())
            //                            .into(holder.img_driver_image);
            //                } catch (JSONException e) {
            //                    e.printStackTrace();
            //                }
            //            }
            //
            //            RelativeLayout.LayoutParams drvParams = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.height_50),(int) activity.getResources().getDimension(R.dimen.height_50));
            //            drvParams.setMargins(0,(int) activity.getResources().getDimension(R.dimen.margin_40),0,0);
            //            holder.img_driver_image.setLayoutParams(drvParams);

        } else if (allTripFeed.status == "6") {
            holder.txt_current_booking.text = activity.resources.getString(R.string.driver_unavailable)
            Picasso.with(activity)
                    .load(R.drawable.status_driver_unavailable)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.GONE

            holder.layout_detail.visibility = View.VISIBLE

        } else if (allTripFeed.status == "7" || allTripFeed.status == "8") {
            var StatusImg = R.drawable.status_on_trip
            if (allTripFeed.status == "8") {
                holder.txt_current_booking.text = activity.resources.getString(R.string.on_trip)
                StatusImg = R.drawable.status_on_trip
            } else if (allTripFeed.status == "7") {
                holder.txt_current_booking.text = activity.resources.getString(R.string.driver_arrived)
                StatusImg = R.drawable.status_driver_arrived
            }
            Picasso.with(activity)
                    .load(StatusImg)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.VISIBLE
            holder.img_driver_image.visibility = View.VISIBLE
            holder.img_cancle_status.visibility = View.GONE
            holder.layout_detail.visibility = View.VISIBLE

            try {
                val DrvObj = JSONObject(allTripFeed.driverDetail)
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl + DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(CircleTransform())
                        .into(holder.img_driver_image)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val drvParams = RelativeLayout.LayoutParams(activity.resources.getDimension(R.dimen.height_50).toInt(), activity.resources.getDimension(R.dimen.height_50).toInt())
            drvParams.setMargins(0, 0, 0, 0)
            holder.img_driver_image.layoutParams = drvParams
        } else if (allTripFeed.status == "9") {
            holder.txt_current_booking.text = activity.resources.getString(R.string.completed)
            Picasso.with(activity)
                    .load(R.drawable.status_completed)
                    .into(holder.img_status)
            holder.layout_status_cancle.visibility = View.VISIBLE
            holder.img_driver_image.visibility = View.VISIBLE
            holder.img_cancle_status.visibility = View.GONE

            holder.layout_detail.visibility = View.VISIBLE

            try {
                val DrvObj = JSONObject(allTripFeed.driverDetail)
                Picasso.with(activity)
                        .load(Uri.parse(Url.DriverImageUrl + DrvObj.getString("image")))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(CircleTransform())
                        .into(holder.img_driver_image)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val drvParams = RelativeLayout.LayoutParams(activity.resources.getDimension(R.dimen.height_50).toInt(), activity.resources.getDimension(R.dimen.height_50).toInt())
            drvParams.setMargins(0, 0, 0, 0)
            holder.img_driver_image.layoutParams = drvParams

            if (allTripFeed.oldLocationList != null && allTripFeed.oldLocationList!!.size > 0) {
                allTripFeed.oldLocationList = null
            }
        }
        //        else if(allTripFeed.getStatus().equals("5")) {
        //            holder.txt_current_booking.setText(activity.getResources().getString(R.string.driver_cancelled));
        //            Picasso.with(activity)
        //                    .load(R.drawable.status_driver_cancelled)
        //                    .into(holder.img_status);
        //            holder.layout_status_cancle.setVisibility(View.VISIBLE);
        //            //holder.7mm.setVisibility(View.VISIBLE);
        //            holder.img_cancle_status.setVisibility(View.VISIBLE);
        //
        //            holder.img_cancel_trip.setVisibility(View.GONE);
        //            holder.img_detail.setVisibility(View.VISIBLE);
        //            Picasso.with(activity)
        //                    .load(R.drawable.details)
        //                    .into(holder.img_detail);
        //        }

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var pickup_date_time = ""
        try {
            val parceDate = simpleDateFormat.parse(allTripFeed.pickupDateTime)
            val parceDateFormat = SimpleDateFormat("dd MMM yyyy")
            pickup_date_time = parceDateFormat.format(parceDate.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        holder.txt_trip_date.text = pickup_date_time
        holder.txt_pickup_address.text = allTripFeed.pickupArea
        holder.txt_drop_address.text = allTripFeed.dropArea
        holder.txt_booking_id_val.text = allTripFeed.bookingId
        holder.txt_truct_type_val.setText(allTripFeed.taxiType!!.toUpperCase())

        holder.layout_footer_detail.tag = holder
        holder.layout_all_trip.tag = holder

        holder.layout_detail.tag = holder
        holder.layout_footer_detail.tag = holder
        Log.d("position", "position = $position==$itemCount")
        if (itemCount > 9 && itemCount == position + 1) {
            if (onAllTripClickListener != null)
                onAllTripClickListener!!.scrollToLoad(position)
        }
    }

    private fun bindLoadingFeedItem(holder: AllTripViewHolder) {
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
        return TripArray.size
    }

    fun updateItems() {
        itemsCount = TripArray.size
        notifyDataSetChanged()
    }

    fun updateItemsFilter(allTripArray: ArrayList<AllTripFeed>) {
        TripArray = allTripArray
        itemsCount = TripArray.size
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {

        val viewId = v.id
        val holder = v.tag as AllTripViewHolder
        if (viewId == R.id.layout_footer_detail) {
            onAllTripClickListener!!.clickDetailTrip(holder.adapterPosition)
        } else if (viewId == R.id.layout_all_trip || viewId == R.id.layout_footer_detail || viewId == R.id.layout_detail) {
            onAllTripClickListener!!.clickDetailTrip(holder.adapterPosition)
        }

    }

    inner class AllTripViewHolder(view: View) : ViewHolder(view) {

        internal var txt_current_booking: TextView
        internal var txt_trip_date: TextView
        internal var txt_pickup_address: TextView
        internal var txt_drop_address: TextView
        internal var txt_booking_id: TextView
        internal var txt_booking_id_val: TextView
        internal var txt_truct_type: TextView
        internal var txt_truct_type_val: TextView
        internal var layout_footer_detail: RelativeLayout
        internal var layout_all_trip: LinearLayout
        internal var layout_status_cancle: RelativeLayout
        internal var img_status: ImageView
        internal var img_driver_image: ImageView
        internal var img_cancle_status: ImageView
        internal var layout_detail: RelativeLayout


        init {

            txt_current_booking = view.findViewById(R.id.txt_current_booking) as TextView
            txt_trip_date = view.findViewById(R.id.txt_trip_date) as TextView
            txt_pickup_address = view.findViewById(R.id.txt_pickup_address) as TextView
            txt_drop_address = view.findViewById(R.id.txt_drop_address) as TextView
            txt_booking_id = view.findViewById(R.id.txt_booking_id) as TextView
            txt_booking_id_val = view.findViewById(R.id.txt_booking_id_val) as TextView
            txt_truct_type = view.findViewById(R.id.txt_truct_type) as TextView
            txt_truct_type_val = view.findViewById(R.id.txt_truct_type_val) as TextView
            layout_footer_detail = view.findViewById(R.id.layout_footer_detail) as RelativeLayout
            layout_all_trip = view.findViewById(R.id.layout_all_trip) as LinearLayout
            layout_status_cancle = view.findViewById(R.id.layout_status_cancle) as RelativeLayout
            img_status = view.findViewById(R.id.img_status) as ImageView
            img_driver_image = view.findViewById(R.id.img_driver_image) as ImageView
            img_cancle_status = view.findViewById(R.id.img_cancle_status) as ImageView
            layout_detail = view.findViewById(R.id.layout_detail) as RelativeLayout
        }
    }

    fun setOnAllTripItemClickListener(onAllTripClickListener: OnAllTripClickListener) {
        this.onAllTripClickListener = onAllTripClickListener
    }

    interface OnAllTripClickListener {
        fun scrollToLoad(position: Int)
        fun clickDetailTrip(position: Int)
        fun tripCancel(position: Int)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(itemsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllTripAdapter> {
        private val VIEW_TYPE_DEFAULT = 1
        private val VIEW_TYPE_LOADER = 2
        override fun createFromParcel(parcel: Parcel): AllTripAdapter {
            return AllTripAdapter(parcel)
        }

        override fun newArray(size: Int): Array<AllTripAdapter?> {
            return arrayOfNulls(size)
        }
    }

}
