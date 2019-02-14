package de.philipp.housesofgot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.list.data.ListViewModelImpl
import de.philipp.housesofgot.model.House
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ListViewModelTest : KoinComponent {

    @Mock
    lateinit var pageObserver: Observer<in PagedList<House>>

    @Mock
    lateinit var stateObserver: Observer<State>

    @Mock
    lateinit var repository: GoTRepository

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val dummyHouses: Single<List<House>>
        get() {
            val dummyHouse = House(
                "test name",
                "test region",
                null, null, null, null
            )
            val dummyHouses = ArrayList<House>()
            for (i in 0..14) {
                dummyHouses.add(dummyHouse)
            }
            return Single.just(dummyHouses)
        }

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { AndroidSchedulers.mainThread() }

        loadKoinModules(module {
            single(override = true) { repository }
        })
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun loadAndSucceed() {
        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(dummyHouses)

        val listViewModel = ListViewModelImpl()
        listViewModel.housesList.observeForever(pageObserver)
        listViewModel.state.observeForever(stateObserver)

        assertEquals(15, listViewModel.housesList.value?.size)
        verify(stateObserver).onChanged(State.DONE)
    }

    @Test
    fun failAndRetry() {
        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(Single.error(RuntimeException()))

        val listViewModel = ListViewModelImpl()
        listViewModel.housesList.observeForever(pageObserver)
        listViewModel.state.observeForever(stateObserver)

        assertEquals(0, listViewModel.housesList.value?.size)
        verify(stateObserver).onChanged(State.ERROR)
        verify(stateObserver, never()).onChanged(State.DONE)

        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(dummyHouses)

        listViewModel.retry()

        assertEquals(15, listViewModel.housesList.value?.size)
        verify(stateObserver).onChanged(State.DONE)
    }

}
