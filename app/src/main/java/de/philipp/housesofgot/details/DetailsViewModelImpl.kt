package de.philipp.housesofgot.details

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.philipp.housesofgot.State
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.model.House
import de.philipp.housesofgot.model.HouseResolved
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 * ViewModel which uses a GoT repository to resolve house details URLs.
 */
class DetailsViewModelImpl(val house: House?) : DetailsViewModel(), KoinComponent {

    /**
     * The GoT repository to get house details from.
     */
    private val repository: GoTRepository by inject()

    override var houseResolved: MutableLiveData<HouseResolved> = MutableLiveData()

    override var state: MutableLiveData<State> = MutableLiveData()

    /**
     * Disposable collection for subsequent disposal.
     */
    private val compositeDisposable = CompositeDisposable()

    init {
        loadData()
    }

    /**
     * Resolve house data in case of URL references.
     */
    private fun loadData() {
        if (house == null) {
            state.postValue(State.ERROR)
        } else if (!house.overlord.isNullOrBlank()) {
            state.postValue(State.LOADING)
            loadOverlord(house.overlord)
        } else {
            state.postValue(State.DONE)
            houseResolved.postValue(HouseResolved(house, null))
        }
    }

    /**
     * Resolve an overlord URL reference.
     */
    private fun loadOverlord(overlord: String) {
        Uri.parse(overlord)?.lastPathSegment?.toIntOrNull()?.let {
            compositeDisposable.add(
                repository.getHouse(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { character ->
                            state.postValue(State.DONE)
                            houseResolved.postValue(
                                HouseResolved(
                                    house,
                                    character.name
                                )
                            )
                        },
                        { t ->
                            Log.e(DetailsViewModelImpl::class.toString(), "Failed to load overlord.", t)
                            state.postValue(State.ERROR)
                        }
                    )
            )
        } ?: let {
            state.postValue(State.DONE)
            houseResolved.postValue(HouseResolved(house, null))
        }
    }

    override fun retry() {
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}