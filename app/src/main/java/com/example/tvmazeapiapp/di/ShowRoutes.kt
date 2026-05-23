package com.example.tvmazeapiapp.di

object ShowRoutes {
    const val LIST_ROUTE = "showList"
    const val DETAILS_ROUTE = "showDetails"
    const val SHOW_ID_ARG = "showId"

    const val DETAILS_ROUTE_PATTERN = "$DETAILS_ROUTE/{$SHOW_ID_ARG}"
    fun details(showId: Int): String = "$DETAILS_ROUTE/$showId"
}