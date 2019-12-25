package come.user.lezco

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.victor.loading.rotate.RotateLoading
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import come.user.lezco.api.FilterApi
import come.user.lezco.model.Driver
import come.user.lezco.model.DriverFilter
import come.user.lezco.utils.Common
import come.user.lezco.utils.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.bottomsheet_wishes.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class FilterActivity : AppCompatActivity() {
    var list:List<DriverFilter.Data>?=ArrayList()
    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        ProgressDialog = Dialog(this@FilterActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading
        if (Common.isNetworkAvailable(this))
            getFilter()

        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this,object: RecyclerItemClickListener.OnItemClickListener
        {
            override fun onItemClick(view: View, position: Int)
            {
                Intent(this@FilterActivity,BookingDetails::class.java).also {
                    it.putExtra("data", list?.get(position))
                    it.putExtra("pick_lat", intent.getDoubleExtra("pick_lat",0.00))
                    it.putExtra("pick_lon", intent.getDoubleExtra("pick_lon",0.00))
                    it.putExtra("drop_lat", intent.getDoubleExtra("drop_lat",0.00))
                    it.putExtra("drop_lon", intent.getDoubleExtra("drop_lon",0.00))
                    it.putExtra("DayNight", intent.getStringExtra("DayNight"))
                    it.putExtra("drop_point", intent.getStringExtra("drop_point"))
                    it.putExtra("pickup_point",intent.getStringExtra("pickup_point"))
                    it.putExtra("totlePrice", intent.getStringExtra("totlePrice"))
                    it.putExtra("truckType",list?.get(position)?.cartype)
                    it.putExtra("comment",intent.getStringExtra("comment"))

                    startActivity(it)
                }
            }

        }))
    }


    private fun getFilter() {
        ProgressDialog.show()
        cusRotateLoading.start()
        FilterApi().getFilter(
            Driver(
                intent.getStringExtra("gender"),
                intent.getStringExtra("min"),
                intent.getStringExtra("rating"),
                intent.getDoubleExtra("pick_lat",0.00),
                intent.getDoubleExtra("pick_lon",0.00),
                intent.getStringExtra("CabId")
            )
        ).enqueue(
            object : retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                    Log.e("driver response", t.message)
                    ProgressDialog.cancel()
                    cusRotateLoading.stop()
                }

                override fun onResponse(
                    call: retrofit2.Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    ProgressDialog.cancel()
                    cusRotateLoading.stop()
                    val data=response.body()?.string()
                    Log.e("driver response", data)
                    val jsonObject = JSONObject(data)
                    if (jsonObject.getInt("total_count") > 0) {
                        val gson = Gson()
                        val filter =
                            gson.fromJson(data, DriverFilter::class.java)
                        list= (filter.data as List<DriverFilter.Data>?)!!
                        initRecylerview((filter.data!! as List<DriverFilter.Data>).toListItem()!!)
                    }else
                    {
                        Toast.makeText(this@FilterActivity,"No Data Found.",Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }
                }

            })

//        Log.e("driver response", driverFilter.execute().body()?.string()!!)

    }

    private fun initRecylerview(toListItem: List<FilterRecylerview>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val grp_adapter = GroupAdapter<ViewHolder>().also {
            it.addAll(toListItem)
        }
        recyclerView.adapter = grp_adapter

    }

    private fun List<DriverFilter.Data>.toListItem(): List<FilterRecylerview> {
        return this.map {
            FilterRecylerview(it)
        }
    }

}
