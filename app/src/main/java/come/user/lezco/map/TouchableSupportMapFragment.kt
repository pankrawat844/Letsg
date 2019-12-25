package come.user.lezco.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.SupportMapFragment

/**
 * Created by techintegrity on 21/07/16.
 */
class TouchableSupportMapFragment : SupportMapFragment() {

    var mContentView: View? = null
    var mTouchView: TouchableWrapper? = null

    override fun onCreateView(
        inflater: LayoutInflater, parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContentView = super.onCreateView(inflater, parent, savedInstanceState)
        mTouchView = TouchableWrapper(activity!!)
        mTouchView!!.addView(mContentView)
        return mTouchView
    }

    override fun getView(): View? {
        return mContentView
    }

}
