package com.hm.core.network

import com.hm.core.network.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("direct")
    suspend fun getGeocoding(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("limit") limit: Int = 5
    ): List<GeocodingResponse>
}

