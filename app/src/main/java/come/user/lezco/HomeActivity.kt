package come.user.lezco


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.koushikdutta.ion.Ion
import com.paypal.android.sdk.payments.*
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.adapter.CabDetailAdapter
import come.user.lezco.adapter.PickupDropLocationAdapter
import come.user.lezco.api.FilterApi
import come.user.lezco.gpsLocation.GPSTracker
import come.user.lezco.gpsLocation.LocationAddress
import come.user.lezco.model.NeareastDriver
import come.user.lezco.utils.CabDetails
import come.user.lezco.utils.Common
import kotlinx.android.synthetic.main.activity_home1.*
import kotlinx.android.synthetic.main.activity_home1.layout_footer
import kotlinx.android.synthetic.main.bottomsheet_filter.*
import kotlinx.android.synthetic.main.bottomsheet_select_car.*
import kotlinx.android.synthetic.main.bottomsheet_wishes.*
import kotlinx.android.synthetic.main.now_dialog_layout.*
import kotlinx.android.synthetic.main.reservation_dialog_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : FragmentActivity(), OnMapReadyCallback,
    CabDetailAdapter.OnCabDetailClickListener,
    PickupDropLocationAdapter.OnDraoppickupClickListener {
    private val TAG: String = "HomeActivity"
    lateinit var userPref: SharedPreferences
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var sheetBehavior_wishes: BottomSheetBehavior<View>
    private lateinit var sheetBehavior_filter: BottomSheetBehavior<View>
    var currentTime = SimpleDateFormat("HH:mm")
    var gpsTracker: GPSTracker? = null
    private var googleMap: GoogleMap? = null
    var cabDetailArray: ArrayList<CabDetails>? = null
    var marker: MarkerOptions? = null
    var PickupLarLng: LatLng? = null
    var DropLarLng: LatLng? = null
    var DropLongtude: Double = 0.toDouble()
    var DropLatitude: Double = 0.toDouble()
    var PickupLongtude: Double = 0.toDouble()
    var PickupLatitude: Double = 0.toDouble()
    var locationArray: ArrayList<HashMap<String, String>>? = null
    private var arrayPoints: ArrayList<LatLng>? = null
    internal var NowDialog: Dialog? = null
    lateinit var CashDialog: Dialog
    var ReservationDialog: Dialog? = null
    var cabDetailAdapter: CabDetailAdapter? = null
    internal var person = ""
    internal var car_rate: String? = null
    internal var fromintailrate: String? = null
    internal var ride_time_rate: String? = "0"
    lateinit var DayNight: String
    var transfertype: String? = null
    lateinit var slidingMenu: SlidingMenu
    internal var arrActualDates = ArrayList<Calendar>()
    internal var sdf = SimpleDateFormat("E MMM, dd", Locale.getDefault())
    internal var distance: Float? = 12.0f
    internal var googleDuration = 0
    lateinit var truckIcon: String
    lateinit var truckType: String
    var CabId: String? = null
    var AreaId: String? = null
    internal var totlePrice: Float? = null
    internal var FirstKm: Float = 0.toFloat()
    internal var totalTime: Int = 0
    internal var CarName = ""
    internal var AstTime = ""
    internal var bothLocationString = ""
    internal var pickupDropLocationAdapter: PickupDropLocationAdapter? = null
    lateinit var pickupDragLayoutManager: LinearLayoutManager
    internal var ClickOkButton = false
    internal var PaymentType = "Cash"
    lateinit var myCalendar: Calendar
    lateinit var date: DatePickerDialog.OnDateSetListener
    private var BookingDateTime = ""
    lateinit var bookingFormate: SimpleDateFormat
    private var devise_width: Int = 0
    internal var transaction_id = ""
    internal var common = Common()
    internal var LocationDistanse = true
    internal var PickupMarker: Marker? = null
    internal var DropMarker: Marker? = null
    internal var CabPositon = 0
    lateinit var BookingType: String
    //    lateinit var FixRateArray: ArrayList<HashMap<String, String>>
    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading
    lateinit var recycle_cab_detail: RecyclerView
    var listLatLng = ArrayList<LatLng?>()
    lateinit var mapFragment: SupportMapFragment
    var gender: String? = "male"
    var star: String? = "5"
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home1)
        userPref = PreferenceManager.getDefaultSharedPreferences(this@HomeActivity)
        val bookinCancel = intent.getStringExtra("cancel_booking")
        if (bookinCancel != null && bookinCancel == "1") {
            Common.showMkSucess(
                this@HomeActivity,
                resources.getString(R.string.your_booking_cancel),
                "yes"
            )
        }
        ProgressDialog = Dialog(this@HomeActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading
        //        Log.d("device_token","device_token = "+Common.device_token);
        //        Log.d("id_device_token","id_device_token = "+userPref.getString("id_device_token",""));

//        if (userPref.getString("id_device_token", "") != "1")
//            Common.CallUnSubscribeTaken(this@HomeActivity, Common.device_token).execute()

        arrayPoints = ArrayList()

        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        devise_width = displaymetrics.widthPixels

//        layout_now!!.layoutParams.width = (devise_width * 0.50).toInt()

        val resParam = RelativeLayout.LayoutParams(
            (devise_width * 0.51).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        resParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        resParam.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout_reservation!!.layoutParams = resParam
        bookingFormate = SimpleDateFormat("h:mm a, d, MMM yyyy,EEE")
        pickupDragLayoutManager = LinearLayoutManager(this@HomeActivity)
        recycle_pickup_location.layoutManager = pickupDragLayoutManager
        /*get Current Location And Set Edittext*/
        PickupLatitude = intent.getDoubleExtra("PickupLatitude", 0.0)
        PickupLongtude = intent.getDoubleExtra("PickupLongtude", 0.0)
        gpsTracker = GPSTracker(this@HomeActivity)

        if (PickupLongtude != 0.0 && PickupLatitude != 0.0) {
            bothLocationString = "pickeup"
            if (Common.isNetworkAvailable(this@HomeActivity)) {
                val locationAddress = LocationAddress
                locationAddress.getAddressFromLocation(
                    PickupLatitude, PickupLongtude,
                    applicationContext, GeocoderHandler()
                )

                PickupLarLng = LatLng(PickupLatitude, PickupLongtude)
                ClickOkButton = true

                Handler().postDelayed({ MarkerAdd() }, 1000)
            } else {
                Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG).show()
            }
        } else {

            if (gpsTracker!!.checkLocationPermission()) {

                PickupLatitude = gpsTracker!!.latitude
                PickupLongtude = gpsTracker!!.longitude
                PickupLarLng = LatLng(gpsTracker!!.latitude, gpsTracker!!.longitude)
                ClickOkButton = true

                bothLocationString = "pickeup"
                if (Common.isNetworkAvailable(this@HomeActivity)) {
                    val locationAddress = LocationAddress
                    locationAddress.getAddressFromLocation(
                        PickupLatitude, PickupLongtude,
                        applicationContext, GeocoderHandler()
                    )

                    PickupLarLng = LatLng(PickupLatitude, PickupLongtude)
                    ClickOkButton = true

                    Handler().postDelayed({ MarkerAdd() }, 1000)
                } else {
                    Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG).show()
                }

            } else {
                gpsTracker!!.showSettingsAlert()
            }
        }

        Log.d(
            "gpsTracker",
            "gpsTracker =" + gpsTracker!!.canGetLocation() + "==" + gpsTracker!!.checkLocationPermission()
        )

        /*Pickup Location autocomplate start*/
        //LocationAutocompleate(edt_pickup_location, "pickeup");
        editorActionListener(edt_pickup_location!!, "pickeup")
        addTextChangeListener(edt_pickup_location!!, "pickeup")
        addSetOnClickListener(edt_pickup_location!!, "pickeup")
        /*Pickup Location autocomplate end*/

        /*Drop Location autocomplate start*/
        //LocationAutocompleate(edt_drop_location, "drop");
        editorActionListener(edt_drop_location!!, "drop")
        addTextChangeListener(edt_drop_location!!, "drop")
        addSetOnClickListener(edt_drop_location!!, "drop")
        /*Drop Location autocomplate end*/

        /*Slide Menu Start*/

        slidingMenu = SlidingMenu(this)
        slidingMenu.mode = SlidingMenu.LEFT
        slidingMenu.touchModeAbove = SlidingMenu.TOUCHMODE_NONE
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width)
        slidingMenu.setFadeDegree(0.20f)
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        slidingMenu.setMenu(R.layout.left_menu)

        common.SlideMenuDesign(slidingMenu, this@HomeActivity, "home")

        layout_slidemenu!!.setOnClickListener { slidingMenu.toggle() }

        /*Slide Menu End*/
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*Get Day Night Time*/

        try {

            val currentLocalTime = currentTime.format(Date())
            var StarDateFrm: Date? = null
            if (Common.StartDayTime != "")
                StarDateFrm = currentTime.parse(Common.StartDayTime)
            var EndDateFrm: Date? = null
            if (Common.StartDayTime != "")
                EndDateFrm = currentTime.parse(Common.EndDayTime)

            val CurDateFrm = currentTime.parse(currentLocalTime)

            if (StarDateFrm != null && EndDateFrm != null) {
                if (CurDateFrm.before(StarDateFrm) || CurDateFrm.after(EndDateFrm)) {
                    Log.d("get time", "get time = before")
                    DayNight = "night"
                } else {
                    DayNight = "day"
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        /*Now Image Click popup start*/
        //NowDialog = new Dialog(HomeActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
        val NowDialog = Dialog(this@HomeActivity, R.style.DialogUpDownAnim)
        NowDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE) //before

        NowDialog!!.setContentView(R.layout.now_dialog_layout)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            NowDialog!!.window!!.statusBarColor = Color.TRANSPARENT
        }
        val spinner_person = NowDialog.findViewById(R.id.spinner_person) as Spinner
        spinner_person.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                person = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

// set listener
        // set listener
        rangeSeekbar1.setOnRangeSeekbarChangeListener(OnRangeSeekbarChangeListener { minValue, maxValue ->
            textMin1.setText("$minValue")
            textMin2.setText("$maxValue")
        })

// set final value listener
        // set final value listener
        rangeSeekbar1.setOnRangeSeekbarFinalValueListener(OnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Log.d(
                "CRS=>",
                "$minValue : $maxValue"
            )
        })
        recycle_cab_detail = NowDialog!!.findViewById(R.id.recycle_cab_detail) as RecyclerView
        val categoryLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycle_cab_detail.layoutManager = categoryLayoutManager

        cabDetailArray = ArrayList()

        val layout_book = NowDialog!!.findViewById(R.id.layout_book) as RelativeLayout
        layout_book.layoutParams.width = (devise_width * 0.50).toInt()
        layout_book.setOnClickListener(View.OnClickListener {
            if (edt_pickup_location!!.text.toString().trim { it <= ' ' }.length == 0) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_pickup))
                return@OnClickListener
            } else if (edt_drop_location!!.text.toString().trim { it <= ' ' }.length == 0) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_drop))
                return@OnClickListener
            }

            NowDialog!!.cancel()

            layout_reservation!!.visibility = View.VISIBLE
            //                if(car_rate != null && fromintailrate != null && ride_time_rate != null)
            //                    totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
            //                else
            //                    totlePrice = 0f;
            Log.d("total price", "total price = " + totlePrice!!)

            if (totlePrice != 0f) {
                val bi = Intent(this@HomeActivity, TripDetailActivity::class.java)
                bi.putExtra(
                    "pickup_point",
                    edt_pickup_location!!.text.toString().trim { it <= ' ' })
                bi.putExtra("drop_point", edt_drop_location!!.text.toString().trim { it <= ' ' })
                bi.putExtra("distance", distance)
                bi.putExtra("truckIcon", truckIcon)
                bi.putExtra("truckType", truckType)
                bi.putExtra("CabId", CabId)
                bi.putExtra("AreaId", AreaId)
                bi.putExtra("booking_date", BookingDateTime)
                bi.putExtra("totlePrice", totlePrice!!)
                bi.putExtra("PickupLatitude", PickupLatitude)
                bi.putExtra("PickupLongtude", PickupLongtude)
                bi.putExtra("DropLatitude", DropLatitude)
                bi.putExtra("DropLongtude", DropLongtude)
//                bi.putExtra("comment", edt_write_comment!!.text.toString().trim { it <= ' ' })
                bi.putExtra("DayNight", DayNight)
                bi.putExtra("transfertype", transfertype)
                bi.putExtra("PaymentType", PaymentType)
                bi.putExtra("person", person)
                bi.putExtra("transaction_id", transaction_id)
                bi.putExtra("BookingType", BookingType)
                bi.putExtra("AstTime", AstTime)
                startActivity(bi)
            } else {
                Common.showMkError(
                    this@HomeActivity,
                    resources.getString(R.string.not_valid_total_price)
                )
            }
        })

        val layout_cancle = NowDialog!!.findViewById(R.id.layout_cancle) as RelativeLayout
        val CanParam = RelativeLayout.LayoutParams(
            (devise_width * 0.51).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        CanParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        CanParam.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout_cancle.layoutParams = CanParam

        layout_cancle.setOnClickListener {
            NowDialog!!.cancel()
            layout_reservation!!.visibility = View.VISIBLE
        }

        val CabDetailAry = Common.CabDetail

        for (ci in 0 until CabDetailAry!!.length()) {
            val cabDetails = CabDetails()

            try {
                val cabObj = CabDetailAry.getJSONObject(ci)
                cabDetails.id = cabObj.getString("cab_id")
                cabDetails.cartype = cabObj.getString("cartype")
                cabDetails.transfertype = cabObj.getString("transfertype")
                cabDetails.intialkm = cabObj.getString("intialkm")
                cabDetails.carRate = cabObj.getString("car_rate")
                cabDetails.fromintialkm = cabObj.getString("intailrate")
                cabDetails.standardrate = cabObj.getString("standardrate")
                cabDetails.fromintailrate = cabObj.getString("fromintailrate")
                cabDetails.fromstandardrate = cabObj.getString("fromstandardrate")
                cabDetails.nightFromintialkm = cabObj.getString("night_fromintialkm")
                cabDetails.nightFromintailrate = cabObj.getString("night_fromintailrate")
                cabDetails.icon = cabObj.getString("icon")
                cabDetails.description = cabObj.getString("description")
                cabDetails.nightIntailrate = cabObj.getString("night_intailrate")
                cabDetails.nightStandardrate = cabObj.getString("night_standardrate")
                cabDetails.rideTimeRate = cabObj.getString("ride_time_rate")
                cabDetails.nightRideTimeRate = cabObj.getString("night_ride_time_rate")
                cabDetails.seatCapacity = cabObj.getString("seat_capacity")
                if (cabObj.has("fix_price")) {
                    cabDetails.fixPrice = cabObj.getString("fix_price")
                } else {
                    cabDetails.fixPrice = ""
                }
                if (cabObj.has("area_id")) {
                    cabDetails.areaId = cabObj.getString("area_id")
                } else {
                    cabDetails.fixPrice = ""
                }

                if (ci == 0)
                    cabDetails.setIsSelected(true)
                else
                    cabDetails.setIsSelected(false)

                cabDetailArray!!.add(cabDetails)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        if (CabDetailAry != null && CabDetailAry.length() > 0) {
            Log.d("cabDetailArray", "cabDetailArray = " + cabDetailArray!!.size)
            if (cabDetailArray != null && cabDetailArray!!.size > 0) {

                val cabDetails = cabDetailArray!![0]
//                txt_car_header!!.text = cabDetails.cartype!!.toUpperCase()
                CarName = cabDetails.cartype!!.toUpperCase()
//                txt_car_descriptin!!.text = cabDetails.description

                if (DayNight == "day") {
                    car_rate = cabDetails.carRate
                    fromintailrate = cabDetails.fromintailrate
                    if (cabDetails.rideTimeRate != null) {
                        ride_time_rate = cabDetails.rideTimeRate
                    }
                } else if (DayNight == "night") {
                    car_rate = cabDetails.nightIntailrate
                    fromintailrate = cabDetails.nightFromintailrate
                    if (cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {
                        ride_time_rate = cabDetails.nightRideTimeRate
                    }
                }
//                txt_first_price!!.text = car_rate
                FirstKm = java.lang.Float.parseFloat(cabDetails.intialkm!!)
//                txt_first_km!!.text =
//                    resources.getString(R.string.first) + " " + FirstKm + " " + resources.getString(
//                        R.string.km
//                    )
//                txt_sec_pric!!.text = fromintailrate + "/" + resources.getString(R.string.km)
//                txt_sec_km!!.text =
//                    resources.getString(R.string.after) + " " + FirstKm + " " + resources.getString(
//                        R.string.km
//                    )

                if (cabDetails.rideTimeRate != null || cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {
//                    layout_three!!.visibility = View.VISIBLE
//                    txt_thd_price!!.text = ride_time_rate + "/" + resources.getString(R.string.min)
                } else {
                    layout_three!!.visibility = View.GONE
                    val params = LinearLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.MATCH_PARENT
                    )
                    params.weight = 1.5f
//                    layout_one!!.layoutParams = params
//                    layout_two!!.layoutParams = params
                }

                truckIcon = cabDetails.icon
                truckType = cabDetails.cartype!!
                CabId = cabDetails.id!!
                AreaId = cabDetails.areaId
                transfertype = cabDetails.transfertype

                cabDetailAdapter = CabDetailAdapter(this@HomeActivity, cabDetailArray!!)
                recycle_cab_detail.adapter = cabDetailAdapter
                cabDetailAdapter!!.setOnCabDetailItemClickListener(this@HomeActivity)
                cabDetailAdapter!!.updateItems()

                val list = ArrayList<String>()
                for (si in 0 until Integer.parseInt(cabDetails.seatCapacity)) {
                    val seat = si + 1
                    list.add(seat.toString())
                }
                val dataAdapter = ArrayAdapter(
                    this@HomeActivity,
                    android.R.layout.simple_spinner_item, list
                )
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_person.adapter = dataAdapter

                Log.d("Fix Price", "Fix Price = " + cabDetails.fixPrice)
            }
        }
//        spinner_sort_by.setOnItemClickListener { adapterView, view, i, l ->
//            star=spinner_sort_by.getItemAtPosition(i).toString()
//        }
        callCabAdapter()
        bottom_sheet()
        wishesBottomsheet()
        bottom_sheet_filter()
        filter_driver.setOnClickListener {
            if (sheetBehavior_filter.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior_filter.state = BottomSheetBehavior.STATE_EXPANDED
                sheetBehavior_filter.isHideable = false
                Log.e("Close sheet", "Close sheet")
            } else {
                sheetBehavior_filter.state = BottomSheetBehavior.STATE_COLLAPSED
                Log.e("Expand sheet", "Expand sheet")
            }
        }

        bottom_sheet_nxt.setOnClickListener {
            if (Common.isNetworkAvailable(this))
                getNearestDriver()
            else
                Toast.makeText(
                    this@HomeActivity,
                    "Make Sure You Have Active Internet Connection.",
                    Toast.LENGTH_LONG
                ).show()

        }

        layout_footer!!.setOnClickListener(View.OnClickListener {

            Log.d("length ", "length = " + edt_pickup_location!!.text.toString().length)
            if (edt_pickup_location!!.text.toString().isEmpty()) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_pickup))
                return@OnClickListener
            } else if (edt_drop_location!!.text.toString().isEmpty()) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_drop))
                return@OnClickListener
            } else if (!LocationDistanse) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.location_long))
                return@OnClickListener
            } else if (distance == 0.0f) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.location_short))
                return@OnClickListener
            }

            BookingDateTime = bookingFormate.format(Calendar.getInstance().time)

            BookingType = "Now"
            layout_reservation!!.visibility = View.GONE
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheetBehavior.isHideable = false
                Log.e("Close sheet", "Close sheet")
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                Log.e("Expand sheet", "Expand sheet")
            }

