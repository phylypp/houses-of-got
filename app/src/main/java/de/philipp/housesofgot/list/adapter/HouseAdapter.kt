package de.philipp.housesofgot.list.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.philipp.housesofgot.State
import de.philipp.housesofgot.model.House


/**
 * View type id of a house view.
 */
private const val DATA_VIEW_TYPE = 1

/**
 * View type id of the loading / retry footer.
 */
private const val FOOTER_VIEW_TYPE = 2

/**
 * The page list adapter for the houses list.
 */
class HouseListAdapter(private val retry: () -> Unit, private val click: (house: House) -> Unit) :
    PagedListAdapter<House, RecyclerView.ViewHolder>(HouseItemDiffCallback) {

    /**
     * The current page state.
     */
    private var state = State.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == DATA_VIEW_TYPE) {
        createHouseViewHolder(parent)
    } else {
        createListFooterViewHolder(retry, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE) {
            (holder as HouseViewHolder).bind(getItem(position), click)
        } else {
            (holder as ListFooterViewHolder).bind(state)
        }
    }

    override fun getItemViewType(position: Int) =
        if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE

    override fun getItemCount() = super.getItemCount() + if (hasFooter()) 1 else 0

    /**
     * @return true if the list displays a footer at the moment
     */
    private fun hasFooter() = super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR)

    /**
     * Update the list state. All list items will be updated.
     *
     * @param state the new list state
     */
    fun setState(state: State) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    companion object {
        /**
         * Callback for the pager adapter to be able to recognize list data changes.
         */
        val HouseItemDiffCallback = object : DiffUtil.ItemCallback<House>() {
            override fun areItemsTheSame(oldItem: House, newItem: House): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: House, newItem: House): Boolean {
                return oldItem == newItem
            }
        }
    }
}