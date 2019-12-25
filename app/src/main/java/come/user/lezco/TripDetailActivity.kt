package come.user.lezco

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.utils.Common
import come.user.lezco.utils.Url
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat

class TripDetailActivity : AppCompatActivity() {

    internal var txt_car_name: TextView? = null
    internal var txt_pickup_point: TextView? = null
    internal var txt_pickup_point_val: TextView? = null
    internal var txt_drop_point: TextView? = null
    internal var txt_drop_point_val: TextView? = null
    internal var txt_truct_type: TextView? = null
    internal var txt_truct_type_val: TextView? = null
    internal var img_car_image: ImageView? = null
    internal var txt_distance: TextView? = null
    internal var txt_distance_val: TextView? = null
    internal var txt_distance_km: TextView? = null
    lateinit var txt_ast_time: TextView
    lateinit var txt_ast_time_val: TextView
    internal var txt_booking_date: TextView? = null
    internal var txt_booking_date_val: TextView? = null
    internal var layout_back_arrow: RelativeLayout? = null
    internal var txt_total_price: TextView? = null
    internal var txt_total_price_dol: TextView? = null
    internal var txt_total_price_val: TextView? = null
    lateinit var txt_to: TextView
    internal var layout_confirm_request: RelativeLayout? = null
    lateinit var txt_payment_type_val: TextView
    lateinit var txt_payment_type: TextView
    lateinit var txt_vehicle_detail: TextView
    lateinit var txt_payment_detail: TextView
    lateinit var txt_confirm_request: TextView

    lateinit var OpenSans_Regular: Typeface
    lateinit var Roboto_Regular: Typeface
    lateinit var Roboto_Medium: Typeface
    lateinit var Roboto_Bold: Typeface
    lateinit var OpenSans_Semibold: Typeface

    internal var pickup_point: String? = null
    internal var drop_point: String? = null
    internal var truckIcon: String? = null
    internal var truckType: String? = null
    internal var CabId: String? = null
    internal var AreaId: String? = null
    internal var distance: Float? = null
    internal var totlePrice: Float? = null
    internal var booking_date: String? = null
    internal var PickupLatitude: Double = 0.toDouble()
    internal var PickupLongtude: Double = 0.toDouble()
    internal var DropLatitude: Double = 0.toDouble()
    internal var DropLongtude: Double = 0.toDouble()
    internal var DayNight: String? = null
    internal var comment: String? = null
    lateinit var pickup_date_time: String
    internal var transfertype: String? = null
    internal var PaymentType: String? = null
    internal var person: String? = null
    internal var transaction_id: String? = null
    internal var BookingType: String? = null
    internal var AstTime: String? = null

