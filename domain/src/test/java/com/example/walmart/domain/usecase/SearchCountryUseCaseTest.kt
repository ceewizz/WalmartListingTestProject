package com.example.walmart.domain.usecase

import com.example.walmart.domain.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class SearchCountryUseCaseTest(
    private val testName: String,  // Test name for clarity
    private val inputList: List<Country>,
    private val query: String?,  // Nullable query for testing
    private val expectedResult: List<Country>
) {
    private val useCase = SearchCountryUseCase()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testData(): Collection<Array<Any?>> = listOf(
            // Test case: Empty query returns all countries
            arrayOf(
                "Empty query returns all countries",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                "",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                )
            ),
            // Test case: Search by country name (case insensitive)
            arrayOf(
                "Search by country name",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                "usa",
                listOf(Country("USA", "North America", "US", "Washington"))
            ),
            // Test case: Search by region
            arrayOf(
                "Search by region",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Brazil", "South America", "BR", "Brasilia")
                ),
                "south",
                listOf(Country("Brazil", "South America", "BR", "Brasilia"))
            ),
            // Test case: Case-insensitive search for Canada
            arrayOf(
                "Case-insensitive search for Canada",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                "CANADA",
                listOf(Country("Canada", "North America", "CA", "Ottawa"))
            ),

            // Test case: Partial name match
            arrayOf(
                "Partial name match",
                listOf(
                    Country("United States", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                "states",
                listOf(Country("United States", "North America", "US", "Washington"))
            ),
            // Test case: No match for Mexico
            arrayOf(
                "No matching results for Mexico",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                "Mexico",
                emptyList<Country>()
            ),
            // Test case: Null query returns all countries
            arrayOf(
                "Null query returns all countries",
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                ),
                null,
                listOf(
                    Country("USA", "North America", "US", "Washington"),
                    Country("Canada", "North America", "CA", "Ottawa")
                )
            ),
            // Test case: Empty input list
            arrayOf(
                "Empty input list",
                emptyList<Country>(),
                "test",
                emptyList<Country>()
            )
        )
    }

    @Test
    fun `search countries returns correct results`() {
        // Print input and query for debugging
        println("Running test: $testName")
        println("Input list: $inputList")
        println("Query: $query")

        // Handle null query with orEmpty
        val result = useCase(inputList, query.orEmpty())

        // Assert the expected result matches the actual result
        assertEquals(expectedResult, result)

        // Print results for further debugging if needed
        println("Expected result: $expectedResult")
        println("Actual result: $result")
    }
}
