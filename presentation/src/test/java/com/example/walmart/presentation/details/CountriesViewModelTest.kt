package com.example.walmart.presentation.countries

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.model.Country
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepo: CountryRepo

    @Mock
    private lateinit var mockErrorFormatter: ErrorFormatter

    private lateinit var viewModel: CountriesViewModel

    private val mockDispatcherProvider = object : DispatcherProvider {
        override fun io() = testDispatcher
        override fun main() = testDispatcher
        override fun default() = testDispatcher
    }

    private val mockCountries = listOf(
        Country("United States", "North America", "US", "Washington"),
        Country("Canada", "North America", "CA", "Ottawa"),
        Country("Brazil", "South America", "BR", "Brasilia")
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads countries`() = runTest {
        whenever(mockRepo.getCountries()).thenReturn(mockCountries)

        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertEquals(mockCountries.size, state.originalItems.size)
        assertEquals(mockCountries, state.originalItems)
        assertEquals(mockCountries, state.items)
        assertTrue { !state.loading }
    }


    @Test
    fun `error handling sets error message`() = runTest {
        val errorMessage = "Network Error"
        val exception = RuntimeException("Network Error")
        whenever(mockRepo.getCountries()).thenThrow(exception)
        whenever(mockErrorFormatter.getDisplayErrorMessage(any())).thenReturn(errorMessage)

        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertEquals(errorMessage, state.errorMessage)
        assertTrue { !state.loading }
    }

    @Test
    fun `item click triggers effect`() = runTest {
        whenever(mockRepo.getCountries()).thenReturn(mockCountries)

        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        testDispatcher.scheduler.runCurrent()

        val country = mockCountries.first()
        viewModel.onItemClick(country)
        testDispatcher.scheduler.runCurrent()

    }

}