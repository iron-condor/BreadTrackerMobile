package com.condor.breadtrackermobile

import retrofit2.Response
import retrofit2.http.PUT
import retrofit2.http.Path

interface BreadTrackerAPI {
    @PUT("/starters/feed/{uuid}")
    suspend fun feedStarter(@Path(value="uuid") uuid: String): Response<UpdateStarterResponse>


    data class UpdateStarterResponse (
        val success: Boolean,
        val errorMessage: String,
        val starter: Starter
    )

    data class Starter(
        val uuid: String,
        val name: String,
        val flourType: String,
        val inFridge: Boolean,
        val timeLastFed: Long,
        val nextFeedingTime: Long
    )
}