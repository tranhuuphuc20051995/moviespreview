package com.jpp.moviespreview.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * This is the ViewModel that backs the behavior supported by the MainActivity.
 * It is a shared ViewModel: all Fragments that needs to update the MainActivity
 * UI can access this ViewModel (since all Fragments are hosted by the MainActivity and this
 * ViewModel updates the MainActivity UI) and do the things that they require.
 */
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val actions = MutableLiveData<MainActivityAction>()

    val viewState: LiveData<MainActivityViewState> = map(actions) { action ->
        when (action) {
            is MainActivityAction.UserSelectedMovieDetails -> MainActivityViewState.ActionBarUnlocked(action.movieImageUrl, action.movieTitle)
            MainActivityAction.UserSelectedMovieList -> MainActivityViewState.ActionBarLocked
        }
    }

    fun onAction(action: MainActivityAction) {
        actions.value = action
    }
}