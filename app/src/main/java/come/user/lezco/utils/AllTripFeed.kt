package come.user.lezco.utils

import com.google.android.gms.maps.model.LatLng

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by techintegrity on 13/07/16.
 */
class AllTripFeed : Serializable {

    var bookingId: String? = null

    var pickupArea: String? = null

    var dropArea: String? = null

    var pickupDateTime: String? = null

    var taxiType: String? = null

    var km: String? = null

    var amount: String? = null

    var carIcon: String? = null

    var driverDetail: String? = null

    var status: String? = null

    var approxTime: String? = null

    var oldLocationList: ArrayList<LatLng>? = null

    var pickupLarLng: LatLng? = null

    var dropLarLng: LatLng? = null

    var driverLarLng: LatLng? = null

    var startPickLatLng: String? = null

    var endPickLatLng: String? = null

    var startDropLatLng: String? = null

    var endDropLatLng: String? = null
}
