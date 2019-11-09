package come.texi.driver

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.koushikdutta.ion.Ion
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import come.texi.driver.adapter.LocationHistoryAdapter
import come.texi.driver.adapter.PickupDropLocationAdapter
import come.texi.driver.gpsLocation.LocationAddress
import come.texi.driver.model.Location
import come.texi.driver.repository.LocationHistoryRepository
import come.texi.driver.room.Appdatabase
import come.texi.driver.utils.Common
import come.texi.driver.utils.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_drop_location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import java.io.UnsupportedEncodingException
import java.lang.Double
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DropLocationActivity : AppCompatActivity(),
    PickupDropLocationAdapter.OnDraoppickupClickListener, KodeinAware {
    internal var bothLocationString = ""
    internal var PickupLarLng: LatLng? = null
    internal var DropLarLng: LatLng? = null
    internal var DropLongtude: kotlin.Double = 0.toDouble()
    internal var DropLatitude: kotlin.Double = 0.toDouble()
    internal var PickupLongtude: kotlin.Double = 0.toDouble()
    internal var PickupLatitude: kotlin.Double = 0.toDouble()
    internal var ClickOkButton = false
    internal var locationArray: ArrayList<HashMap<String, String>>? = null
    internal var pickupDropLocationAdapter: PickupDropLocationAdapter? = null
    lateinit var pickupDragLayoutManager: RecyclerView.LayoutManager;
    lateinit var userPref: SharedPreferences
    lateinit var db: Appdatabase
    lateinit var list:ArrayList<Location>
    lateinit var picDrpHash:HashMap<String,String>
    override val kodein by kodein()
    var datepicker:Calendar= Calendar.getInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drop_location)
