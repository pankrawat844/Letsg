package come.user.lezco.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import come.user.lezco.R

class SelectCarBottomSheet:BottomSheetDialogFragment() {
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view:View=LayoutInflater.from(context).inflate(R.layout.bottomsheet_select_car,null)
        dialog.setContentView(view)

        val param:CoordinatorLayout.LayoutParams=(view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior=param.behavior

        if(behavior!=null && behavior is BottomSheetBehavior)
        {
            (behavior as BottomSheetBehavior).setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
                override fun onSlide(p0: View, p1: Float) {

                }

                override fun onStateChanged(bottonSheet: View, newState: Int) {
                    var state:String=""
                when(newState)
                {
                    BottomSheetBehavior.STATE_DRAGGING ->
                    {
                        state = "DRAGGING"
                        Log.e("BottomSheet",state)
                    }
                    BottomSheetBehavior.STATE_EXPANDED->
                    {
                        state="Expanded"
                        Log.e("BottomSheet",state)
                    }
                    BottomSheetBehavior.STATE_SETTLING->
                    {
                        state="Settling"
                        Log.e("BottomSheet",state)
                    }
                    BottomSheetBehavior.STATE_HIDDEN->
                    {
                        state="Hidden"
                        Log.e("BottomSheet",state)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }

                }

            })
        }
    }
}