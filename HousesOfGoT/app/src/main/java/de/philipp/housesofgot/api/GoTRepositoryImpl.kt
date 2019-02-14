package de.philipp.housesofgot.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository implementation which loads house data using retrofit.
 */
class GoTRepositoryImpl : GoTRepository {

    private val apiOfIceAndFire: ApiOfIceAndFire = Retrofit.Builder()
        .baseUrl("https://anapioficeandfire.com/api/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiOfIceAndFire::class.java)

    override fun getHouses(page: Int, pageSize: Int) = apiOfIceAndFire.getHouses(page, pageSize)

    override fun getHouse(houseId: Int) = apiOfIceAndFire.getHouse(houseId)
}