package de.philipp.housesofgot.list.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import de.philipp.housesofgot.State
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.isConnected
import de.philipp.housesofgot.model.House
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 * Data source providing methods for page loading.
 */
class HouseDataSource(private val compositeDisposable: CompositeDisposable) :
    PageKeyedDataSource<Int, House>(), KoinComponent {

    /**
     * The repository to load house data from.
     */
    private val repository: GoTRepository by inject()

    /**
     * The current loading state.
     */
    var state: MutableLiveData<State> = MutableLiveData()

    /**
     * Action to perform when retry is triggered (load initial page or next pages).
     */
    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, House>) {
        updateState(State.LOADING)
        compositeDisposable.add(
            repository.getHouses(1, params.requestedLoadSize)
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(
                            response,
                            null,
                            2
                        )
                    },
                    {
                        Log.e(HouseDataSource::class.toString(), "Failed to load houses.", it)
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, House>) {
        updateState(State.LOADING)
        compositeDisposable.add(
            repository.getHouses(params.key, params.requestedLoadSize)
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(
                            response,
                            params.key + 1
                        )
                    },
                    {
                        Log.e(HouseDataSource::class.toString(), "Failed to load houses.", it)
                        updateState(State.ERROR)
                        setRetry(Action { loadAfter(params, callback) })
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, House>) {
        // not used
    }

    /**
     * Update the current state.
     */
    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    /**
     * Retry the page loading.
     */
    fun retry() {
        retryCompletable?.let {
            compositeDisposable.add(
                it
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            )
        }
    }

    /**
     * Define what to do when retry is triggered.
     *
     * @param action the action like loadAfter or loadInitial
     */
    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }
}