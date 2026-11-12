
package com.example.brewkery.data.repository

import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.remote.BrewkeryApi

class MenuRepository(
    private val api: BrewkeryApi
) {
    suspend fun getMenu(): MenuResponse {
        return api.getMenu()
    }

    suspend fun getItem(itemId: Int): MenuItem {
        return api.getItem(itemId)
    }
}
