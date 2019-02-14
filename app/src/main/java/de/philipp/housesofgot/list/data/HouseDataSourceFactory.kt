package de.philipp.housesofgot.list.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import de.philipp.housesofgot.model.House
import io.reactivex.disposables.CompositeDisposable

/**
 * House data source factory, necessary for the LivePagedListBuilder.
 */
class HouseDataSourceFactory(private val compositeDisposable: CompositeDisposable) :
    DataSource.Factory<Int, House>() {

    /**
     * The created house data source.
     */
    val houseDataSourceLiveData = MutableLiveData<HouseDataSource>()

    override fun create(): DataSource<Int, House> {
        val houseDataSource = HouseDataSource(compositeDisposable)
        houseDataSourceLiveData.postValue(houseDataSource)
        return houseDataSource
    }
}