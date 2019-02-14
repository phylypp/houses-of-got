@file:Suppress("unused")

package de.philipp.housesofgot.api

import de.philipp.housesofgot.model.House
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The API of Ice And Fire.
 *
 *  @see <a href="https://anapioficeandfire.com/Documentation">API documentation</a>
 */
interface ApiOfIceAndFire {

    /**
     * Get a list of houses sorted alphabetically.
     *
     * @param page page number, first is 1
     * @param pageSize page size, the maximum is 50
     */
    @GET("houses")
    fun getHouses(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Single<List<House>>

    /**
     * Get a specific house.
     *
     * @param houseId id of the house
     */
    @GET("houses/{houseId}")
    fun getHouse(@Path("houseId") houseId: Int): Single<House>

}