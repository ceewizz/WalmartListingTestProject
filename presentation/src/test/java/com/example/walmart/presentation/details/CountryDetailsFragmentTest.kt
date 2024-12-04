package com.example.walmart.presentation.details

import android.app.Application
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.walmart.domain.model.Country
import com.example.walmart.presentation.R
import com.google.android.material.textview.MaterialTextView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.action.ViewActions.swipeDown

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.P],
    application = CountryDetailsFragmentTest.TestApplication::class
)
class CountryDetailsFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockViewModel: CountryDetailsViewModel
    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        // Mock ViewModel
        mockViewModel = mockk(relaxed = true) {
            every { state } returns MutableStateFlow(CountryDetailsViewModel.State())
        }

        // Setup NavController
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        navController.setGraph(R.navigation.main_graph)
    }

    @Test
    fun `fragment launches successfully`() {
        val scenario = launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = bundleOf(CountryDetailsArg.COUNTRY_CODE to "US"),
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountryDetailsFragment().apply {
                viewModel = mockViewModel
            }
        }

        scenario.onFragment { fragment ->
            assertNotNull("Fragment view should not be null", fragment.view)
        }
    }

    @Test
    fun `toolbar is configured correctly`() {
        val scenario = launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = bundleOf(CountryDetailsArg.COUNTRY_CODE to "US"),
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountryDetailsFragment().apply {
                viewModel = mockViewModel
            }
        }

        scenario.onFragment { fragment ->
            val toolbar = fragment.view?.findViewById<Toolbar>(R.id.action_bar)
            assertNotNull("Toolbar should be inflated", toolbar)

            // Verify toolbar title
            assertEquals("Country Details", toolbar?.title)

            // Verify navigation icon
            assertNotNull("Navigation icon should be set", toolbar?.navigationIcon)
        }
    }

    @Test
    fun `displays country details correctly`() = runTest {
        // Prepare test data
        val testCountry = Country(
            name = "United States",
            region = "North America",
            code = "US",
            capital = "Washington D.C."
        )

        // Mock ViewModel state
        every { mockViewModel.state } returns MutableStateFlow(
            CountryDetailsViewModel.State(country = testCountry)
        )

        val scenario = launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = bundleOf(CountryDetailsArg.COUNTRY_CODE to "US"),
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountryDetailsFragment().apply {
                viewModel = mockViewModel
            }
        }

        scenario.onFragment { fragment ->
            // Check country details are displayed
            onView(withId(R.id.name_with_region_view))
                .check(matches(withText(containsString("United States, North America"))))

            onView(withId(R.id.code_view))
                .check(matches(withText("US")))

            onView(withId(R.id.capital_view))
                .check(matches(withText("Washington D.C.")))
        }
    }

    @Test
    fun `displays error message when error occurs`() {
        // Prepare error state
        every { mockViewModel.state } returns MutableStateFlow(
            CountryDetailsViewModel.State(errorMessage = "Network Error")
        )

        val scenario = launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = bundleOf(CountryDetailsArg.COUNTRY_CODE to "US"),
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountryDetailsFragment().apply {
                viewModel = mockViewModel
            }
        }

        scenario.onFragment { fragment ->
            val errorView = fragment.view?.findViewById<MaterialTextView>(R.id.error_message)
            assertNotNull("Error message view should exist", errorView)

            // Check error message visibility and text
            onView(withId(R.id.error_message))
                .check(matches(isDisplayed()))
                .check(matches(withText("Network Error")))
        }
    }

    @Test
    fun `loading state shows progress`() {
        // Prepare loading state
        every { mockViewModel.state } returns MutableStateFlow(
            CountryDetailsViewModel.State(loading = true)
        )

        val scenario = launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = bundleOf(CountryDetailsArg.COUNTRY_CODE to "US"),
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountryDetailsFragment().apply {
                viewModel = mockViewModel
            }
        }

        scenario.onFragment { fragment ->
            val swipeRefreshLayout = fragment.view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
            assertNotNull("SwipeRefreshLayout should exist", swipeRefreshLayout)

            // Check if SwipeRefreshLayout is in loading state
            assertTrue("SwipeRefreshLayout should be refreshing", swipeRefreshLayout?.isRefreshing == true)
        }
    }

    // Test Application class for Robolectric
    class TestApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            // Any test-specific initialization
        }
    }
}