package come.user.lezco.utils

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import come.user.lezco.R
import come.user.lezco.model.DriverFilter

class DataBinder {
    companion object {
        @BindingAdapter("starfilter", requireAll = false)
        @JvmStatic
        fun starFilter(star: RatingBar, data: DriverFilter.Data) {
            star.rating = data.rating?.toFloat()!!
        }

        @BindingAdapter("calculateDistance",requireAll = true)
        @JvmStatic
        fun calculate(txt:TextView,data: DriverFilter.Data)
        {
            txt.text=(data.distance!!.div(1000)).toString()+"KM"
        }

        @BindingAdapter("adddriverimg",requireAll = false)
        @JvmStatic
        fun driver_img(imgview:ImageView,data: DriverFilter.Data)
        {
                val iconUri = Url.DriverImageUrl + data.image
                Glide.with(imgview.context)
                    .load(iconUri)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(imgview)



        }
    }
}