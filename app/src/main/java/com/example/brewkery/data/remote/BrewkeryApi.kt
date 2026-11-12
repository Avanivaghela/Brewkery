
package com.example.brewkery.data.remote

import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.model.MenuItem
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApi {

    // Fetch complete menu and store information
    @GET("data.json")
    suspend fun getMenu(): MenuResponse

    // Fetch individual item details
    @GET("api/items/{id}.json")
    suspend fun getItem(
        @Path("id") itemId: Int
    ): MenuItem
}
