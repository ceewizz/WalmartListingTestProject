package com.example.walmart.presentation.countries

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.walmart.presentation.R
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.P]
)
class CountriesFragmentLayoutTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `layout structure is correct`() {
        // Create a mock ViewModel with predefined state
        val mockViewModel = mockk<CountriesViewModel>(relaxed = true) {
            every { state } returns MutableStateFlow(
                CountriesViewModel.State(
                    items = emptyList(),
                    loading = false
                )
            )
            every { effectFlow } returns MutableStateFlow(
                CountriesViewModel.Effect.OpenDetails("US")
            )
        }

        // Launch the fragment with the mock ViewModel
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountriesFragment().apply {
                setViewModel(mockViewModel) // Inject the mock ViewModel
            }
        }

        scenario.onFragment { fragment ->
            val rootView = fragment.requireView() as LinearLayout

            // Verify layout parameters
            assert(rootView.layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                "Root layout width should match parent"
            }
            assert(rootView.layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                "Root layout height should match parent"
            }

            // Verify orientation
            assert(rootView.orientation == LinearLayout.VERTICAL) {
                "LinearLayout should have vertical orientation"
            }

            // Verify number of direct children
            assert((rootView as ViewGroup).childCount == 2) {
                "Root layout should have exactly 2 children (Toolbar and SwipeRefreshLayout)"
            }
        }
    }

    @Test
    fun `toolbar configuration is correct`() {
        // Create a mock ViewModel
        val mockViewModel = mockk<CountriesViewModel>(relaxed = true) {
            every { state } returns MutableStateFlow(
                CountriesViewModel.State(
                    items = emptyList(),
                    loading = false
                )
            )
            every { effectFlow } returns MutableStateFlow(
                CountriesViewModel.Effect.OpenDetails("US")
            )
        }

        // Launch fragment with mock ViewModel
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountriesFragment().apply {
                setViewModel(mockViewModel) // Inject the mock ViewModel
            }
        }

        // Verify Toolbar exists and has correct attributes
        onView(withId(R.id.action_bar))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withText(R.string.countries_title))))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `recycler view configuration is correct`() {
        // Create a mock ViewModel
        val mockViewModel = mockk<CountriesViewModel>(relaxed = true) {
            every { state } returns MutableStateFlow(
                CountriesViewModel.State(
                    items = emptyList(),
                    loading = false
                )
            )
            every { effectFlow } returns MutableStateFlow(
                CountriesViewModel.Effect.OpenDetails("US")
            )
        }

        // Launch fragment with mock ViewModel
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        ) {
            CountriesFragment().apply {
                setViewModel(mockViewModel) // Inject the mock ViewModel
            }
        }

        // Verify RecyclerView exists and has correct configuration
        onView(withId(R.id.country_rv))
            .check(matches(isDisplayed()))
            .check(matches(hasLinearLayoutManager()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    // Custom matcher for LinearLayoutManager
    private fun hasLinearLayoutManager(): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView with LinearLayoutManager")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                return recyclerView.layoutManager is LinearLayoutManager
            }
        }
    }

    // Test Application class for Robolectric
    class TestApplication : android.app.Application() {
        override fun onCreate() {
            super.onCreate()
            // Any test-specific initialization
        }
    }
}
