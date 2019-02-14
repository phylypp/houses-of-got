package de.philipp.housesofgot

import android.app.Application
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.api.GoTRepositoryImpl
import de.philipp.housesofgot.details.DetailsViewModel
import de.philipp.housesofgot.details.DetailsViewModelImpl
import de.philipp.housesofgot.list.data.ListRepository
import de.philipp.housesofgot.list.data.ListRepositoryImpl
import de.philipp.housesofgot.list.data.ListViewModel
import de.philipp.housesofgot.list.data.ListViewModelImpl
import de.philipp.housesofgot.model.House
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Base application class.
 */
@Suppress("unused")
class App : Application() {

    /**
     * Module definitions for dependency injection.
     */
    private val appModule = module {
        single<GoTRepository> { GoTRepositoryImpl() }
        factory<ListRepository> { (disposable: CompositeDisposable) ->
            ListRepositoryImpl(disposable)
        }
        viewModel<ListViewModel> { ListViewModelImpl() }
        viewModel<DetailsViewModel> { (house: House) -> DetailsViewModelImpl(house) }
    }

    override fun onCreate() {
        super.onCreate()

        // init dependency injection framework
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}