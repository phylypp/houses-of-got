package de.philipp.housesofgot.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.philipp.housesofgot.R
import de.philipp.housesofgot.State
import de.philipp.housesofgot.details.DetailsActivity
import de.philipp.housesofgot.details.EXTRA_HOUSE
import de.philipp.housesofgot.list.adapter.HouseListAdapter
import de.philipp.housesofgot.list.data.ListViewModel
import de.philipp.housesofgot.setStateView
import kotlinx.android.synthetic.main.activity_houses_list.*
import kotlinx.android.synthetic.main.loading_state.*
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.KoinComponent


/**
 * Activity displaying a paged list of houses.
 */
class ListActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: ListViewModel by viewModel()

    /**
     * The page list adapter for the houses list.
     */
    private lateinit var houseListAdapter: HouseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_houses_list)

        initAdapter()
        initState()
    }

    /**
     * Initialize the houses list and observe house data changes.
     */
    private fun initAdapter() {
        houseListAdapter =
            HouseListAdapter({ viewModel.retry() }, { house ->
                startActivity(Intent(this, DetailsActivity::class.java)
                    .apply { putExtra(EXTRA_HOUSE, house) })
            })
        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.adapter = houseListAdapter
        viewModel.housesList.observe(this, Observer {
            houseListAdapter.submitList(it)
        })
    }

    /**
     * Observe the loading state.
     */
    private fun initState() {
        btn_retry.setOnClickListener { viewModel.retry() }
        viewModel.state.observe(this, Observer { state ->
            setStateView(
                this,
                viewModel.isListEmpty() && state == State.LOADING,
                viewModel.isListEmpty() && state == State.ERROR,
                state_view
            )
            if (!viewModel.isListEmpty()) {
                houseListAdapter.setState(state ?: State.DONE) // footer update
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = if (item?.itemId == R.id.about) {
        AlertDialog.Builder(this)
            .setTitle(R.string.licenses)
            .setMessage(R.string.licenses_list)
            .show()
        true
    } else {
        super.onOptionsItemSelected(item)
    }

}