//        ClickOkButton = true
        db = Appdatabase.getDatabase(this)
        list=ArrayList()
        picDrpHash= HashMap()
        userPref = PreferenceManager.getDefaultSharedPreferences(this@DropLocationActivity)
        pickupDragLayoutManager = LinearLayoutManager(this@DropLocationActivity)
        recycle_pickup_location1.layoutManager = pickupDragLayoutManager
        EditorActionListener(edt_pickup_location!!, "pickeup")
        AddTextChangeListener(edt_pickup_location!!, "pickeup")
        AddSetOnClickListener(edt_pickup_location!!, "pickeup")

        /*Drop Location autocomplate start*/
        //LocationAutocompleate(edt_drop_location, "drop");
        EditorActionListener(edt_drop_location!!, "drop")
        AddTextChangeListener(edt_drop_location!!, "drop")
        AddSetOnClickListener(edt_drop_location!!, "drop")
        img_pickup_close.setOnClickListener {
            edt_pickup_location!!.setText("")
            PickupLarLng = null
            PickupLatitude = 0.0
            PickupLongtude = 0.0

        }

        img_drop_close.setOnClickListener {
            edt_drop_location!!.setText("")
            DropLarLng = null
            DropLongtude = 0.0
            DropLatitude = 0.0
        }
        back.setOnClickListener {
            onBackPressed()
        }
        history_recylerview?.let {
            it.layoutManager = LinearLayoutManager(this)

        }

        CoroutineScope(Dispatchers.Main).launch {
            LocationHistoryRepository(db).getAllLocation().observe(this@DropLocationActivity,
                Observer {
                    Log.e("TAG", "onCreate: " +it.toString());
                    initRecylerView(it.changeData())
                    list.clear()
                    list.addAll(it)
                })
        }

        date.setOnClickListener {
           showDateTimePicker()
        }

        history_recylerview.addOnItemTouchListener(RecyclerItemClickListener(this,object :RecyclerItemClickListener.OnItemClickListener
        {
            override fun onItemClick(view: View, position: Int) {
                if(edt_pickup_location.isFocused)
                {
                    edt_pickup_location.text.clear()
                    edt_pickup_location.setText(list[position].fulladdress)
                }
                else if(edt_drop_location.isFocused)
                {
                    edt_drop_location.text.clear()
                    edt_drop_location.setText(list[position].fulladdress)
                }else if(edt_pickup_location.text.isEmpty())
                {
                    edt_pickup_location.text.clear()
                    edt_pickup_location.setText(list[position].fulladdress)
                }else if(edt_drop_location.text.isEmpty())
                {
                    edt_drop_location.text.clear()
                    edt_drop_location.setText(list[position].fulladdress)
                }

            }
        }))
    }



    fun getTime(textView: TextView, context: Context){

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        textView.setOnClickListener {
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }
    private fun List<Location>.changeData(): List<LocationHistoryAdapter> {
        return this.map {
            LocationHistoryAdapter(it)
        }
    }

    private fun initRecylerView(list: List<LocationHistoryAdapter>) {
        val adapte = GroupAdapter<ViewHolder>().apply {
            addAll(list)
        }
        history_recylerview.apply {
            this.layoutManager = LinearLayoutManager(this@DropLocationActivity)
            this.adapter = adapte

        }
    }

    private fun EditorActionListener(locationEditext: EditText, clickText: String) {

        locationEditext.setOnEditorActionListener { v, actionId, event ->
            Log.d("Edit text", "Edit text = " + v.text.toString())

            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                Log.d("locationEditext", "locationEditext = " + locationEditext.text.toString())
                if (locationEditext.text.toString().length > 0) {

                    if (clickText == "pickeup") {
                        if (Common.isNetworkAvailable(this@DropLocationActivity)) {
                            bothLocationString = "pickeup"
                            LocationAddress.getAddressFromLocation(
                                edt_pickup_location!!.text.toString(),
                                applicationContext,
                                GeocoderHandlerLatitude()
                            )
                        } else {
                            Toast.makeText(
                                this@DropLocationActivity,
                                "No Network",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    } else if (clickText == "drop") {
                        if (Common.isNetworkAvailable(this@DropLocationActivity)) {
                            bothLocationString = "drop"
                            LocationAddress.getAddressFromLocation(
                                edt_drop_location!!.text.toString(),
                                applicationContext,
                                GeocoderHandlerLatitude()
                            )
                        } else {
                            Toast.makeText(
                                this@DropLocationActivity,
                                "No Network",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    if (edt_drop_location!!.text.length > 0 && edt_pickup_location!!.text.length > 0) {
                        if (Common.isNetworkAvailable(this@DropLocationActivity)) {
                            //new CaculationDiration().execute();
                            //CaculationDirationIon();
                        } else {
                            Common.showInternetInfo(this@DropLocationActivity, "")
                        }
                    }

                } else {
                    PickupLarLng = null
                    PickupLatitude = 0.0
                    PickupLongtude = 0.0
                    Toast.makeText(
                        this@DropLocationActivity,
                        "Please Enter Location",
                        Toast.LENGTH_LONG
                    )
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

                }

            }
        })

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
                    Toast.makeText(
                        this@DropLocationActivity,
                        "No Network conection",
                        Toast.LENGTH_LONG
                    )
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
                            Double.parseDouble(LocationSplit[0]),
                            Double.parseDouble(LocationSplit[1])
                        )
                    } else if (bothLocationString == "drop") {
                        DropLongtude = java.lang.Double.parseDouble(LocationSplit[1])
                        DropLatitude = java.lang.Double.parseDouble(LocationSplit[0])

                        DropLarLng = LatLng(
                            Double.parseDouble(LocationSplit[0]),
                            Double.parseDouble(LocationSplit[1])
                        )
                    }
                    if(PickupLarLng!=null && DropLarLng!=null ) {
                        Intent(this@DropLocationActivity, SelectCarActivity::class.java).also {
                            startActivity(it)
                        }
                        for(i in list.iterator())
                        {
                            if(i.fulladdress.equals(picDrpHash["location name"]!!))
                            {
                                return
                            }

                        }
                        CoroutineScope(IO).launch {
                            LocationHistoryRepository(db).saveLocation(
                                Location(
                                    address = picDrpHash["address"]!!,
                                    fulladdress = picDrpHash["location name"]!!
                                )
                            )
                        }
                    }

                }
            }
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
        Ion.with(this@DropLocationActivity)
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
                                locHashMap["address"] = predsJsonArray.getJSONObject(i)
                                    .getJSONObject("structured_formatting")
                                    .getString("secondary_text")
                                locationArray!!.add(locHashMap)

                            }

                            if (locationArray != null && locationArray!!.size > 0) {
                                recycle_pickup_location1.visibility = View.VISIBLE
                                layout_no_result.visibility = View.GONE
                                pickupDropLocationAdapter =
                                    PickupDropLocationAdapter(
                                        this@DropLocationActivity,
                                        locationArray!!
                                    )
                                recycle_pickup_location1.adapter = pickupDropLocationAdapter
                                pickupDropLocationAdapter!!.setOnDropPickupClickListener(this@DropLocationActivity)
                                pickupDropLocationAdapter!!.updateItems()
                            }

                            Log.d("locationArray", "locationArray = " + locationArray!!.size)
                        } else if (resObj.getString("status") == "ZERO_RESULTS") {

                            if (locationArray != null && locationArray!!.size > 0)
                                locationArray!!.clear()

                            layout_no_result.visibility = View.VISIBLE
                            recycle_pickup_location1.visibility = View.GONE

                            Log.d("locationArray", "locationArray = " + locationArray!!.size)
                            if (pickupDropLocationAdapter != null)
                                pickupDropLocationAdapter!!.updateBlankItems(locationArray!!)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Common.ShowHttpErrorMessage(this@DropLocationActivity, error.message)
                }
            }
    }

    override fun PickupDropClick(position: Int) {
        if (locationArray != null && locationArray!!.size > 0) {
             picDrpHash = locationArray!![position]
            Log.d("bothLocationString", "bothLocationString = $bothLocationString")

            if (bothLocationString != "") {
                if (bothLocationString == "pickeup") {
                    edt_pickup_location!!.setText(picDrpHash["location name"])
                    if (Common.isNetworkAvailable(this@DropLocationActivity)) {
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
                        Toast.makeText(this@DropLocationActivity, "No Network", Toast.LENGTH_LONG)
                            .show()
                    }
                } else if (bothLocationString == "drop") {
                    edt_drop_location!!.setText(picDrpHash["location name"])
                    if (Common.isNetworkAvailable(this@DropLocationActivity)) {
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
                        Toast.makeText(this@DropLocationActivity, "No Network", Toast.LENGTH_LONG)
                            .show()
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

    fun AddSetOnClickListener(locationEditext: EditText, ClickValue: String) {

        locationEditext.setOnTouchListener { v, event ->
            ClickOkButton = false
            val constraintSet = ConstraintSet()
            bothLocationString = ClickValue
            val params = ConstraintLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            constraintSet.clone(constraintlayout)
            if (ClickValue == "drop") {
//
            } else if (ClickValue == "pickeup") {
//
            }
//            layout_pickup_drag_location.layoutParams = params
            false
        }

        locationEditext.setOnClickListener {
            ClickOkButton = false
            val constraintSet = ConstraintSet()

            bothLocationString = ClickValue
            val params = ConstraintLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            constraintSet.clone(constraintlayout)

            if (ClickValue == "drop") {
//
            } else if (ClickValue == "pickeup") {
//
            }
//            layout_pickup_drag_location.layoutParams = params
        }
    }


    fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        datepicker = Calendar.getInstance()
        DatePickerDialog(this, object:DatePickerDialog.OnDateSetListener {
           override fun onDateSet(view:DatePicker, year:Int, monthOfYear:Int, dayOfMonth:Int) {
               datepicker.set(year, monthOfYear, dayOfMonth)
                TimePickerDialog(this@DropLocationActivity, object:TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view:TimePicker, hourOfDay:Int, minute:Int) {
                        datepicker.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        datepicker.set(Calendar.MINUTE, minute)
                        Log.v("TAG", "The choosen one " + datepicker.getTime())
                        date.setText(datepicker.time.date.toString()+"-"+Int.to(datepicker.time.month)+1+"-"+datepicker.time.getYear()+", "+datepicker.time.hours+":"+datepicker.time.minutes)
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show()
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show()
    }

}
