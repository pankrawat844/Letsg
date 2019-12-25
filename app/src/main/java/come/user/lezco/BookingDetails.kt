package come.user.lezco

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gocashfree.cashfreesdk.CFPaymentService
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.koushikdutta.ion.Ion
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.api.CashfreeApi
import come.user.lezco.api.SmsApi
import come.user.lezco.model.DriverFilter
import come.user.lezco.model.NeareastDriver
import come.user.lezco.utils.Common
import come.user.lezco.utils.Constants
import come.user.lezco.utils.Url
import kotlinx.android.synthetic.main.activity_booking_details.*
import kotlinx.android.synthetic.main.bottomsheet_otp.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.random.Random
import kotlin.random.nextInt

class BookingDetails : AppCompatActivity(), OnMapReadyCallback {
    private var BookingType: String? = null
    private var transfertype: String? = null
    private var CabId: String? = null
    private var AreaId: String? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var token: String
    var listLatLng = ArrayList<LatLng?>()
    var marker: MarkerOptions? = null
    var PickupLarLng: LatLng? = null
    var DropLarLng: LatLng? = null
    internal var PickupMarker: Marker? = null
    internal var DropMarker: Marker? = null
    var DropLongtude: Double = 0.toDouble()
    var DropLatitude: Double = 0.toDouble()
    var PickupLongtude: Double = 0.toDouble()
    var PickupLatitude: Double = 0.toDouble()
    lateinit var userPref: SharedPreferences
    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading
    lateinit var DayNight: String
    lateinit var drop_point: String
    lateinit var pickup_point: String
    lateinit var  totlePrice: String
    lateinit var truckType: String
    lateinit var transaction_id: String
    var astTime: String? = ""
    var user_id: String? = ""
    var user_name: String = ""
    var driver_id:String?=""
    lateinit var one_type_password: String
    lateinit var otp_bottomsheet: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)
        if (intent.hasExtra("data"))
            updateUI(intent.getParcelableExtra("data") as DriverFilter.Data)
        else
            updateNearest(intent.getParcelableExtra("nearest_data") as NeareastDriver.Data)
        userPref = PreferenceManager.getDefaultSharedPreferences(this@BookingDetails)
        ProgressDialog = Dialog(this@BookingDetails, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        PickupLatitude = intent.getDoubleExtra("pick_lat", 0.00)
        PickupLongtude = intent.getDoubleExtra("pick_lon", 0.00)
        DropLatitude = intent.getDoubleExtra("drop_lat", 0.00)
        DropLongtude = intent.getDoubleExtra("drop_lon", 0.00)
        if (intent.hasExtra("DayNight"))
            DayNight = intent.getStringExtra("DayNight")
        if (intent.hasExtra("drop_point"))
            drop_point = intent.getStringExtra("drop_point")
        if (intent.hasExtra("pickup_point"))
            pickup_point = intent.getStringExtra("pickup_point")
        if (intent.hasExtra("totlePrice"))
            totlePrice = intent.getStringExtra("totlePrice")
        if (intent.hasExtra("truckType"))
            truckType = intent.getStringExtra("truckType")
        if (intent.hasExtra("CabId"))
            CabId = intent.getStringExtra("CabId")
        if (intent.hasExtra("transfertype"))
            transfertype = intent.getStringExtra("transfertype")
        if (intent.hasExtra("BookingType"))
            BookingType = intent.getStringExtra("BookingType")
        if (intent.hasExtra("astTime"))
            BookingType = intent.getStringExtra("astTime")


        PickupLarLng = LatLng(PickupLatitude, PickupLongtude)
        DropLarLng = LatLng(DropLatitude, DropLongtude)
        edit_address.text = drop_point
        mapFragment.getMapAsync(this)
        if (userPref.contains("isLogin")) {
            if (userPref.getString("isLogin", "").equals("1")) {
                name_layout.visibility = View.GONE
                phone_layout.visibility = View.GONE
            }
        }
        otp_bottomsheet()
        payment_btn.setOnClickListener {
            if (spinner_payment.selectedItem.toString() == "Online") {
                if (userPref.contains("isLogin")) {
                    user_id = userPref.getString("id", "")
                    user_name = userPref.getString("username", "")!!
                    val jsonObject = JSONObject()
                    transaction_id = "Order" + System.currentTimeMillis().toString()
                    jsonObject.put("orderId", transaction_id)
                    jsonObject.put("orderAmount", totlePrice)
                    jsonObject.put("orderCurrency", "INR")
                    val body: RequestBody = RequestBody.create(
                        okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        jsonObject.toString()
                    );

                    Log.e("dfsd", jsonObject.toString())
                    CashfreeApi().getToken(body).enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(
                                this@BookingDetails,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                cashFreeApi(response)
                            } else
                                Toast.makeText(
                                    this@BookingDetails,
                                    response.errorBody()!!.string(),
                                    Toast.LENGTH_SHORT
                                ).show()
                        }

                    })
                } else {
                    if (edit_name.text.isNullOrEmpty() || edit_mobile.text.isNullOrEmpty()) {
                        Toast.makeText(
                            this@BookingDetails,
                            "Both Fields Are Mandatory.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        user_id = ""
                        user_name = edit_mobile.text.toString()
                        transaction_id = "Order" + System.currentTimeMillis().toString()
                        user_id = userPref.getString("id", "")
                        user_name = userPref.getString("username", "")!!
                        val jsonObject = JSONObject()
                        transaction_id = "Order" + System.currentTimeMillis().toString()
                        jsonObject.put("orderId", transaction_id)
                        jsonObject.put("orderAmount", totlePrice)
                        jsonObject.put("orderCurrency", "INR")
                        val body: RequestBody = RequestBody.create(
                            okhttp3.MediaType.parse("application/json; charset=utf-8"),
                            jsonObject.toString()
                        );

                        Log.e("dfsd", jsonObject.toString())
                        CashfreeApi().getToken(body).enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(
                                    this@BookingDetails,
                                    t.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    cashFreeApi(response)
                                } else
                                    Toast.makeText(
                                        this@BookingDetails,
                                        response.errorBody()!!.string(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }

                        })
                    }
                }
            } else {
                if (userPref.contains("isLogin")) {
                    if (userPref.getString("isLogin", "").equals("1")) {
                        user_id = userPref.getString("id", "")
                        user_name = userPref.getString("username", "")!!
                        transaction_id = "Order" + System.currentTimeMillis().toString()
                        bookConfirmation()
                    }
                } else {
                    if (edit_name.text.isNullOrEmpty() || edit_mobile.text.isNullOrEmpty()) {
                        Toast.makeText(
                            this@BookingDetails,
                            "Both Fields Are Mandatory.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (otp_bottomsheet.state != BottomSheetBehavior.STATE_EXPANDED) {

                            one_type_password = Random.nextInt(1000..9999).toString()
                            otp_bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
                            otp_bottomsheet.isHideable = true
                            if (Common.isNetworkAvailable(this))
                                SmsApi().otp_send(
                                    getString(R.string.sms_gateway),
                                    "LEZCAB",
                                    "91" + edit_mobile.text.toString(),
                                    "Hi " + edit_name.text.toString() + ", your one time password is " + one_type_password + ".",
                                    "json"
                                ).enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.e("response", t.message.toString())

                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Log.e("response", response.body()?.string())
                                    }

                                })
                            else
                                Toast.makeText(
                                    this,
                                    "Please Check Your Internet Connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
//
                    }
                }
            }
        }
    }

    private fun otp_bottomsheet() {
        otp_bottomsheet = BottomSheetBehavior.from(bottom_sheet_otp)
        otp_bottomsheet.state = BottomSheetBehavior.STATE_HIDDEN
        otp_bottomsheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
            }

        })

        bottom_sheet_otp_nxt.setOnClickListener {
            if (one_type_password.equals(otp.text.toString())) {

                user_id = ""
                user_name = edit_mobile.text.toString()
                transaction_id = "Order" + System.currentTimeMillis().toString()
                if (Common.isNetworkAvailable(this))
                    bookConfirmation()
                else
                    Toast.makeText(
                        this@BookingDetails,
                        "Please Check Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun cashFreeApi(response: Response<ResponseBody>) {
        try {
            // Log.e("result", response.body()!!.string())
            val json = response.body()!!.string()
            Log.e("json", json)
            val token = JSONObject(json).getString("cftoken")
            val stage = "TEST"
            val appId = "8602ff415d26ae4a703315422068"
            val orderId = transaction_id
            val orderAmount = totlePrice
            val orderNote = ""
            val customerName = user_name
            var mobile:String?=null
            if (userPref.contains("mobile"))
                mobile = userPref.getString("mobile", "")
            else
                mobile = edit_mobile.text.toString()
            var customerEmail:String? =null
                if (userPref.contains("email"))
                    customerEmail = userPref.getString("email", "")
            else
                    customerEmail = edit_address.text.toString()

            val params: HashMap<String, String> = HashMap<String, String>()
            params.put(Constants.PARAM_APP_ID, appId)
            params.put(Constants.PARAM_ORDER_ID, orderId)
            params.put(Constants.PARAM_ORDER_AMOUNT, orderAmount)
            params.put(Constants.PARAM_ORDER_NOTE, orderNote)
            params.put(Constants.PARAM_CUSTOMER_NAME, customerName)
            params.put(Constants.PARAM_CUSTOMER_PHONE, mobile!!)
            params.put(Constants.PARAM_CUSTOMER_EMAIL, customerEmail!!)
            params.put("card_id", "asdfads")
            params.put("paymentModes", "cc, dc, nb, upi, wallet")
            for (entry in params.entries) {
                Log.d("CFSKDSample", entry.key + " " + entry.value)
            }
            val cfPaymentService =
                CFPaymentService.getCFPaymentServiceInstance()
            cfPaymentService.setOrientation(this@BookingDetails, 0)
            cfPaymentService.setConfirmOnExit(this@BookingDetails, true)
            cfPaymentService.doPayment(
                this@BookingDetails,
                params,
                token,
                stage
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUI(data: DriverFilter.Data) {
        driver_id=data.id
        driver_name.text = data.name
        Glide.with(this).load(Url.carImageUrl + data.icon).into(car_img)
        Glide.with(this).load(Url.DriverImageUrl + data.image).into(driver_img)
        driver_rating.rating = data.rating?.toFloat()!!
        car_name.text = data.cartype
        car_number.text = data.carNo


    }

    private fun updateNearest(data: NeareastDriver.Data) {
        driver_id=data.id
        driver_name.text = data.name
        Glide.with(this).load(Url.carImageUrl + data.icon).into(car_img)
        Glide.with(this).load(Url.DriverImageUrl + data.image).into(driver_img)
        driver_rating.rating = data.rating?.toFloat()!!
        car_name.text = data.cartype
        car_number.text = data.car_no

        distance.text = TimeUnit.SECONDS.toMinutes(data.duration.toLong()).toString() + " Min Away"
        val currenttime = Calendar.getInstance()
        currenttime.add(Calendar.MINUTE, TimeUnit.SECONDS.toMinutes(data.duration.toLong()).toInt())
        var hour = currenttime.get(Calendar.HOUR_OF_DAY)
        var min = currenttime.get(Calendar.MINUTE)

        estimate_time.text = "Arrived By " + hour + ":" + min

    }

    private fun bookConfirmation() {
        var mobile: String? = ""
        if (userPref.contains("mobile"))
            mobile = userPref.getString("mobile", "")
        else
            mobile = edit_mobile.text.toString()

        val date = Calendar.getInstance()
        ProgressDialog.show()
        cusRotateLoading.start()
        Ion.with(this@BookingDetails)
            .load(Url.bookCabUrl)
            .setTimeout(10000)
            //.setJsonObjectBody(json)
            .setMultipartParameter("user_id", user_id)
            .setMultipartParameter("username", user_name)
            .setMultipartParameter(
                "pickup_date_time",
                date.get(Calendar.DATE).toString() + "/" + date.get(Calendar.MONTH).toString() + "/" + date.get(
                    Calendar.YEAR
                )
            )
            .setMultipartParameter("drop_area", drop_point)
            .setMultipartParameter("pickup_area", pickup_point)
            .setMultipartParameter("time_type", DayNight)
            .setMultipartParameter("amount", totlePrice)
            .setMultipartParameter("km", distance.toString())
            .setMultipartParameter("pickup_lat", PickupLatitude.toString())
            .setMultipartParameter("pickup_longs", PickupLongtude.toString())
            .setMultipartParameter("drop_lat", DropLatitude.toString())
            .setMultipartParameter("drop_longs", DropLongtude.toString())
            .setMultipartParameter("isdevice", "1")
            .setMultipartParameter("flag", "0")
            .setMultipartParameter("taxi_type", truckType)
            .setMultipartParameter("taxi_id", CabId)
            .setMultipartParameter("area_id", AreaId)
            .setMultipartParameter("purpose", transfertype)
            .setMultipartParameter("comment", intent.getStringExtra("comment"))
            .setMultipartParameter("person", "1")
            .setMultipartParameter("payment_type", spinner_payment.selectedItem.toString())
            .setMultipartParameter("book_create_date_time", BookingType)
            .setMultipartParameter("transaction_id", transaction_id)
//            .setMultipartParameter("approx_time", distance.text.toString().replace(" Min Away",""))
            .setMultipartParameter("approx_time", distance.text.toString().replace(" Away", ""))
            .setMultipartParameter("mobile", mobile)
            .setMultipartParameter("driver_id",driver_id)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error
                Log.d("Booking result", "Booking result = $result==$error")
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                if (error == null) {

                    try {
                        val resObj = JSONObject(result.toString())
                        Log.d("Booking result", "Booking result = $resObj")
                        if (resObj.getString("status") == "success") {
                            //Common.showMkSucess(TripDetailActivity.this, resObj.getString("message").toString(), "yes");
                            Handler().postDelayed({
                                val ai = Intent(this@BookingDetails, AllTripActivity::class.java)
                                startActivity(ai)
                                finish()
                            }, 500)
                        } else if (resObj.getString("status") == "false") {

                            Common.user_InActive = 1
                            Common.InActive_msg = resObj.getString("message")

                            val editor = userPref.edit()
                            editor.clear()
                            editor.commit()
                            val logInt = Intent(this@BookingDetails, LoginOptionActivity::class.java)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(logInt)
                        } else {
                            Common.showMkError(
                                this@BookingDetails,
                                resObj.getString("error code").toString()
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    Common.ShowHttpErrorMessage(this@BookingDetails, error.message)
                }
            }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings?.isMyLocationButtonEnabled = true
        try {
            val res: Boolean =
                googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!res)
                Log.e("TAG", "Style parsing failed.")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        CaculationDirationIon()
        if (mapFragment.view!!.findViewById<View>(Integer.parseInt("1")) != null) {
            val locationButton: ImageView =
                (mapFragment.view!!.findViewById<View>(Integer.parseInt("1")).getParent() as View).findViewById<View>(
                    Integer.parseInt("2")
                ) as ImageView
            locationButton.setImageResource(R.drawable.ic_location)
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 100, 200)
        }

    }

    fun CaculationDirationIon() {
        var CaculationLocUrl = ""
        listLatLng.clear()
        CaculationLocUrl =
            "https://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=$PickupLatitude,$PickupLongtude&destination=$DropLatitude,$DropLongtude&key=" + getString(
                R.string.google_server_key
            )
        Log.d("CaculationLocUrl", "CaculationLocUrl = $CaculationLocUrl")
        Ion.with(this@BookingDetails)
            .load(CaculationLocUrl)
            .setTimeout(10000)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error


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


                        } else {
                            Toast.makeText(
                                this@BookingDetails,
                                resources.getString(R.string.location_long),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Common.ShowHttpErrorMessage(this@BookingDetails, error.message)
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Same request code for all payment APIs.
        Log.d(
            "sample.cfsdksample",
            "ReqCode : " + CFPaymentService.REQ_CODE
        )
        Log.d("sample.cfsdksample", "API Response : ")
        //Prints all extras. Replace with app logic.
        if (data != null) {
            val bundle = data.extras
            if (bundle != null) for (key in bundle.keySet()) {
                if (bundle.getString(key) != null) {
                    Log.d(
                        "response",
                        key + " : " + bundle.getString(key)
                    )
                    if(key=="txStatus")
                        if(bundle.getString(key)=="SUCCESS")
                             bookConfirmation()
                        else
                        Common.showMkError(this, bundle.getString("txMsg")!!)
                }
            }
        }
    }

    private fun checkReady(): Boolean {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

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
//
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
                val cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 50)
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
}
