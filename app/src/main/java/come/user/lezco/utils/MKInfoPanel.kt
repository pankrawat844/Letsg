package come.user.lezco.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import come.user.lezco.R

/**
 * Created by techintegrity on 04/07/16.
 */
class MKInfoPanel(
    internal var context: Context,
    type: MKInfoPanelType,
    titletext: String,
    subtitletext: String,
    interval: Int
) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

    //private TextView title;
    private val subtitle: TextView
    //private ImageView image;
    private val layout_info_panel: RelativeLayout
    private val rl: RelativeLayout


    enum class MKInfoPanelType {
        MKInfoPanelTypeInfo,
        MKInfoPanelTypeError
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.mkinfopanel)

        rl = findViewById(R.id.main) as RelativeLayout
        //title = ((TextView)findViewById(R.id.title));
        subtitle = findViewById(R.id.subtitle) as TextView
        //image = ((ImageView)findViewById(R.id.image));
        layout_info_panel = findViewById(R.id.layout_info_panel) as RelativeLayout

        if (type == MKInfoPanelType.MKInfoPanelTypeError) {
            layout_info_panel.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
        } else if (type == MKInfoPanelType.MKInfoPanelTypeInfo) {
            layout_info_panel.setBackgroundColor(Color.GREEN)
        }

        //title.setText(titletext);
        subtitle.text = subtitletext

        setCancelable(true)

        //        Handler handler =  new Handler();
        //        handler.postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                animateHide();
        //            }
        //        }, interval);

    }

    fun animateHide() {
        val activity = context as Activity
        val height = Common.getDisplayHeight(activity)
        val slideUp = TranslateAnimation(0f, 0f, 0f, height * 0.50f)
        slideUp.startOffset = 500
        slideUp.duration = 2000
        slideUp.fillAfter = true
        slideUp.interpolator = BounceInterpolator()
        layout_info_panel.startAnimation(slideUp)
        slideUp.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                cancel()
                dismiss()
            }
        })
    }

    companion object {

        protected val TAG = MKInfoPanel::class.java!!.getName()
    }

}
