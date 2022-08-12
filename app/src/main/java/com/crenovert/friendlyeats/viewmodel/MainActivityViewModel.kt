package com.crenovert.friendlyeats.viewmodel

import androidx.lifecycle.ViewModel
import com.crenovert.friendlyeats.Filters

/**
 * ViewModel for [com.crenovert.friendlyeats.MainActivity].
 */
class MainActivityViewModel : ViewModel() {
    var isSigningIn = false
    private var mFilters: Filters

    var filters: Filters
        get() = mFilters
        set(mFilters) {
            this.mFilters = mFilters
        }

    init {
        mFilters = Filters.default
    }
}
