package com.jpp.moviespreview.screens.main.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.movie_list_item.view.*
import javax.inject.Inject

/**
 * Base fragment definition to show the list of movies selected by the user.
 * The Fragment takes care of rendering a list of movies based on the section that
 * is being currently shown to the user.
 *
 * Each implementation of this abstract class needs to provide the section that is actually
 * representing in the application.
 * For the moment, the supported sections are:
 * 1 - Now Playing.
 * 2 - Popular.
 * 3 - Upcoming.
 * 4 - Top Rated.
 *
 * It uses the Paging Library to allow infinite scrolling in the list of movies.
 */
abstract class MoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val movieSelectionListener: (MovieItem) -> Unit = {
        findNavController().navigate(getNavDirectionsForMovieDetails(it.movieId.toString(), it.contentImageUrl, it.title))
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Set up the MovieList */
        moviesList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MoviesAdapter(movieSelectionListener)
        }

        moviesList.toZeroAlpha()
        moviesLoadingErrorView.toZeroAlpha()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        when (savedInstanceState) {
            null -> withViewModel {
                init(getScreenSizeInPixels().x, getScreenSizeInPixels().x)
            }
        }

        /*
         * Hook-up the LiveData that will be updated when the data source is created
         * and then update the adapter with the new data.
         */
        withViewModel {
            /*
             * VERY IMPORTANT: whenever we're adding an Observer to a LiveData in a Fragment,
             * we MUST use viewLifecycleOwner.
             * Discussion here: https://www.reddit.com/r/androiddev/comments/8j4ei3/fix_for_livedata_problems_with_fragments/
             */
            viewState.observe(this@MoviesFragment.viewLifecycleOwner, Observer { fragmentViewState ->
                renderViewState(fragmentViewState)
            })
            pagedList.observe(this@MoviesFragment.viewLifecycleOwner, Observer<PagedList<MovieItem>> {
                (moviesList.adapter as MoviesAdapter).submitList(it)
            })
        }
    }

    /**
     * Render the view state by hiding and showing the proper views and animations.
     */
    private fun renderViewState(viewState: MoviesFragmentViewState) {
        with(viewState) {
            when (this) {
                MoviesFragmentViewState.Loading -> {
                    moviesList.toZeroAlpha()
                    moviesLoadingErrorView.toOneAlpha()
                    moviesLoadingErrorView.showLoadingImmediate()
                }
                MoviesFragmentViewState.ErrorUnknown -> {
                    moviesList.toZeroAlpha()
                    moviesLoadingErrorView.toOneAlpha()
                    moviesLoadingErrorView.animateToUnknownError {
                        withViewModel { retryMoviesListFetch() }
                    }
                }
                MoviesFragmentViewState.ErrorUnknownWithItems -> {
                    snackBar(moviesFragmentContent, R.string.error_unexpected_error_message, R.string.error_retry) {
                        withViewModel { retryMoviesListFetch() }
                    }
                }
                MoviesFragmentViewState.ErrorNoConnectivity -> {
                    moviesList.toZeroAlpha()
                    moviesLoadingErrorView.toOneAlpha()
                    moviesLoadingErrorView.animateToNoConnectivityError {
                        withViewModel { retryMoviesListFetch() }
                    }
                }
                MoviesFragmentViewState.ErrorNoConnectivityWithItems -> {
                    snackBar(moviesFragmentContent, R.string.error_no_network_connection_message, R.string.error_retry) {
                        withViewModel { retryMoviesListFetch() }
                    }
                }
                MoviesFragmentViewState.InitialPageLoaded -> {
                    moviesLoadingErrorView.hideWithAnimation(500, 300) {
                        moviesLoadingErrorView.toZeroAlpha()
                        moviesList.animateToOneAlpha()
                    }
                }
            }
        }
    }

    private fun withViewModel(action: MoviesFragmentViewModel.() -> Unit) {
        getViewModelInstance(viewModelFactory).action()
    }


    /**
     * MUST be implemented for all fragments that are showing a list of movies in order to enable
     * navigation to the movie details section.
     */
    abstract fun getNavDirectionsForMovieDetails(movieId: String, movieImageUrl: String, movieTitle: String): NavDirections

    abstract fun getViewModelInstance(viewModelFactory: ViewModelProvider.Factory): MoviesFragmentViewModel


    class MoviesAdapter(private val movieSelectionListener: (MovieItem) -> Unit) : PagedListAdapter<MovieItem, MoviesAdapter.ViewHolder>(MovieDiffCallback()) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovie(it, movieSelectionListener)
            }
        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

            fun bindMovie(movie: MovieItem, movieSelectionListener: (MovieItem) -> Unit) {
                with(itemView) {
                    movieListItemHeaderIcon.loadImageUrlAsCircular(movie.headerImageUrl)
                    movieListItemTitle.text = movie.title
                    movieListItemImage.loadImageUrl(movie.contentImageUrl)
                    movieListItemPopularityText.text = movie.popularity
                    movieListItemVoteCountText.text = movie.voteCount
                    setOnClickListener { movieSelectionListener(movie) }
                }
            }
        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<MovieItem>() {

        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.title == newItem.title
        }
    }

}