//        val bottomsheet=SelectCarBottomSheet()
//            bottomsheet.show(supportFragmentManager,"bottom")
//            sheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
//
//            NowDialog!!.show()
        })

        /*Cash Dialog Strat*/
        CashDialog = Dialog(this@HomeActivity, R.style.DialogUpDownAnim)
        CashDialog.setContentView(R.layout.cash_dialog_layout)
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CashDialog.window!!.statusBarColor = Color.TRANSPARENT
        }

        val layout__cash_cash = CashDialog.findViewById(R.id.layout__cash_cash) as RelativeLayout
        layout__cash_cash.setOnClickListener {
            CashDialog.cancel()
            NowDialog!!.show()
            PaymentType = "Cash"
        }
        val layout_cash_paypal = CashDialog.findViewById(R.id.layout_cash_paypal) as RelativeLayout
        layout_cash_paypal.setOnClickListener {
            PaymentType = "Paypal"
            onBuyPressed()
        }
        val layout_cash_credit_card =
            CashDialog.findViewById(R.id.layout_cash_credit_card) as RelativeLayout
        layout_cash_credit_card.setOnClickListener {
            PaymentType = "Stripe"
            val si = Intent(this@HomeActivity, StripeFormActivity::class.java)
            startActivityForResult(si, REQUESTCODESTRIPE)
        }
        val layout_cash_cancel = CashDialog.findViewById(R.id.layout_cash_cancel) as RelativeLayout
        layout_cash_cancel.setOnClickListener {
            CashDialog.cancel()
            NowDialog!!.show()
            PaymentType = "Cash"
        }

