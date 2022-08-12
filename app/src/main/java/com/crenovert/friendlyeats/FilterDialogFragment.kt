package com.crenovert.friendlyeats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.crenovert.friendlyeats.databinding.DialogFiltersBinding
import com.crenovert.friendlyeats.model.Restaurant
import com.google.firebase.firestore.Query

/**
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment() {
    private var _binding: DialogFiltersBinding? = null
    private val binding get() = _binding!!

    interface FilterListener {
        fun onFilter(filters: Filters)
    }

    private var mFilterListener: FilterListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFiltersBinding.inflate(inflater)
        binding.buttonSearch.setOnClickListener {
            mFilterListener?.onFilter(filters)
            dismiss()
        }
        binding.buttonCancel.setOnClickListener { dismiss() }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            mFilterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val selectedCategory: String?
        get() {
            val selected = binding.spinnerCategory.selectedItem as String
            return if (getString(R.string.value_any_category) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedCity: String?
        get() {
            val selected = binding.spinnerCity.selectedItem as String
            return if (getString(R.string.value_any_city) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedPrice: Int
        get() {
            return when (binding.spinnerPrice.selectedItem as String) {
                getString(R.string.price_1) -> 1
                getString(R.string.price_2) -> 2
                getString(R.string.price_3) -> 3
                else -> -1
            }
        }
    private val selectedSortBy: String?
        get() {
            return when (binding.spinnerSort.selectedItem as String) {
                getString(R.string.sort_by_rating) -> Restaurant.FIELD_AVG_RATING
                getString(R.string.sort_by_price) -> Restaurant.FIELD_PRICE
                getString(R.string.sort_by_popularity) -> Restaurant.FIELD_POPULARITY
                else -> null
            }

        }
    private val sortDirection: Query.Direction?
        get() {
            return when (binding.spinnerSort.selectedItem as String) {
                getString(R.string.sort_by_rating) -> Query.Direction.DESCENDING
                getString(R.string.sort_by_price) -> Query.Direction.ASCENDING
                getString(R.string.sort_by_popularity) -> Query.Direction.DESCENDING
                else -> null
            }
        }

    fun resetFilters() {
        binding.spinnerCategory.setSelection(0)
        binding.spinnerCity.setSelection(0)
        binding.spinnerPrice.setSelection(0)
        binding.spinnerSort.setSelection(0)
    }

    private val filters: Filters
        get() {
            val filters = Filters()
            filters.category = selectedCategory
            filters.city = selectedCity
            filters.price = selectedPrice
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection

            return filters
        }

    companion object {
        const val TAG = "FilterDialog"
    }
}