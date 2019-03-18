package com.jpp.mp.screens.main.credits

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jpp.mp.R
import com.jpp.mp.assertions.*
import com.jpp.mp.di.TestMPViewModelFactory
import com.jpp.mp.extras.launch
import com.jpp.mp.testutils.FragmentTestActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditsFragmentTest {

    @get:Rule
    val activityTestRule = object : ActivityTestRule<FragmentTestActivity>(FragmentTestActivity::class.java, true, false) {}

    private fun launchAndInjectFragment() {
        val fragment = CreditsFragment().apply {
            arguments = Bundle().apply {
                putString("movieId", "12")
                putString("movieTitle", "aMovie")
            }
        }
        activityTestRule.activity.startFragment(fragment, this@CreditsFragmentTest::inject)
    }

    private fun inject(fragment: CreditsFragment) {
        val viewModel = mockk<CreditsViewModel>(relaxed = true)
        every { viewModel.viewState() } returns viewStateLiveData
        fragment.viewModelFactory = TestMPViewModelFactory().apply {
            addVm(viewModel)
        }
    }

    private val viewStateLiveData = MutableLiveData<CreditsViewState>()

    @Before
    fun setUp() {
        activityTestRule.launch()
    }

    @Test
    fun shouldShowLoading() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(CreditsViewState.Loading)

        onContentView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()

        onLoadingView().assertDisplayed()
    }

    @Test
    fun shouldShowErrorUnknownView() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(CreditsViewState.ErrorUnknown)

        onLoadingView().assertNotDisplayed()
        onContentView().assertNotDisplayed()

        onErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_unexpected_error_message)
    }

    @Test
    fun shouldShowConnectivityError() {
        launchAndInjectFragment()

        viewStateLiveData.postValue(CreditsViewState.ErrorNoConnectivity)

        onLoadingView().assertNotDisplayed()
        onContentView().assertNotDisplayed()

        onErrorView().assertDisplayed()
        onErrorViewText().assertWithText(R.string.error_no_network_connection_message)
    }

    @Test
    fun shouldShowContent() {
        val personList = creditsPersonList()

        launchAndInjectFragment()

        viewStateLiveData.postValue(CreditsViewState.ShowCredits(personList))

        onLoadingView().assertNotDisplayed()
        onErrorView().assertNotDisplayed()

        onContentView().assertDisplayed()
        onContentView().assertItemCount(personList.size)

        onView(withViewInRecyclerView(R.id.creditsRv, 0, R.id.creditsItemTitle))
                .check(matches(ViewMatchers.withText(personList[0].title)))

        onView(withViewInRecyclerView(R.id.creditsRv, 0, R.id.creditsItemSubTitle))
                .check(matches(ViewMatchers.withText(personList[0].subTitle)))

        onView(withViewInRecyclerView(R.id.creditsRv, 1, R.id.creditsItemTitle))
                .check(matches(ViewMatchers.withText(personList[1].title)))

        onView(withViewInRecyclerView(R.id.creditsRv, 1, R.id.creditsItemSubTitle))
                .check(matches(ViewMatchers.withText(personList[1].subTitle)))
    }

    private fun onLoadingView() = onView(withId(R.id.creditsLoadingView))
    private fun onErrorView() = onView(withId(R.id.creditsErrorView))
    private fun onContentView() = onView(withId(R.id.creditsRv))


    private fun creditsPersonList(): List<CreditPerson> {
        val list = mutableListOf<CreditPerson>()
        for (i in 0..19) {
            list.add(CreditPerson(id = i.toDouble(),
                    profilePath = "aPath$i",
                    title = "aTitle$i",
                    subTitle = "aSubtitle$i"))
        }
        return list
    }
}