package come.user.lezco.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NeareastDriver(
    val data: Data?=null,
    val error_msg: String,
    val status: Boolean
):Parcelable {
    @Parcelize
    data class Data(
        val cab_id: String,
        val car_no: String,
        val car_rate: String,
        val cartype: String,
        val description: String,
        val distance: Int,
        val duration: String,
        val gender: String,
        val icon: String,
        val id: String,
        val image: String,
        val name: String,
        val phone: String,
        val rating: String,
        val seat_capacity: String
    ):Parcelable
}