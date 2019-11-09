package come.texi.driver.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.databinding.BindableItem
import come.texi.driver.R
import come.texi.driver.databinding.ItemHistoryBinding
import come.texi.driver.model.Location

class LocationHistoryAdapter(val value:Location):BindableItem<ItemHistoryBinding>()
{
    override fun getLayout(): Int {
        return R.layout.item_history
    }

    override fun bind(viewBinding: ItemHistoryBinding, position: Int) {
        viewBinding.result=value
    }

}