package come.texi.driver.map

/**
 * Created by techintegrity on 21/07/16.
 */
import android.content.Context
import android.view.MotionEvent
import android.widget.FrameLayout

class TouchableWrapper(context: Context) : FrameLayout(context) {

    private var mTouchActionDown: TouchActionDown? = null
    private var mTouchActionUp: TouchActionUp? = null

    init {
        // Force the host activity to implement the TouchActionDown Interface
        try {
            mTouchActionDown = context as TouchActionDown
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement TouchActionDown")
        }

        // Force the host activity to implement the TouchActionDown Interface
        try {
            mTouchActionUp = context as TouchActionUp
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement mTouchActionUp")
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mTouchActionDown!!.onTouchDown(event)
            MotionEvent.ACTION_UP -> mTouchActionUp!!.onTouchUp(event)
        }
        return super.dispatchTouchEvent(event)
    }

    interface TouchActionDown {
        fun onTouchDown(event: MotionEvent)
    }

    interface TouchActionUp {
        fun onTouchUp(event: MotionEvent)
    }

}
