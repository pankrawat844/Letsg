package come.texi.driver

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
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

import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.koushikdutta.ion.Ion
import com.paypal.android.sdk.payments.*
import com.victor.loading.rotate.RotateLoading
import come.texi.driver.adapter.CabDetailAdapter
import come.texi.driver.adapter.PickupDropLocationAdapter
import come.texi.driver.gpsLocation.AutoCompleteAdapter
import come.texi.driver.gpsLocation.GPSTracker
import come.texi.driver.gpsLocation.LocationAddress
import come.texi.driver.utils.CabDetails
import come.texi.driver.utils.Common
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.util.EntityUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.edt_drop_location
import kotlinx.android.synthetic.main.activity_home.edt_pickup_location
import kotlinx.android.synthetic.main.activity_home.edt_write_comment
import kotlinx.android.synthetic.main.activity_home.img_drop_close
import kotlinx.android.synthetic.main.activity_home.img_pickup_close
import kotlinx.android.synthetic.main.activity_home.layout_no_result
import kotlinx.android.synthetic.main.activity_home.layout_now
import kotlinx.android.synthetic.main.activity_home.layout_pickup_drag_location
import kotlinx.android.synthetic.main.activity_home.layout_reservation
import kotlinx.android.synthetic.main.activity_home.layout_slidemenu
import kotlinx.android.synthetic.main.activity_home.no_location
import kotlinx.android.synthetic.main.activity_home.please_check
import kotlinx.android.synthetic.main.activity_home.recycle_pickup_location
import kotlinx.android.synthetic.main.activity_home.txt_home
import kotlinx.android.synthetic.main.activity_home.txt_not_found
import kotlinx.android.synthetic.main.activity_home.txt_now
import kotlinx.android.synthetic.main.activity_home.txt_reservation
import kotlinx.android.synthetic.main.activity_home1.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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

    var OpenSans_Regular: Typeface? = null
    lateinit var OpenSans_Bold: Typeface
    lateinit var Roboto_Regular: Typeface
    lateinit var Roboto_Medium: Typeface
    lateinit var Roboto_Bold: Typeface
    internal var currentTime = SimpleDateFormat("HH:mm")
    internal var gpsTracker: GPSTracker? = null
    private var googleMap: GoogleMap? = null
    internal var cabDetailArray: ArrayList<CabDetails>? = null
    internal var marker: MarkerOptions? = null
    internal var PickupLarLng: LatLng? = null
    internal var DropLarLng: LatLng? = null
    internal var DropLongtude: Double = 0.toDouble()
    internal var DropLatitude: Double = 0.toDouble()
    internal var PickupLongtude: Double = 0.toDouble()
    internal var PickupLatitude: Double = 0.toDouble()
    internal var locationArray: ArrayList<HashMap<String, String>>? = null
    private var arrayPoints: ArrayList<LatLng>? = null
    internal var NowDialog: Dialog? = null
    lateinit var CashDialog: Dialog
    internal var ReservationDialog: Dialog? = null
    internal var cabDetailAdapter: CabDetailAdapter? = null

    var txt_car_header: TextView? = null
    lateinit var txt_currency: TextView
    lateinit var txt_far_breakup: TextView
    lateinit var txt_book: TextView
    lateinit var txt_cancel: TextView
    internal var txt_car_descriptin: TextView? = null
    internal var txt_first_price: TextView? = null
    internal var txt_first_km: TextView? = null
    internal var txt_sec_pric: TextView? = null
    internal var txt_sec_km: TextView? = null
    internal var txt_thd_price: TextView? = null
    lateinit var txt_locatons: TextView
    internal var layout_one: RelativeLayout? = null
    internal var layout_two: RelativeLayout? = null
    internal var layout_three: RelativeLayout? = null
    lateinit var txt_total_price: TextView
    lateinit var txt_cash: TextView
    lateinit var layout_cash: RelativeLayout
    lateinit var spinner_person: Spinner
    internal var person = ""
    lateinit var txt_first_currency: TextView
    lateinit var txt_secound_currency: TextView
    lateinit var txt_thd_currency: TextView
    lateinit var layout_timming: LinearLayout
    lateinit var layout_far_breakup: RelativeLayout

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
    lateinit var txt_date: TextView
    lateinit var txt_time: TextView
    lateinit var date: DatePickerDialog.OnDateSetListener
    internal var BookingDateTime = ""
    lateinit var bookingFormate: SimpleDateFormat
    internal var devise_width: Int = 0

    internal var transaction_id = ""

    internal var common = Common()

    internal var LocationDistanse = true
    internal var PickupMarker: Marker? = null
    internal var DropMarker: Marker? = null
    internal var CabPositon = 0

    lateinit var BookingType: String

    lateinit var FixRateArray: ArrayList<HashMap<String, String>>

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading
    lateinit var recycle_cab_detail: RecyclerView
    var listLatLng = ArrayList<LatLng?>()
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

        if (userPref.getString("id_device_token", "") != "1")
            Common.CallUnSubscribeTaken(this@HomeActivity, Common.device_token).execute()

        arrayPoints = ArrayList()

        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        devise_width = displaymetrics.widthPixels

        layout_now!!.layoutParams.width = (devise_width * 0.50).toInt()

        val resParam = RelativeLayout.LayoutParams(
            (devise_width * 0.51).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        resParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        resParam.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout_reservation!!.layoutParams = resParam

        bookingFormate = SimpleDateFormat("h:mm a, d, MMM yyyy,EEE")

        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        Roboto_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_home!!.typeface = OpenSans_Bold
        txt_not_found.typeface = OpenSans_Bold
//        txt_locatons.typeface = Roboto_Bold
        txt_reservation.typeface = Roboto_Bold
        txt_now.typeface = Roboto_Bold

        edt_pickup_location!!.typeface = OpenSans_Regular
        edt_drop_location!!.typeface = OpenSans_Regular
        edt_write_comment!!.typeface = Roboto_Regular
        no_location.typeface = Roboto_Regular
        please_check.typeface = Roboto_Regular

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
        EditorActionListener(edt_pickup_location!!, "pickeup")
        AddTextChangeListener(edt_pickup_location!!, "pickeup")
        AddSetOnClickListener(edt_pickup_location!!, "pickeup")
        /*Pickup Location autocomplate end*/

        /*Drop Location autocomplate start*/
        //LocationAutocompleate(edt_drop_location, "drop");
        EditorActionListener(edt_drop_location!!, "drop")
        AddTextChangeListener(edt_drop_location!!, "drop")
        AddSetOnClickListener(edt_drop_location!!, "drop")
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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
        NowDialog = Dialog(this@HomeActivity, R.style.DialogUpDownAnim)
        NowDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE) //before

        NowDialog!!.setContentView(R.layout.now_dialog_layout)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            NowDialog!!.window!!.statusBarColor = Color.TRANSPARENT
        }

        txt_car_header = NowDialog!!.findViewById(R.id.txt_car_header) as TextView
        txt_car_header!!.typeface = Roboto_Medium
        txt_currency = NowDialog!!.findViewById(R.id.txt_currency) as TextView
        txt_currency.typeface = Roboto_Regular
        txt_far_breakup = NowDialog!!.findViewById(R.id.txt_far_breakup) as TextView
        txt_far_breakup.typeface = Roboto_Bold
        txt_book = NowDialog!!.findViewById(R.id.txt_book) as TextView
        txt_book.typeface = Roboto_Bold
        txt_cancel = NowDialog!!.findViewById(R.id.txt_cancel) as TextView
        txt_cancel.typeface = Roboto_Bold

        txt_car_descriptin = NowDialog!!.findViewById(R.id.txt_car_descriptin) as TextView
        txt_car_descriptin!!.typeface = Roboto_Regular
        txt_first_price = NowDialog!!.findViewById(R.id.txt_first_price) as TextView
        txt_first_price!!.typeface = Roboto_Regular
        txt_first_km = NowDialog!!.findViewById(R.id.txt_first_km) as TextView
        txt_first_km!!.typeface = Roboto_Regular
        txt_sec_pric = NowDialog!!.findViewById(R.id.txt_sec_pric) as TextView
        txt_sec_pric!!.typeface = Roboto_Regular
        txt_sec_km = NowDialog!!.findViewById(R.id.txt_sec_km) as TextView
        txt_sec_km!!.typeface = Roboto_Regular
        txt_thd_price = NowDialog!!.findViewById(R.id.txt_thd_price) as TextView
        txt_thd_price!!.typeface = Roboto_Regular
        layout_one = NowDialog!!.findViewById(R.id.layout_one) as RelativeLayout
        layout_two = NowDialog!!.findViewById(R.id.layout_two) as RelativeLayout
        layout_three = NowDialog!!.findViewById(R.id.layout_three) as RelativeLayout
        txt_total_price = NowDialog!!.findViewById(R.id.txt_total_price) as TextView
        txt_cash = NowDialog!!.findViewById(R.id.txt_cash) as TextView
        spinner_person = NowDialog!!.findViewById(R.id.spinner_person) as Spinner
        txt_first_currency = NowDialog!!.findViewById(R.id.txt_first_currency) as TextView
        txt_secound_currency = NowDialog!!.findViewById(R.id.txt_secound_currency) as TextView
        txt_thd_currency = NowDialog!!.findViewById(R.id.txt_thd_currency) as TextView
        layout_timming = NowDialog!!.findViewById(R.id.layout_timming) as LinearLayout
        layout_far_breakup = NowDialog!!.findViewById(R.id.layout_far_breakup) as RelativeLayout

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

        val txt_specailChr_note = NowDialog!!.findViewById(R.id.txt_specailChr_note) as TextView
        txt_specailChr_note.typeface = Roboto_Regular

        txt_total_price.typeface = Roboto_Regular
        txt_cash.typeface = Roboto_Regular
        txt_currency.text = Common.Currency
        txt_first_currency.text = Common.Currency
        txt_first_currency.typeface = Roboto_Bold
        txt_secound_currency.text = Common.Currency
        txt_secound_currency.typeface = Roboto_Bold
        txt_thd_currency.text = Common.Currency
        txt_thd_currency.typeface = Roboto_Bold

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
                bi.putExtra("comment", edt_write_comment!!.text.toString().trim { it <= ' ' })
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
                txt_car_header!!.text = cabDetails.cartype!!.toUpperCase()
                CarName = cabDetails.cartype!!.toUpperCase()
                txt_car_descriptin!!.text = cabDetails.description

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
                txt_first_price!!.text = car_rate
                FirstKm = java.lang.Float.parseFloat(cabDetails.intialkm!!)
                txt_first_km!!.text =
                    resources.getString(R.string.first) + " " + FirstKm + " " + resources.getString(
                        R.string.km
                    )
                txt_sec_pric!!.text = fromintailrate + "/" + resources.getString(R.string.km)
                txt_sec_km!!.text =
                    resources.getString(R.string.after) + " " + FirstKm + " " + resources.getString(
                        R.string.km
                    )

                if (cabDetails.rideTimeRate != null || cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {
                    layout_three!!.visibility = View.VISIBLE
                    txt_thd_price!!.text = ride_time_rate + "/" + resources.getString(R.string.min)
                } else {
                    layout_three!!.visibility = View.GONE
                    val params = LinearLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.MATCH_PARENT
                    )
                    params.weight = 1.5f
                    layout_one!!.layoutParams = params
                    layout_two!!.layoutParams = params
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

        layout_now!!.setOnClickListener(View.OnClickListener {
            Log.d("length ", "length = " + edt_pickup_location!!.text.toString().length)
            if (edt_pickup_location!!.text.toString().length == 0) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_pickup))
                return@OnClickListener
            } else if (edt_drop_location!!.text.toString().length == 0) {
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




            NowDialog!!.show()

            //                if(intailrate != null && fromintailrate != null && ride_time_rate != null)
            //                    totlePrice = Common.getTotalPrice(intailrate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
            //                else
            //                    totlePrice = 0f;
            //                Log.d("totlePrice","totlePrice = "+totlePrice);
            //
            //                txt_total_price.setText(String.valueOf(totlePrice));
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
            startActivityForResult(si, REQUEST_CODE_STRIPE)
        }
        val layout_cash_cancel = CashDialog.findViewById(R.id.layout_cash_cancel) as RelativeLayout
        layout_cash_cancel.setOnClickListener {
            CashDialog.cancel()
            NowDialog!!.show()
            PaymentType = "Cash"
        }

        layout_cash = NowDialog!!.findViewById(R.id.layout_cash) as RelativeLayout
        layout_cash.setOnClickListener {
            //NowDialog.cancel();
            //CashDialog.show();
        }

        /*Cash Dialog End*/

        /*Now Image Click popup end*/

        /*Reservation Image Click popup start*/

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

        txt_date = ReservationDialog!!.findViewById(R.id.txt_date) as TextView
        txt_time = ReservationDialog!!.findViewById(R.id.txt_time) as TextView

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
            val dateVal = CheckDates(DateTimeString, currentDate)

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
            if (edt_pickup_location!!.text.toString().length == 0) {
                Common.showMkError(this@HomeActivity, resources.getString(R.string.enter_pickup))
                return@OnClickListener
            } else if (edt_drop_location!!.text.toString().length == 0) {
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
        }

        img_drop_close.setOnClickListener {
            edt_drop_location!!.setText("")
            DropLarLng = null
            DropLongtude = 0.0
            DropLatitude = 0.0
            MarkerAdd()
        }
        des_layout.setOnClickListener {
            Intent(this,DropLocationActivity::class.java
            ).also {
                startActivity(it)
            }
        }
    }


    private fun getAddress(latitude: Double, longitude: Double): String {
        var addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address: String = addresses.get(0)
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city: String = addresses.get(0).locality
        val state: String = addresses.get(0).adminArea
        val country = addresses.get(0).countryName
        val postalCode = addresses.get(0).postalCode
        val knownName = addresses.get(0).featureName // Only if available else return
        return address
    }

    fun onBuyPressed() {
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


    fun LocationAutocompleate(locationEditext: AutoCompleteTextView, clickText: String) {
        locationEditext.threshold = 1

        //Set adapter to AutoCompleteTextView
        locationEditext.setAdapter(
            AutoCompleteAdapter(
                this@HomeActivity,
                R.layout.location_list_item
            )
        )
        locationEditext.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
        locationEditext.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Show Alert
                Toast.makeText(
                    baseContext,
                    "Position:" + position + "==" + clickText + "==" + " Month:" + parent.getItemAtPosition(
                        position
                    ),
                    Toast.LENGTH_LONG
                ).show()

                Log.d(
                    "AutocompleteContacts",
                    "Position:" + position + " Month:" + parent.getItemAtPosition(position)
                )

                val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)

                if (clickText == "pickeup") {
                    if (edt_pickup_location!!.text.toString().length > 0) {
                        //DrowLineGoogleMap();
                        if (Common.isNetworkAvailable(this@HomeActivity)) {
                            Log.d(
                                "Location name",
                                "Location name = " + edt_pickup_location!!.text.toString()
                            )
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
                    }
                } else if (clickText == "drop") {
                    Log.d(
                        "Location name",
                        "Location name = " + edt_pickup_location!!.text.toString()
                    )
                    if (Common.isNetworkAvailable(this@HomeActivity)) {
                        bothLocationString = "drop"
                        LocationAddress.getAddressFromLocation(
                            edt_drop_location!!.text.toString(),
                            applicationContext,
                            GeocoderHandlerLatitude()
                        )
                    } else {
                        Toast.makeText(this@HomeActivity, "No Network", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun updateLabel() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        txt_date.text = sdf.format(myCalendar.time)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap!!.isMyLocationEnabled = true
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
//
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
        //        try {
        //            DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+URLEncoder.encode(edt_pickup_location.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(edt_drop_location.getText().toString(), "UTF-8");
        //        } catch (UnsupportedEncodingException e) {
        //            e.printStackTrace();
        //        }
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
                        if (resObj.getString("status").toLowerCase() == "ok") {


                            val routArray = JSONArray(resObj.getString("routes"))
                            val routObj = routArray.getJSONObject(0)
                            val overview_polylines = routObj.getJSONObject("overview_polyline")
                            listLatLng.addAll(decodePoly(overview_polylines.getString("points")))
                            Log.d("geoObj", "DrowLocUrl geoObj one= $routObj")
                            val legsArray = JSONArray(routObj.getString("legs"))
                            val legsObj = legsArray.getJSONObject(0)
                            val legsSteps = legsObj.getJSONArray("steps")
//                            for (i in 0 until legsSteps.length()) {
//                                val startLocJSON = legsSteps.getJSONObject(i)
//                                val startLoc = startLocJSON.getJSONObject("start_location")
//                                val endLoc = startLocJSON.getJSONObject("end_location")
//
//                                listLatLng.add(
//                                    LatLng(
//                                        startLoc.getDouble("lat"),
//                                        startLoc.getDouble("lng")
//                                    )
//                                )
//                                listLatLng.add(
//                                    LatLng(
//                                        endLoc.getDouble("lat"),
//                                        endLoc.getDouble("lng")
//                                    )
//                                )
//
//                            }
                            AddPollyLines(listLatLng)
                            val disObj = JSONObject(legsObj.getString("distance"))
                            //if (disObj.getInt("value") > 1000)
                            distance = disObj.getInt("value").toFloat() / 1000
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


                            if (FixRateArray.size > 0) {
                                for (ci in cabDetailArray!!.indices) {

                                    val FixCabDetails = cabDetailArray!![ci]

                                    for (fi in FixRateArray.indices) {
                                        val FixHasMap = FixRateArray[fi]

                                        Log.d(
                                            "car_type_id",
                                            "car_type_id = " + FixHasMap["car_type_id"] + "==" + FixCabDetails.id
                                        )
                                        if (FixHasMap["car_type_id"] == FixCabDetails.id) {
                                            val cabDetails = cabDetailArray!![ci]
                                            cabDetails.fixPrice = FixHasMap["fix_price"].toString()
                                            cabDetails.areaId = FixHasMap["area_id"].toString()
                                            break
                                        }

                                        Log.d("fi", "car_type_id fi = $fi")
                                    }
                                    Log.d("ci", "car_type_id ci = $ci")
                                }
                            } else {
                                for (ci in cabDetailArray!!.indices) {
                                    val AllCabDetails = cabDetailArray!![ci]
                                    AllCabDetails.fixPrice = ""
                                    AllCabDetails.areaId = ""
                                }
                            }
                            val cabDetails = cabDetailArray!![0]
                            if (cabDetails.fixPrice != "") {
                                layout_timming.visibility = View.GONE
                                layout_far_breakup.visibility = View.INVISIBLE
                                totlePrice = java.lang.Float.parseFloat(cabDetails.fixPrice!!)
                                txt_total_price.text = Math.round(totlePrice!!).toString() + ""
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

                                layout_timming.visibility = View.VISIBLE
                                layout_far_breakup.visibility = View.VISIBLE
                                txt_total_price.text = Math.round(totlePrice!!).toString() + ""
                            }

                            LocationDistanse = true


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

        SetNowDialogCabValue()
    }

    inner class CaculationDiration : AsyncTask<String, Int, String>() {
        private var content: String? = null

        internal var DrowLocUrl = ""

        init {
            try {
                DrowLocUrl =
                    "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=" + URLEncoder.encode(
                        edt_pickup_location!!.text.toString(),
                        "UTF-8"
                    ) + "&destination=" + URLEncoder.encode(
                        edt_drop_location!!.text.toString(),
                        "UTF-8"
                    )
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

        }

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String): String? {
            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)
                Log.d("DrowLocUrl", "DrowLocUrl = $DrowLocUrl")
                val getMethod = HttpGet(DrowLocUrl)
                //getMethod.setEntity(entity);
                client.execute(getMethod) { httpResponse ->
                    val httpEntity = httpResponse.entity
                    content = EntityUtils.toString(httpEntity)
                    Log.d("Result >>>", "DrowLocUrl Result One" + content!!)

                    null
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Indiaries", "DrowLocUrl Result error$e")
                return e.message
            }

            return content
        }

        override fun onPostExecute(result: String) {
            val isStatus = Common.ShowHttpErrorMessage(this@HomeActivity, result)
            if (isStatus) {
                try {
                    val resObj = JSONObject(result)
                    if (resObj.getString("status").toLowerCase() == "ok") {


                        val routArray = JSONArray(resObj.getString("routes"))
                        val routObj = routArray.getJSONObject(0)
                        Log.d("geoObj", "DrowLocUrl geoObj one= $routObj")
                        val legsArray = JSONArray(routObj.getString("legs"))
                        val legsObj = legsArray.getJSONObject(0)

                        val disObj = JSONObject(legsObj.getString("distance"))
                        if (disObj.getInt("value") > 1000)
                            distance = disObj.getInt("value").toFloat() / 1000
                        else if (disObj.getInt("value") > 100)
                            distance = disObj.getInt("value").toFloat() / 100
                        else if (disObj.getInt("value") > 10)
                            distance = disObj.getInt("value").toFloat() / 10
                        else if (disObj.getInt("value") == 0)
                            distance = disObj.getInt("value").toFloat()
                        Log.d("distance", "distance = " + distance!!)
                        Log.d("dis", "dis = " + distance!!)

                        val duration = JSONObject(legsObj.getString("duration"))

                        val durText = duration.getString("text")
                        val durTextSpi =
                            durText.split(" ".toRegex()).dropLastWhile({ it.isEmpty() })
                                .toTypedArray()
                        Log.d("durTextSpi", "min  = durTextSpi = " + durTextSpi.size)
                        var hours = 0
                        var mintus = 0
                        if (durTextSpi.size == 4) {
                            hours = Integer.parseInt(durTextSpi[0]) * 3600
                            mintus = Integer.parseInt(durTextSpi[2])
                        } else if (durTextSpi.size == 2) {
                            if (durTextSpi[1].contains("mins"))
                                mintus = Integer.parseInt(durTextSpi[0])
                            else
                                mintus = Integer.parseInt(durTextSpi[0]) * 3600
                        }

                        totalTime = mintus + hours

                        googleDuration = duration.getInt("value")
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

                        txt_total_price.text = Math.round(totlePrice!!).toString() + ""
                        LocationDistanse = true
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

            }
        }
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

            txt_car_header!!.text = cabDetails.cartype!!.toUpperCase()
            CarName = cabDetails.cartype!!.toUpperCase()
            txt_car_descriptin!!.text = cabDetails.description
            txt_first_price!!.text = "$" + car_rate!!
            FirstKm = java.lang.Float.parseFloat(cabDetails.intialkm!!)
            txt_first_km!!.text = "First $FirstKm km"
            txt_sec_pric!!.text = "$ $fromintailrate/km"
            txt_sec_km!!.text = "After $FirstKm km"

            truckIcon = cabDetails.icon
            truckType = cabDetails.cartype!!
            CabId = cabDetails.id!!
            AreaId = cabDetails.areaId
            transfertype = cabDetails.transfertype

            if (cabDetails.rideTimeRate != null || cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {

                layout_three!!.visibility = View.GONE
                val params = LinearLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT
                )
                params.weight = 1.0f
                layout_one!!.layoutParams = params
                layout_two!!.layoutParams = params
                layout_three!!.layoutParams = params

                layout_three!!.visibility = View.VISIBLE
                txt_thd_price!!.text = ride_time_rate!! + "/min"
            } else {
                layout_three!!.visibility = View.GONE
                val params = LinearLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT
                )
                params.weight = 1.5f
                layout_one!!.layoutParams = params
                layout_two!!.layoutParams = params
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
            spinner_person.adapter = dataAdapter

            if (cabDetails.fixPrice != "") {
                layout_timming.visibility = View.GONE
                layout_far_breakup.visibility = View.INVISIBLE
                txt_total_price.text = cabDetails.fixPrice
                totlePrice = java.lang.Float.parseFloat(cabDetails.fixPrice!!)
            } else {
                layout_timming.visibility = View.VISIBLE
                layout_far_breakup.visibility = View.VISIBLE
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
                txt_total_price.text = Math.round(totlePrice!!).toString() + ""
            }

        }
    }


    fun SetNowDialogCabValue() {

        val CabDetailAry = Common.CabDetail
        if (CabDetailAry != null && CabDetailAry.length() > 0) {
            Log.d("cabDetailArray", "cabDetailArray = " + cabDetailArray!!.size)
            if (cabDetailArray != null && cabDetailArray!!.size > 0) {

                val cabDetails = cabDetailArray!![0]
                txt_car_header!!.text = cabDetails.cartype!!.toUpperCase()
                CarName = cabDetails.cartype!!.toUpperCase()
                txt_car_descriptin!!.text = cabDetails.description

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
                txt_first_price!!.text = car_rate
                FirstKm = java.lang.Float.parseFloat(cabDetails.intialkm!!)
                txt_first_km!!.text =
                    resources.getString(R.string.first) + " " + FirstKm + " " + resources.getString(
                        R.string.km
                    )
                txt_sec_pric!!.text = fromintailrate + "/" + resources.getString(R.string.km)
                txt_sec_km!!.text =
                    resources.getString(R.string.after) + " " + FirstKm + " " + resources.getString(
                        R.string.km
                    )

                if (cabDetails.rideTimeRate != null || cabDetails.nightRideTimeRate != null && cabDetails.nightRideTimeRate != "0") {
                    layout_three!!.visibility = View.VISIBLE
                    txt_thd_price!!.text = ride_time_rate + "/" + resources.getString(R.string.min)
                } else {
                    layout_three!!.visibility = View.GONE
                    val params = LinearLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.MATCH_PARENT
                    )
                    params.weight = 1.5f
                    layout_one!!.layoutParams = params
                    layout_two!!.layoutParams = params
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

    }

    fun PickupFixRateCall() {

//        ProgressDialog.show()
//        cusRotateLoading.start()

        FixRateArray = ArrayList()
//
//        val FixAreaUrl =
//            Url.FixAreaUrl + "?pick_lat=" + PickupLatitude + "&pick_long=" + PickupLongtude + "&drop_lat=" + DropLatitude + "&drop_long=" + DropLongtude
//        Log.d("FixAreaUrl", "FixAreaUrl =$FixAreaUrl")
//        Ion.with(this@HomeActivity)
//            .load(FixAreaUrl)
//            .setTimeout(6000)
//            .asJsonObject()
//            .setCallback { error, result ->
//                // do stuff with the result or error
//                Log.d("load_trips result", "load_trips result = $result==$error")
//                if (error == null) {
//
//                    try {
//                        val resObj = JSONObject(result.toString())
//                        Log.d("loadTripsUrl", "loadTripsUrl two= $resObj")
//                        val fixAreaArray = JSONArray(resObj.getString("fixAreaPriceList"))
//                        for (fi in 0 until fixAreaArray.length()) {
//                            val fixAreaObj = fixAreaArray.getJSONObject(fi)
//
//                            Log.d("FixRateArray", "FixAreaUrl FixRateArray = $fixAreaObj")
//                            if (fixAreaObj.getString("fix_price") != "0") {
//                                val FixHasMap = HashMap<String, String>()
//                                FixHasMap["fix_price"] =
//                                    fixAreaObj.getString("fix_price").toString()
//                                FixHasMap["car_type_id"] =
//                                    fixAreaObj.getString("car_type_id").toString()
//                                FixHasMap["car_type_name"] =
//                                    fixAreaObj.getString("car_type_name").toString()
//                                FixHasMap["area_title"] =
//                                    fixAreaObj.getString("area_title").toString()
//                                FixHasMap["area_id"] = fixAreaObj.getString("area_id").toString()
//                                FixRateArray.add(FixHasMap)
//                            }
//
//                        }
//                        Log.d("FixRateArray", "FixAreaUrl FixRateArray = " + FixRateArray.size)
//                        CaculationDirationIon()
//
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//
//
//                } else {
//                    ProgressDialog.cancel()
//                    cusRotateLoading.stop()
        CaculationDirationIon()
//                    Common.ShowHttpErrorMessage(this@HomeActivity, error.message)
//                }
//            }

    }

    private inner class GeocoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                }
                else -> locationAddress = null
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
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                }
                else -> locationAddress = null
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


                    if (edt_drop_location!!.text.length > 0 && edt_pickup_location!!.text.length > 0) {
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


            //

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
//                            if (direction!!.isOK()) {
//                                routeSuccess2(direction, "success", PickupLarLng!!, DropLarLng!!);
//                            } else {
//                                //selectedRawLine = ERROR;
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
//    fun routeSuccess2(
//        direction: Direction,
//        message: String,
//        sourceLatLng: LatLng,
//        DestLatlng: LatLng
//    ) {
//        val bounds: LatLngBounds.Builder
//
//        for (route in direction.routeList) {
//            var polyOption = PolylineOptions()
////                polyOption.fillColor(Color.BLUE)
////                polyOption.strokeColor(Color.RED)
////                polyOption.strokeWidth(5f)
//
//            polyOption.color(Color.RED)
//            polyOption.width(12f)
//            polyOption.addAll(route.overviewPolyline.pointList)
//            googleMap!!.clear()
//            googleMap!!.addPolyline(polyOption)
//
//        }
//        bounds = LatLngBounds.builder()
//        if (sourceLatLng != null) {
//            bounds.include(sourceLatLng)
//        }
//        if (DestLatlng != null) {
//            bounds.include(DestLatlng)
//        }
//        try {
//
//
//            val latLngBounds: LatLngBounds = bounds.build()
//            val cameraUpdate: CameraUpdate =
//                CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)
//            googleMap?.animateCamera(cameraUpdate);
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

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

    fun EditorActionListener(locationEditext: EditText, clickText: String) {

        locationEditext.setOnEditorActionListener { v, actionId, event ->
            Log.d("Edit text", "Edit text = " + v.text.toString())

            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                Log.d("locationEditext", "locationEditext = " + locationEditext.text.toString())
                if (locationEditext.text.toString().length > 0) {

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
                    if (edt_drop_location!!.text.length > 0 && edt_pickup_location!!.text.length > 0) {
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

    fun AddTextChangeListener(locationEditext: EditText, clickText: String) {
        locationEditext.addTextChangedListener(object : TextWatcher {

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
                Log.d("clickText", "clickText = $clickText")
                if (s.length != 0) {

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

    fun AddSetOnClickListener(locationEditext: EditText, ClickValue: String) {

        locationEditext.setOnTouchListener { v, event ->
            ClickOkButton = false
            bothLocationString = ClickValue
            val params = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (ClickValue == "drop") {
                params.setMargins(0, resources.getDimension(R.dimen.height_175).toInt(), 0, 0)
            } else if (ClickValue == "pickeup") {
                params.setMargins(0, resources.getDimension(R.dimen.height_130).toInt(), 0, 0)
            }
            layout_pickup_drag_location.layoutParams = params
            false
        }

        locationEditext.setOnClickListener {
            ClickOkButton = false
            bothLocationString = ClickValue
            val params = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (ClickValue == "drop") {
                params.setMargins(0, resources.getDimension(R.dimen.height_175).toInt(), 0, 0)
            } else if (ClickValue == "pickeup") {
                params.setMargins(0, resources.getDimension(R.dimen.height_130).toInt(), 0, 0)
            }
            layout_pickup_drag_location.layoutParams = params
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
                        if (resObj.getString("status").toLowerCase() == "ok") {
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
        txt_car_header = null
        txt_car_descriptin = null
        txt_first_price = null
        txt_first_km = null
        txt_sec_pric = null
        txt_sec_km = null
        txt_thd_price = null
        layout_one = null
        layout_two = null
        layout_three = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestCode", "requestCode = $requestCode")
        if (requestCode == 3) {
            if (data != null) {
                val userUpd = data.getStringExtra("update_user_profile").toString()
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
                        /**
                         * TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         */
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
        } else if (requestCode == REQUEST_CODE_STRIPE) {
            if (data != null) {
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
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK
        // note that these credentials will differ between live & sandbox environments.
        private val CONFIG_CLIENT_ID =
            "AYqm_vX5LIbsdhuZBgkVBHAJ9YR6yA2_3N81R9wZGkjBZPMHDu91uo47fwL7779Bxly6li5vQWfrO0fy"
        private val REQUEST_CODE_PAYMENT = 1
        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

        private val REQUEST_CODE_STRIPE = 2

        fun CheckDates(startDate: String, currentDate: String): Boolean {
            val dfDate = SimpleDateFormat("dd/MM/yyyy HH:mm")
            var b = false
            try {
                if (dfDate.parse(startDate).after(dfDate.parse(currentDate))) {
                    b = true//If start date is before end date
                }
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return b
        }
    }

}
