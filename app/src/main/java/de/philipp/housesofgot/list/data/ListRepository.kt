package de.philipp.housesofgot.list.data

import androidx.lifecycle.MutableLiveData

/**
 * Wrapper bundle with a house data factory and its created value as live data.
 */
interface ListRepository {

    /**
     * The house data source factory.
     */
    val factory: HouseDataSourceFactory

    /**
     * The house data source to observe.
     */
    val houseDataSourceLiveData: MutableLiveData<HouseDataSource>
}