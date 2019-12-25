package come.user.lezco.utils

/**
 * Created by techintegrity on 21/07/16.
 */
object DistanceUtil {

    fun distance(
        lat1: Double, lon1: Double, lat2: Double,
        lon2: Double
    ): Double {

        return if (lat1 == lat2 && lon1 == lon2) {
            0.0
        } else
            distance(lat1, lon1, lat2, lon2, 'K')
    }

    fun distance(
        lat1: Double, lon1: Double, lat2: Double,
        lon2: Double, unit: Char
    ): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            )
                    * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515
        if (unit == 'K') {
            dist = dist * 1.609344
        } else if (unit == 'N') {
            dist = dist * 0.8684
        }
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}
