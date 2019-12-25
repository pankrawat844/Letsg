package come.user.lezco.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.maps.model.LatLng
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import come.user.lezco.R
import come.user.lezco.utils.AllTripFeed
import come.user.lezco.utils.CircleTransform
import come.user.lezco.utils.Common
import come.user.lezco.utils.Url
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by techintegrity on 13/07/16.
 */

class AllTripAdapter(
    internal var activity: Activity,
    internal var TripArray: ArrayList<AllTripFeed>
) : Adapter<AllTripAdapter.AllTripViewHolder>(), View.OnClickListener,
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
        OpenSans_Semi_Bold =
            Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Semibold_0.ttf")
        OpenSans_Light = Typeface.createFromAsset(activity.assets, "fonts/OpenSans-Light_0.ttf")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTripViewHolder {

        val view =
            LayoutInflater.from(activity).inflate(R.layout.alltrip_list_layout, parent, false)
        val allTrpViewHol = AllTripViewHolder(view)
        allTrpViewHol.layout_footer_detail.setOnClickListener(this)
        allTrpViewHol.layout_all_trip.setOnClickListener(this)
        allTrpViewHol.layout_footer_detail.setOnClickListener(this)
        allTrpViewHol.layout_detail.setOnClickListener(this)
        return allTrpViewHol
    }


    override fun onBindViewHolder(viewHolder: AllTripViewHolder, position: Int) {
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

            val drvParams = RelativeLayout.LayoutParams(
                activity.resources.getDimension(R.dimen.height_50).toInt(),
                activity.resources.getDimension(R.dimen.height_50).toInt()
            )
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


        } else if (allTripFeed.status == "6") {
            holder.txt_current_booking.text =
                activity.resources.getString(R.string.driver_unavailable)
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
                holder.txt_current_booking.text =
                    activity.resources.getString(R.string.driver_arrived)
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

            val drvParams = RelativeLayout.LayoutParams(
                activity.resources.getDimension(R.dimen.height_50).toInt(),
                activity.resources.getDimension(R.dimen.height_50).toInt()
            )
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

            val drvParams = RelativeLayout.LayoutParams(
                activity.resources.getDimension(R.dimen.height_50).toInt(),
                activity.resources.getDimension(R.dimen.height_50).toInt()
            )
            drvParams.setMargins(0, 0, 0, 0)
            holder.img_driver_image.layoutParams = drvParams

            if (allTripFeed.oldLocationList != null && allTripFeed.oldLocationList!!.size > 0)
            {
                allTripFeed.oldLocationList = null
            }
        }


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
        val display: Display = activity.windowManager.defaultDisplay
        val point: Point = Point()
        display.getSize(point)
//        Log.e("alltrip....",caculationDirationIon(allTripFeed.pickupArea!!,allTripFeed.dropArea!!))
        caculationDirationIon(holder, allTripFeed.pickupArea!!, allTripFeed.dropArea!!)
//       holder.mapWebView.loadUrl("https://maps.googleapis.com/maps/api/staticmap?size=350x150&path=enc:"+caculationDirationIon(allTripFeed.pickupArea!!,allTripFeed.dropArea!!)+"&key=AIzaSyCGXH2hb0dzSQ7gttkAxZKul1E7GjWhU1o")
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


        var txt_current_booking: TextView
        var txt_trip_date: TextView
        var txt_pickup_address: TextView
        var txt_drop_address: TextView
        var txt_booking_id: TextView
        var txt_booking_id_val: TextView
        var txt_truct_type: TextView
        var txt_truct_type_val: TextView
        var layout_footer_detail: RelativeLayout
        var layout_all_trip: LinearLayout
        var layout_status_cancle: RelativeLayout
        var img_status: ImageView
        var img_driver_image: ImageView
        var img_cancle_status: ImageView
        var layout_detail: RelativeLayout
        var mapWebView: WebView

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
            mapWebView = view.findViewById(R.id.map_webview) as WebView
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

    fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {

        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }

            val location = address!![0]
            p1 = LatLng(location.getLatitude(), location.getLongitude())

        } catch (ex: IOException) {

            ex.printStackTrace()
        }

        return p1
    }


    fun caculationDirationIon(
        holder: AllTripViewHolder,
        pickupAddress: String,
        dropAddress: String
    ): String {
        var CaculationLocUrl = ""
        var poly_result = ""
        CaculationLocUrl =
            "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=" + pickupAddress.replace(
                " ",
                "%20"
            ) + "&destination=" + dropAddress.replace(" ", "%20") + "&key=" + activity.getString(
                R.string.google_server_key
            )
        Log.d("CaculationLocUrl", "CaculationLocUrl = $CaculationLocUrl")
        Ion.with(activity)
            .load(CaculationLocUrl)
            .setTimeout(10000)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error


                Log.d("Login result", "Login result = $result==$error")
                if (error == null) {
                    try {
                        val resObj = JSONObject(result.toString())
                        if (resObj.getString("status").toLowerCase() == "ok") {


                            val routArray = JSONArray(resObj.getString("routes"))
                            val routObj = routArray.getJSONObject(0)
                            val overview_polylines = routObj.getJSONObject("overview_polyline")
                            poly_result = overview_polylines.getString("points")
                            holder.mapWebView.loadUrl("https://maps.googleapis.com/maps/api/staticmap?size=350x150&path=enc:" + poly_result + "&key=AIzaSyCGXH2hb0dzSQ7gttkAxZKul1E7GjWhU1o")
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    Common.ShowHttpErrorMessage(activity, error.message)
                }

            }
        return poly_result
//        MarkerAdd()

//        SetNowDialogCabValue()
    }
}
