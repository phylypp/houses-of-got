package de.philipp.housesofgot

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import kotlinx.android.synthetic.main.loading_state.view.*

/**
 * Loading state.
 */
enum class State {
    DONE, LOADING, ERROR
}

/**
 * Configures loading indicator, retry button and error message.
 *
 * @param context view context
 * @param showLoading if the loading indicator should be displayed
 * @param showError if an error message should be displayed
 * @param stateView state view reference
 */
fun setStateView(context: Context, showLoading: Boolean, showError: Boolean, stateView: View) {

    stateView.pb_loading.visibility = if (showLoading) View.VISIBLE else View.GONE

    if (showError) {
        stateView.btn_retry.visibility = View.VISIBLE
        stateView.tv_error.visibility = View.VISIBLE
        stateView.tv_error.text = context.getString(if (isConnected(context)) R.string.error else R.string.no_internet)
    } else {
        stateView.btn_retry.visibility = View.GONE
        stateView.tv_error.visibility = View.GONE
    }
}

/**
 * @return true if a wifi or mobile internet connection is active
 */
@Suppress("DEPRECATION")
fun isConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}