package com.example.travelplanner10

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("searchJSON")
    fun searchLocations(
        @Query("q") query: String,
        @Query("maxRows") maxRows: Int = 10,
        @Query("username") username: String = "gaspare"
    ): Call<GeoNamesResponse>
}

data class GeoNamesResponse(
    val geonames: List<GeoName>
)

data class GeoName(
    val name: String,
    val countryName: String,
    val lat: Double,
    val lng: Double
)
