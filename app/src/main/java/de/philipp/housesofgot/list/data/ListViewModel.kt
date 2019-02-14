package de.philipp.housesofgot.list.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import de.philipp.housesofgot.State
import de.philipp.housesofgot.model.House

/**
 * Abstraction for the list view ViewModel.
 */
abstract class ListViewModel : ViewModel() { // not an interface to enforce ViewModel dependency

    /**
     * Observable page list data will give all the houses.
     */
    abstract var housesList: LiveData<PagedList<House>>

    /**
     * Observers will be notified about the current state of the loading process.
     */
    abstract val state: LiveData<State>

    /**
     * Restarts the data loading.
     */
    abstract fun retry()

    /**
     * @return true if the house live data does not contain any values.
     */
    abstract fun isListEmpty(): Boolean
}