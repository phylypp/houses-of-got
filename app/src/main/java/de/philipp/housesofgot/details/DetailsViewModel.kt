package de.philipp.housesofgot.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.philipp.housesofgot.State
import de.philipp.housesofgot.model.HouseResolved

/**
 * Abstraction for the details view ViewModel.
 */
abstract class DetailsViewModel : ViewModel() { // not an interface to enforce ViewModel dependency

    /**
     * Observers will be notified about resolved house data.
     */
    abstract var houseResolved: MutableLiveData<HouseResolved>

    /**
     * Observers will be notified about the current state of the loading process.
     */
    abstract var state: MutableLiveData<State>

    /**
     * Restarts the data loading.
     */
    abstract fun retry()
}