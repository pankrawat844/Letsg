package come.user.lezco.gpsLocation

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log

import java.io.IOException
import java.util.Locale

/**
 * Created by techintegrity on 08/07/16.
 */
object LocationAddress {

    private val TAG = "LocationAddress"

    fun getAddressFromLocation(latitude: Double, longitude: Double,
                               context: Context, handler: Handler) {
        val thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocation(
                            latitude, longitude, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        //                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        //                            sb.append(address.getAddressLine(i)).append("\n");
                        //                        }
                        //                        sb.append(address.getLocality()).append("\n");
                        //                        sb.append(address.getPostalCode()).append("\n");
                        for (i in addressList.indices) {
                            if (address.getAddressLine(i) != "null")
                                sb.append(address.getAddressLine(i))
                            result = sb.toString()
                            return
                        }

                        if (address.locality != null && address.locality != "null")
                            sb.append(address.locality).append(",")
                        if (address.postalCode != null && address.postalCode != "null")
                            sb.append(address.postalCode).append(",")
                        if (address.countryName != null && address.countryName != "null")
                            sb.append(address.countryName)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable connect to Geocoder", e)
                    result = "Unable connect to Geocoder"

                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        //                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                        //                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result)
                        message.data = bundle
                    } else {
                        message.what = 1
                        val bundle = Bundle()
                        //                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                        //                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result)
                        message.data = bundle
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }

    fun getAddressFromLocation(address: String,
                               context: Context, handler: Handler) {
        val thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocationName(address, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()

                        sb.append(address.latitude).append(",")
                        sb.append(address.longitude)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable connect to Geocoder", e)
                    result = "Unable connect to Geocoder"

                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        //                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                        //                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result)
                        message.data = bundle
                    } else {
                        message.what = 1
                        val bundle = Bundle()
                        //                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                        //                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result)
                        message.data = bundle
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }

}

