package com.jpp.mpdata.cache

import android.util.SparseArray
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDAO
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.datasources.moviepage.MoviesDb
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * [MoviesDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MoviesCache(private val roomDatabase: MPRoomDataBase,
                  private val adapter: RoomModelAdapter,
                  private val timestampHelper: CacheTimestampHelper) : MoviesDb {

    private val favoriteMovies = SparseArray<MoviePage>()
    private val ratedMovies = SparseArray<MoviePage>()
    private val watchlist = SparseArray<MoviePage>()

    override fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage? {
        return withMovieDao {
            getMoviePage(page, section.name, now())?.let { dbMoviePage ->
                getMoviesFromPage(dbMoviePage.id)?.let { movieList ->
                    transformWithAdapter { adaptDBMoviePageToDataMoviePage(dbMoviePage, movieList) }
                }
            }
        }
    }

    override fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection) {
        withMovieDao {
            insertMoviePage(transformWithAdapter {
                adaptDataMoviePageToDBMoviePage(moviePage, section.name, moviePagesRefreshTime())
            })
        }.let {
            moviePage.results.map { movie -> transformWithAdapter { adaptDataMovieToDBMovie(movie, it) } }
        }.also {
            withMovieDao { insertMovies(it) }
        }
    }

    override fun getFavoriteMovies(page: Int): MoviePage? = favoriteMovies[page]

    override fun saveFavoriteMoviesPage(page: Int, moviePage: MoviePage) {
        favoriteMovies.put(page, moviePage)
    }

    override fun getRatedMovies(page: Int): MoviePage? = ratedMovies[page]

    override fun saveRatedMoviesPage(page: Int, moviePage: MoviePage) {
        ratedMovies.put(page, moviePage)
    }

    override fun getWatchlistMoviePage(page: Int): MoviePage? = watchlist[page]

    override fun saveWatchlistMoviePage(page: Int, moviePage: MoviePage) {
        watchlist.put(page, moviePage)
    }

    override fun flushFavoriteMoviePages() {
        favoriteMovies.clear()
    }

    override fun flushRatedMoviePages() {
        ratedMovies.clear()
    }

    override fun flushWatchlistMoviePages() {
        watchlist.clear()
    }

    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withMovieDao(action: MovieDAO.() -> T): T = with(roomDatabase.moviesDao()) { action.invoke(this) }

    private fun now() = timestampHelper.now()

    private fun moviePagesRefreshTime() = with(timestampHelper) { now() + moviePagesRefreshTime() }
}