package com.example.walmart.domain.usecase

import com.example.walmart.domain.model.Country

class SearchCountryUseCase {
    operator fun invoke(
        countries: List<Country>,
        query: String
    ): List<Country> {
        if (query.isBlank()) return countries

        return countries.filter { country ->
            country.name.contains(query, ignoreCase = true) ||
                    country.region.equals(query, ignoreCase = true) // Use exact region match
        }
    }
}
