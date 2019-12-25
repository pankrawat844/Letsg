package come.user.lezco

import android.app.Dialog
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.squareup.picasso.Picasso
import come.user.lezco.adapter.CarTypeAdapter
import come.user.lezco.utils.CircleTransform
import come.user.lezco.utils.Common
import come.user.lezco.utils.Url
import org.json.JSONArray
import org.json.JSONException

class RateCardActivity : AppCompatActivity(), CarTypeAdapter.OnCarTypeClickListener {

    lateinit var layout_slidemenu: RelativeLayout
    lateinit var layout_category: RelativeLayout
    lateinit var img_truck_icon: ImageView
    internal var cabDetail: String? = null

    lateinit var txt_rate_card: TextView
    lateinit var txt_cateogry: TextView
    lateinit var txt_truck_typ_val: TextView
    lateinit var txt_loading_capacity: TextView
    lateinit var txt_loading_capacity_value: TextView
    lateinit var txt_standars_rate_day: TextView
    lateinit var txt_day_fir_km: TextView
    lateinit var txt_fir_day_currency: TextView
    lateinit var txt_fir_day_price: TextView
    lateinit var txt_after_km: TextView
    lateinit var txt_aft_day_currency: TextView
    lateinit var txt_aft_day_price: TextView
    lateinit var txt_aft_day_per_km: TextView
    lateinit var txt_standars_rate_night: TextView
    lateinit var txt_night_fir_km: TextView
    lateinit var txt_fir_night_currency: TextView
    lateinit var txt_fir_nigth_price: TextView
    lateinit var txt_after_night_km: TextView
    lateinit var txt_aft_night_currency: TextView
    lateinit var txt_aft_night_price: TextView
    lateinit var txt_aft_night_per_km: TextView
    lateinit var txt_extra_charges: TextView
    lateinit var txt_ride_time_chr_day: TextView
    lateinit var txt_ride_time_day_currency: TextView
    lateinit var txt_ride_time_day_price: TextView
    lateinit var txt_ride_time_day_per_km: TextView
    lateinit var txt_wait_time_day: TextView
    lateinit var txt_ride_time_chr_night: TextView
    lateinit var txt_ride_time_night_currency: TextView
    lateinit var txt_ride_time_night_price: TextView
    lateinit var txt_ride_time_night_per_km: TextView
    lateinit var txt_wait_time_night: TextView
    lateinit var txt_per_time_charges: TextView
    lateinit var txt_per_time_charges_des: TextView
    lateinit var txt_service_tex: TextView
    lateinit var txt_service_tex_des: TextView
    lateinit var txt_toll_tex: TextView

    lateinit var slidingMenu: SlidingMenu

    lateinit var OpenSans_Bold: Typeface
    lateinit var OpenSans_Regular: Typeface
    lateinit var Robot_Regular: Typeface
    lateinit var Roboto_medium: Typeface

    lateinit var CarTypeDialog: Dialog
    lateinit var recycle_car_type: RecyclerView
    var CarTypeLayoutManager: RecyclerView.LayoutManager? = null
    lateinit var carTypeAdapter: CarTypeAdapter
    lateinit var cabDetailArray: JSONArray

