package come.user.lezco

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager

import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu

import com.koushikdutta.ion.Ion
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.adapter.AllTripAdapter
import come.user.lezco.utils.AllTripFeed
import come.user.lezco.utils.Common
import come.user.lezco.utils.Url
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AllTripActivity : AppCompatActivity(), AllTripAdapter.OnAllTripClickListener {

    internal var layout_slidemenu: RelativeLayout? = null
    internal var txt_all_trip: TextView? = null
    internal var layout_filter: RelativeLayout? = null
    internal var recycle_all_trip: RecyclerView? = null
    internal var swipe_refresh_layout: SwipeRefreshLayout? = null
    internal var layout_background: RelativeLayout? = null
    internal var layout_no_recourd_found: RelativeLayout? = null
    internal var layout_recycleview: LinearLayout? = null

    internal var chk_all: CheckBox? = null
    internal var chk_pen_book: CheckBox? = null
    internal var chk_com_book: CheckBox? = null
    internal var chk_drv_reject: CheckBox? = null
    internal var chk_user_reject: CheckBox? = null
    lateinit var chk_drv_accept: CheckBox

    internal var checkAllClick = false

    internal var allTripAdapter: AllTripAdapter? = null

    lateinit var userPref: SharedPreferences

    internal var allTripArray: ArrayList<AllTripFeed>? = null
    private var AllTripLayoutManager: RecyclerView.LayoutManager? = null

    lateinit var OpenSans_Bold: Typeface
    lateinit var OpenSans_Regular: Typeface
    lateinit var Roboto_Bold: Typeface

    lateinit var filterDialog: Dialog
    internal var FilterString = ""

    lateinit var slidingMenu: SlidingMenu

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    lateinit var chk_drive_late: CheckBox
    lateinit var chk_changed_mind: CheckBox
    lateinit var chk_another_cab: CheckBox
    lateinit var chk_denied_duty: CheckBox
    lateinit var SelTripFeeds: AllTripFeed

    internal var common = Common()
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_trip)

        layout_slidemenu = findViewById(R.id.layout_slidemenu) as RelativeLayout?
        txt_all_trip = findViewById(R.id.txt_all_trip) as TextView?
        layout_filter = findViewById(R.id.layout_filter) as RelativeLayout?
        recycle_all_trip = findViewById(R.id.recycle_all_trip) as RecyclerView?
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout?
        layout_background = findViewById(R.id.layout_background) as RelativeLayout?
        layout_no_recourd_found = findViewById(R.id.layout_no_recourd_found) as RelativeLayout?
        layout_recycleview = findViewById(R.id.layout_recycleview) as LinearLayout?


        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_all_trip!!.typeface = OpenSans_Bold

        userPref = PreferenceManager.getDefaultSharedPreferences(this@AllTripActivity)

        AllTripLayoutManager = LinearLayoutManager(this)
        recycle_all_trip!!.layoutManager = AllTripLayoutManager

        ProgressDialog = Dialog(this@AllTripActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading


        Handler().postDelayed({
            if (Common.isNetworkAvailable(this@AllTripActivity)) {
                ProgressDialog.show()
                cusRotateLoading.start()
                Log.d(
                    "check Value",
                    "check value = " + userPref.getInt(
                        "pending booking",
                        5
                    ) + "==" + userPref.getInt(
                        "user reject",
                        5
                    ) + "==" + userPref.getInt(
                        "driver unavailable",
                        5
                    ) + "==" + userPref.getInt("complete booking", 5)
                )
                if (userPref.getBoolean("setFilter", false) == true) {
                    if (userPref.getInt("pending booking", 0) == 1) {
                        FilterString += 1.toString() + ","
                    }
                    if (userPref.getInt("complete booking", 0) == 9) {
                        FilterString += 9.toString() + ","
                    }
                    if (userPref.getInt("driver unavailable", 0) == 6) {
                        FilterString += 6.toString() + ","
                    }
                    if (userPref.getInt("user reject", 0) == 4) {
                        FilterString += 4.toString() + ","
                    }
                    if (userPref.getInt("driver accept", 0) == 3) {
                        FilterString += 3.toString() + ","
                    }

                    FilterString = FilterString.substring(0, FilterString.length - 1)
                    Log.d("FilterString", "FilterString = $FilterString")

                    if (userPref.getInt(
                            "pending booking",
                            0
                        ) == 1 && userPref.getInt(
                            "complete booking",
                            0
                        ) == 9 && userPref.getInt(
                            "driver unavailable",
                            0
                        ) == 6 && userPref.getInt(
                            "user reject",
                            0
                        ) == 4 && userPref.getInt("driver accept", 0) == 3
                    ) {
                        val checkAll = userPref.edit()
                        checkAll.putString("check all", "check all")
                        checkAll.commit()
                        FilterString = ""
                    } else {
                        val checkAll = userPref.edit()
                        checkAll.putString("check all", "")
                        checkAll.commit()
                    }
                    FilterAllTrips(0, "filter")
                    FilterString = ""
                } else {
                    getAllTrip(0)
                }

            } else {
                Common.showInternetInfo(this@AllTripActivity, "Network is not available")
                swipe_refresh_layout!!.isEnabled = false
            }
        }, 1000)

        swipe_refresh_layout!!.setOnRefreshListener {
            recycle_all_trip!!.isEnabled = false
            if (Common.isNetworkAvailable(this@AllTripActivity)) {
                if (userPref.getBoolean("setFilter", false) == true) {
                    if (userPref.getInt("pending booking", 6) == 1) {
                        FilterString += 1.toString() + ","
                    }
                    if (userPref.getInt("complete booking", 0) == 9) {
                        FilterString += 9.toString() + ","
                    }
                    if (userPref.getInt("driver unavailable", 0) == 6) {
                        FilterString += 6.toString() + ","
                    }
                    if (userPref.getInt("user reject", 0) == 4) {
                        FilterString += 4.toString() + ","
                    }
                    if (userPref.getInt("user reject", 0) == 4) {
                        FilterString += 4.toString() + ","
                    }
                    if (userPref.getInt("driver accept", 0) == 3) {
                        FilterString += 3.toString() + ","
                    }

                    FilterString = FilterString.substring(0, FilterString.length - 1)
                    Log.d("FilterString", "FilterString = $FilterString")

                    if (userPref.getInt(
                            "pending booking",
                            0
                        ) == 1 && userPref.getInt(
                            "complete booking",
                            0
                        ) == 9 && userPref.getInt(
                            "driver unavailable",
                            0
                        ) == 6 && userPref.getInt(
                            "user reject",
                            0
                        ) == 4 && userPref.getInt("driver accept", 0) == 3
                    ) {
                        FilterString = ""
                    }

                    FilterAllTrips(0, "")
                    FilterString = ""
                } else {
                    getAllTrip(0)
                }
            } else {
                //Network is not available
                recycle_all_trip!!.isEnabled = true
                Common.showInternetInfo(this@AllTripActivity, "Network is not available")
            }
        }

        /*Filter Dialog Start*/

        filterDialog = Dialog(this@AllTripActivity, R.style.DialogSlideAnim)
        filterDialog.setContentView(R.layout.all_trip_filter_dialog)

        layout_filter!!.setOnClickListener {
            //                if (layout_background.getVisibility() == View.GONE) {
            //                    layout_background.animate()
            //                        .translationY(layout_background.getHeight()).alpha(2.0f)
            //                        .setListener(new AnimatorListenerAdapter() {
            //                            @Override
            //                            public void onAnimationStart(Animator animation) {
            //                                super.onAnimationStart(animation);
            //                                layout_background.setVisibility(View.VISIBLE);
            //                                layout_background.setAlpha(0.0f);
            //                            }
            //                        });
            //                }
            layout_background!!.visibility = View.VISIBLE
            filterDialog.show()
        }

        val txt_all = filterDialog.findViewById(R.id.txt_all) as TextView
        txt_all.typeface = OpenSans_Regular
        val txt_pending_booking = filterDialog.findViewById(R.id.txt_pending_booking) as TextView
        txt_pending_booking.typeface = OpenSans_Regular
        val txt_com_booking = filterDialog.findViewById(R.id.txt_com_booking) as TextView
        txt_com_booking.typeface = OpenSans_Regular
        val txt_drv_una = filterDialog.findViewById(R.id.txt_drv_una) as TextView
        txt_drv_una.typeface = OpenSans_Regular
        val txt_usr_rej = filterDialog.findViewById(R.id.txt_usr_rej) as TextView
        txt_usr_rej.typeface = OpenSans_Regular
        val txt_drv_accept = filterDialog.findViewById(R.id.txt_drv_accept) as TextView
        txt_drv_accept.typeface = OpenSans_Regular

        chk_all = filterDialog.findViewById(R.id.chk_all) as CheckBox

        val layout_all_check = filterDialog.findViewById(R.id.layout_all_check) as RelativeLayout
        CheckBoxChecked(layout_all_check, chk_all!!, "all")

        chk_pen_book = filterDialog.findViewById(R.id.chk_pen_book) as CheckBox
        val layour_pen_book_check =
            filterDialog.findViewById(R.id.layour_pen_book_check) as RelativeLayout
        CheckBoxChecked(layour_pen_book_check, chk_pen_book!!, "pending book")

        chk_com_book = filterDialog.findViewById(R.id.chk_com_book) as CheckBox
        val layout_com_book_check =
            filterDialog.findViewById(R.id.layout_com_book_check) as RelativeLayout
        CheckBoxChecked(layout_com_book_check, chk_com_book!!, "completed book")

        chk_drv_reject = filterDialog.findViewById(R.id.chk_drv_reject) as CheckBox
        val layout_drv_reject_check =
            filterDialog.findViewById(R.id.layout_drv_reject_check) as RelativeLayout
        CheckBoxChecked(layout_drv_reject_check, chk_drv_reject!!, "driver reject")

        chk_user_reject = filterDialog.findViewById(R.id.chk_user_reject) as CheckBox
        val layout_user_reject_check =
            filterDialog.findViewById(R.id.layout_user_reject_check) as RelativeLayout
        CheckBoxChecked(layout_user_reject_check, chk_user_reject!!, "user reject")

        chk_drv_accept = filterDialog.findViewById(R.id.chk_drv_accept) as CheckBox
        val layout_drv_accept_check =
            filterDialog.findViewById(R.id.layout_drv_accept_check) as RelativeLayout
        CheckBoxChecked(layout_drv_accept_check, chk_drv_accept, "drive accept")

        Log.d(
            "check Value",
            "check value = " + userPref.getInt(
                "pending booking",
                5
            ) + "==" + userPref.getInt(
                "user reject",
                5
            ) + "==" + userPref.getInt(
                "driver unavailable",
                5
            ) + "==" + userPref.getInt("complete booking", 5)
        )
        if (userPref.getInt("user reject", 0) == 4)
            chk_user_reject!!.isChecked = true
        if (userPref.getInt("driver unavailable", 0) == 6)
            chk_drv_reject!!.isChecked = true
        if (userPref.getInt("complete booking", 0) == 9)
            chk_com_book!!.isChecked = true
        if (userPref.getInt("pending booking", 0) == 1)
            chk_pen_book!!.isChecked = true
        if (userPref.getInt("driver accept", 0) == 3) {
            chk_drv_accept.isChecked = true
        }

        if (userPref.getInt("user reject", 0) == 4 && userPref.getInt(
                "driver unavailable",
                0
            ) == 6 && userPref.getInt(
                "complete booking",
                0
            ) == 9 && userPref.getInt("pending booking", 0) == 1 && userPref.getInt(
                "driver accept",
                0
            ) == 3
        ) {
            chk_all!!.isChecked = true
        }

        val img_close_icon = filterDialog.findViewById(R.id.img_close_icon) as ImageView
        img_close_icon.setOnClickListener {
            layout_background!!.visibility = View.GONE
            filterDialog.cancel()
        }

        //        TextView txt_cancel_popup = (TextView)filterDialog.findViewById(R.id.txt_cancel_popup);
        //        txt_cancel_popup.setTypeface(Roboto_Bold);

        val layout_calcel = filterDialog.findViewById(R.id.layout_calcel) as RelativeLayout
        layout_calcel.setOnClickListener {
            layout_background!!.visibility = View.GONE
            filterDialog.cancel()
        }

        val layout_apply = filterDialog.findViewById(R.id.layout_apply) as RelativeLayout
        layout_apply.setOnClickListener {
            ProgressDialog.show()
            cusRotateLoading.start()

            layout_background!!.visibility = View.GONE
            filterDialog.cancel()
            var setFilter = false
            val pending_booking = userPref.edit()
            if (chk_pen_book!!.isChecked) {
                FilterString = 1.toString() + ","
                pending_booking.putInt("pending booking", 1)
                setFilter = true
            } else
                pending_booking.putInt("pending booking", 0)
            pending_booking.commit()
            val complete_booking = userPref.edit()
            if (chk_com_book!!.isChecked) {
                FilterString += 9.toString() + ","
                complete_booking.putInt("complete booking", 9)
                setFilter = true
            } else
                complete_booking.putInt("complete booking", 0)
            complete_booking.commit()

            val user_reject = userPref.edit()
            if (chk_user_reject!!.isChecked) {
                FilterString += 4.toString() + ","
                user_reject.putInt("user reject", 4)
                setFilter = true
            } else
                user_reject.putInt("user reject", 0)
            user_reject.commit()

            val driver_accept = userPref.edit()
            if (chk_drv_accept.isChecked) {
                FilterString += 3.toString() + ","
                driver_accept.putInt("driver accept", 3)
                setFilter = true
            } else
                driver_accept.putInt("driver accept", 0)
            driver_accept.commit()

            val driver_unavailable = userPref.edit()
            if (chk_drv_reject!!.isChecked) {
                FilterString += 6.toString() + ","
                driver_unavailable.putInt("driver unavailable", 6)
                setFilter = true
            } else
                driver_unavailable.putInt("driver unavailable", 0)
            driver_unavailable.commit()

            if (FilterString.length > 0)
                FilterString = FilterString.substring(0, FilterString.length - 1)

            val clickfilter = userPref.edit()
            clickfilter.putBoolean("setFilter", setFilter)
            clickfilter.commit()

            if (userPref.getInt("pending booking", 0) == 1 && userPref.getInt(
                    "complete booking",
                    0
                ) == 9 && userPref.getInt(
                    "driver unavailable",
                    0
                ) == 6 && userPref.getInt("user reject", 0) == 4 && userPref.getInt(
                    "driver accept",
                    0
                ) == 3
            ) {
                FilterString = ""
            }

            FilterAllTrips(0, "filter")
            FilterString = ""
        }

        /*Filter Dialog End*/


        /*Slide Menu Start*/

        slidingMenu = SlidingMenu(this)
        slidingMenu.mode = SlidingMenu.LEFT
        slidingMenu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width)
        slidingMenu.setFadeDegree(0.20f)
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        slidingMenu.setMenu(R.layout.left_menu)

        common.SlideMenuDesign(slidingMenu, this@AllTripActivity, "all trip")

        layout_slidemenu!!.setOnClickListener { slidingMenu.toggle() }
    }

    fun CheckBoxChecked(relativeLayout: RelativeLayout, checkBox: CheckBox, checkBoxValue: String) {

        checkBox.setOnClickListener {
            Log.d("checkAllClick", "checkAllClick = $checkAllClick==$checkAllClick")
            if (checkBoxValue == "all") {
                if (checkAllClick) {
                    chk_all!!.isChecked = false
                    chk_pen_book!!.isChecked = false
                    chk_com_book!!.isChecked = false
                    chk_drv_reject!!.isChecked = false
                    chk_user_reject!!.isChecked = false
                    chk_drv_accept.isChecked = false
                    checkAllClick = false
                } else {
                    chk_all!!.isChecked = true
                    chk_pen_book!!.isChecked = true
                    chk_com_book!!.isChecked = true
                    chk_drv_reject!!.isChecked = true
                    chk_user_reject!!.isChecked = true
                    chk_drv_accept.isChecked = true
                    checkAllClick = true
                }
            } else {
                if (chk_pen_book!!.isChecked && chk_com_book!!.isChecked && chk_drv_reject!!.isChecked && chk_user_reject!!.isChecked && chk_drv_accept.isChecked) {
                    chk_all!!.isChecked = true
                    checkAllClick = true
                } else {
                    chk_all!!.isChecked = false
                    checkAllClick = false
                }

            }
        }

        relativeLayout.setOnClickListener {
            if (checkBox.isChecked)
                checkBox.isChecked = false
            else
                checkBox.isChecked = true
            Log.d("checkAllClick", "checkAllClick = $checkAllClick==$checkAllClick")
            if (checkBoxValue == "all") {
                if (checkAllClick) {
                    chk_all!!.isChecked = false
                    chk_pen_book!!.isChecked = false
                    chk_com_book!!.isChecked = false
                    chk_drv_reject!!.isChecked = false
                    chk_user_reject!!.isChecked = false
                    chk_drv_accept.isChecked = false
                    checkAllClick = false
                } else {
                    chk_all!!.isChecked = true
                    chk_pen_book!!.isChecked = true
                    chk_com_book!!.isChecked = true
                    chk_drv_reject!!.isChecked = true
                    chk_user_reject!!.isChecked = true
                    chk_drv_accept.isChecked = true
                    checkAllClick = true
                }
            } else {
                if (chk_pen_book!!.isChecked && chk_com_book!!.isChecked && chk_drv_reject!!.isChecked && chk_user_reject!!.isChecked && chk_drv_accept.isChecked) {
                    chk_all!!.isChecked = true
                    checkAllClick = true
                } else {
                    chk_all!!.isChecked = false
                    checkAllClick = false
                }

            }
        }
    }

    fun getAllTrip(offset: Int) {

        if (offset == 0) {
            allTripArray = ArrayList()
        }

        Log.d(
            "loadTripsUrl",
            "loadTripsUrl =" + Url.loadTripsUrl + "=" + userPref.getString(
                "id",
                ""
            ) + "==" + offset.toString()
        )
        Ion.with(this@AllTripActivity)
            .load(Url.loadTripsUrl)
            .setTimeout(6000)
            //.setJsonObjectBody(json)
            .setMultipartParameter("user_id", userPref.getString("id", ""))
            .setMultipartParameter("off", offset.toString())
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error
                Log.d("load_trips result", "load_trips result = $result==$error")
                if (error == null) {

                    ProgressDialog.cancel()
                    cusRotateLoading.stop()
                    try {
                        val resObj = JSONObject(result.toString())
                        Log.d("loadTripsUrl", "loadTripsUrl two= $resObj")
                        swipe_refresh_layout!!.isRefreshing = false
                        if (resObj.getString("status") == "success") {
                            recycle_all_trip!!.isEnabled = true
                            val tripArray = JSONArray(resObj.getString("all_trip"))
                            for (t in 0 until tripArray.length()) {
                                val trpObj = tripArray.getJSONObject(t)
                                val allTripFeed = AllTripFeed()
                                allTripFeed.bookingId = trpObj.getString("id")
                                allTripFeed.dropArea = trpObj.getString("drop_area")
                                allTripFeed.pickupArea = trpObj.getString("pickup_area")
                                allTripFeed.taxiType = trpObj.getString("taxi_type")
                                allTripFeed.pickupDateTime =
                                    trpObj.getString("book_create_date_time")
                                allTripFeed.amount = trpObj.getString("amount")
                                allTripFeed.carIcon = trpObj.getString("icon")
                                allTripFeed.km = trpObj.getString("distance")
                                allTripFeed.driverDetail = trpObj.getString("driver_detail")
                                allTripFeed.status = trpObj.getString("status")
                                allTripFeed.approxTime = trpObj.getString("approx_time")
                                allTripFeed.oldLocationList = null
                                allTripFeed.startPickLatLng = trpObj.getString("pickup_lat")
                                allTripFeed.endPickLatLng = trpObj.getString("pickup_longs")
                                allTripFeed.startDropLatLng = trpObj.getString("drop_lat")
                                allTripFeed.endDropLatLng = trpObj.getString("drop_longs")

                                allTripArray!!.add(allTripFeed)
                            }
                            Log.d("loadTripsUrl", "loadTripsUrl three= " + allTripArray!!.size)
                            if (allTripArray != null && allTripArray!!.size > 0) {
                                if (offset == 0) {
                                    layout_recycleview!!.visibility = View.VISIBLE
                                    layout_no_recourd_found!!.visibility = View.GONE
                                    allTripAdapter =
                                        AllTripAdapter(this@AllTripActivity, allTripArray!!)
                                    recycle_all_trip!!.adapter = allTripAdapter
                                    allTripAdapter!!.setOnAllTripItemClickListener(this@AllTripActivity)
                                    ProgressDialog.cancel()
                                    cusRotateLoading.stop()
                                }
                                allTripAdapter!!.updateItems()
                                swipe_refresh_layout!!.isEnabled = true
                            }
                        } else if (resObj.getString("status") == "false") {
                            Common.user_InActive = 1
                            Common.InActive_msg = resObj.getString("message")
                            val editor = userPref.edit()
                            editor.clear()
                            editor.commit()
                            val logInt =
                                Intent(this@AllTripActivity, LoginOptionActivity::class.java)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(logInt)

                        } else {
                            if (offset == 0) {
                                ProgressDialog.cancel()
                                cusRotateLoading.stop()
                                layout_recycleview!!.visibility = View.GONE
                                layout_no_recourd_found!!.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@AllTripActivity,
                                    resObj.getString("message").toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    ProgressDialog.cancel()
                    cusRotateLoading.stop()

                    Common.ShowHttpErrorMessage(this@AllTripActivity, error.message)
                }
            }

    }

    fun FilterAllTrips(offset: Int, filter: String) {

        if (offset == 0)
            allTripArray = ArrayList()

        Log.d(
            "loadTripsUrl",
            "loadTripsUrl FilterString= " + Url.loadTripsFiltersUrl + "==" + FilterString + "==" + userPref.getString(
                "id",
                ""
            ) + "==" + offset.toString()
        )

        Ion.with(this@AllTripActivity)
            .load(Url.loadTripsFiltersUrl)
            .setTimeout(6000)
            //.setJsonObjectBody(json)
            .setMultipartParameter("user_id", userPref.getString("id", ""))
            .setMultipartParameter("off", offset.toString())
            .setMultipartParameter("filter", FilterString)
            .asJsonObject()
            .setCallback { error, result ->
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                if (error == null) {
                    try {
                        val resObj = JSONObject(result.toString())
                        Log.d(
                            "loadTripsUrl",
                            "loadTripsUrl filter two= " + resObj.getString("status") + "==" + resObj
                        )
                        if (resObj.getString("status") == "success") {

                            recycle_all_trip!!.isEnabled = true

                            val tripArray = JSONArray(resObj.getString("all_trip"))
                            for (t in 0 until tripArray.length()) {
                                val trpObj = tripArray.getJSONObject(t)
                                val allTripFeed = AllTripFeed()
                                allTripFeed.bookingId = trpObj.getString("id")
                                allTripFeed.dropArea = trpObj.getString("drop_area")
                                allTripFeed.pickupArea = trpObj.getString("pickup_area")
                                allTripFeed.taxiType = trpObj.getString("car_type")
                                allTripFeed.pickupDateTime =
                                    trpObj.getString("book_create_date_time")
                                allTripFeed.amount = trpObj.getString("amount")
                                allTripFeed.carIcon = trpObj.getString("icon")
                                allTripFeed.km = trpObj.getString("km")
                                allTripFeed.driverDetail = trpObj.getString("driver_detail")
                                allTripFeed.status = trpObj.getString("status")
                                allTripFeed.approxTime = trpObj.getString("approx_time")
                                allTripFeed.oldLocationList = null
                                allTripFeed.startPickLatLng = trpObj.getString("pickup_lat")
                                allTripFeed.endPickLatLng = trpObj.getString("pickup_longs")
                                allTripFeed.startDropLatLng = trpObj.getString("drop_lat")
                                allTripFeed.endDropLatLng = trpObj.getString("drop_longs")
                                allTripArray!!.add(allTripFeed)
                            }
                            Log.d("loadTripsUrl", "loadTripsUrl three= " + allTripArray!!.size)
                            if (allTripArray != null && allTripArray!!.size > 0) {
                                layout_recycleview!!.visibility = View.VISIBLE
                                layout_no_recourd_found!!.visibility = View.GONE
                                if (offset == 0) {
                                    allTripAdapter =
                                        AllTripAdapter(this@AllTripActivity, allTripArray!!)
                                    recycle_all_trip!!.adapter = allTripAdapter
                                    allTripAdapter!!.setOnAllTripItemClickListener(this@AllTripActivity)
                                    swipe_refresh_layout!!.isRefreshing = false

                                }
                                allTripAdapter!!.updateItemsFilter(allTripArray!!)
                                if (swipe_refresh_layout != null)
                                    swipe_refresh_layout!!.isEnabled = true
                            }
                        } else if (resObj.getString("status") == "false") {
                            Common.user_InActive = 1
                            Common.InActive_msg = resObj.getString("message")

                            val editor = userPref.edit()
                            editor.clear()
                            editor.commit()

                            val logInt =
                                Intent(this@AllTripActivity, LoginOptionActivity::class.java)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(logInt)
                        } else if (resObj.getString("status") == "failed") {
                            Log.d("allTripArray", "allTripArray = " + allTripArray!!.size)
                            if (swipe_refresh_layout != null)
                                swipe_refresh_layout!!.isEnabled = false
                            if (allTripAdapter != null)
                                allTripAdapter!!.updateItemsFilter(allTripArray!!)

                            if (offset == 0) {
                                ProgressDialog.cancel()
                                cusRotateLoading.stop()
                                layout_recycleview!!.visibility = View.GONE
                                layout_no_recourd_found!!.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    this@AllTripActivity,
                                    resObj.getString("message").toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    Common.ShowHttpErrorMessage(this@AllTripActivity, error.message)
                }
            }

    }

    override fun scrollToLoad(position: Int) {

        if (userPref.getBoolean("setFilter", true) == true && userPref.getString(
                "check all",
                ""
            ) != "check all"
        ) {
            if (userPref.getInt("pending booking", 0) == 1) {
                FilterString += 1.toString() + ","
            }
            if (userPref.getInt("complete booking", 0) == 9) {
                FilterString += 9.toString() + ","
            }
            if (userPref.getInt("driver unavailable", 0) == 6) {
                FilterString += 6.toString() + ","
            }
            if (userPref.getInt("user reject", 0) == 4) {
                FilterString += 4.toString() + ","
            }
            if (userPref.getInt("driver accept", 0) == 3) {
                FilterString += 3.toString() + ","
            }

            if (FilterString.length > 0)
                FilterString = FilterString.substring(0, FilterString.length - 1)

            FilterAllTrips(position + 1, "")
            FilterString = ""
        } else {
            getAllTrip(position + 1)
        }

    }

    override fun clickDetailTrip(position: Int) {

        if (allTripArray!!.size > 0) {
            Common.allTripFeeds = allTripArray!![position]
            val di = Intent(this@AllTripActivity, BookingDetailActivity::class.java)
            startActivity(di)
        }
    }

    override fun tripCancel(position: Int) {

        val CancelBookingDialog =
            Dialog(this@AllTripActivity, android.R.style.Theme_Translucent_NoTitleBar)
        CancelBookingDialog.setContentView(R.layout.cancel_booking_dialog)
        CancelBookingDialog.show()

        chk_drive_late = CancelBookingDialog.findViewById(R.id.chk_drive_late) as CheckBox
        chk_drive_late.setOnClickListener { CheckBoxCheck("driver_late") }


        val layout_driver_late =
            CancelBookingDialog.findViewById(R.id.layout_driver_late) as RelativeLayout
        layout_driver_late.setOnClickListener { CheckBoxCheck("driver_late") }

        chk_changed_mind = CancelBookingDialog.findViewById(R.id.chk_changed_mind) as CheckBox
        chk_changed_mind.setOnClickListener { CheckBoxCheck("changed_mind") }

        val layout_change_mind =
            CancelBookingDialog.findViewById(R.id.layout_change_mind) as RelativeLayout
        layout_change_mind.setOnClickListener { CheckBoxCheck("changed_mind") }

        chk_another_cab = CancelBookingDialog.findViewById(R.id.chk_another_cab) as CheckBox
        chk_another_cab.setOnClickListener { CheckBoxCheck("another_cab") }
        val layout_another_cab =
            CancelBookingDialog.findViewById(R.id.layout_another_cab) as RelativeLayout
        layout_another_cab.setOnClickListener { CheckBoxCheck("another_cab") }
        chk_denied_duty = CancelBookingDialog.findViewById(R.id.chk_denied_duty) as CheckBox
        chk_denied_duty.setOnClickListener { CheckBoxCheck("denied_duty") }
        val layout_denied_dute =
            CancelBookingDialog.findViewById(R.id.layout_denied_dute) as RelativeLayout
        layout_denied_dute.setOnClickListener { CheckBoxCheck("denied_duty") }

        val layout_dont_cancel =
            CancelBookingDialog.findViewById(R.id.layout_dont_cancel) as RelativeLayout
        layout_dont_cancel.setOnClickListener { CancelBookingDialog.cancel() }

        val layout_cancel_ride =
            CancelBookingDialog.findViewById(R.id.layout_cancel_ride) as RelativeLayout
        layout_cancel_ride.setOnClickListener {
            CancelBookingDialog.cancel()

            ProgressDialog.show()
            cusRotateLoading.start()

            SelTripFeeds = allTripArray!![position]

            Log.d(
                "deleteCabUrl",
                "deleteCabUrl = " + Url.deleteCabUrl + "?" + SelTripFeeds.bookingId + "==" + userPref.getString(
                    "id",
                    ""
                )
            )
            Ion.with(this@AllTripActivity)
                .load(
                    Url.deleteCabUrl + "?booking_id=" + SelTripFeeds.bookingId + "&uid=" + userPref.getString(
                        "id",
                        ""
                    )
                )
                .setTimeout(6000)
                //.setJsonObjectBody(json)
                //                        .setMultipartParameter("booking_id", SelTripFeeds.getBookingId())
                //                        .setMultipartParameter("uid", userPref.getString("id", ""))
                .asJsonObject()
                .setCallback { error, result ->
                    ProgressDialog.cancel()
                    cusRotateLoading.stop()
                    if (error == null) {
                        try {
                            val resObj = JSONObject(result.toString())
                            if (resObj.getString("status") == "success") {
                                SelTripFeeds.status = "4"
                                allTripAdapter!!.notifyItemChanged(position)

                                Handler().postDelayed({
                                    val homeInt =
                                        Intent(this@AllTripActivity, HomeActivity::class.java)
                                    homeInt.putExtra("cancel_booking", "1")
                                    startActivity(homeInt)
                                    finish()
                                }, 1000)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        ProgressDialog.cancel()
                        cusRotateLoading.stop()
                        Common.ShowHttpErrorMessage(this@AllTripActivity, error.message)
                    }
                }
        }

    }

    fun CheckBoxCheck(CheckString: String) {

        if (CheckString == "driver_late")
            chk_drive_late.isChecked = true
        else
            chk_drive_late.isChecked = false

        Log.d("CheckString", "CheckString = $CheckString")
        if (CheckString == "changed_mind")
            chk_changed_mind.isChecked = true
        else
            chk_changed_mind.isChecked = false

        if (CheckString == "another_cab")
            chk_another_cab.isChecked = true
        else
            chk_another_cab.isChecked = false

        if (CheckString == "denied_duty")
            chk_denied_duty.isChecked = true
        else
            chk_denied_duty.isChecked = false
    }

    public override fun onDestroy() {
        super.onDestroy()

        layout_slidemenu = null
        txt_all_trip = null
        layout_filter = null
        recycle_all_trip = null
        swipe_refresh_layout = null
        layout_background = null
        layout_no_recourd_found = null
        layout_recycleview = null
        chk_all = null
        chk_pen_book = null
        chk_com_book = null
        chk_drv_reject = null
        chk_user_reject = null
        allTripAdapter = null

        unregisterReceiver(receiver)
    }

    public override fun onResume() {
        super.onResume()

        val filter = IntentFilter("come.naqil.naqil.AllTripActivity")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (Common.isNetworkAvailable(this@AllTripActivity)) {

                    ProgressDialog.show()
                    cusRotateLoading.start()

                    if (userPref.getBoolean("setFilter", false) == true) {
                        if (userPref.getInt("pending booking", 6) == 1) {
                            FilterString += 1.toString() + ","
                        }
                        if (userPref.getInt("complete booking", 0) == 9) {
                            FilterString += 9.toString() + ","
                        }
                        if (userPref.getInt("driver unavailable", 0) == 6) {
                            FilterString += 6.toString() + ","
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4.toString() + ","
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4.toString() + ","
                        }
                        if (userPref.getInt("driver accept", 0) == 3) {
                            FilterString += 3.toString() + ","
                        }

                        FilterString = FilterString.substring(0, FilterString.length - 1)
                        Log.d("FilterString", "FilterString = $FilterString")

                        if (userPref.getInt(
                                "pending booking",
                                0
                            ) == 1 && userPref.getInt(
                                "complete booking",
                                0
                            ) == 9 && userPref.getInt(
                                "driver unavailable",
                                0
                            ) == 6 && userPref.getInt(
                                "user reject",
                                0
                            ) == 4 && userPref.getInt("driver accept", 0) == 3
                        ) {
                            FilterString = ""
                        }

                        FilterAllTrips(0, "")
                        FilterString = ""
                    } else {
                        getAllTrip(0)
                    }
                } else {
                    //Network is not available
                    recycle_all_trip!!.isEnabled = true
                    Common.showInternetInfo(this@AllTripActivity, "Network is not available")
                }
            }
        }
        registerReceiver(receiver, filter)


        if (Common.is_pusnotification == 1) {
            Common.is_pusnotification = 0
            Handler().postDelayed({
                if (Common.isNetworkAvailable(this@AllTripActivity)) {
                    ProgressDialog.show()
                    cusRotateLoading.start()
                    val allFilter: Boolean
                    Log.d(
                        "check Value",
                        "check value = " + userPref.getInt(
                            "pending booking",
                            5
                        ) + "==" + userPref.getInt(
                            "user reject",
                            5
                        ) + "==" + userPref.getInt(
                            "driver unavailable",
                            5
                        ) + "==" + userPref.getInt("complete booking", 5)
                    )
                    if (userPref.getBoolean("setFilter", false) == true) {
                        if (userPref.getInt("pending booking", 0) == 1) {
                            FilterString += 1.toString() + ","
                        }
                        if (userPref.getInt("complete booking", 0) == 9) {
                            FilterString += 9.toString() + ","
                        }
                        if (userPref.getInt("driver unavailable", 0) == 6) {
                            FilterString += 6.toString() + ","
                        }
                        if (userPref.getInt("user reject", 0) == 4) {
                            FilterString += 4.toString() + ","
                        }
                        if (userPref.getInt("driver accept", 0) == 3) {
                            FilterString += 3.toString() + ","
                        }

                        FilterString = FilterString.substring(0, FilterString.length - 1)
                        Log.d("FilterString", "FilterString = $FilterString")

                        if (userPref.getInt(
                                "pending booking",
                                0
                            ) == 1 && userPref.getInt(
                                "complete booking",
                                0
                            ) == 9 && userPref.getInt(
                                "driver unavailable",
                                0
                            ) == 6 && userPref.getInt(
                                "user reject",
                                0
                            ) == 4 && userPref.getInt("driver accept", 0) == 3
                        ) {
                            val checkAll = userPref.edit()
                            checkAll.putString("check all", "check all")
                            checkAll.commit()
                            FilterString = ""
                        } else {
                            val checkAll = userPref.edit()
                            checkAll.putString("check all", "")
                            checkAll.commit()
                        }
                        FilterAllTrips(0, "filter")
                        FilterString = ""
                    } else {
                        getAllTrip(0)
                    }

                } else {
                    Common.showInternetInfo(this@AllTripActivity, "Network is not available")
                    swipe_refresh_layout!!.isEnabled = false
                }
            }, 500)
        }

        common.SlideMenuDesign(slidingMenu, this@AllTripActivity, "all trip")
    }

    override fun onBackPressed() {

        if (slidingMenu.isMenuShowing) {
            slidingMenu.toggle()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { arg0, arg1 -> super@AllTripActivity.onBackPressed() }
                .create().show()
        }
    }
}
