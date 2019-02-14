package de.philipp.housesofgot.list.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.philipp.housesofgot.State
import de.philipp.housesofgot.model.House
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

/**
 * The page size to use when loading data.
 */
private const val PAGE_SIZE = 15

/**
 * Implementation of a list view model.
 */
class ListViewModelImpl : ListViewModel(), KoinComponent {

    override lateinit var housesList: LiveData<PagedList<House>>

    /**
     * Disposable collection for subsequent disposal.
     */
    private val compositeDisposable = CompositeDisposable()

    /**
     * Provides a factory for a house data source.
     */
    private val listRepository: ListRepository by inject { parametersOf(compositeDisposable) }

    override val state: LiveData<State>
        get() = Transformations.switchMap<HouseDataSource, State>(
            listRepository.houseDataSourceLiveData,
            HouseDataSource::state
        )

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setEnablePlaceholders(false)
            .build()
        housesList = LivePagedListBuilder<Int, House>(listRepository.factory, config).build()
    }

    override fun retry() {
        listRepository.houseDataSourceLiveData.value?.retry()
    }

    override fun isListEmpty() = housesList.value?.isEmpty() != false

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}