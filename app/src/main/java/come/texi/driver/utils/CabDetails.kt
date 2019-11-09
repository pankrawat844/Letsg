package come.texi.driver.utils

import java.io.Serializable

/**
 * Created by techintegrity on 11/07/16.
 */
class CabDetails : Serializable {

    var id: String? = null

    internal var is_selected: Boolean = false

    var cartype: String? = null

    var transfertype: String = ""

    var intialkm: String? = null


    var carRate: String? = null

    //    String intailrate;
    //    public String getIntailrate(){
    //        return intailrate;
    //    }
    //    public void setIntailrate(String intrate){
    //        this.intailrate = intrate;
    //    }

    var standardrate: String? = null

    var fromintialkm: String? = null

    var fromintailrate: String? = null

    var fromstandardrate: String? = null

    var nightFromintialkm: String? = null

    var nightFromintailrate: String? = null

    var icon: String= ""

    var nightIntailrate: String? = null

    var nightStandardrate: String? = null

    var rideTimeRate: String? = null

    var nightRideTimeRate: String? = null

    var description: String = ""

    var seatCapacity: String = ""

    var areaId: String = ""

    var fixPrice: String? = null
    fun getiIsSelected(): Boolean {
        return is_selected
    }

    fun setIsSelected(is_selected: Boolean) {
        this.is_selected = is_selected
    }

}
