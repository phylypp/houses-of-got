package de.philipp.housesofgot.details

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import de.philipp.housesofgot.R
import de.philipp.housesofgot.State
import de.philipp.housesofgot.model.House
import de.philipp.housesofgot.model.HouseResolved
import de.philipp.housesofgot.setStateView
import kotlinx.android.synthetic.main.activity_house_details.*
import kotlinx.android.synthetic.main.loading_state.*
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.KoinComponent
import org.koin.core.parameter.parametersOf

const val EXTRA_HOUSE = "EXTRA_HOUSE"

/**
 * Activity displaying details of a house.
 */
class DetailsActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: DetailsViewModel by viewModel { parametersOf(house) }

    /**
     * The house to display the details for.
     */
    private val house: House?  by lazy { intent?.extras?.getSerializable(EXTRA_HOUSE) as? House }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_details)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = house?.name

        initState()
        initDetails()
    }

    /**
     * Observe changes for house details with resolved URLs.
     */
    private fun initDetails() {
        viewModel.houseResolved.observe(this, Observer {
            showHouseData(it)
        })
    }

    /**
     * Observe the loading state.
     */
    private fun initState() {
        btn_retry.setOnClickListener { viewModel.retry() }
        viewModel.state.observe(this, Observer { state ->
            setStateView(
                state == State.LOADING,
                state == State.ERROR,
                state_view
            )
        })
    }

    /**
     * Display the house details.
     *
     * @param house house details with resolved URLs
     */
    private fun showHouseData(house: HouseResolved) {
        showDetail(house.region, tv_region_title, tv_region_content)
        showDetail(house.coatOfArms, tv_coat_title, tv_coat_content)
        showDetail(house.words, tv_words_title, tv_words_content)
        showDetail(house.seats, tv_seats_title, tv_seats_content)
        showDetail(house.overlord, tv_overlord_title, tv_overlord_content)
    }

    /**
     * Display a house info.
     *
     * @param detail the info to display
     * @param tvTitle the view displaying the info title
     * @param tvContent the view displaying the info
     */
    private fun showDetail(detail: String?, tvTitle: TextView, tvContent: TextView) {
        detail?.takeIf { it.isNotBlank() }?.let {
            tvTitle.visibility = View.VISIBLE
            tvContent.visibility = View.VISIBLE
            tvContent.text = it
        }
    }

    /**
     * Display a house info list.
     *
     * @param detail the info list to display
     * @param tvTitle the view displaying the info title
     * @param tvContent the view displaying the info
     */
    private fun showDetail(detail: List<String>?, tvTitle: TextView, tvContent: TextView) {
        showDetail(detail?.joinToString(), tvTitle, tvContent)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = if (item?.itemId == android.R.id.home) {
        onBackPressed()
        true
    } else {
        super.onOptionsItemSelected(item)
    }
}