package come.user.lezco

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.app.facebooklibrary.FBBean
import com.app.facebooklibrary.FB_Callback
import com.app.facebooklibrary.FacebookLoginClass
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.utils.AllTripFeed
import come.user.lezco.utils.CircleTransform
import come.user.lezco.utils.Common
import come.user.lezco.utils.Url
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailActivity : AppCompatActivity(), FB_Callback {

    internal var txt_booking_id: TextView? = null
    lateinit var txt_cancel_request: TextView
    internal var txt_booking_id_val: TextView? = null
    internal var txt_pickup_point: TextView? = null
    internal var txt_pickup_point_val: TextView? = null
    internal var txt_booking_date: TextView? = null
    internal var txt_drop_point: TextView? = null
    internal var txt_drop_point_val: TextView? = null
    internal var img_car_image: ImageView? = null
    internal var txt_distance: TextView? = null
    internal var txt_distance_val: TextView? = null
    internal var txt_distance_km: TextView? = null
    internal var txt_total_price: TextView? = null
    internal var txt_total_price_dol: TextView? = null
    internal var txt_total_price_val: TextView? = null
    internal var txt_booking_detail: TextView? = null
    internal var txt_track_truck: TextView? = null
    internal var layout_back_arrow: RelativeLayout? = null
    internal var layout_track_truck: RelativeLayout? = null
    lateinit var layout_car_detail: LinearLayout
    lateinit var layout_driver_detail: LinearLayout
    lateinit var txt_truct_type_val: TextView
    lateinit var layout_cancel_request_button: RelativeLayout
    lateinit var img_driver_image: ImageView
    lateinit var txt_driver_name: TextView
    lateinit var txt_drv_trc_typ: TextView
    lateinit var txt_num_plate: TextView
    lateinit var txt_mobile_num: TextView
    lateinit var txt_lic_num: TextView
    lateinit var scroll_view: ScrollView
    lateinit var layout_accepted: LinearLayout
    lateinit var layout_accepted_call: RelativeLayout
    lateinit var layout_completed: LinearLayout
    lateinit var layout_cancel_user: LinearLayout
    lateinit var layout_on_trip: LinearLayout
    lateinit var layout_driver_unavailabel: LinearLayout
    lateinit var layout_cancel_driver: LinearLayout
    lateinit var layout_pending: RelativeLayout
    lateinit var txt_travel_time: TextView
    lateinit var txt_travel_time_val: TextView
    lateinit var txt_to: TextView
    lateinit var txt_vehicle_detail: TextView
    lateinit var txt_payment_detail: TextView

    lateinit var layout_accepted_share_eta: RelativeLayout
    lateinit var layout_completed_eta: RelativeLayout
    lateinit var layout_completed_eta_chield: RelativeLayout
    lateinit var layout_share_on_trip: RelativeLayout
    lateinit var layout_share_driver_unavailabel: RelativeLayout
    lateinit var layout_share_cancel_driver: RelativeLayout

    lateinit var OpenSans_Regular: Typeface
    lateinit var Roboto_Regular: Typeface
    lateinit var Roboto_Medium: Typeface
    lateinit var Roboto_Bold: Typeface
    lateinit var OpenSans_Semibold: Typeface

    internal var allTripFeed: AllTripFeed? = null
    lateinit var userPref: SharedPreferences

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    lateinit var chk_drive_late: CheckBox
    lateinit var chk_changed_mind: CheckBox
    lateinit var chk_another_cab: CheckBox
    lateinit var chk_denied_duty: CheckBox

    internal var DriverPhNo = ""
    lateinit var receiver: BroadcastReceiver

    lateinit var callbackManager: CallbackManager

    lateinit var ShareDialog: Dialog

    lateinit var ShareDesc: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_booking_detail)

        callbackManager = CallbackManager.Factory.create()

        txt_cancel_request = findViewById(R.id.txt_cancel_request) as TextView
        txt_booking_id = findViewById(R.id.txt_booking_id) as TextView?
        txt_booking_id_val = findViewById(R.id.txt_booking_id_val) as TextView?
        txt_pickup_point = findViewById(R.id.txt_pickup_point) as TextView?
        txt_pickup_point_val = findViewById(R.id.txt_pickup_point_val) as TextView?
        txt_booking_date = findViewById(R.id.txt_booking_date) as TextView?
        txt_drop_point = findViewById(R.id.txt_drop_point) as TextView?
        txt_drop_point_val = findViewById(R.id.txt_drop_point_val) as TextView?
        img_car_image = findViewById(R.id.img_car_image) as ImageView?
        txt_distance = findViewById(R.id.txt_distance) as TextView?
        txt_distance_val = findViewById(R.id.txt_distance_val) as TextView?
        txt_distance_km = findViewById(R.id.txt_distance_km) as TextView?
        txt_total_price = findViewById(R.id.txt_total_price) as TextView?
        txt_total_price_dol = findViewById(R.id.txt_total_price_dol) as TextView?
        txt_total_price_val = findViewById(R.id.txt_total_price_val) as TextView?
        txt_booking_detail = findViewById(R.id.txt_booking_detail) as TextView?
        txt_track_truck = findViewById(R.id.txt_track_truck) as TextView?
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout?
        layout_track_truck = findViewById(R.id.layout_track_truck) as RelativeLayout?
        layout_car_detail = findViewById(R.id.layout_car_detail) as LinearLayout
        layout_driver_detail = findViewById(R.id.layout_driver_detail) as LinearLayout
        txt_truct_type_val = findViewById(R.id.txt_truct_type_val) as TextView
        layout_cancel_request_button =
            findViewById(R.id.layout_cancel_request_button) as RelativeLayout
        img_driver_image = findViewById(R.id.img_driver_image) as ImageView
        txt_driver_name = findViewById(R.id.txt_driver_name) as TextView
        txt_drv_trc_typ = findViewById(R.id.txt_drv_trc_typ) as TextView
        txt_num_plate = findViewById(R.id.txt_num_plate) as TextView
        txt_mobile_num = findViewById(R.id.txt_mobile_num) as TextView
        txt_lic_num = findViewById(R.id.txt_lic_num) as TextView
        scroll_view = findViewById(R.id.scroll_view) as ScrollView
        layout_accepted = findViewById(R.id.layout_accepted) as LinearLayout
        layout_accepted_call = findViewById(R.id.layout_accepted_call) as RelativeLayout
        layout_completed = findViewById(R.id.layout_completed) as LinearLayout
        layout_cancel_user = findViewById(R.id.layout_cancel_user) as LinearLayout
        layout_on_trip = findViewById(R.id.layout_on_trip) as LinearLayout
        layout_driver_unavailabel = findViewById(R.id.layout_driver_unavailabel) as LinearLayout
        layout_cancel_driver = findViewById(R.id.layout_cancel_driver) as LinearLayout
        layout_pending = findViewById(R.id.layout_pending) as RelativeLayout
        txt_travel_time = findViewById(R.id.txt_travel_time) as TextView
        txt_travel_time_val = findViewById(R.id.txt_travel_time_val) as TextView
        txt_to = findViewById(R.id.txt_to) as TextView
        txt_vehicle_detail = findViewById(R.id.txt_vehicle_detail) as TextView
        txt_payment_detail = findViewById(R.id.txt_payment_detail) as TextView

        userPref = PreferenceManager.getDefaultSharedPreferences(this@BookingDetailActivity)

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        Roboto_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Semibold = Typeface.createFromAsset(assets, "fonts/OpenSans-Semibold_0.ttf")

        txt_booking_detail!!.typeface = OpenSans_Regular
        txt_track_truck!!.typeface = Roboto_Bold
        txt_to.typeface = Roboto_Bold
        txt_vehicle_detail.typeface = Roboto_Bold
        txt_payment_detail.typeface = Roboto_Bold
        txt_cancel_request.typeface = Roboto_Bold

        allTripFeed = Common.allTripFeeds

        ProgressDialog =
            Dialog(this@BookingDetailActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading


        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var pickup_date_time = ""
        try {
            val parceDate = simpleDateFormat.parse(allTripFeed!!.pickupDateTime)
            val parceDateFormat = SimpleDateFormat("h:mm a,dd,MMM yyyy")
            pickup_date_time = parceDateFormat.format(parceDate.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        txt_booking_id_val!!.text = allTripFeed!!.bookingId
        txt_pickup_point_val!!.text = allTripFeed!!.pickupArea
        txt_drop_point_val!!.text = allTripFeed!!.dropArea
        txt_booking_date!!.text = pickup_date_time
        txt_distance_val!!.text = allTripFeed!!.km
        txt_total_price_val!!.text = allTripFeed!!.amount
        Log.d("TaxiType", "TaxiType = " + allTripFeed!!.taxiType)
        txt_truct_type_val.text = allTripFeed!!.taxiType
        txt_travel_time_val.text = allTripFeed!!.approxTime

        txt_booking_id!!.typeface = Roboto_Regular
        txt_pickup_point!!.typeface = Roboto_Regular
        txt_booking_date!!.typeface = Roboto_Regular
        txt_drop_point!!.typeface = Roboto_Regular
        txt_distance_km!!.typeface = Roboto_Regular
        txt_total_price_dol!!.typeface = Roboto_Regular
        txt_total_price_dol!!.text = Common.Currency


        txt_pickup_point_val!!.typeface = OpenSans_Regular
        txt_booking_date!!.typeface = OpenSans_Regular
        txt_drop_point_val!!.typeface = OpenSans_Regular
        txt_distance!!.typeface = OpenSans_Regular
        txt_distance_val!!.typeface = OpenSans_Regular
        txt_total_price!!.typeface = OpenSans_Regular
        txt_total_price_val!!.typeface = OpenSans_Regular
        txt_truct_type_val.typeface = OpenSans_Regular
        txt_travel_time.typeface = OpenSans_Regular
        txt_travel_time_val.typeface = OpenSans_Regular

        txt_driver_name.typeface = Roboto_Regular
        txt_drv_trc_typ.typeface = Roboto_Regular
        txt_num_plate.typeface = Roboto_Regular
        txt_mobile_num.typeface = Roboto_Regular
        txt_lic_num.typeface = Roboto_Regular


        if (allTripFeed!!.driverDetail != null && allTripFeed!!.driverDetail == "null") {
            layout_car_detail.visibility = View.VISIBLE
            layout_driver_detail.visibility = View.GONE
            Log.d("allTripFeed", "allTripFeed = " + Url.carImageUrl + allTripFeed!!.carIcon)
            Picasso.with(this@BookingDetailActivity)
                .load(Uri.parse(Url.carImageUrl + allTripFeed!!.carIcon))
                .placeholder(R.drawable.truck_icon)
                .transform(CircleTransform())
                .into(img_car_image)
            layout_track_truck!!.isEnabled = false
        } else {
            layout_car_detail.visibility = View.GONE
            layout_driver_detail.visibility = View.VISIBLE
            txt_drv_trc_typ.text = allTripFeed!!.taxiType
            if (allTripFeed!!.status == "9")
                layout_track_truck!!.isEnabled = false
            else
                layout_track_truck!!.isEnabled = true
            try {
                val drvObj = JSONObject(allTripFeed!!.driverDetail)
                txt_driver_name.text = drvObj.getString("name")

                txt_num_plate.text = drvObj.getString("car_no")
                DriverPhNo = drvObj.getString("phone")
                txt_mobile_num.text = DriverPhNo
                txt_lic_num.text = drvObj.getString("license_plate")

                Picasso.with(this@BookingDetailActivity)
                    .load(Uri.parse(Url.DriverImageUrl + drvObj.getString("image")))
                    .placeholder(R.drawable.avatar_placeholder)
                    .transform(CircleTransform())
                    .into(img_driver_image)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        Log.d("Status", "Status = " + allTripFeed!!.status)

        if (allTripFeed!!.status == "1") {
            layout_pending.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "3") {
            layout_accepted.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "9") {
            layout_completed.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "4") {
            layout_cancel_user.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "8") {
            layout_on_trip.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "6") {
            layout_driver_unavailabel.visibility = View.VISIBLE
        } else if (allTripFeed!!.status == "5") {
            layout_pending.visibility = View.VISIBLE
        }

        layout_cancel_request_button.setOnClickListener {
            val CancelBookingDialog =
                Dialog(this@BookingDetailActivity, android.R.style.Theme_Translucent_NoTitleBar)
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
                DeleteCab()
            }
        }


        layout_back_arrow!!.setOnClickListener { finish() }

        layout_track_truck!!.setOnClickListener {
            val di = Intent(this@BookingDetailActivity, TrackTruckActivity::class.java)
            startActivity(di)
        }

        /*Footer click event*/
        layout_accepted_call.setOnClickListener {
            Handler().postDelayed({
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:$DriverPhNo")
                startActivity(callIntent)
            }, 100)
        }

        /*Share Layout Start*/
        layout_accepted_share_eta = findViewById(R.id.layout_accepted_share_eta) as RelativeLayout

        layout_accepted_share_eta.setOnClickListener { ShareDialog() }

        layout_completed_eta = findViewById(R.id.layout_completed_eta) as RelativeLayout
        layout_completed_eta.setOnClickListener { ShareDialog() }
        layout_completed_eta_chield =
            findViewById(R.id.layout_completed_eta_chield) as RelativeLayout
        layout_completed_eta_chield.setOnClickListener { ShareDialog() }

        layout_share_on_trip = findViewById(R.id.layout_share_on_trip) as RelativeLayout
        layout_share_on_trip.setOnClickListener { ShareDialog() }

        layout_share_driver_unavailabel =
            findViewById(R.id.layout_share_driver_unavailabel) as RelativeLayout
        layout_share_driver_unavailabel.setOnClickListener { ShareDialog() }

        layout_share_cancel_driver = findViewById(R.id.layout_share_cancel_driver) as RelativeLayout
        layout_share_cancel_driver.setOnClickListener { ShareDialog() }

        ShareDesc = "Name : " + userPref.getString("name", "") + ","
        ShareDesc += "Pickup Address : " + allTripFeed!!.pickupArea + ","
        ShareDesc += "Drop Address : " + allTripFeed!!.dropArea + ","

        /*Share Layout End*/
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

    fun ShareDialog() {
        ShareDialog =
            Dialog(this@BookingDetailActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ShareDialog.setContentView(R.layout.camera_dialog_layout)

        val facebook_text = ShareDialog.findViewById(R.id.txt_open_camera) as TextView
        facebook_text.text = "Facebook Share"

        val twitter_text = ShareDialog.findViewById(R.id.txt_open_gallery) as TextView
        twitter_text.text = "Twitter Share"

        val layout_open_camera = ShareDialog.findViewById(R.id.layout_open_camera) as RelativeLayout
        layout_open_camera.setOnClickListener {
            ShareDialog.cancel()
            ShareFacebookLink(ShareDesc)
        }

        val layout_open_gallery =
            ShareDialog.findViewById(R.id.layout_open_gallery) as RelativeLayout
        layout_open_gallery.setOnClickListener {
            ShareDialog.cancel()
            ShareTwitterLink()
        }

        val layout_open_cancel = ShareDialog.findViewById(R.id.layout_open_cancel) as RelativeLayout
        layout_open_cancel.setOnClickListener { ShareDialog.cancel() }

        ShareDialog.show()
    }

    fun ShareFacebookLink(Description: String) {
        val loggedIn = AccessToken.getCurrentAccessToken() != null
        if (loggedIn) {
            val shareLink = FacebookLoginClass(this@BookingDetailActivity, callbackManager)
            shareLink.postStatusUpdate("NaqilCom", Description, Url.AppLogUrl, "")
        } else {

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().logInWithPublishPermissions(
                this@BookingDetailActivity,
                Arrays.asList("publish_actions")
            )

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {

                        Log.d("loginResult", "loginResult = $loginResult")
                        // App code
                        val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                        ) { `object`, response ->
                            Log.d("object", "object = $`object`==$response")

                            if (`object` != null) {
                                val shareLink =
                                    FacebookLoginClass(this@BookingDetailActivity, callbackManager)
                                shareLink.postStatusUpdate(
                                    "NaqilCom",
                                    Description,
                                    Url.AppLogUrl,
                                    ""
                                )

                            } else {
                                Toast.makeText(
                                    this@BookingDetailActivity,
                                    "Something went wrong",
                                    Toast.LENGTH_LONG
                                )
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        Log.d("cancel", "cancel = ")
                    }

                    override fun onError(exception: FacebookException) {
                        Log.d("fb error", "fb error = " + exception.message)
                        println("exception >>" + exception.message)
                    }
                })
        }
    }

    fun ShareTwitterLink() {
        val twitterUrl = Url.SocialShareUrl + "?uid=" + userPref.getString("id", "id")
        var builder: TweetComposer.Builder? = null


        builder = TweetComposer.Builder(this@BookingDetailActivity)
            .text(ShareDesc)
            //.url(new URL(twitterUrl));

            .image(Uri.parse(Url.AppLogUrl))


        val intent = builder!!.createIntent()
        intent.type = "text/plain"
        startActivityForResult(intent, 111)
    }

    fun DeleteCab() {

        ProgressDialog.show()
        cusRotateLoading.start()

        Ion.with(this@BookingDetailActivity)
            .load(
                Url.deleteCabUrl + "?booking_id=" + allTripFeed!!.bookingId + "&uid=" + userPref.getString(
                    "id",
                    ""
                )
            )
            .setTimeout(10000)
            .asJsonObject()
            .setCallback { error, result ->
                // do stuff with the result or error
                Log.d("Login result", "Login result = $result==$error")
                if (error == null) {

                    ProgressDialog.cancel()
                    cusRotateLoading.stop()

                    try {
                        val resObj = JSONObject(result.toString())
                        if (resObj.getString("status") == "success") {
                            Handler().postDelayed({
                                val homeInt =
                                    Intent(this@BookingDetailActivity, HomeActivity::class.java)
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

                    Common.ShowHttpErrorMessage(this@BookingDetailActivity, error.message)
                }
            }

    }

    public override fun onResume() {
        super.onResume()  // Always call the superclass method first

        val filter = IntentFilter("come.naqil.naqil.BookingDetailActivity")

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Common.is_pusnotification = 1
            }
        }
        registerReceiver(receiver, filter)
    }

    public override fun onDestroy() {
        super.onDestroy()

        txt_booking_id = null
        txt_booking_id_val = null
        txt_pickup_point = null
        txt_pickup_point_val = null
        txt_booking_date = null
        txt_drop_point = null
        txt_drop_point_val = null
        img_car_image = null
        txt_distance = null
        txt_distance_val = null
        txt_distance_km = null
        txt_total_price = null
        txt_total_price_dol = null
        txt_total_price_val = null
        txt_booking_detail = null
        txt_track_truck = null
        layout_back_arrow = null
        layout_track_truck = null

        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.extras!!.containsKey("extra_is_retweet")) {
                    val isReTweet = data.extras!!.getBoolean("extra_is_retweet")
                    if (isReTweet) {
                        Toast.makeText(
                            this@BookingDetailActivity,
                            "Duplicate Tweet. This tweet has been posted very recently",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@BookingDetailActivity,
                            "Your post was shared successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    println("Image ID>>>>" + data!!.extras!!.getString("image_id")!!)
                }

            }
        }
    }

    override fun onLoginSuccess(beanObject: FBBean) {

    }

    override fun onLoginFailure(message: String) {
        Toast.makeText(this@BookingDetailActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun onPostSuccess(postID: String, message: String) {
        Toast.makeText(this@BookingDetailActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun onPostFailure(message: String) {
        Toast.makeText(this@BookingDetailActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun onLogout() {

    }
}