//        layout_cash.setOnClickListener {
//            //NowDialog.cancel();
//            //CashDialog.show();
//        }

        myCalendar = Calendar.getInstance()

        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        ReservationDialog = Dialog(this@HomeActivity, R.style.DialogUpDownAnim)
        ReservationDialog!!.setContentView(R.layout.reservation_dialog_layout)
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            ReservationDialog!!.window!!.statusBarColor = Color.TRANSPARENT
        }


        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val layout_res = ReservationDialog!!.findViewById(R.id.layout_res) as RelativeLayout
        layout_res.layoutParams.height = (dm.heightPixels * 0.40).toInt()

        val layout_select_date =
            ReservationDialog!!.findViewById(R.id.layout_select_date) as RelativeLayout
        layout_select_date.setOnClickListener {
            val aftoneMont = Calendar.getInstance()
            aftoneMont.add(Calendar.MONTH, 1)

            val dpd = DatePickerDialog(
                this@HomeActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            var d: Date? = null
            try {
                val formattedDate = sdf.format(Calendar.getInstance().time)
                d = sdf.parse(formattedDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            dpd.datePicker.minDate = d!!.time
            dpd.datePicker.maxDate = aftoneMont.timeInMillis
            dpd.show()
        }


        val layout_select_time =
            ReservationDialog!!.findViewById(R.id.layout_select_time) as RelativeLayout
        layout_select_time.setOnClickListener {
            val hour = myCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = myCalendar.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@HomeActivity,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    txt_time.text = "$selectedHour:$selectedMinute"
                },
                hour,
                minute,
                false
            )//Yes 24 hour time
            mTimePicker.setTitle(resources.getString(R.string.select_time_res))
            mTimePicker.show()
        }

        val layout_done = ReservationDialog!!.findViewById(R.id.layout_done) as RelativeLayout
        layout_done.setOnClickListener(View.OnClickListener {
            Log.d("txt_date ", "txt_date = " + txt_date.text.toString())
            if (txt_date.text.toString().length == 0) {
                Common.showMkError(
                    this@HomeActivity,
                    resources.getString(R.string.please_enter_date)
                )
                return@OnClickListener
            } else if (txt_time.text.length == 0) {
                Common.showMkError(
                    this@HomeActivity,
                    resources.getString(R.string.please_enter_time)
                )
                return@OnClickListener
            }

            val currentDateFormate = SimpleDateFormat("dd/MM/yyyy HH:mm aa")
            val DateTimeString = txt_date.text.toString() + " " + txt_time.text
            var SeletedtDate = ""
            val SeletDate: Date
            try {
                SeletDate = currentDateFormate.parse(DateTimeString)
                SeletedtDate = currentDateFormate.format(SeletDate.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val currentDate = currentDateFormate.format(Calendar.getInstance().time)
            val dateVal = checkDates(DateTimeString, currentDate)

            /*After One Month Validation*/
            var afterOneMonth = false
            val onMonCal = Calendar.getInstance()
            onMonCal.add(Calendar.MONTH, 1)
            val curOneMonDate = currentDateFormate.format(onMonCal.time)
            val dfDate = SimpleDateFormat("dd/MM/yyyy HH:mm aa")
            Log.d("curOneMonDate", "curOneMonDate = $curOneMonDate==$DateTimeString")
            try {
                val CrtDate = dfDate.parse(curOneMonDate)
                val SelDate = dfDate.parse(DateTimeString)
                if (SelDate.after(CrtDate)) {
                    Log.d("After", "curOneMonDate After One")
                    afterOneMonth = true
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            try {
                val selectedDateFormate = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val parshDate = selectedDateFormate.parse(DateTimeString)
                BookingDateTime = bookingFormate.format(parshDate)

            } catch (e: ParseException) {
                e.printStackTrace()
                Log.d("date Error", "DateTimeString Error = " + e.message)
            }

            //SimpleDateFormat bookingFormate = new SimpleDateFormat("h:mm a, d, MMM yyyy,EEE");
            Log.d("DateTimeString", "DateTimeString one= $DateTimeString")
            Log.d("DateTimeString", "DateTimeString two= $currentDate")
            Log.d("DateTimeString", "DateTimeString three= $dateVal")
            Log.d("DateTimeString", "DateTimeString for= $BookingDateTime")
            if (afterOneMonth) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.time_is_large))
                return@OnClickListener
            } else if (!dateVal) {
                Common.showMkError(
                    this@HomeActivity,
                    resources.getString(R.string.date_time_not_valid)
                )
                return@OnClickListener
            }
            try {
                val ResCurDateFrm = currentTime.parse(txt_time.text.toString())
                var ResStarDateFrm: Date? = null
                if (Common.StartDayTime != "")
                    ResStarDateFrm = currentTime.parse(Common.StartDayTime)

                var ResEndDateFrm: Date? = null
                if (Common.StartDayTime != "")
                    ResEndDateFrm = currentTime.parse(Common.EndDayTime)

                if (ResStarDateFrm != null && ResEndDateFrm != null) {
                    if (ResCurDateFrm.before(ResStarDateFrm) || ResCurDateFrm.after(ResEndDateFrm)) {
                        Log.d("get time", "get time = before")
                        DayNight = "night"
                    } else {
                        DayNight = "day"
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            Log.d("DayNight", "DayNight = $DayNight")
            val ResCabDetails = cabDetailArray!![CabPositon]
            if (DayNight == "day") {
                car_rate = ResCabDetails.carRate
                fromintailrate = ResCabDetails.fromintailrate
                if (ResCabDetails.rideTimeRate != null) {
                    ride_time_rate = ResCabDetails.rideTimeRate
                }
            } else if (DayNight == "night") {
                car_rate = ResCabDetails.nightIntailrate
                fromintailrate = ResCabDetails.nightFromintailrate
                if (ResCabDetails.nightRideTimeRate != null && ResCabDetails.nightRideTimeRate != "0") {
                    ride_time_rate = ResCabDetails.nightRideTimeRate
                }
            }
            if (ResCabDetails.rideTimeRate != null || ResCabDetails.nightRideTimeRate != null && ResCabDetails.nightRideTimeRate != "0") {
                layout_three!!.visibility = View.VISIBLE
                txt_thd_price!!.text = ride_time_rate + "/" + resources.getString(R.string.min)
            }
            txt_first_price!!.text = car_rate
            txt_sec_pric!!.text = fromintailrate + "/" + resources.getString(R.string.km)

            layout_reservation!!.visibility = View.GONE
            NowDialog!!.show()
            ReservationDialog!!.cancel()
        })

        layout_reservation!!.setOnClickListener(View.OnClickListener {
            Log.d("length ", "length = " + edt_pickup_location!!.text.toString().length)
            if (edt_pickup_location!!.text.toString().isEmpty()) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_pickup))
                return@OnClickListener
            } else if (edt_drop_location!!.text.toString().isEmpty()) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_drop))
                return@OnClickListener
            } else if (!LocationDistanse) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.location_long))
                return@OnClickListener
            } else if (distance == 0.0f) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.location_short))
                return@OnClickListener
            }
            BookingType = "Reservation"
            ReservationDialog!!.show()
        })

        /*Reservation Image Click popup end*/

        img_pickup_close.setOnClickListener {
            edt_pickup_location!!.setText("")
            PickupLarLng = null
            PickupLatitude = 0.0
            PickupLongtude = 0.0
            MarkerAdd()
            sheetBehavior.isHideable = true
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sheetBehavior_wishes.isHideable = true
            sheetBehavior_wishes.state = BottomSheetBehavior.STATE_HIDDEN
        }

        img_drop_close.setOnClickListener {
            edt_drop_location!!.setText("")
            DropLarLng = null
            DropLongtude = 0.0
            DropLatitude = 0.0
            MarkerAdd()
            sheetBehavior.isHideable = true
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sheetBehavior_wishes.isHideable = true
            sheetBehavior_wishes.state = BottomSheetBehavior.STATE_HIDDEN
        }
