package de.philipp.housesofgot.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.philipp.housesofgot.R
import de.philipp.housesofgot.model.House
import kotlinx.android.synthetic.main.item_house.view.*

/**
 * View holder for a house view.
 */
class HouseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(house: House?, click: (house: House) -> Unit) {
        if (house != null) {
            itemView.tv_house_name.text = house.name
            itemView.setOnClickListener { click(house) }
        }
    }
}

/**
 * @return a freshly inflated house view holder
 */
fun createHouseViewHolder(parent: ViewGroup): HouseViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.item_house, parent, false)
    return HouseViewHolder(view)
}