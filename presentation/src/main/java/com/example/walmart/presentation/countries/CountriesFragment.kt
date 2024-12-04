package com.example.walmart.presentation.countries

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.example.walmart.domain.di.ServiceProvider.get
import com.example.walmart.presentation.R
import com.example.walmart.presentation.databinding.CountriesFragmentBinding
import com.example.walmart.presentation.details.CountryDetailsArg
import com.example.walmart.presentation.ext.repeatOnStart
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class CountriesFragment : Fragment(R.layout.countries_fragment) {

    // Use a backing property to hold the ViewModel
    private var _viewModel: CountriesViewModel? = null
    private val viewModel: CountriesViewModel
        get() = _viewModel ?: viewModels<CountriesViewModel> { get<CountriesViewModelFactory>() }.value

    private var viewBinding: CountriesFragmentBinding? = null
    private lateinit var adapter: CountriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = CountriesFragmentBinding.bind(view)
        initViews()
        repeatOnStart { viewModel.state.collectLatest(::renderState) }
        repeatOnStart { viewModel.effectFlow.collectLatest(::onEffect) }
    }

    private fun initViews() {
        adapter = CountriesAdapter(viewModel::onItemClick)
        adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
        viewBinding?.apply {
            swipeRefreshLayout.setOnRefreshListener { viewModel.reloadList() }
            countryRv.adapter = adapter
            countryRv.addItemDecoration(
                CountriesItemDecoration(resources.getDimensionPixelSize(R.dimen.item_inner_space))
            )
            (actionBar.menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel.search(query.orEmpty())
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.search(newText.orEmpty())
                        return true
                    }
                }
            )
        }
    }

    private fun onEffect(effect: CountriesViewModel.Effect) {
        when (effect) {
            is CountriesViewModel.Effect.OpenDetails -> {
                findNavController().navigate(
                    resId = R.id.countryDetailsFragment,
                    args = bundleOf(CountryDetailsArg.COUNTRY_CODE to effect.countryCode)
                )
            }
        }
    }

    private fun renderState(state: CountriesViewModel.State) {
        viewBinding?.apply {
            swipeRefreshLayout.isRefreshing = state.loading
            adapter.submitList(state.items)
            state.errorMessage?.let { showError(it) }
        }
    }

    private fun showError(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) { viewModel.reloadList() }
            .show()
    }

    // New method to set the ViewModel for testing
    fun setViewModel(viewModel: CountriesViewModel) {
        _viewModel = viewModel
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }
}
