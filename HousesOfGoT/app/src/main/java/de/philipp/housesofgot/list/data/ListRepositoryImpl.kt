package de.philipp.housesofgot.list.data

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable

class ListRepositoryImpl(compositeDisposable: CompositeDisposable) :
    ListRepository {

    override val factory: HouseDataSourceFactory = HouseDataSourceFactory(compositeDisposable)

    override val houseDataSourceLiveData: MutableLiveData<HouseDataSource> = factory.houseDataSourceLiveData
}