//        des_layout.setOnClickListener {
//            Intent(this,DropLocationActivity::class.java
//            ).also {
//                startActivity(it)
//            }
//        }
    }

    private fun callCabAdapter() {
        car_recylercerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cabDetailAdapter = CabDetailAdapter(this, cabDetailArray!!)
        car_recylercerview.adapter = cabDetailAdapter
        cabDetailAdapter!!.setOnCabDetailItemClickListener(this@HomeActivity)
        cabDetailAdapter!!.updateItems()
    }

    private fun bottom_sheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(bottonSheet: View, newState: Int) {
                var state: String = ""
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {

                        state = "DRAGGING"
                        Log.e("BottomSheet", state)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //                        sheetBehavior.peekHeight = 500
                        state = "Expanded"
                        Log.e("BottomSheet", state)
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        state = "Settling"
                        Log.e("BottomSheet", state)

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        state = "Hidden"
                        Log.e("BottomSheet", state)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }
        })
    }

    private fun bottom_sheet_filter() {
        sheetBehavior_filter = BottomSheetBehavior.from(bottom_sheet_filter)
        sheetBehavior_filter.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehavior_filter.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(bottonSheet: View, newState: Int) {
                var state: String = ""
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {

                        state = "DRAGGING"
                        Log.e("BottomSheet", state)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //                        sheetBehavior.peekHeight = 500
                        state = "Expanded"
                        Log.e("BottomSheet", state)
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        state = "Settling"
                        Log.e("BottomSheet", state)

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        state = "Hidden"
                        Log.e("BottomSheet", state)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }
        })
        close_filter.setOnClickListener {
            val dialog = AlertDialog.Builder(this@HomeActivity)
            dialog.setCancelable(false)
            dialog.setTitle("Are your Sure?")
            dialog.setMessage("If You go back, your data will be lost. Are You Sure?")
            dialog.setPositiveButton("Ok", object : OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    sheetBehavior_filter.isHideable = true
                    sheetBehavior_filter.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })

            dialog.setNegativeButton("Cancel", object : OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })
            dialog.create().show()
        }
        bottom_sheet_filter_nxt.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getFilter()
            }
        }

        spinner_car_type.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> gender = "male"
                1 -> gender = "female"
                2 -> gender = "Other"
            }

        }
    }

    private fun wishesBottomsheet() {
        sheetBehavior_wishes = BottomSheetBehavior.from(bottom_sheet_wishes)
        sheetBehavior_wishes.state = BottomSheetBehavior.STATE_HIDDEN

        wishes_constraint.setOnClickListener {
            if (sheetBehavior_wishes.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior_wishes.state = BottomSheetBehavior.STATE_EXPANDED
                sheetBehavior_wishes.isHideable = false
                Log.e("Close sheet", "Close sheet")
            } else {
                sheetBehavior_wishes.state = BottomSheetBehavior.STATE_COLLAPSED
                Log.e("Expand sheet", "Expand sheet")
            }
        }

        wishes_send.setOnClickListener {

            sheetBehavior_wishes.isHideable = true
            sheetBehavior_wishes.state = BottomSheetBehavior.STATE_HIDDEN
            Log.e("Expand sheet", "Expand sheet")
        }

        close.setOnClickListener {
            sheetBehavior_wishes.isHideable = true
            sheetBehavior_wishes.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun onBuyPressed() {
        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(this@HomeActivity, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    private fun getThingToBuy(paymentIntent: String): PayPalPayment {
        return PayPalPayment(
            BigDecimal(Math.round(totlePrice!!)), "USD", CarName,
            paymentIntent
        )
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        txt_date.text = sdf.format(myCalendar.time)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        val locationButton: ImageView =
            (mapFragment.view!!.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                Integer.parseInt("2")
            ) as ImageView
        locationButton.setImageResource(R.drawable.ic_location)
        // and next place it, on bottom right (as Google Maps app)
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 100, 250)
        try {
            val res: Boolean =
                googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!res)
                Log.e(TAG, "Style parsing failed.")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Log.d("Map Ready", "Map Ready" + gpsTracker!!.latitude + "==" + gpsTracker!!.longitude)
//        googleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
//            override fun onMapClick(p0: LatLng?) {
//                ClickOkButton=true
//                edt_pickup_location?.setText(getAddress(p0!!.latitude,p0!!.longitude))
//                Log.e(TAG, "onMapClick: ${p0.toString()}");
//            }
//        }
//        )
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private fun checkReady(): Boolean {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun CaculationDirationIon() {
        var CaculationLocUrl = ""
        listLatLng.clear()
        CaculationLocUrl =
            "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=$PickupLatitude,$PickupLongtude&destination=$DropLatitude,$DropLongtude&key=" + getString(
                R.string.google_server_key
            )
        Log.d("CaculationLocUrl", "CaculationLocUrl = $CaculationLocUrl")
        Ion.with(this@HomeActivity)
            .load(CaculationLocUrl)
            .setTimeout(10000)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error

                ProgressDialog.cancel()
                cusRotateLoading.stop()

                Log.d("Login result", "Login result = $result==$error")
                if (error == null) {
                    try {
                        val resObj = JSONObject(result.toString())
                        if (resObj.getString("status").toLowerCase(Locale.getDefault()) == "ok") {
                            val routArray = JSONArray(resObj.getString("routes"))
                            val routObj = routArray.getJSONObject(0)
                            val overview_polylines = routObj.getJSONObject("overview_polyline")
                            listLatLng.addAll(decodePoly(overview_polylines.getString("points")))
                            Log.d("geoObj", "DrowLocUrl geoObj one= $routObj")
                            val legsArray = JSONArray(routObj.getString("legs"))
                            val legsObj = legsArray.getJSONObject(0)
                            val legsSteps = legsObj.getJSONArray("steps")
//
                            AddPollyLines(listLatLng)
                            val disObj = JSONObject(legsObj.getString("distance"))
                            //if (disObj.getInt("value") > 1000)
                            distance = disObj.getString("text").replace(" km","").toFloat()
                            //                                    else if (disObj.getInt("value") > 100)
                            //                                        distance = (float) disObj.getInt("value") / 100;
                            //                                    else if (disObj.getInt("value") > 10)
                            //                                        distance = (float) disObj.getInt("value") / 10;
                            //                                    else if(disObj.getInt("value") == 0)
                            //                                        distance = (float) disObj.getInt("value");
                            Log.d("distance", "distance = " + distance!!)
                            Log.d("dis", "dis = " + distance!!)

                            val duration = JSONObject(legsObj.getString("duration"))

                            AstTime = duration.getString("text")
                            val durTextSpi =
                                AstTime.split(" ".toRegex()).dropLastWhile({ it.isEmpty() })
                                    .toTypedArray()
                            Log.d("durTextSpi", "min  = durTextSpi = " + durTextSpi.size)
                            var hours = 0
                            var mintus = 0
                            if (durTextSpi.size == 4) {
                                hours = Integer.parseInt(durTextSpi[0]) * 60
                                mintus = Integer.parseInt(durTextSpi[2])
                            } else if (durTextSpi.size == 2) {
                                if (durTextSpi[1].contains("mins"))
                                    mintus = Integer.parseInt(durTextSpi[0])
                                else
                                    mintus = Integer.parseInt(durTextSpi[0])
                            }
                            Log.d("hours", "hours = $hours==$mintus")
                            totalTime = mintus + hours

                            googleDuration = duration.getInt("value")
//
//                            if (FixRateArray.size > 0) {
//                                for (ci in cabDetailArray!!.indices) {
//
//                                    val FixCabDetails = cabDetailArray!![ci]
//
//                                    for (fi in FixRateArray.indices) {
//                                        val FixHasMap = FixRateArray[fi]
//
//                                        Log.d(
//                                            "car_type_id",
//                                            "car_type_id = " + FixHasMap["car_type_id"] + "==" + FixCabDetails.id
//                                        )
//                                        if (FixHasMap["car_type_id"] == FixCabDetails.id) {
//                                            val cabDetails = cabDetailArray!![ci]
//                                            cabDetails.fixPrice = FixHasMap["fix_price"].toString()
//                                            cabDetails.areaId = FixHasMap["area_id"].toString()
//                                            break
//                                        }
//
//                                        Log.d("fi", "car_type_id fi = $fi")
//                                    }
//                                    Log.d("ci", "car_type_id ci = $ci")
//                                }
//                            } else {
//                                for (ci in cabDetailArray!!.indices) {
//                                    val AllCabDetails = cabDetailArray!![ci]
//                                    AllCabDetails.fixPrice = ""
//                                    AllCabDetails.areaId = ""
//                                }
//                            }
                            val cabDetails = cabDetailArray!![0]
                            if (cabDetails.fixPrice != "") {
//                                layout_timming.visibility = View.GONE
//                                layout_far_breakup.visibility = View.INVISIBLE
                                totlePrice = java.lang.Float.parseFloat(cabDetails.fixPrice!!)
//                                txt_total_price.text = Math.round(totlePrice!!).toString() + ""
                            } else {
                                Log.d(
                                    "fromintailrate",
                                    "fromintailrate = $car_rate==$FirstKm==$distance==$fromintailrate==$ride_time_rate==$totalTime"
                                )
                                if (car_rate != null && fromintailrate != null && ride_time_rate != null)
                                    totlePrice = Common.getTotalPrice(
                                        car_rate!!,
                                        FirstKm,
                                        distance,
                                        fromintailrate!!,
                                        ride_time_rate!!,
                                        totalTime
                                    )
                                else
                                    totlePrice = 0f

                                Log.d("totlePrice", "totlePrice = " + totlePrice!!)

//                                layout_timming.visibility = View.VISIBLE
//                                layout_far_breakup.visibility = View.VISIBLE
//                                txt_total_price.text = Math.round(totlePrice!!).toString() + ""
                            }

                            LocationDistanse = true
//                            layout_footer.visibility = View.VISIBLE
                            BookingDateTime = bookingFormate.format(Calendar.getInstance().time)
                            BookingType = "Now"
//                            layout_reservation!!.visibility = View.GONE
                            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {

                                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                sheetBehavior.isHideable = false
                                cabDetailAdapter!!.firstItemSelected()

                            } else {
                                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                Log.e("Expand sheet", "Expand sheet")
                            }
                        } else {
                            LocationDistanse = false
                            Toast.makeText(
                                this@HomeActivity,
                                resources.getString(R.string.location_long),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Common.ShowHttpErrorMessage(this@HomeActivity, error.message)
                }
            }
//        MarkerAdd()
//        SetNowDialogCabValue()
    }

    @SuppressLint("SetTextI18n")
    override fun CarDetailTab(position: Int) {
        CabPositon = position

        if (cabDetailArray != null && cabDetailArray!!.size > 0) {
            val cabDetails = cabDetailArray!![position]

            if (DayNight == "day") {
                car_rate = cabDetails.carRate
                fromintailrate = cabDetails.fromintailrate
                Log.d("ride_time_rate", "ride_time_rate one day= " + cabDetails.rideTimeRate)
                if (cabDetails.rideTimeRate != null) {
                    ride_time_rate = cabDetails.rideTimeRate
                }
            } else if (DayNight == "night") {
                car_rate = cabDetails.nightIntailrate
                fromintailrate = cabDetails.nightFromintailrate
                Log.d("ride_time_rate", "ride_time_rate one night= " + cabDetails.rideTimeRate)
                if (cabDetails.nightRideTimeRate != null) {
                    ride_time_rate = cabDetails.nightRideTimeRate
                }
            }

            Log.d("ride_time_rate", "ride_time_rate two= " + ride_time_rate!!)

            cab_type!!.text = cabDetails.cartype!!.toUpperCase() + ": "
            CarName = cabDetails.cartype!!.toUpperCase()
//            txt_car_descriptin!!.text = cabDetails.description
//            txt_first_price!!.text = "$" + car_rate!!
            FirstKm = java.lang.Float.parseFloat(cabDetails.intialkm!!)
//            txt_first_km!!.text = "First $FirstKm km"
//            txt_sec_pric!!.text = "$ $fromintailrate/km"
//            txt_sec_km!!.text = "After $FirstKm km"

            truckIcon = cabDetails.icon
            truckType = cabDetails.cartype!!
            CabId = cabDetails.id!!
            AreaId = cabDetails.areaId
            transfertype = cabDetails.transfertype

            if (cabDetails.rideTimeRate != null || cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {

//                layout_three!!.visibility = View.GONE
                val params = LinearLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT
                )
                params.weight = 1.0f

            } else {
//                layout_three!!.visibility = View.GONE
                val params = LinearLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT
                )
                params.weight = 1.5f
//                layout_one!!.layoutParams = params
//                layout_two!!.layoutParams = params
            }

            for (i in cabDetailArray!!.indices) {
                val allcabDetails = cabDetailArray!![i]
                Log.d("position", "position = $position==$i")
                if (i == position) {
                    allcabDetails.setIsSelected(true)
                } else {
                    allcabDetails.setIsSelected(false)
                }
            }
            cabDetailAdapter!!.notifyDataSetChanged()

            val list = ArrayList<String>()
            for (si in 0 until Integer.parseInt(cabDetails.seatCapacity)) {
                val seat = si + 1
                list.add(seat.toString())
            }
            val dataAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner_person.adapter = dataAdapter

            if (cabDetails.fixPrice != "") {
//                layout_timming.visibility = View.GONE
//                layout_far_breakup.visibility = View.INVISIBLE
                txt_total_price.text = cabDetails.fixPrice
                totlePrice = java.lang.Float.parseFloat(cabDetails.fixPrice!!)
            } else {
//                layout_timming.visibility = View.VISIBLE
//                layout_far_breakup.visibility = View.VISIBLE
                Log.d(
                    "fromintailrate",
                    "fromintailrate = $car_rate==$fromintailrate==$ride_time_rate"
                )
                if (car_rate != null && fromintailrate != null && ride_time_rate != null)
                    totlePrice = Common.getTotalPrice(
                        car_rate!!,
                        FirstKm,
                        distance,
                        fromintailrate!!,
                        ride_time_rate!!,
                        totalTime
                    )
                Log.d("totlePrice", "totlePrice = " + totlePrice!!)
                amount.text = "Rs " + Math.round(totlePrice!!).toString() + ""
                distance_km.text = distance.toString() + " km"
                time.text = AstTime.toString()
            }
        }
    }

    fun PickupFixRateCall() {
        CaculationDirationIon()
    }

    private inner class GeocoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address")
                }
                else -> null
            }
            if (locationAddress != null) {
                if (locationAddress == "Unable connect to Geocoder") {
                    Toast.makeText(this@HomeActivity, "No Network conection", Toast.LENGTH_LONG)
                        .show()
                } else {

                    Log.d(
                        "locationAddress1",
                        "locationAddress1 = $locationAddress==$bothLocationString"
                    )
                    if (bothLocationString == "pickeup" && edt_pickup_location != null) {
                        edt_pickup_location!!.setText(locationAddress)

                    } else if (bothLocationString == "drop" && edt_drop_location != null) {
                        edt_drop_location!!.setText(locationAddress)

                    }
                    if (edt_drop_location!!.text.toString().length != 0 && edt_pickup_location!!.text.toString().length != 0)
                        PickupFixRateCall()
                }
            } else {
                NowDialog!!.cancel()
                layout_reservation!!.visibility = View.VISIBLE
                Toast.makeText(
                    this@HomeActivity,
                    resources.getString(R.string.location_long),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class GeocoderHandlerLatitude : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address")
                }
                else -> null
            }
            Log.d("locationAddress2", "locationAddress2 = " + locationAddress!!)
            if (locationAddress != null) {
                if (locationAddress == "Unable connect to Geocoder") {
                    Toast.makeText(this@HomeActivity, "No Network conection", Toast.LENGTH_LONG)
                        .show()
                } else {
                    val LocationSplit =
                        locationAddress.split("\\,".toRegex()).dropLastWhile({ it.isEmpty() })
                            .toTypedArray()
                    Log.d(
                        "locationAddress2",
                        "locationAddress2 = " + locationAddress + "==" + java.lang.Double.parseDouble(
                            LocationSplit[0]
                        ) + "==" + java.lang.Double.parseDouble(LocationSplit[1])
                    )
                    if (bothLocationString == "pickeup") {
                        PickupLatitude = java.lang.Double.parseDouble(LocationSplit[0])
                        PickupLongtude = java.lang.Double.parseDouble(LocationSplit[1])
                        PickupLarLng = LatLng(
                            java.lang.Double.parseDouble(LocationSplit[0]),
                            java.lang.Double.parseDouble(LocationSplit[1])
                        )
                    } else if (bothLocationString == "drop") {
                        DropLongtude = java.lang.Double.parseDouble(LocationSplit[1])
                        DropLatitude = java.lang.Double.parseDouble(LocationSplit[0])

                        DropLarLng = LatLng(
                            java.lang.Double.parseDouble(LocationSplit[0]),
                            java.lang.Double.parseDouble(LocationSplit[1])
                        )
                    }

                    if (edt_drop_location!!.text.isNotEmpty() && edt_pickup_location!!.text.isNotEmpty()) {
                        if (checkReady() && Common.isNetworkAvailable(this@HomeActivity)) {

                            PickupFixRateCall()
                        } else {
                            Common.showInternetInfo(this@HomeActivity, "")
                        }
                    } else {
                        MarkerAdd()
                    }
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        common.SlideMenuDesign(slidingMenu, this@HomeActivity, "home")
    }

    /*Add marker function*/
    fun MarkerAdd() {
        if (checkReady()) {
            if (marker != null)
                googleMap!!.clear()

            val builder = LatLngBounds.Builder()

            if (PickupLarLng != null) {
                marker = MarkerOptions()
                    .position(PickupLarLng!!)
                    .title("Pick Up Location")

                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_location_icon))
                PickupMarker = googleMap!!.addMarker(marker)
                PickupMarker!!.isDraggable = true
                builder.include(marker!!.position)
            }

            if (DropLarLng != null) {
                marker = MarkerOptions()
                    .position(DropLarLng!!)
                    .title("Drop Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_location_icon))

                DropMarker = googleMap!!.addMarker(marker)
                DropMarker!!.isDraggable = true
                builder.include(marker!!.position)
            }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if (DropLarLng != null || PickupLarLng != null) {
                val bounds = builder.build()

                //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300))
                if (areBoundsTooSmall(bounds, 300)) {
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                    val cu = CameraUpdateFactory.newLatLngZoom(bounds.center, 15f)
                    googleMap!!.animateCamera(cu, object : GoogleMap.CancelableCallback {

                        override fun onFinish() {
                            if (PickupMarker != null)
                                BounceAnimationMarker(PickupMarker, PickupLarLng)
                            if (DropMarker != null)
                                BounceAnimationMarker(DropMarker, DropLarLng)
                        }

                        override fun onCancel() {
                        }
                    })

                    //                    new Handler().postDelayed(new Runnable() {
                    //                        @Override
                    //                        public void run() {
                    //                            if(PickupMarker!=null)
                    //                                BounceAnimationMarker(PickupMarker,PickupLarLng);
                    //                            if(DropMarker != null)
                    //                                BounceAnimationMarker(DropMarker,DropLarLng);
                    //                        }
                    //                    }, 1000);

                } else {
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 3));
                    //                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    //                        bounds,
                    //                        (int) (this.getResources().getDisplayMetrics().widthPixels * 1),
                    //                        (int) (this.getResources().getDisplayMetrics().heightPixels * 1),
                    //                        200));

                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 50)
                    googleMap!!.animateCamera(cu, object : GoogleMap.CancelableCallback {

                        override fun onFinish() {
                            val zout = CameraUpdateFactory.zoomBy((-1.0).toFloat())
                            googleMap!!.animateCamera(zout)
                            BounceAnimationMarker(PickupMarker, PickupLarLng)
                            BounceAnimationMarker(DropMarker, DropLarLng)
                        }

                        override fun onCancel() {
                            //                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            //                            googleMap.animateCamera(zout);
                        }
                    })
                }
            }

            //            CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);
            //            googleMap.animateCamera(zoom);
            //googleMap.moveCamera(cu);

            googleMap!!.setOnMarkerClickListener {
                Log.d(
                    "bothLocationString",
                    "bothLocationString pickup= " + bothLocationString + "==" + marker!!.title + "==" + edt_pickup_location!!.text.toString()
                )
                if (marker!!.title == "Pick Up Location")
                    bothLocationString = "pickeup"
                else if (marker!!.title == "Drop Location")
                    bothLocationString = "drop"
                Log.d(
                    "bothLocationString",
                    "bothLocationString pickup= " + bothLocationString + "==" + marker!!.title + "==" + edt_pickup_location!!.text.toString()
                )
                Log.d(
                    "bothLocationString",
                    "bothLocationString drop= " + bothLocationString + "==" + marker!!.title + "==" + edt_drop_location!!.text.toString()
                )

                false
            }

            googleMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {
                    if (marker.title == "Pick Up Location")
                        bothLocationString = "pickeup"
                    else if (marker.title == "Drop Location")
                        bothLocationString = "drop"
                    Log.d(
                        "bothLocationString",
                        "bothLocationString pickup= " + bothLocationString + "==" + marker.title + "==" + edt_pickup_location!!.text.toString()
                    )
                    Log.d(
                        "bothLocationString",
                        "bothLocationString drop= " + bothLocationString + "==" + marker.title + "==" + edt_drop_location!!.text.toString()
                    )
                    Log.d("latitude", "latitude one = " + marker.position.latitude)
                }

                override fun onMarkerDrag(marker: Marker) {
                    Log.d("latitude", "latitude two= " + marker.position.latitude)
                }

                override fun onMarkerDragEnd(mrk: Marker) {
                    Log.d(
                        "latitude",
                        "latitude three = " + mrk.position.latitude + "==" + mrk.position.longitude
                    )
                    if (Common.isNetworkAvailable(this@HomeActivity)) {
                        ClickOkButton = true
                        val locationAddress = LocationAddress
                        locationAddress.getAddressFromLocation(
                            mrk.position.latitude, mrk.position.longitude,
                            applicationContext, GeocoderHandler()
                        )
                        if (bothLocationString == "pickeup") {
                            PickupLarLng = LatLng(mrk.position.latitude, mrk.position.longitude)
                            PickupLatitude = mrk.position.latitude
                            PickupLongtude = mrk.position.longitude
                        }
                        if (bothLocationString == "drop") {
                            DropLarLng = LatLng(mrk.position.latitude, mrk.position.longitude)
                            DropLatitude = mrk.position.latitude
                            DropLongtude = mrk.position.longitude
                        }
                        Log.d("bothLocationString", "bothLocationString = $bothLocationString")
                        //                        if (bothLocationString.equals("pickeup"))
                        //                            mrk.setTitle(edt_pickup_location.getText().toString());
                        //                        if (bothLocationString.equals("drop"))
                        //                            mrk.setTitle(edt_drop_location.getText().toString());
                    } else {
                        Toast.makeText(this@HomeActivity, "No network", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    fun AddPollyLines(list: ArrayList<LatLng?>) {
        val bounds: LatLngBounds.Builder
        if (checkReady()) {
            if (marker != null)
                googleMap!!.clear()
            if (listLatLng.size > 1) {
                var polyOption = PolylineOptions()
                polyOption.color(Color.RED)
                polyOption.width(12f)
                polyOption.addAll(list)
                googleMap!!.clear()
                googleMap!!.addPolyline(polyOption)
                bounds = LatLngBounds.builder()
                for (i in list) {
                    bounds.include(i)
                }
//                GoogleDirection.withServerKey(getString(R.string.google_server_key))
//                    .from(PickupLarLng)
//                    .to(DropLarLng)
//                    .execute(object : DirectionCallback {
//                        override fun onDirectionFailure(t: Throwable?) {
//
//                        }
//
//                        override fun onDirectionSuccess(direction: Direction?) {
//                            if (direction!!.isOK) {
//                                googleMap?.addMarker(MarkerOptions().position(PickupLarLng!!))
//                                googleMap?.addMarker(MarkerOptions().position(DropLarLng!!))
//                                for (i in 0 until direction.routeList.size) {
//                                    val route = direction.routeList[i]
//                                    val color = ContextCompat.getColor(this@HomeActivity, R.color.black)
//                                    val directionPositionList = route.legList[0].directionPoint
//                                    googleMap?.addPolyline(DirectionConverter.createPolyline(this@HomeActivity, directionPositionList, 5, color))
//                                }
//                                setCameraWithCoordinationBounds(direction.routeList[0])
//                            } else {
//                            }
//                        }
//
//                    })

                val latLngBounds: LatLngBounds = bounds.build()
                val cameraUpdate: CameraUpdate =
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)

                if (PickupLarLng != null) {
                    marker = MarkerOptions()
                        .position(PickupLarLng!!)
                        .title("Pick Up Location")

                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_location_icon))
                    PickupMarker = googleMap!!.addMarker(marker)
                    PickupMarker!!.isDraggable = true
                    bounds.include(marker!!.position)
                }

                if (DropLarLng != null) {
                    marker = MarkerOptions()
                        .position(DropLarLng!!)
                        .title("Drop Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_location_icon))

                    DropMarker = googleMap!!.addMarker(marker)
                    DropMarker!!.isDraggable = true
                    bounds.include(marker!!.position)
                }
            }
        }
    }

    //    fun setCameraWithCoordinationBounds(route: Route) {
//        val southwest = route.bound.southwestCoordination.coordination
//        val northeast = route.bound.northeastCoordination.coordination
//        val bounds = LatLngBounds(southwest, northeast)
//        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
//    }
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded.get(index++).toInt() - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = (if ((result and 1) != 0) (result shr 1).inv() else (result shr 1))
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded.get(index++).toInt() - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = (if ((result and 1) != 0) (result shr 1).inv() else (result shr 1))
            lng += dlng
            val p = LatLng(
                ((lat.toDouble() / 1E5)),
                ((lng.toDouble() / 1E5))
            )
            poly.add(p)
        }
        return poly
    }

    fun BounceAnimationMarker(animationMarker: Marker?, animationLatLng: LatLng?) {
        if (animationLatLng != null) {
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val proj = googleMap!!.projection
            val startPoint = proj.toScreenLocation(animationLatLng)
            startPoint.offset(0, -100)
            val startLatLng = proj.fromScreenLocation(startPoint)
            val duration: Long = 1500
            val interpolator = BounceInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val lng = t * animationLatLng.longitude + (1 - t) * startLatLng.longitude
                    val lat = t * animationLatLng.latitude + (1 - t) * startLatLng.latitude
                    animationMarker!!.position = LatLng(lat, lng)
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    }
                }
            })
        }
    }

    private fun areBoundsTooSmall(bounds: LatLngBounds, minDistanceInMeter: Int): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(
            bounds.southwest.latitude,
            bounds.southwest.longitude,
            bounds.northeast.latitude,
            bounds.northeast.longitude,
            result
        )
        return result[0] < minDistanceInMeter
    }

    fun editorActionListener(locationEditText: EditText, clickText: String) {

        locationEditText.setOnEditorActionListener { v, actionId, event ->
            Log.d("Edit text", "Edit text = " + v.text.toString())

            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                Log.d("locationEditText", "locationEditText = " + locationEditText.text.toString())
                if (locationEditText.text.toString().isNotEmpty()) {

                    if (clickText == "pickeup") {
                        if (Common.isNetworkAvailable(this@HomeActivity)) {
                            bothLocationString = "pickeup"
                            LocationAddress.getAddressFromLocation(
                                edt_pickup_location!!.text.toString(),
                                applicationContext,
                                GeocoderHandlerLatitude()
                            )
                        } else {
                            Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else if (clickText == "drop") {
                        if (Common.isNetworkAvailable(this@HomeActivity)) {
                            bothLocationString = "drop"
                            LocationAddress.getAddressFromLocation(
                                edt_drop_location!!.text.toString(),
                                applicationContext,
                                GeocoderHandlerLatitude()
                            )
                        } else {
                            Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    layout_pickup_drag_location.visibility = View.GONE
                    if (edt_drop_location!!.text.isNotEmpty() && edt_pickup_location!!.text.isNotEmpty()) {
                        if (checkReady() && Common.isNetworkAvailable(this@HomeActivity)) {
                            //new CaculationDiration().execute();
                            //CaculationDirationIon();
                        } else {
                            Common.showInternetInfo(this@HomeActivity, "")
                        }
                    }

                } else {
                    PickupLarLng = null
                    PickupLatitude = 0.0
                    PickupLongtude = 0.0
                    Toast.makeText(this@HomeActivity, "Please Enter Location", Toast.LENGTH_LONG)
                        .show()
                }
            }
            false
        }
    }

    private fun addTextChangeListener(locationEditText: EditText, clickText: String) {
        locationEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val d = Log.d("clickText", "clickText = $clickText")
                if (s.isNotEmpty()) {
                    if (clickText == "drop") {
                        img_drop_close.visibility = View.VISIBLE
                    } else if (clickText == "pickeup") {
                        img_pickup_close.visibility = View.VISIBLE
                    }
                    Log.d("ClickOkButton", "ClickOkButton = $ClickOkButton")
                    if (!ClickOkButton) {
                        layout_pickup_drag_location.visibility = View.VISIBLE
                        Log.d("ClickOkButton", "ClickOkButton = $s")
                        //new getPickupDropAddress(s.toString()).execute();
                        getPickupDropAddressIon(s.toString())
                    }
                } else {
                    if (clickText == "drop") {
                        img_drop_close.visibility = View.GONE
                        DropLarLng = null
                        DropLongtude = 0.0
                        DropLatitude = 0.0
                    } else if (clickText == "pickeup") {
                        img_pickup_close.visibility = View.GONE
                        PickupLarLng = null
                        PickupLatitude = 0.0
                        PickupLongtude = 0.0
                    }
                    layout_pickup_drag_location.visibility = View.GONE
                    MarkerAdd()
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addSetOnClickListener(locationEditext: EditText, ClickValue: String) {
        locationEditext.setOnTouchListener { _, _ ->
            ClickOkButton = false
            bothLocationString = ClickValue
//            val params = RelativeLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            if (ClickValue == "drop") {
//                params.setMargins(0, resources.getDimension(R.dimen.height_175).toInt(), 0, 0)
//            } else if (ClickValue == "pickeup") {
//                params.setMargins(0, resources.getDimension(R.dimen.height_130).toInt(), 0, 0)
//            }
//            layout_pickup_drag_location.layoutParams = params
            false
        }

        locationEditext.setOnClickListener {
            ClickOkButton = false
            bothLocationString = ClickValue
//            val params = RelativeLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            if (ClickValue == "drop") {
//                params.setMargins(0, resources.getDimension(R.dimen.height_175).toInt(), 0, 0)
//            } else if (ClickValue == "pickeup") {
//                params.setMargins(0, resources.getDimension(R.dimen.height_130).toInt(), 0, 0)
//            }
//            layout_pickup_drag_location.layoutParams = params
        }
    }

    fun getPickupDropAddressIon(inputSting: String) {
        var locatinUrl = ""
        locationArray = ArrayList()
        try {
            locatinUrl =
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + getString(R.string.google_server_key) + "&input=" + URLEncoder.encode(
                    inputSting,
                    "UTF-8"
                )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        Log.d("locatinUrl", "Login locatinUrl = $locatinUrl")
        Ion.with(this@HomeActivity)
            .load(locatinUrl)
            .setTimeout(10000)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error
                Log.d("Login result", "Login result = $result==$error")

                if (error == null) {
                    try {
                        val resObj = JSONObject(result.toString())
                        if (resObj.getString("status").toLowerCase(Locale.getDefault()) == "ok") {
                            val predsJsonArray = resObj.getJSONArray("predictions")
                            for (i in 0 until predsJsonArray.length()) {
                                val locHashMap = HashMap<String, String>()
                                locHashMap["location name"] =
                                    predsJsonArray.getJSONObject(i).getString("description")
                                locationArray!!.add(locHashMap)
                            }
                            if (locationArray != null && locationArray!!.size > 0) {
                                recycle_pickup_location.visibility = View.VISIBLE
                                layout_no_result.visibility = View.GONE
                                pickupDropLocationAdapter =
                                    PickupDropLocationAdapter(this@HomeActivity, locationArray!!)
                                recycle_pickup_location.adapter = pickupDropLocationAdapter
                                pickupDropLocationAdapter!!.setOnDropPickupClickListener(this@HomeActivity)
                                pickupDropLocationAdapter!!.updateItems()
                            }

                            Log.d("locationArray", "locationArray = " + locationArray!!.size)
                        } else if (resObj.getString("status") == "ZERO_RESULTS") {
                            if (locationArray != null && locationArray!!.size > 0)
                                locationArray!!.clear()

                            layout_no_result.visibility = View.VISIBLE
                            recycle_pickup_location.visibility = View.GONE

                            Log.d("locationArray", "locationArray = " + locationArray!!.size)
                            if (pickupDropLocationAdapter != null)
                                pickupDropLocationAdapter!!.updateBlankItems(locationArray!!)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Common.ShowHttpErrorMessage(this@HomeActivity, error.message)
                }
            }
    }

    override fun PickupDropClick(position: Int) {
        if (locationArray != null && locationArray!!.size > 0) {
            val picDrpHash = locationArray!![position]
            Log.d("bothLocationString", "bothLocationString = $bothLocationString")
            if (bothLocationString != "") {
                if (bothLocationString == "pickeup") {
                    edt_pickup_location!!.setText(picDrpHash["location name"])
                    if (Common.isNetworkAvailable(this@HomeActivity)) {
                        Log.d(
                            "Location name",
                            "Location name = " + edt_pickup_location!!.text.toString()
                        )
                        bothLocationString = "pickeup"
                        LocationAddress.getAddressFromLocation(
                            picDrpHash["location name"]!!,
                            applicationContext,
                            GeocoderHandlerLatitude()
                        )
                    } else {
                        Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG).show()
                    }
                } else if (bothLocationString == "drop") {
                    edt_drop_location!!.setText(picDrpHash["location name"])
                    if (Common.isNetworkAvailable(this@HomeActivity)) {
                        Log.d(
                            "Location name",
                            "Location name = " + edt_pickup_location!!.text.toString()
                        )
                        bothLocationString = "drop"
                        LocationAddress.getAddressFromLocation(
                            picDrpHash["location name"]!!,
                            applicationContext,
                            GeocoderHandlerLatitude()
                        )
                    } else {
                        Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG).show()
                    }
                }

                //                if (Common.isNetworkAvailable(HomeActivity.this)) {
                //                    if (checkReady() && edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                //                        //new CaculationDiration().execute();
                //                        CaculationDirationIon();
                //                    }
                //                } else {
                //                    Common.showInternetInfo(HomeActivity.this, "");
                //                    return;
                //                }
            }
        }
        val imm = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        layout_pickup_drag_location.visibility = View.GONE
        //recycle_pickup_location.setVisibility(View.GONE);
    }

    public override fun onDestroy() {
        super.onDestroy()

        gpsTracker = null
        googleMap = null
        cabDetailArray = null
        marker = null
        PickupLarLng = null
        DropLarLng = null
        arrayPoints = null
        NowDialog = null
        ReservationDialog = null
        cabDetailAdapter = null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestCode", "requestCode = $requestCode")
        if (requestCode == 3) {
            if (data != null) {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val userUpd =
                    data.getStringExtra("update_user_profile").toString()
                Log.d("requestCode", "requestCode = $userUpd")
                if (userUpd == "1") {
                    common.SlideMenuDesign(slidingMenu, this@HomeActivity, "home")
                }
            }
        } else if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm =
                    data!!.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.d("paypal data", "paypal data = $confirm")
                        Log.e("Show", "Show" + confirm.toJSONObject().toString(4))
                        Log.e("Show", "Show" + confirm.payment.toJSONObject().toString(4))
                        CashDialog.cancel()
                        NowDialog!!.show()
                        val conObj = JSONObject(confirm.toJSONObject().toString(4))
                        val ResObj = JSONObject(conObj.getString("response"))
                        transaction_id = ResObj.getString("id")
                        Log.d("stripe_id", "stripe_id = $transaction_id")
                        Toast.makeText(
                            applicationContext,
                            "PaymentConfirmation info received" + " from PayPal",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: JSONException) {
                        Toast.makeText(
                            applicationContext,
                            "an extremely unlikely failure" + " occurred:",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "The user canceled.", Toast.LENGTH_LONG).show()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(
                    applicationContext,
                    "An invalid Payment or PayPalConfiguration" + " was submitted. Please see the docs.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (requestCode == REQUESTCODESTRIPE) {
            if (data != null) {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                transaction_id = data.getStringExtra("stripe_id").toString()
                Log.d("stripe_id", "stripe_id = $transaction_id")
                CashDialog.cancel()
                NowDialog!!.show()
            }
        }
    }

    override fun onBackPressed() {
        if (slidingMenu.isMenuShowing) {
            slidingMenu.toggle()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { arg0, arg1 -> super@HomeActivity.onBackPressed() }
                .create().show()
        }
    }

    companion object {
        /*Paypall integration variable*/
        private const val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK
        // note that these credentials will differ between live & sandbox environments.
        private const val CONFIG_CLIENT_ID =
            "AYqm_vX5LIbsdhuZBgkVBHAJ9YR6yA2_3N81R9wZGkjBZPMHDu91uo47fwL7779Bxly6li5vQWfrO0fy"
        private const val REQUEST_CODE_PAYMENT = 1
        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

        private const val REQUESTCODESTRIPE = 2

        @SuppressLint("SimpleDateFormat")
        fun checkDates(startDate: String, currentDate: String): Boolean {
            val dfDate = SimpleDateFormat("dd/MM/yyyy HH:mm")
            var b = false
            try {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                if (dfDate.parse(startDate).after(dfDate.parse(currentDate))) {
                    b = true //If start date is before end date
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return b
        }
    }

    private fun getFilter() {
        Intent(this, FilterActivity::class.java).also {
            it.putExtra("gender", gender)
            it.putExtra("max", textMin2.text.toString())
            it.putExtra("min", textMin1.text.toString())
            it.putExtra("rating", spinner_sort_by.selectedItemPosition.toString())
            it.putExtra("pick_lat", PickupLatitude)
            it.putExtra("pick_lon", PickupLongtude)
            it.putExtra("drop_lat", DropLatitude)
            it.putExtra("drop_lon", DropLongtude)
            it.putExtra("DayNight", DayNight)
            it.putExtra("drop_point", edt_drop_location!!.text.toString().trim { it <= ' ' })
            it.putExtra(
                "pickup_point",
                edt_pickup_location!!.text.toString().trim { it <= ' ' })
            it.putExtra("totlePrice", totlePrice.toString())
            it.putExtra("truckType", truckType)
            it.putExtra("CabId", CabId)
            it.putExtra("comment", edtwishes.text.toString())
            it.putExtra("transfertype", transfertype)
            it.putExtra("BookingType", BookingType)
            it.putExtra("astTime", AstTime)
            it.putExtra("distance", distance)
            startActivity(it)
        }
    }

    private fun getNearestDriver() {
        ProgressDialog.show()
        cusRotateLoading.start()
        FilterApi().getNearestDriver(
            getCityFromLatLon(PickupLatitude, PickupLongtude),
            PickupLatitude,
            PickupLongtude,
            CabId?.toInt()!!
        ).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("result", t.message)
                ProgressDialog.dismiss()
                cusRotateLoading.stop()

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//            Log.e("result",response.body()?.string()!!)
                ProgressDialog.dismiss()
                cusRotateLoading.stop()
                val gson = Gson()
                val data = gson.fromJson(response.body()?.string()!!, NeareastDriver::class.java)
                if (data.status)
                    Intent(this@HomeActivity, BookingDetails::class.java).also {
                        it.putExtra("nearest_data", data.data)
                        it.putExtra("gender", gender)
                        it.putExtra("max", textMin2.text.toString())
                        it.putExtra("min", textMin1.text.toString())
                        it.putExtra("rating", spinner_sort_by.selectedItemPosition.toString())
                        it.putExtra("pick_lat", PickupLatitude)
                        it.putExtra("pick_lon", PickupLongtude)
                        it.putExtra("drop_lat", DropLatitude)
                        it.putExtra("drop_lon", DropLongtude)
                        it.putExtra("DayNight", DayNight)
                        it.putExtra("comment", edtwishes.text.toString())
                        it.putExtra(
                            "drop_point",
                            edt_drop_location!!.text.toString().trim { it <= ' ' })
                        it.putExtra(
                            "pickup_point",
                            edt_pickup_location!!.text.toString().trim { it <= ' ' })
                        it.putExtra("totlePrice", totlePrice.toString())
                        it.putExtra("truckType", truckType)
                        it.putExtra("CabId", CabId)
                        it.putExtra("transfertype", transfertype)
                        it.putExtra("BookingType", BookingType)
                        it.putExtra("astTime", AstTime)
                        it.putExtra("distance", distance)
                        startActivity(it)
                    }
                else
                    Toast.makeText(this@HomeActivity, data.error_msg, Toast.LENGTH_LONG).show()
            }

        })
//
    }


    private fun getCityFromLatLon(pickupLatitude: Double, pickupLongtude: Double): String {
        var address: List<Address>? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        address = geocoder.getFromLocation(pickupLatitude, pickupLongtude, 1)
        return address[0].locality

    }
}
