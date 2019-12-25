package come.user.lezco.adapter

import com.xwray.groupie.databinding.BindableItem
import come.user.lezco.R
import come.user.lezco.databinding.ItemHistoryBinding
import come.user.lezco.model.Location

class LocationHistoryAdapter(val value:Location):BindableItem<ItemHistoryBinding>()
{
    override fun getLayout(): Int {
        return R.layout.item_history
    }

    override fun bind(viewBinding: ItemHistoryBinding, position: Int) {
        viewBinding.result=value
    }

}