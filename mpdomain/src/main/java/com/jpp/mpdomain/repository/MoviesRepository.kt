package com.jpp.mpdomain.repository

import com.jpp.mpdomain.*

/**
 * Repository definition to access all movies related data.
 */
interface MoviesRepository {

    /**
     * Retrieves a [MoviePage] for the provided [section] and [language].
     * @return The [MoviePage] indicated by [page] if any [MoviePage] exists for it.
     * Otherwise, null.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection, language: SupportedLanguage): MoviePage?

    /**
     * Retrieves a [MovieDetail] for a particular movie.
     * @return a [MovieDetail] instance if a detail can be found for the provided [movieId],
     * null in any other case.
     */
    fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail?


    /**
     * @return the [MovieAccountState] of a particular movie identified by [movieId].
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState?
}