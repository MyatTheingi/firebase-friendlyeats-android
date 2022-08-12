package com.crenovert.friendlyeats

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crenovert.friendlyeats.adapter.RestaurantAdapter
import com.crenovert.friendlyeats.databinding.ActivityMainBinding
import com.crenovert.friendlyeats.util.FirebaseUtil
import com.crenovert.friendlyeats.util.RestaurantUtil.getRandom
import com.crenovert.friendlyeats.viewmodel.MainActivityViewModel
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity(), View.OnClickListener,
    FilterDialogFragment.FilterListener, RestaurantAdapter.OnRestaurantSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var mFirestore: FirebaseFirestore? = null
    private var mQuery: Query? = null

    private lateinit var mFilterDialog: FilterDialogFragment
    private lateinit var mAdapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // View model
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Filter Dialog
        // Apply filters
        mFilterDialog = FilterDialogFragment()

        binding.filterBar.setOnClickListener(this)
        binding.buttonClearFilter.setOnClickListener(this)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        // Initialize Firestore and the main RecyclerView
        mFirestore = FirebaseUtil.firestore

        // Get the 50 highest rated restaurants
        mQuery = mFirestore!!.collection("restaurants")
            .orderBy("avgRating", Query.Direction.DESCENDING)
            .limit(LIMIT)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter =
            object : RestaurantAdapter(mQuery, this@MainActivity) {
                override fun onDataChanged() {
                    // Show/hide content if the query returns empty.
                    if (itemCount == 0) {
                        binding.recyclerRestaurants.visibility = View.GONE
                        binding.viewEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerRestaurants.visibility = View.VISIBLE
                        binding.viewEmpty.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException?) {
                    // Show a snackbar on errors
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        binding.recyclerRestaurants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    public override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn()
            return
        }

        // Apply filters
        onFilter(viewModel.filters)

        // Start listening for Firestore updates
        mAdapter.startListening()

    }

    public override fun onStop() {
        super.onStop()
        mAdapter.stopListening()

    }

    private fun onAddItemsClicked() {
        // Get a reference to the restaurants collection
        val restaurants = mFirestore!!.collection("restaurants")
        for (i in 0..9) {
            // Get a random Restaurant POJO
            val restaurant = getRandom(this)

            // Add a new document to the restaurants collection
            restaurants.add(restaurant)
        }
    }

    override fun onFilter(filters: Filters) {
        // Construct basic query
        var query: Query = mFirestore!!.collection("restaurants")


        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.category)
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.city)
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.price)
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.sortBy!!, filters.sortDirection!!)
        }

        // Limit items
        query = query.limit(LIMIT)

        // Update the query
        mQuery = query
        mAdapter.setQuery(query)

        // Set header
        binding.textCurrentSearch.text = Html.fromHtml(filters.getSearchDescription(this))
        binding.textCurrentSortBy.text = filters.getOrderDescription(this)

        // Save filters
        viewModel.filters = filters

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                FirebaseUtil.authUI!!.signOut(this)
                startSignIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            viewModel.isSigningIn = false
            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn()
            }
        }
    }


    override fun onRestaurantSelected(restaurant: DocumentSnapshot) {
        // Go to the details page for the selected restaurant
        val intent = Intent(this, RestaurantDetailActivity::class.java)
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.id)
        startActivity(intent)
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSigningIn && FirebaseUtil.auth?.currentUser == null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent: Intent = FirebaseUtil.authUI!!
            .createSignInIntentBuilder()
            .setAvailableProviders(
                listOf(
                    EmailBuilder().build()
                )
            )
            .setIsSmartLockEnabled(false)
            .build()
        startActivityForResult(intent, RC_SIGN_IN)
        viewModel.isSigningIn = true
    }

    private fun showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
        private const val LIMIT = 50L
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.filter_bar -> mFilterDialog.show(supportFragmentManager, FilterDialogFragment.TAG)

            R.id.button_clear_filter -> {
                mFilterDialog.resetFilters()
                onFilter(Filters.default)
            }

        }

    }
}