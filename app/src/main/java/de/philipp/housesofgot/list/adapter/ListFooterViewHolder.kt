package de.philipp.housesofgot.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.philipp.housesofgot.R
import de.philipp.housesofgot.State
import de.philipp.housesofgot.setStateView
import kotlinx.android.synthetic.main.loading_state.view.*

/**
 * View holder for the footer view.
 */
class ListFooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(status: State?) {
        setStateView(
            itemView.context,
            status == State.LOADING,
            status == State.ERROR,
            itemView.state_view
        )
    }
}

/**
 * @return a freshly inflated footer view holder
 */
fun createListFooterViewHolder(retry: () -> Unit, parent: ViewGroup): ListFooterViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.item_list_footer, parent, false)
    view.btn_retry.setOnClickListener { retry() }
    return ListFooterViewHolder(view)
}