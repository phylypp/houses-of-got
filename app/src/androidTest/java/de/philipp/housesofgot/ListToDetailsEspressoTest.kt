package de.philipp.housesofgot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import de.philipp.housesofgot.api.GoTRepository
import de.philipp.housesofgot.api.GoTRepositoryImpl
import de.philipp.housesofgot.list.ListActivity
import de.philipp.housesofgot.list.adapter.HouseViewHolder
import de.philipp.housesofgot.model.House
import io.reactivex.Single
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class ListToDetailsEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(ListActivity::class.java, true, false)

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

    @Test
    fun loadListAndDetailsMocked() {
        val repository = mock(GoTRepository::class.java)
        loadKoinModules(module {
            single(override = true) { repository }
        })

        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(dummyHouses)

        activityRule.launchActivity(null)

        onView(withText("Houses of GoT"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HouseViewHolder>(0, click()))

        onView(withText("Region"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_region_content))
            .check(matches(withText("test region")))
    }

    @Test
    fun listErrorAndRetryMocked() {
        val repository = mock(GoTRepository::class.java)
        loadKoinModules(module {
            single(override = true) { repository }
        })

        val housesResult: Single<List<House>> = Single.error(RuntimeException())
        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(housesResult)

        activityRule.launchActivity(null)

        onView(withText("Houses of GoT"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btn_retry))
            .check(matches(isDisplayed()))

        `when`(repository.getHouses(anyInt(), anyInt()))
            .thenReturn(dummyHouses)

        onView(withId(R.id.btn_retry))
            .perform(click())

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HouseViewHolder>(0, click()))

        onView(withText("Region"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadListAndDetailsLive() {
        loadKoinModules(module {
            single<GoTRepository>(override = true) { GoTRepositoryImpl() }
        })

        activityRule.launchActivity(null)

        onView(withText("Houses of GoT"))
            .check(matches(isDisplayed()))

        Thread.sleep(500) // in case animations not switched off

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HouseViewHolder>(0, click()))

        Thread.sleep(500) // in case animations not switched off

        onView(withText("Region"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tv_region_content))
            .check(matches(Matchers.not(withText(""))))
    }

}
