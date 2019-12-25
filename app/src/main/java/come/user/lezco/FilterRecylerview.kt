package come.user.lezco

import com.xwray.groupie.databinding.BindableItem
import come.user.lezco.databinding.ItemFilterBinding
import come.user.lezco.model.DriverFilter

class FilterRecylerview(val item:DriverFilter.Data):BindableItem<ItemFilterBinding>()
{
    override fun getLayout(): Int=R.layout.item_filter

    override fun bind(viewBinding: ItemFilterBinding, position: Int) {
        viewBinding.data=item
    }

}