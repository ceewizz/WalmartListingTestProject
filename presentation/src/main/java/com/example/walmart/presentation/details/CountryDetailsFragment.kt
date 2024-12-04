package com.example.walmart.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.walmart.domain.di.ServiceProvider.get
import com.example.walmart.presentation.R
import com.example.walmart.presentation.databinding.CountryDetailsFragmentBinding
import com.example.walmart.presentation.ext.repeatOnStart
import kotlinx.coroutines.flow.collectLatest

class CountryDetailsFragment : Fragment(R.layout.country_details_fragment) {

    // Option 1: Mutable backing field for testing
    private var _viewModel: CountryDetailsViewModel? = null
    var viewModel: CountryDetailsViewModel
        get() = _viewModel ?: viewModelDelegate
        set(value) {
            _viewModel = value
        }

    // Original view models delegate
    private val viewModelDelegate by viewModels<CountryDetailsViewModel> {
        get<CountryDetailsViewModelFactory>()
    }

    private var viewBinding: CountryDetailsFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = CountryDetailsFragmentBinding.bind(view)

        // Use the getter to ensure we always have a ViewModel
        val currentViewModel = viewModel

        repeatOnStart { currentViewModel.state.collectLatest(::renderState) }
        repeatOnStart { currentViewModel.effectFlow.collectLatest(::onEffect) }

        viewBinding?.apply {
            swipeRefreshLayout.setOnRefreshListener { currentViewModel.reloadList() }
            actionBar.setNavigationOnClickListener { currentViewModel.onBackClicked() }
        }
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun renderState(state: CountryDetailsViewModel.State) {
        viewBinding?.apply {
            swipeRefreshLayout.isRefreshing = state.loading
            errorMessage.text = state.errorMessage
            state.country?.run {
                with(countryItemLayout) {
                    nameWithRegionView.text =
                        root.context.getString(R.string.format_name_with_region, name, region)
                    codeView.text = code
                    capitalView.text = capital
                }
            }
        }
    }

    private fun onEffect(state: CountryDetailsViewModel.Effect) {
        when (state) {
            CountryDetailsViewModel.Effect.OnBack -> findNavController().popBackStack()
        }
    }

    // Companion object for creating fragment with arguments
    companion object {
        private const val ARG_COUNTRY_CODE = "country_code"

        fun newInstance(countryCode: String): CountryDetailsFragment {
            return CountryDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COUNTRY_CODE, countryCode)
                }
            }
        }
    }
}