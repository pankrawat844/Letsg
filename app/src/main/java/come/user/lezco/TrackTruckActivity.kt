package come.user.lezco

import android.app.Dialog
import android.content.*
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.multidex.MultiDex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import come.user.lezco.utils.*
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.util.EntityUtils
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URISyntaxException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class TrackTruckActivity : FragmentActivity(), OnMapReadyCallback {

    internal var txt_track_truck: TextView? = null
    internal var txt_month: TextView? = null
    internal var txt_date: TextView? = null
    internal var txt_locaton: TextView? = null
    internal var txt_point_val: TextView? = null
    internal var txt_remaining: TextView? = null
    internal var txt_remaining_val: TextView? = null
    internal var txt_covered: TextView? = null
    internal var txt_covered_val: TextView? = null
    internal var txt_total_km: TextView? = null
    internal var txt_total_km_val: TextView? = null
    internal var layout_main_drv_car: RelativeLayout? = null
    lateinit var layout_white_main: RelativeLayout
    internal var layout_footer: LinearLayout? = null
    internal var layout_location_heading: RelativeLayout? = null
    internal var layout_dirvier_car: RelativeLayout? = null
    internal var layout_booking_detail: LinearLayout? = null
    internal var txt_driver_other: TextView? = null
    internal var txt_driver_name: TextView? = null
    internal var txt_mob_num: TextView? = null
    internal var txt_car_name: TextView? = null
    internal var txt_car_model: TextView? = null
    internal var txt_number_plate: TextView? = null
    internal var txt_pickup_address: TextView? = null
    internal var txt_drop_address: TextView? = null
    internal var txt_contact: TextView? = null
    internal var txt_share_eta: TextView? = null
    internal var txt_cancel: TextView? = null
    lateinit var txt_location: TextView
    lateinit var img_drv_car_img: ImageView
    lateinit var img_drv_img: ImageView
    internal var layout_back_arrow: RelativeLayout? = null
    lateinit var layout_main_linear: LinearLayout
    lateinit var layout_contact: RelativeLayout
    lateinit var layout_share: RelativeLayout
    lateinit var layout_cancel: RelativeLayout

    lateinit var OldLatLog: LatLng


    lateinit var OpenSans_Regular: Typeface
    lateinit var OpenSans_Bold: Typeface
    lateinit var Roboto_Regular: Typeface
    lateinit var Roboto_Medium: Typeface
    lateinit var Roboto_Bold: Typeface
    lateinit var OpenSans_Semi_Bold: Typeface

    private var googleMap: GoogleMap? = null

    internal var devise_height: Int = 0

    internal var animationStart = true
    internal var MapLayoutClick = false
    internal var FooterMapLayoutClick = false

    private var arrayPoints: ArrayList<LatLng>? = null
    lateinit var markerOption: MarkerOptions
    lateinit var DriverMarkerOption: MarkerOptions
    internal var DriverMarker: Marker? = null

    internal var markerPositon: Int = 0

    internal var mSocket: Socket? = null

    lateinit var slideUpAnimation: Animation
    lateinit var slideDownAnimation: Animation

    lateinit var ShareDialog: Dialog
    internal var DriverPhNo = ""
    lateinit var userPref: SharedPreferences
    lateinit var receiver: BroadcastReceiver

    internal var allTripFeed: AllTripFeed? = null

    internal var LatLngCom = 0
    internal var googleZoom = 0f
    internal var CoverdValue = 0.0

    private val onSocketConnectionListener = Emitter.Listener { args ->
        runOnUiThread {
            // handle the response args
            Toast.makeText(this@TrackTruckActivity, "Driver on the way..", Toast.LENGTH_LONG).show()
            val data = args[0] as JSONObject

            Log.d("data", "connected data = $data")
            try {
                if (data.getString("success") == "true") {
                    val dataArray = JSONArray(data.getString("data"))
                    Log.d("dataArray", "dataArray = $dataArray")
                    for (di in 0 until dataArray.length()) {
                        val dataObj = dataArray.getJSONObject(di)
                        val Lotlon = dataObj.getString("loc")
                        val LotLanArray = JSONArray(dataObj.getString("loc"))
                        val SplLotlon = Lotlon.split("\\,".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

                        val DrvLat = SplLotlon[0].replace("[", "")
                        val DrvLng = SplLotlon[1].replace("]", "")

                        allTripFeed!!.driverLarLng = LatLng(java.lang.Double.parseDouble(DrvLat), java.lang.Double.parseDouble(DrvLng))
                        WayMarker(allTripFeed!!.driverLarLng!!, dataObj.getString("booking_status").toString())
                        Log.d("Lotlon", "connected Lotlon = $DrvLng==$DrvLat")
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Listener for socket connection error.. listener registered at the time of socket connection
     */
    private val onConnectError = Emitter.Listener {
        runOnUiThread {
            if (mSocket != null)
                if (mSocket!!.connected() == false) {
                    Log.d("connected", "connected two= " + mSocket!!.connected())
                    //socketConnection();
                } else {
                    Log.d("connected", "connected three= " + mSocket!!.connected())
                }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_truck)

        println(distance(22.3038866, 70.802139, 22.4709549, 70.0577137, "M").toString() + " Miles\n")
        println(distance(22.2812257, 70.7762461, 22.2810114, 70.7757268, "K").toString() + " Kilometers\n")
        println(distance(22.3038866, 70.802139, 22.4709549, 70.0577137, "N").toString() + " Nautical Miles\n")

        println(getDistance(LatLng(22.2812257, 70.7762461), LatLng(22.2810114, 70.7757268)).toString() + " Nautical Miles Test\n")

        userPref = PreferenceManager.getDefaultSharedPreferences(this@TrackTruckActivity)

        txt_track_truck = findViewById(R.id.txt_track_truck) as TextView
        txt_month = findViewById(R.id.txt_month) as TextView
        txt_date = findViewById(R.id.txt_date) as TextView
        txt_locaton = findViewById(R.id.txt_locaton) as TextView
        txt_point_val = findViewById(R.id.txt_point_val) as TextView
        txt_remaining = findViewById(R.id.txt_remaining) as TextView
        txt_remaining_val = findViewById(R.id.txt_remaining_val) as TextView
        txt_covered = findViewById(R.id.txt_covered) as TextView
        txt_covered_val = findViewById(R.id.txt_covered_val) as TextView
        txt_total_km = findViewById(R.id.txt_total_km) as TextView
        txt_total_km_val = findViewById(R.id.txt_total_km_val) as TextView
        layout_main_drv_car = findViewById(R.id.layout_main_drv_car) as RelativeLayout
        layout_white_main = findViewById(R.id.layout_white_main) as RelativeLayout
        layout_footer = findViewById(R.id.layout_footer) as LinearLayout
        layout_location_heading = findViewById(R.id.layout_location_heading) as RelativeLayout
        layout_dirvier_car = findViewById(R.id.layout_dirvier_car) as RelativeLayout
        layout_booking_detail = findViewById(R.id.layout_booking_detail) as LinearLayout
        txt_driver_other = findViewById(R.id.txt_driver_other) as TextView
        txt_driver_name = findViewById(R.id.txt_driver_name) as TextView
        txt_mob_num = findViewById(R.id.txt_mob_num) as TextView
        txt_car_name = findViewById(R.id.txt_car_name) as TextView
        txt_car_model = findViewById(R.id.txt_car_model) as TextView
        txt_number_plate = findViewById(R.id.txt_number_plate) as TextView
        txt_pickup_address = findViewById(R.id.txt_pickup_address) as TextView
        txt_drop_address = findViewById(R.id.txt_drop_address) as TextView
        txt_contact = findViewById(R.id.txt_contact) as TextView
        txt_share_eta = findViewById(R.id.txt_share_eta) as TextView
        txt_cancel = findViewById(R.id.txt_cancel) as TextView
        img_drv_car_img = findViewById(R.id.img_drv_car_img) as ImageView
        img_drv_img = findViewById(R.id.img_drv_img) as ImageView
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout
        layout_main_linear = findViewById(R.id.layout_main_linear) as LinearLayout
        layout_contact = findViewById(R.id.layout_contact) as RelativeLayout
        layout_share = findViewById(R.id.layout_share) as RelativeLayout
        layout_cancel = findViewById(R.id.layout_cancel) as RelativeLayout
        txt_location = findViewById(R.id.txt_location) as TextView

        allTripFeed = Common.allTripFeeds

        arrayPoints = ArrayList()

        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        devise_height = displaymetrics.heightPixels

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Semi_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Semibold_0.ttf")
        Roboto_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_location.typeface = Roboto_Bold
        txt_track_truck!!.typeface = OpenSans_Bold
        txt_month!!.typeface = OpenSans_Semi_Bold
        txt_date!!.typeface = OpenSans_Bold
        txt_locaton!!.typeface = OpenSans_Regular
        txt_point_val!!.typeface = OpenSans_Bold
        txt_remaining!!.typeface = OpenSans_Regular
        txt_covered!!.typeface = OpenSans_Regular
        txt_total_km!!.typeface = OpenSans_Regular
        txt_remaining_val!!.typeface = Roboto_Medium
        txt_covered_val!!.typeface = Roboto_Medium
        txt_total_km_val!!.typeface = Roboto_Medium

        txt_driver_other!!.typeface = OpenSans_Regular
        txt_driver_name!!.typeface = OpenSans_Regular
        txt_mob_num!!.typeface = OpenSans_Regular
        txt_car_name!!.typeface = OpenSans_Regular
        txt_car_model!!.typeface = OpenSans_Regular
        txt_number_plate!!.typeface = OpenSans_Regular
        txt_pickup_address!!.typeface = OpenSans_Regular
        txt_drop_address!!.typeface = OpenSans_Regular

        txt_contact!!.typeface = OpenSans_Semi_Bold
        txt_share_eta!!.typeface = OpenSans_Semi_Bold
        txt_cancel!!.typeface = OpenSans_Semi_Bold

        allTripFeed!!.pickupDateTime
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var pickup_date_time = ""
        try {
            val parceDate = simpleDateFormat.parse(allTripFeed!!.pickupDateTime)
            val parceDateFormat = SimpleDateFormat("MMM dd")
            pickup_date_time = parceDateFormat.format(parceDate.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val pick_date_spl = pickup_date_time.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        Log.d("pickup_date_time", "pickup_date_time = $pickup_date_time")
        if (pick_date_spl.size > 0) {
            txt_month!!.setText(pick_date_spl[0])
            txt_date!!.setText(pick_date_spl[1])
        }


        txt_locaton!!.text = allTripFeed!!.dropArea
        txt_total_km_val!!.text = java.lang.Double.parseDouble(allTripFeed!!.km!!).toString() + " km"
        txt_pickup_address!!.text = allTripFeed!!.pickupArea
        txt_drop_address!!.text = allTripFeed!!.dropArea
        txt_remaining_val!!.text = java.lang.Double.parseDouble(allTripFeed!!.km!!).toString() + " km"
        txt_car_name!!.text = allTripFeed!!.taxiType
        /*Driver Layout*/

        if (allTripFeed!!.driverDetail != null && allTripFeed!!.driverDetail != "") {

            try {
                val drvObj = JSONObject(allTripFeed!!.driverDetail)
                txt_driver_name!!.text = drvObj.getString("name")
                txt_driver_other!!.text = drvObj.getString("address")
                DriverPhNo = drvObj.getString("phone")
                txt_mob_num!!.text = DriverPhNo


                txt_car_model!!.text = drvObj.getString("car_no")
                txt_number_plate!!.text = drvObj.getString("license_plate")

                Picasso.with(this@TrackTruckActivity)
                        .load(Uri.parse(Url.DriverImageUrl + drvObj.getString("image")))
                        .placeholder(R.drawable.avatar_placeholder)
                        .transform(CircleTransform())
                        .into(img_drv_car_img)

                Picasso.with(this@TrackTruckActivity)
                        .load(Uri.parse(Url.carImageUrl + allTripFeed!!.carIcon))
                        .placeholder(R.drawable.mail_defoult)
                        .transform(CircleTransform())
                        .into(img_drv_img)


                try {
                    mSocket = IO.socket(Url.socketUrl)
                    mSocket!!.emit(Socket.EVENT_CONNECT_ERROR, onConnectError)
                    mSocket!!.connect()

                    val userobj = JSONObject()
                    userobj.put("driver_id", drvObj.getInt("id"))
                    Log.d("connected ", "connected one = " + mSocket!!.connected() + "==" + userobj)
                    mSocket!!.emit("New User Register", userobj)

                    mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                    mSocket!!.on("Driver Detail", onSocketConnectionListener)
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        layout_contact.setOnTouchListener { v, event ->
            FooterMapLayoutClick = false
            false
        }
        layout_contact.setOnClickListener {
            FooterMapLayoutClick = true
            animationStart = false
            val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
            anim.duration = 50
            layout_main_drv_car!!.startAnimation(anim)
            animationStart = true
            layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)

            Handler().postDelayed({
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:$DriverPhNo")
                startActivity(callIntent)
            }, 100)
        }

        layout_share.setOnTouchListener { v, event ->
            FooterMapLayoutClick = false
            false
        }

        layout_share.setOnClickListener {
            ShareDialog = Dialog(this@TrackTruckActivity, android.R.style.Theme_Translucent_NoTitleBar)
            ShareDialog.setContentView(R.layout.camera_dialog_layout)

            val facebook_text = ShareDialog.findViewById(R.id.txt_open_camera) as TextView
            facebook_text.text = "Facebook Share"

            val twitter_text = ShareDialog.findViewById(R.id.txt_open_gallery) as TextView
            twitter_text.text = "Twitter Share"

            val layout_open_camera = ShareDialog.findViewById(R.id.layout_open_camera) as RelativeLayout
            layout_open_camera.setOnClickListener {
                ShareDialog.cancel()
                val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
                anim.duration = 50
                layout_main_drv_car!!.startAnimation(anim)
                animationStart = true
                layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)
            }

            val layout_open_gallery = ShareDialog.findViewById(R.id.layout_open_gallery) as RelativeLayout
            layout_open_gallery.setOnClickListener {
                ShareDialog.cancel()
                val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
                anim.duration = 50
                layout_main_drv_car!!.startAnimation(anim)
                animationStart = true
                layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)
            }

            val layout_open_cancel = ShareDialog.findViewById(R.id.layout_open_cancel) as RelativeLayout
            layout_open_cancel.setOnClickListener {
                ShareDialog.cancel()
                FooterMapLayoutClick = true
            }

            ShareDialog.show()
        }

        layout_cancel.setOnTouchListener { v, event ->
            FooterMapLayoutClick = false
            false
        }
        layout_cancel.setOnClickListener {
            animationStart = false
            val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
            anim.duration = 50
            layout_main_drv_car!!.startAnimation(anim)
            animationStart = true
            layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)
        }

        layout_back_arrow!!.setOnTouchListener { v, event ->
            MapLayoutClick = false
            false
        }

        layout_back_arrow!!.setOnClickListener { finish() }

        layout_main_drv_car!!.setOnTouchListener { v, event ->
            if (!FooterMapLayoutClick)
                FooterMapLayoutClick = true
            false
        }

        layout_main_drv_car!!.setOnClickListener {
            if (!FooterMapLayoutClick)
                FooterMapLayoutClick = true
        }

        layout_dirvier_car!!.setOnTouchListener { v, event ->
            FooterMapLayoutClick = false
            false
        }

        layout_dirvier_car!!.setOnClickListener {
            layout_white_main.isEnabled = false
            layout_dirvier_car!!.isEnabled = false
            val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
            anim.duration = 50
            layout_main_drv_car!!.startAnimation(anim)

            if (animationStart) {
                animationStart = false
                //MapLayoutClick = false;
                layout_dirvier_car!!.setBackgroundResource(0)
            } else {
                animationStart = true
                // MapLayoutClick = true;
                layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)
            }
        }

        OldLatLog = LatLng(java.lang.Double.parseDouble(allTripFeed!!.startPickLatLng!!), java.lang.Double.parseDouble(allTripFeed!!.endPickLatLng!!))

        Handler().postDelayed({
            if (checkReady()) {
                if (allTripFeed!!.oldLocationList != null && allTripFeed!!.oldLocationList!!.size > 0) {
                    //Toast.makeText(TrackTruckActivity.this,"without Call",Toast.LENGTH_LONG).show();
                    drawDashedPolyLine(googleMap, allTripFeed!!.oldLocationList!!, resources.getColor(R.color.red))
                    MarkerAdd()
                } else {
                    //Toast.makeText(TrackTruckActivity.this,"with Call",Toast.LENGTH_LONG).show();
                    DrowLineGoogleMap().execute()
                }
            }
        }, 500)
    }

    public override fun onResume() {
        super.onResume()  // Always call the superclass method first

        val filter = IntentFilter("come.naqil.naqil.TrackTruckActivity")

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Common.is_pusnotification = 1
            }
        }
        registerReceiver(receiver, filter)

        //        if(!FooterMapLayoutClick && animationStart){
        //            animationStart = false;
        //            DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
        //            anim.setDuration(50);
        //            animationStart = true;
        //            layout_main_drv_car.startAnimation(anim);
        //            layout_dirvier_car.setBackgroundResource(R.drawable.footer_bg_white);
        //        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val geocoder = Geocoder(this@TrackTruckActivity, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName("india", 1)

            if (addressList != null && addressList.size > 0) {
                val address = addressList[0]
                Log.d("addressList", "addressList = " + address.latitude + "==" + address.longitude)
                val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(address.latitude, address.longitude))      // Sets the center of the map to location user
                        .zoom(10f)                   // Sets the zoom
                        //.bearing(90)                // Sets the orientation of the camera to east
                        //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build()                   // Creates a CameraPosition from the builder
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        googleMap!!.setOnMapClickListener {
            // MapLayoutClick = true;
            // layout_main_drv_car.getLayoutParams().height = (int) getResources().getDimension(R.dimen.height_100);
        }

        googleMap!!.setOnMapLongClickListener {
            MapLayoutClick = true

            slideDownAnimation = AnimationUtils.loadAnimation(applicationContext,
                    R.anim.slide_down_map)
            layout_main_drv_car!!.startAnimation(slideDownAnimation)
            layout_main_linear.startAnimation(slideDownAnimation)
            FooterMapLayoutClick = false

            // layout_main_drv_car.startAnimation(slideDownAnimation);
            //                layout_booking_detail.setAlpha(0);
            //                layout_main_drv_car.setAlpha(0);
        }

        googleMap!!.setOnCameraMoveCanceledListener { Toast.makeText(this@TrackTruckActivity, "Zoom Cancel", Toast.LENGTH_LONG).show() }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> Handler().postDelayed({
                Log.d("MapLayoutClick", "MapLayoutClick Down= $MapLayoutClick==$FooterMapLayoutClick")
                //                        if (MapLayoutClick) {
                //                            layout_booking_detail.setAlpha(0);
                //                            layout_main_drv_car.setAlpha(0);
                //                        }
                if (FooterMapLayoutClick) {

                    FooterMapLayoutClick = false
                    val anim = DropDownAnim(layout_main_drv_car!!, (devise_height * 0.55).toInt(), animationStart)
                    anim.duration = 500
                    layout_main_drv_car!!.startAnimation(anim)
                    animationStart = true
                    layout_dirvier_car!!.setBackgroundResource(R.drawable.track_truck_footer_bg)

                }
            }, 20)
            MotionEvent.ACTION_UP -> Handler().postDelayed({
                Log.d("MapLayoutClick", "MapLayoutClick Up = $MapLayoutClick")
                if (MapLayoutClick) {
                    slideUpAnimation = AnimationUtils.loadAnimation(applicationContext,
                            R.anim.slide_up_map)
                    layout_main_drv_car!!.startAnimation(slideUpAnimation)
                    layout_main_linear.startAnimation(slideUpAnimation)
                    MapLayoutClick = false
                }
                //if(MapLayoutClick) {
                // layout_main_drv_car.setAlpha(1);
                // layout_booking_detail.setAlpha(1);
                //}
            }, 50)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun checkReady(): Boolean {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    inner class DropDownAnim(private val relativeLayout: RelativeLayout, private val targetHeight: Int, private val down: Boolean) : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val newHeight: Int
            if (down) {

                Log.d("newHeight", "newHeight one= $interpolatedTime")
                if (interpolatedTime.toDouble() != 0.0 && interpolatedTime >= 0.3) {
                    newHeight = (targetHeight * interpolatedTime).toInt()
                    Log.d("newHeight", "newHeight two= $interpolatedTime==$newHeight")
                } else {
                    newHeight = resources.getDimension(R.dimen.height_100).toInt()
                }
            } else {
                if (interpolatedTime <= 0.3) {
                    newHeight = (targetHeight * (1 - interpolatedTime)).toInt()
                } else {
                    newHeight = resources.getDimension(R.dimen.height_100).toInt()
                }
            }
            Log.d("newHeight", "newHeight three= $newHeight==$interpolatedTime")
            relativeLayout.layoutParams.height = newHeight
            relativeLayout.requestLayout()
            if (newHeight > targetHeight * 0.60)
                layout_location_heading!!.visibility = View.VISIBLE
            else
                layout_location_heading!!.visibility = View.GONE
            if (newHeight > targetHeight * 0.80) {
                layout_footer!!.visibility = View.VISIBLE
                FooterMapLayoutClick = true
                layout_white_main.isEnabled = true
                layout_dirvier_car!!.isEnabled = true
            } else {
                layout_footer!!.visibility = View.GONE
                FooterMapLayoutClick = false
            }
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int,
                                parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    inner class DrowLineGoogleMap : AsyncTask<String, Int, String>() {

        private var content: String? = null

        internal var DrowLocUrl = ""

        init {
            //DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+ URLEncoder.encode(txt_pickup_address.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(txt_drop_address.getText().toString(),"UTF-8");
            DrowLocUrl = "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=" + allTripFeed!!.startPickLatLng + "," + allTripFeed!!.endPickLatLng + "&destination=" + allTripFeed!!.startDropLatLng + "," + allTripFeed!!.endDropLatLng+"&key="+getString(R.string.google_server_key)

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
            val isStatus = Common.ShowHttpErrorMessage(this@TrackTruckActivity, result)
            if (isStatus) {
                try {
                    val resObj = JSONObject(result)
                    if (resObj.getString("status").toLowerCase() == "ok") {

                        val routArray = JSONArray(resObj.getString("routes"))
                        val routObj = routArray.getJSONObject(0)
                        Log.d("geoObj", "geoObj one= $routObj")
                        val legsArray = JSONArray(routObj.getString("legs"))
                        val legsObj = legsArray.getJSONObject(0)

                        val duration = JSONObject(legsObj.getString("duration"))
                        val distanceObj = JSONObject(legsObj.getString("distance"))

                        val durText = duration.getString("text")
                        val durTextSpi = durText.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
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

                        /*Start Location Latitude Longitude*/

                        val polylineOptions = PolylineOptions()
                        polylineOptions.color(resources.getColor(R.color.red))
                        polylineOptions.width(5f)
                        polylineOptions.visible(true)


                        val endObj = JSONObject(legsObj.getString("end_location"))
                        allTripFeed!!.dropLarLng = LatLng(java.lang.Double.parseDouble(endObj.getString("lat")), java.lang.Double.parseDouble(endObj.getString("lng")))

                        //arrayPoints.add(new LatLng(Endlat, Endlng));

                        //options.add(PickupLarLng);
                        if (arrayPoints!!.size > 0)
                            arrayPoints!!.clear()


                        val strLocObj = JSONObject(legsObj.getString("start_location"))
                        allTripFeed!!.pickupLarLng = LatLng(java.lang.Double.parseDouble(strLocObj.getString("lat")), java.lang.Double.parseDouble(strLocObj.getString("lng")))
                        allTripFeed!!.driverLarLng = LatLng(java.lang.Double.parseDouble(strLocObj.getString("lat")), java.lang.Double.parseDouble(strLocObj.getString("lng")))
                        arrayPoints!!.add(LatLng(java.lang.Double.parseDouble(strLocObj.getString("lat")), java.lang.Double.parseDouble(strLocObj.getString("lng"))))
                        val stepsArray = JSONArray(legsObj.getString("steps"))
                        Log.d("Step Array Length", "Step Array Length = " + stepsArray.length())
                        for (si in 0 until stepsArray.length()) {

                            val strObj = stepsArray.getJSONObject(si)

                            /*Start Location Latitude Longitude*/
                            val StrLocObj = JSONObject(strObj.getString("start_location"))
                            val startlat = java.lang.Double.parseDouble(StrLocObj.getString("lat"))
                            val startlng = java.lang.Double.parseDouble(StrLocObj.getString("lng"))

                            arrayPoints!!.add(LatLng(startlat, startlng))

                            /*Start Location Latitude Longitude*/
                            val EndLocObj = JSONObject(strObj.getString("end_location"))
                            val endlat = java.lang.Double.parseDouble(EndLocObj.getString("lat"))
                            val endlng = java.lang.Double.parseDouble(EndLocObj.getString("lng"))

                            arrayPoints!!.add(LatLng(endlat, endlng))
                        }

                        allTripFeed!!.oldLocationList = arrayPoints
                        markerPositon = arrayPoints!!.size - 1

                        // MarkerMoveTimer(arrayPoints.size() * 2000);
                        MarkerAdd()
                        //polylineOptions.addAll(arrayPoints);
                        // googleMap.addPolyline(polylineOptions);

                        /*End Location Latitude Longitude*/
                        //options.add(DropLarLng);

                        drawDashedPolyLine(googleMap, arrayPoints!!, resources.getColor(R.color.red))


                    } else {
                        Toast.makeText(this@TrackTruckActivity, resources.getString(R.string.location_long), Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }


    /*Add marker function*/
    fun MarkerAdd() {

        if (checkReady()) {

            val builder = LatLngBounds.Builder()

            if (allTripFeed!!.pickupLarLng != null) {
                markerOption = MarkerOptions()
                        .position(allTripFeed!!.pickupLarLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point))
                googleMap!!.addMarker(markerOption)
                builder.include(markerOption.position)

            }

            if (allTripFeed!!.dropLarLng != null) {
                markerOption = MarkerOptions()
                        .position(allTripFeed!!.dropLarLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point))
                googleMap!!.addMarker(markerOption)
                builder.include(markerOption.position)

                DriverMarkerOption = MarkerOptions()
                        .position(allTripFeed!!.driverLarLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                DriverMarker = googleMap!!.addMarker(DriverMarkerOption)
                builder.include(DriverMarker!!.position)

            }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if (allTripFeed!!.driverLarLng != null || allTripFeed!!.pickupLarLng != null) {
                val bounds = builder.build()

                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300))
                if (areBoundsTooSmall(bounds, 300)) {
                    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 10f))
                } else {

                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 50)
                    googleMap!!.animateCamera(cu, object : GoogleMap.CancelableCallback {

                        override fun onFinish() {
                            val zout = CameraUpdateFactory.zoomBy((-1.0).toFloat())
                            googleMap!!.animateCamera(zout)
                        }

                        override fun onCancel() {
                            //                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            //                            googleMap.animateCamera(zout);
                        }
                    })

                }
            }

            googleMap!!.setOnCameraChangeListener(object : GoogleMap.OnCameraChangeListener {

                private var currentZoom = -1f

                override fun onCameraChange(pos: CameraPosition) {
                    if (pos.zoom != currentZoom) {
                        currentZoom = pos.zoom
                        googleZoom = currentZoom
                        Log.d("currentZoom", "currentZoom = $currentZoom")
                        // do you action here
                    }
                }
            })

            Log.d("googleZoom", "googleZoom = $googleZoom")

            // googleZoom = googleMap.getCameraPosition().zoom;
        }
    }

    fun WayMarker(WayDriverLarLng: LatLng, booking_status: String?) {
        if (DriverMarker != null)
            DriverMarker!!.remove()
        if (checkReady()) {
            val builder = LatLngBounds.Builder()
            DriverMarker = googleMap!!.addMarker(MarkerOptions()
                    .position(WayDriverLarLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)))
            builder.include(DriverMarker!!.position)

            val bounds = builder.build()

            Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + googleZoom + "==" + areBoundsTooSmall(bounds, 300))
            //if (areBoundsTooSmall(bounds, 300)) {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, googleZoom))
            /*} else {

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }

                    @Override
                    public void onCancel() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }
                });

            }*/
        }


        Log.d("DriverLarLng", "DriverLarLng = " + allTripFeed!!.driverLarLng!!.latitude + "==" + allTripFeed!!.driverLarLng!!.longitude + "==" + WayDriverLarLng.latitude + "==" + WayDriverLarLng.longitude)
        Log.d("booking_status", "booking_status = " + booking_status!!)
        if (booking_status != null && booking_status != "" && booking_status == "begin trip") {
            Log.d("LatLngCom", "LatLngCom = $LatLngCom")
            //            if(LatLngCom == 0)
            //                new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),WayDriverLarLng).execute();

            println(getDistance(LatLng(OldLatLog.latitude, OldLatLog.longitude), LatLng(WayDriverLarLng.latitude, WayDriverLarLng.longitude)).toString() + " Metre Nautical Miles Test\n")

            CoverdValue = CoverdValue + getDistance(LatLng(OldLatLog.latitude, OldLatLog.longitude), LatLng(WayDriverLarLng.latitude, WayDriverLarLng.longitude))

            val TotalKmMet = (java.lang.Double.parseDouble(allTripFeed!!.km!!) * 1000).toFloat()
            if (TotalKmMet > CoverdValue) {
                var FinalRemValue = TotalKmMet - CoverdValue

                var RemValueStr = ""
                if (FinalRemValue > 1000) {
                    FinalRemValue = FinalRemValue / 1000
                    //System.out.println();
                    RemValueStr = FinalRemValue.toString() + ""
                    RemValueStr = String.format("%.2f", java.lang.Float.parseFloat(RemValueStr)) + resources.getString(R.string.km)
                } else {
                    RemValueStr = FinalRemValue.toString() + ""
                    RemValueStr = String.format("%.2f", java.lang.Float.parseFloat(RemValueStr)) + " M"
                }
                txt_remaining_val!!.text = RemValueStr
                Log.d("CoverdValue ", "CoverdValue =$CoverdValue==$TotalKmMet==$RemValueStr")
            }

            var CoverValueStr = ""
            if (CoverdValue > 1000) {
                val CoverValueKm = CoverdValue / 1000
                //System.out.println();
                CoverValueStr = CoverValueKm.toString() + ""
                CoverValueStr = String.format("%.2f", java.lang.Float.parseFloat(CoverValueStr)) + resources.getString(R.string.km)
            } else {
                CoverValueStr = CoverdValue.toString() + ""
                CoverValueStr = String.format("%.2f", java.lang.Float.parseFloat(CoverValueStr)) + " M"
            }
            Log.d("CoverdValue ", "CoverdValue =$CoverdValue==$TotalKmMet==$CoverValueStr")
            txt_covered_val!!.text = CoverValueStr

            OldLatLog = WayDriverLarLng
        }

    }

    fun MarkerMoveTimer(totalTimer: Int) {
        object : CountDownTimer(totalTimer.toLong(), 2000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            override fun onTick(millisUntilFinished: Long) {

                Log.d("arrayPoints", "arrayPoints = " + arrayPoints!!.size + "==" + markerPositon)
                val latLng = arrayPoints!![markerPositon]
                WayMarker(latLng, "begin trip")

                Log.d("LatLngCom", "LatLngCom = $LatLngCom")
                //                if(LatLngCom == 0)
                //                    new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),latLng).execute();

                //System.out.println(distance(OldLatLog.latitude,OldLatLog.longitude,latLng.latitude,latLng.longitude, "K") + " Kilometers\n");
                println(getDistance(LatLng(OldLatLog.latitude, OldLatLog.longitude), LatLng(latLng.latitude, latLng.longitude)).toString() + " Metre Nautical Miles Test\n")

                CoverdValue = CoverdValue + getDistance(LatLng(OldLatLog.latitude, OldLatLog.longitude), LatLng(latLng.latitude, latLng.longitude))

                val TotalKmMet = (java.lang.Double.parseDouble(allTripFeed!!.km!!) * 1000).toFloat()
                var FinalRemValue = TotalKmMet - CoverdValue

                var RemValueStr = ""
                if (FinalRemValue > 1000) {
                    FinalRemValue = FinalRemValue / 1000
                    //System.out.println();
                    RemValueStr = FinalRemValue.toString() + ""
                    RemValueStr = String.format("%.2f", java.lang.Float.parseFloat(RemValueStr)) + resources.getString(R.string.km)
                } else {
                    RemValueStr = FinalRemValue.toString() + ""
                    RemValueStr = String.format("%.2f", java.lang.Float.parseFloat(RemValueStr)) + " M"
                }

                var CoverValueStr = ""
                if (CoverdValue > 1000) {
                    val CoverValueKm = CoverdValue / 1000
                    //System.out.println();
                    CoverValueStr = CoverValueKm.toString() + ""
                    CoverValueStr = String.format("%.2f", java.lang.Float.parseFloat(CoverValueStr)) + resources.getString(R.string.km)
                } else {
                    CoverValueStr = CoverdValue.toString() + ""
                    CoverValueStr = String.format("%.2f", java.lang.Float.parseFloat(CoverValueStr)) + " M"
                }
                Log.d("CoverdValue ", "CoverdValue =$CoverdValue==$TotalKmMet==$RemValueStr==$CoverValueStr")
                txt_covered_val!!.text = CoverValueStr
                txt_remaining_val!!.text = RemValueStr
                OldLatLog = latLng

                markerPositon = markerPositon - 1
            }

            override fun onFinish() {
                val latLng = arrayPoints!![0]
                WayMarker(latLng, "begin trip")
            }
        }.start()

    }

    fun drawDashedPolyLine(mMap: GoogleMap?, listOfPoints: ArrayList<LatLng>, color: Int) {
        /* Boolean to control drawing alternate lines */
        var added = false
        Log.d("listOfPoints", "listOfPoints = " + listOfPoints.size)
        for (i in 0 until listOfPoints.size - 1) {

            /* Get distance between current and next point */
            val distance = getConvertedDistance(listOfPoints[i], listOfPoints[i + 1])

            /* If distance is less than 0.020 kms */
            if (distance < 0.020) {
                if (!added) {
                    mMap!!.addPolyline(PolylineOptions()
                            .add(listOfPoints[i])
                            .add(listOfPoints[i + 1])
                            .color(color))
                    added = true
                } else {/* Skip this piece */
                    added = false
                }
            } else {
                /* Get how many divisions to make of this line */
                val countOfDivisions = (distance / 0.020).toInt()

                /* Get difference to add per lat/lng */
                val latdiff = (listOfPoints[i + 1].latitude - listOfPoints[i].latitude) / countOfDivisions
                val lngdiff = (listOfPoints[i + 1].longitude - listOfPoints[i].longitude) / countOfDivisions

                /* Last known indicates start point of polyline. Initialized to ith point */
                var lastKnowLatLng = LatLng(listOfPoints[i].latitude, listOfPoints[i].longitude)
                for (j in 0 until countOfDivisions) {

                    /* Next point is point + diff */
                    val nextLatLng = LatLng(lastKnowLatLng.latitude + latdiff, lastKnowLatLng.longitude + lngdiff)
                    if (!added) {
                        mMap!!.addPolyline(PolylineOptions()
                                .add(lastKnowLatLng)
                                .add(nextLatLng)
                                .color(color))
                        added = true
                    } else {
                        added = false
                    }
                    lastKnowLatLng = nextLatLng
                }
            }
        }
    }

    private fun getConvertedDistance(latlng1: LatLng, latlng2: LatLng): Double {
        val distance = DistanceUtil.distance(latlng1.latitude,
                latlng1.longitude,
                latlng2.latitude,
                latlng2.longitude)
        val bd = BigDecimal(distance)
        val res = bd.setScale(3, RoundingMode.DOWN)
        return res.toDouble()
    }

    private fun areBoundsTooSmall(bounds: LatLngBounds, minDistanceInMeter: Int): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result)
        return result[0] < minDistanceInMeter
    }

    public override fun onDestroy() {
        super.onDestroy()
        txt_track_truck = null
        txt_month = null
        txt_date = null
        txt_locaton = null
        txt_point_val = null
        txt_remaining = null
        txt_remaining_val = null
        txt_covered = null
        txt_covered_val = null
        txt_total_km = null
        txt_total_km_val = null
        layout_main_drv_car = null
        layout_location_heading = null
        layout_footer = null
        layout_dirvier_car = null
        layout_booking_detail = null
        txt_driver_name = null
        txt_driver_other = null
        txt_mob_num = null
        txt_car_name = null
        txt_car_model = null
        txt_number_plate = null
        txt_pickup_address = null
        txt_drop_address = null
        txt_contact = null
        txt_share_eta = null
        txt_cancel = null
        layout_back_arrow = null

        mSocket!!.disconnect()

        unregisterReceiver(receiver)
    }


    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {

        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515
        if (unit === "K") {
            dist *= 1.609344
        } else if (unit === "N") {
            dist *= 0.8684
        }

        return dist
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }


    private fun getDistance(my_latlong: LatLng, frnd_latlong: LatLng): Double {
        val l1 = Location("One")
        l1.latitude = my_latlong.latitude
        l1.longitude = my_latlong.longitude

        val l2 = Location("Two")
        l2.latitude = frnd_latlong.latitude
        l2.longitude = frnd_latlong.longitude

        var distance = l1.distanceTo(l2)
        var dist = distance

        if (distance > 1000.0f) {
            distance = distance / 1000.0f
            dist = distance
        }
        return dist.toDouble()
    }


    inner class DistanceTwoLatLng(DriverLatLng: LatLng, WayDriverLarLng: LatLng) : AsyncTask<String, Int, String>() {

        private var content: String? = null

        internal var LatLonUrl = ""

        init {
            LatLngCom = 1
            LatLonUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + DriverLatLng.latitude + "," + DriverLatLng.longitude + "&destinations=" + WayDriverLarLng.latitude + "," + WayDriverLarLng.longitude + "&mode=driving"
        }

        override fun onPreExecute() {
            //Toast.makeText(TrackTruckActivity.this,"Start",Toast.LENGTH_LONG).show();
        }

        override fun doInBackground(vararg params: String): String? {
            try {
                val client = DefaultHttpClient()
                val HttpParams = client.params
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000)
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000)
                Log.d("DrowLocUrl", "DrowLocUrl = $LatLonUrl")
                val getMethod = HttpGet(LatLonUrl)
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
            //Toast.makeText(TrackTruckActivity.this,"End = "+result,Toast.LENGTH_LONG).show();
            LatLngCom = 0
            val isStatus = Common.ShowHttpErrorMessage(this@TrackTruckActivity, result)
            if (isStatus) {
                try {

                    val resObj = JSONObject(result)
                    if (resObj.getString("status").toLowerCase() == "ok") {

                        val rowArray = JSONArray(resObj.getString("rows"))
                        for (li in 0 until rowArray.length()) {
                            val eleObj = rowArray.getJSONObject(li)

                            val eleArray = JSONArray(eleObj.getString("elements"))
                            for (ei in 0 until eleArray.length()) {

                                val elementObj = eleArray.getJSONObject(li)
                                Log.d("Status", "Status = " + elementObj.getString("status"))
                                if (elementObj.getString("status") == "OK") {

                                    val distanceObj = JSONObject(elementObj.getString("distance"))

                                    val DistanceVal = Integer.parseInt(distanceObj.getString("value")) / 1000
                                    Log.d("DistanceVal", "DistanceVal$DistanceVal")
                                    if (txt_covered_val != null)
                                        txt_covered_val!!.text = "$DistanceVal Km"


                                    val remDis = java.lang.Double.parseDouble(allTripFeed!!.km!!) - DistanceVal
                                    if (txt_remaining_val != null)
                                        txt_remaining_val!!.setText(String.format("%.2f Km", remDis))
                                }
                            }
                        }


                    } else {
                        Toast.makeText(this@TrackTruckActivity, resources.getString(R.string.location_long), Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }
}