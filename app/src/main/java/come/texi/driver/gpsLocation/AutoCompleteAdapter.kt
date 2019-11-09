package come.texi.driver.gpsLocation

import android.app.Activity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import come.texi.driver.utils.Common
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.*

/**
 * Created by techintegrity on 08/07/16.
 */
class AutoCompleteAdapter(internal var activity: Activity, textViewResourceId: Int) : ArrayAdapter<String>(activity, textViewResourceId), Filterable {

    private var resultList: ArrayList<*>? = null

    override fun getCount(): Int {
        return if (resultList != null) resultList!!.size else 0
    }

    override fun getItem(index: Int): String? {
        return resultList!![index] as String
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString(), activity)

                    // Assign the data to the FilterResults
                    filterResults.values = resultList
                    if (resultList != null)
                        filterResults.count = resultList!!.size
                    else
                        filterResults.count = 0
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    companion object {

        fun autocomplete(input: String, activity: Activity): ArrayList<*>? {


            var resultList: ArrayList<String>? = null
            var conn: HttpURLConnection? = null
            val jsonResults = StringBuilder()

            var locationUrl = ""
            try {
                locationUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD5QFt6IdBuIHpvV1Z9FdAs0yBnBBwyI_g&input=" + URLEncoder.encode(input, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            try {

                val url = URL(locationUrl)
                conn = url.openConnection() as HttpURLConnection
                val into = InputStreamReader(conn.inputStream)

                // Load the results into a StringBuilder
                val buff = CharArray(1024)
                val read = into.read(buff)
                while (read != -1) {
                    jsonResults.append(buff, 0, read)
                }
            } catch (e: MalformedURLException) {
                Log.e("", "Error processing Places API URL", e)

            } catch (e: IOException) {
                Log.e("", "Error connecting to Places API", e)
                Common.ShowHttpErrorMessage(activity, "Connect to")
                return resultList
            } finally {
                conn?.disconnect()
            }

            try {
                // Create a JSON object hierarchy from the results
                val jsonObj = JSONObject(jsonResults.toString())
                Log.d("jsonObj", "jsonObj = $jsonObj")
                val predsJsonArray = jsonObj.getJSONArray("predictions")

                // Extract the Place descriptions from the results
                resultList = ArrayList(predsJsonArray.length())
                for (i in 0 until predsJsonArray.length()) {
                    println(predsJsonArray.getJSONObject(i))
                    println("============================================================")
                    resultList!!.add(predsJsonArray.getJSONObject(i).getString("description"))
                }
            } catch (e: JSONException) {
                Log.e("", "Cannot process JSON results", e)
            }

            return resultList
        }
    }
}