    internal var common = Common()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_card)

        cabDetailArray = Common.CabDetail!!

        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        Robot_Regular = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_medium = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")

        layout_slidemenu = findViewById(R.id.layout_slidemenu) as RelativeLayout


        txt_rate_card = findViewById(R.id.txt_rate_card) as TextView
        txt_cateogry = findViewById(R.id.txt_cateogry) as TextView
        txt_truck_typ_val = findViewById(R.id.txt_truck_typ_val) as TextView
        txt_loading_capacity = findViewById(R.id.txt_loading_capacity) as TextView
        txt_loading_capacity_value = findViewById(R.id.txt_loading_capacity_value) as TextView
        txt_standars_rate_day = findViewById(R.id.txt_standars_rate_day) as TextView
        txt_day_fir_km = findViewById(R.id.txt_day_fir_km) as TextView
        txt_fir_day_currency = findViewById(R.id.txt_fir_day_currency) as TextView
        txt_fir_day_price = findViewById(R.id.txt_fir_day_price) as TextView
        txt_after_km = findViewById(R.id.txt_after_km) as TextView
        txt_aft_day_currency = findViewById(R.id.txt_aft_day_currency) as TextView
        txt_aft_day_price = findViewById(R.id.txt_aft_day_price) as TextView
        txt_aft_day_per_km = findViewById(R.id.txt_aft_day_per_km) as TextView
        txt_standars_rate_night = findViewById(R.id.txt_standars_rate_night) as TextView
        txt_night_fir_km = findViewById(R.id.txt_night_fir_km) as TextView
        txt_fir_night_currency = findViewById(R.id.txt_fir_night_currency) as TextView
        txt_fir_nigth_price = findViewById(R.id.txt_fir_nigth_price) as TextView
        txt_after_night_km = findViewById(R.id.txt_after_night_km) as TextView
        txt_aft_night_currency = findViewById(R.id.txt_aft_night_currency) as TextView
        txt_aft_night_price = findViewById(R.id.txt_aft_night_price) as TextView
        txt_aft_night_per_km = findViewById(R.id.txt_aft_night_per_km) as TextView
        txt_extra_charges = findViewById(R.id.txt_extra_charges) as TextView
        txt_ride_time_chr_day = findViewById(R.id.txt_ride_time_chr_day) as TextView
        txt_ride_time_day_currency = findViewById(R.id.txt_ride_time_day_currency) as TextView
        txt_ride_time_day_price = findViewById(R.id.txt_ride_time_day_price) as TextView
        txt_ride_time_day_per_km = findViewById(R.id.txt_ride_time_day_per_km) as TextView
        txt_wait_time_day = findViewById(R.id.txt_wait_time_day) as TextView
        txt_ride_time_chr_night = findViewById(R.id.txt_ride_time_chr_night) as TextView
        txt_ride_time_night_currency = findViewById(R.id.txt_ride_time_night_currency) as TextView
        txt_ride_time_night_price = findViewById(R.id.txt_ride_time_night_price) as TextView
        txt_ride_time_night_per_km = findViewById(R.id.txt_ride_time_night_per_km) as TextView
        txt_wait_time_night = findViewById(R.id.txt_wait_time_night) as TextView
        txt_per_time_charges = findViewById(R.id.txt_per_time_charges) as TextView
        txt_per_time_charges_des = findViewById(R.id.txt_per_time_charges_des) as TextView
        txt_service_tex = findViewById(R.id.txt_service_tex) as TextView
        txt_service_tex_des = findViewById(R.id.txt_service_tex_des) as TextView
        txt_toll_tex = findViewById(R.id.txt_toll_tex) as TextView
        img_truck_icon = findViewById(R.id.img_truck_icon) as ImageView

        txt_cateogry.typeface = Roboto_medium
        txt_truck_typ_val.typeface = Roboto_medium
        txt_loading_capacity.typeface = Roboto_medium
        txt_loading_capacity_value.typeface = Roboto_medium
        txt_day_fir_km.typeface = Roboto_medium
        txt_fir_day_currency.typeface = Roboto_medium
        txt_fir_day_price.typeface = Roboto_medium
        txt_after_km.typeface = Roboto_medium
        txt_aft_day_currency.typeface = Roboto_medium
        txt_aft_day_price.typeface = Roboto_medium
        txt_aft_day_per_km.typeface = Roboto_medium
        txt_night_fir_km.typeface = Roboto_medium
        txt_fir_night_currency.typeface = Roboto_medium
        txt_fir_nigth_price.typeface = Roboto_medium
        txt_after_night_km.typeface = Roboto_medium
        txt_aft_night_currency.typeface = Roboto_medium
        txt_aft_night_price.typeface = Roboto_medium
        txt_aft_night_per_km.typeface = Roboto_medium
        txt_ride_time_chr_day.typeface = Roboto_medium
        txt_ride_time_day_currency.typeface = Roboto_medium
        txt_ride_time_day_price.typeface = Roboto_medium
        txt_ride_time_day_per_km.typeface = Roboto_medium
        txt_wait_time_day.typeface = Roboto_medium
        txt_ride_time_chr_night.typeface = Roboto_medium
        txt_ride_time_night_currency.typeface = Roboto_medium
        txt_ride_time_night_price.typeface = Roboto_medium
        txt_ride_time_night_per_km.typeface = Roboto_medium
        txt_per_time_charges.typeface = Roboto_medium
        txt_service_tex.typeface = Roboto_medium

        txt_rate_card.typeface = OpenSans_Bold
        txt_standars_rate_day.typeface = Robot_Regular
        txt_standars_rate_night.typeface = Robot_Regular
        txt_extra_charges.typeface = Robot_Regular
        txt_toll_tex.typeface = Robot_Regular
        txt_service_tex_des.typeface = Robot_Regular
        txt_per_time_charges_des.typeface = Robot_Regular
        txt_wait_time_night.typeface = Robot_Regular
        txt_wait_time_day.typeface = Robot_Regular


        /*Cab Detail*/
        CabDetailView(0)

        layout_category = findViewById(R.id.layout_category) as RelativeLayout

        layout_category.setOnClickListener {
            /*Car Type Dialog Start*/
            CarTypeDialog =
                Dialog(this@RateCardActivity, android.R.style.Theme_Translucent_NoTitleBar)
            CarTypeDialog.setContentView(R.layout.cartype_dialog)
            recycle_car_type = CarTypeDialog.findViewById(R.id.recycle_car_type) as RecyclerView

            CarTypeLayoutManager = LinearLayoutManager(this@RateCardActivity)
            recycle_car_type.layoutManager = CarTypeLayoutManager

            carTypeAdapter = CarTypeAdapter(this@RateCardActivity, cabDetailArray)
            carTypeAdapter.updateItems()
            carTypeAdapter.setOnCarTypeItemClickListener(this@RateCardActivity)
            recycle_car_type.adapter = carTypeAdapter

            CarTypeDialog.show()
            /*Car Type Dialog End*/
        }

        /*Slide Menu Start*/
        slidingMenu = SlidingMenu(this)
        slidingMenu.mode = SlidingMenu.LEFT
        slidingMenu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width)
        slidingMenu.setFadeDegree(0.20f)
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        slidingMenu.setMenu(R.layout.left_menu)

        common.SlideMenuDesign(slidingMenu, this@RateCardActivity, "rate card")

        layout_slidemenu.setOnClickListener { slidingMenu.toggle() }
    }

    fun CabDetailView(position: Int) {

        try {

            val cabDetailObj = cabDetailArray.getJSONObject(position)

            Picasso.with(this@RateCardActivity)
                .load(Uri.parse(Url.carImageUrl + cabDetailObj.getString("icon").toString()))
                .placeholder(R.drawable.truck_slide_icon)
                .transform(CircleTransform())
                .into(img_truck_icon)

            txt_truck_typ_val.text = cabDetailObj.getString("cartype").toString()
            txt_loading_capacity_value.text = cabDetailObj.getString("seat_capacity").toString()

            /*Day*/
            txt_fir_day_currency.text = Common.Currency
            txt_day_fir_km.text =
                resources.getString(R.string.first) + " " + cabDetailObj.getString("intialkm").toString() + " " + resources.getString(
                    R.string.km
                )
            txt_fir_day_price.text = cabDetailObj.getString("car_rate").toString()
            txt_after_km.text =
                resources.getString(R.string.after) + " " + cabDetailObj.getString("intialkm").toString() + " " + resources.getString(
                    R.string.km
                )
            txt_aft_day_currency.text = Common.Currency
            txt_aft_day_price.text = cabDetailObj.getString("fromintailrate").toString()

            /*Night*/
            txt_night_fir_km.text =
                resources.getString(R.string.first) + " " + cabDetailObj.getString("intialkm").toString() + " " + resources.getString(
                    R.string.km
                )
            txt_fir_night_currency.text = Common.Currency
            txt_fir_nigth_price.text = cabDetailObj.getString("night_intailrate").toString()
            txt_after_night_km.text =
                resources.getString(R.string.after) + " " + cabDetailObj.getString("intialkm").toString() + " " + resources.getString(
                    R.string.km
                )
            txt_aft_night_currency.text = Common.Currency
            txt_aft_night_price.text = cabDetailObj.getString("night_fromintailrate").toString()

            /*Extra Charge*/
            txt_ride_time_day_currency.text = Common.Currency
            txt_ride_time_day_price.text = cabDetailObj.getString("ride_time_rate").toString()
            txt_ride_time_night_currency.text = Common.Currency
            txt_ride_time_night_price.text =
                cabDetailObj.getString("night_ride_time_rate").toString()

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun SelectCarType(position: Int) {
        CabDetailView(position)
        CarTypeDialog.cancel()
    }

    public override fun onResume() {
        super.onResume()

        common.SlideMenuDesign(slidingMenu, this@RateCardActivity, "rate card")
    }
}
