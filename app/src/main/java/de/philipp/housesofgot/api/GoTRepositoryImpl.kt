package de.philipp.housesofgot.api

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository implementation which loads house data using retrofit.
 */
class GoTRepositoryImpl : GoTRepository {

    private val apiOfIceAndFire: ApiOfIceAndFire = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                // workaround for https://github.com/square/okhttp/issues/3146
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()
        )
        .baseUrl("https://anapioficeandfire.com/api/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiOfIceAndFire::class.java)

    override fun getHouses(page: Int, pageSize: Int) = apiOfIceAndFire.getHouses(page, pageSize)

    override fun getHouse(houseId: Int) = apiOfIceAndFire.getHouse(houseId)
}