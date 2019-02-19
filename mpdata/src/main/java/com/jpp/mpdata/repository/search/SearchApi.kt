package com.jpp.mpdata.repository.search

import com.jpp.mpdomain.SearchPage

/**
 * API definition to perform a searchPage.
 */
interface SearchApi {

    /**
     * @return a [SearchPage] that contains all the results for the searchPage
     * executed for the given parameters.
     * Null if no data is available.
     */
    fun performSearch(query: String, page: Int): SearchPage?
}