    lateinit var userPref: SharedPreferences

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)


        txt_car_name = findViewById(R.id.txt_car_name) as TextView?
        txt_pickup_point = findViewById(R.id.txt_pickup_point) as TextView?
        txt_pickup_point_val = findViewById(R.id.txt_pickup_point_val) as TextView?
        txt_drop_point = findViewById(R.id.txt_drop_point) as TextView?
        txt_drop_point_val = findViewById(R.id.txt_drop_point_val) as TextView?
        txt_truct_type = findViewById(R.id.txt_truct_type) as TextView?
        txt_truct_type_val = findViewById(R.id.txt_truct_type_val) as TextView?
        img_car_image = findViewById(R.id.img_car_image) as ImageView?
        txt_distance = findViewById(R.id.txt_distance) as TextView?
        txt_distance_val = findViewById(R.id.txt_distance_val) as TextView?
        txt_distance_km = findViewById(R.id.txt_distance_km) as TextView?
        txt_ast_time = findViewById(R.id.txt_ast_time) as TextView
        txt_ast_time_val = findViewById(R.id.txt_ast_time_val) as TextView
        txt_booking_date = findViewById(R.id.txt_booking_date) as TextView?
        txt_booking_date_val = findViewById(R.id.txt_booking_date_val) as TextView?
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout?
        txt_total_price = findViewById(R.id.txt_total_price) as TextView?
        txt_total_price_dol = findViewById(R.id.txt_total_price_dol) as TextView?
        txt_total_price_val = findViewById(R.id.txt_total_price_val) as TextView?
        layout_confirm_request = findViewById(R.id.layout_confirm_request) as RelativeLayout?
        txt_payment_type_val = findViewById(R.id.txt_payment_type_val) as TextView
        txt_payment_type = findViewById(R.id.txt_payment_type) as TextView
        txt_to = findViewById(R.id.txt_to) as TextView
        txt_vehicle_detail = findViewById(R.id.txt_vehicle_detail) as TextView
        txt_payment_detail = findViewById(R.id.txt_payment_detail) as TextView
        txt_confirm_request = findViewById(R.id.txt_confirm_request) as TextView

        ProgressDialog = Dialog(this@TripDetailActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        userPref = PreferenceManager.getDefaultSharedPreferences(this@TripDetailActivity)

        pickup_point = intent.extras!!.getString("pickup_point")
        drop_point = intent.extras!!.getString("drop_point")
        truckIcon = intent.extras!!.getString("truckIcon")
        truckType = intent.extras!!.getString("truckType")
        CabId = intent.extras!!.getString("CabId")
        AreaId = intent.extras!!.getString("AreaId")
        distance = intent.extras!!.getFloat("distance")
        totlePrice = intent.extras!!.getFloat("totlePrice")
        booking_date = intent.extras!!.getString("booking_date")
        PickupLatitude = intent.extras!!.getDouble("PickupLatitude")
        PickupLongtude = intent.extras!!.getDouble("PickupLongtude")
        DropLatitude = intent.extras!!.getDouble("DropLatitude")
        DropLongtude = intent.extras!!.getDouble("DropLongtude")
        comment = intent.extras!!.getString("comment")
        DayNight = intent.extras!!.getString("DayNight")
        transfertype = intent.extras!!.getString("transfertype")
        PaymentType = intent.extras!!.getString("PaymentType")
        person = intent.extras!!.getString("person")
        transaction_id = intent.extras!!.getString("transaction_id")
        BookingType = intent.extras!!.getString("BookingType")
        AstTime = intent.extras!!.getString("AstTime")

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        Roboto_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Semibold = Typeface.createFromAsset(assets, "fonts/OpenSans-Semibold_0.ttf")

        txt_car_name!!.typeface = OpenSans_Regular

        txt_pickup_point!!.typeface = Roboto_Regular
        txt_booking_date!!.typeface = Roboto_Regular
        txt_drop_point!!.typeface = Roboto_Regular
        txt_truct_type!!.typeface = Roboto_Regular
        txt_distance_km!!.typeface = Roboto_Regular
        txt_total_price_dol!!.typeface = Roboto_Regular
        txt_total_price_dol!!.text = Common.Currency
        txt_payment_type.typeface = Roboto_Regular
        txt_to.typeface = Roboto_Bold
        txt_vehicle_detail.typeface = Roboto_Bold
        txt_payment_detail.typeface = Roboto_Bold
        txt_confirm_request.typeface = Roboto_Bold

        txt_pickup_point_val!!.typeface = OpenSans_Regular
        txt_pickup_point_val!!.text = pickup_point
        txt_booking_date_val!!.typeface = OpenSans_Regular
        txt_booking_date_val!!.text = booking_date
        txt_drop_point_val!!.typeface = OpenSans_Regular
        txt_drop_point_val!!.text = drop_point
        txt_truct_type_val!!.typeface = OpenSans_Regular
        txt_truct_type_val!!.setText(truckType!!.toUpperCase())
        txt_distance!!.typeface = OpenSans_Regular
        txt_distance_val!!.typeface = OpenSans_Regular
        txt_distance_val!!.text = distance!!.toString() + ""
        txt_ast_time.typeface = OpenSans_Regular
        txt_ast_time_val.typeface = OpenSans_Regular
        txt_ast_time_val.text = AstTime
        txt_total_price!!.typeface = OpenSans_Regular
        txt_total_price_val!!.typeface = OpenSans_Regular
        txt_total_price_val!!.text = Math.round(totlePrice!!).toString() + ""
        txt_payment_type_val.typeface = OpenSans_Regular
        txt_payment_type_val.text = PaymentType

        Log.d("truckIcon", "truckIcon = " + truckIcon!!)
        Picasso.with(this@TripDetailActivity)
                .load(Uri.parse(Url.carImageUrl + truckIcon!!))
                .placeholder(R.drawable.truck_icon)
                .into(img_car_image)

        layout_back_arrow!!.setOnClickListener { finish() }

        val simpleDateFormat = SimpleDateFormat("h:mm a, d, MMM yyyy,EEE")
        try {
            val parceDate = simpleDateFormat.parse(booking_date)
            val parceDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            pickup_date_time = parceDateFormat.format(parceDate.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        layout_confirm_request!!.setOnClickListener {
            ProgressDialog.show()
            cusRotateLoading.start()

            Ion.with(this@TripDetailActivity)
                    .load(Url.bookCabUrl)
                    .setTimeout(10000)
                    //.setJsonObjectBody(json)
                    .setMultipartParameter("user_id", userPref.getString("id", ""))
                    .setMultipartParameter("username", userPref.getString("username", ""))
                    .setMultipartParameter("pickup_date_time", pickup_date_time)
                    .setMultipartParameter("drop_area", drop_point)
                    .setMultipartParameter("pickup_area", pickup_point)
                    .setMultipartParameter("time_type", DayNight)
                    .setMultipartParameter("amount", totlePrice.toString())
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
                    .setMultipartParameter("comment", comment)
                    .setMultipartParameter("person", person)
                    .setMultipartParameter("payment_type", PaymentType)
                    .setMultipartParameter("book_create_date_time", BookingType)
                    .setMultipartParameter("transaction_id", transaction_id)
                    .setMultipartParameter("approx_time", AstTime)
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
                                        val ai = Intent(this@TripDetailActivity, AllTripActivity::class.java)
                                        startActivity(ai)
                                        finish()
                                    }, 500)
                                } else if (resObj.getString("status") == "false") {

                                    Common.user_InActive = 1
                                    Common.InActive_msg = resObj.getString("message")

                                    val editor = userPref.edit()
                                    editor.clear()
                                    editor.commit()

                                    val logInt = Intent(this@TripDetailActivity, LoginOptionActivity::class.java)
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(logInt)
                                } else {
                                    Common.showMkError(this@TripDetailActivity, resObj.getString("error code").toString())
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        } else {
                            Common.ShowHttpErrorMessage(this@TripDetailActivity, error.message)
                        }
                    }


            //                Log.d("confirmParam", "confirmParam = " + Url.bookCabUrl + "?" + confirmParam);
            //                //ConfirmClient.get("",)
            //                ConfirmClient.get(Url.bookCabUrl, confirmParam, new AsyncHttpResponseHandler() {
            //
            //                    @Override
            //                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            //                        Log.d("responseBody", "responseBody" + new String(responseBody));
            //                        ProgressDialog.cancel();
            //                        cusRotateLoading.stop();
            //
            //
            //                    }
            //
            //                    @Override
            //                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //                        ProgressDialog.cancel();
            //                        cusRotateLoading.stop();
            //                        Common.ShowHttpErrorMessage(TripDetailActivity.this, error.getMessage());
            //                    }
            //                });
        }

    }

    public override fun onDestroy() {
        super.onDestroy()

        txt_car_name = null
        txt_pickup_point = null
        txt_pickup_point_val = null
        txt_drop_point = null
        txt_drop_point_val = null
        txt_truct_type = null
        txt_truct_type_val = null
        img_car_image = null
        txt_distance = null
        txt_distance_val = null
        txt_distance_km = null
        txt_booking_date = null
        txt_booking_date_val = null
        layout_back_arrow = null
        txt_total_price = null
        txt_total_price_dol = null
        txt_total_price_val = null
        layout_confirm_request = null

    }

}
