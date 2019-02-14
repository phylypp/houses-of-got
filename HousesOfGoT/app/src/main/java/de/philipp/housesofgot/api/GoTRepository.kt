package de.philipp.housesofgot.api

import de.philipp.housesofgot.model.House
import io.reactivex.Single

/**
 * The data source for house data. Corresponds to the API of Ice And Fire.
 */
interface GoTRepository {

    /**
     * @see de.philipp.housesofgot.api.ApiOfIceAndFire.getHouses
     */
    fun getHouses(page: Int, pageSize: Int): Single<List<House>>

    /**
     * @see de.philipp.housesofgot.api.ApiOfIceAndFire.getHouse
     */
    fun getHouse(houseId: Int): Single<House>
}
