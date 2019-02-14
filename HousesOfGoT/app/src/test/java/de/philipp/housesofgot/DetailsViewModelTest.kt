package de.philipp.housesofgot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.details.DetailsViewModelImpl
import de.philipp.housesofgot.model.House
import de.philipp.housesofgot.model.HouseResolved
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class DetailsViewModelTest : KoinComponent {

    @Mock
    lateinit var houseObserver: Observer<HouseResolved>

    @Mock
    lateinit var stateObserver: Observer<State>

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { AndroidSchedulers.mainThread() }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun loadAndSucceed() {
        val dummyHouse = House(
            "test name",
            "test region",
            null, null, null, null
        )
        val detailsViewModel = DetailsViewModelImpl(dummyHouse)

        detailsViewModel.houseResolved.observeForever(houseObserver)
        detailsViewModel.state.observeForever(stateObserver)
        assertEquals(State.DONE, detailsViewModel.state.value)
    }

    @Test
    fun loadAndResolve() {
        val repository = Mockito.mock(GoTRepository::class.java)
        loadKoinModules(module {
            single(override = true) { repository }
        })

        val dummyHouse =
            House(
                "test name",
                "test region",
                null, null, null,
                "https://www.anapioficeandfire.com/api/houses/1"
            )

        val dummyOverlord = Single.just(
            House(
                "test overlord name", "test region",
                null, null, null, null
            )
        )
        `when`(repository.getHouse(1))
            .thenReturn(dummyOverlord)

        val detailsViewModel = DetailsViewModelImpl(dummyHouse)

        detailsViewModel.houseResolved.observeForever(houseObserver)
        detailsViewModel.state.observeForever(stateObserver)

        assertEquals(State.DONE, detailsViewModel.state.value)
        assertEquals("test overlord name", detailsViewModel.houseResolved.value?.overlord)
    }

    @Test
    fun failAndRetry() {
        val repository = Mockito.mock(GoTRepository::class.java)
        loadKoinModules(module {
            single(override = true) { repository }
        })

        val dummyHouse =
            House(
                "test name",
                "test region",
                null, null, null,
                "https://www.anapioficeandfire.com/api/houses/1"
            )

        `when`(repository.getHouse(1))
            .thenReturn(Single.error(RuntimeException()))

        val detailsViewModel = DetailsViewModelImpl(dummyHouse)

        detailsViewModel.houseResolved.observeForever(houseObserver)
        detailsViewModel.state.observeForever(stateObserver)

        assertEquals(State.ERROR, detailsViewModel.state.value)
        assertNull(detailsViewModel.houseResolved.value)

        `when`(repository.getHouse(1))
            .thenReturn(Single.just(dummyHouse))

        detailsViewModel.retry()

        assertEquals(State.DONE, detailsViewModel.state.value)
        assertNotNull(detailsViewModel.houseResolved.value)
    }
}
