package com.crenovert.friendlyeats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.crenovert.friendlyeats.adapter.RatingAdapter
import com.crenovert.friendlyeats.databinding.ActivityRestaurantDetailBinding
import com.crenovert.friendlyeats.model.Rating
import com.crenovert.friendlyeats.model.Restaurant
import com.crenovert.friendlyeats.util.FirebaseUtil
import com.crenovert.friendlyeats.util.RestaurantUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*


class RestaurantDetailActivity : AppCompatActivity(), View.OnClickListener,
    EventListener<DocumentSnapshot>, RatingDialogFragment.RatingListener {
    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var mRatingDialog: RatingDialogFragment
    private lateinit var mRatingAdapter: RatingAdapter

    private var mFirestore: FirebaseFirestore? = null
    private var mRestaurantRef: DocumentReference? = null
    private var mRestaurantRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.restaurantButtonBack.setOnClickListener(this)
        binding.fabShowRatingDialog.setOnClickListener(this)

        // Get restaurant ID from extras
        val restaurantId = intent.extras!!.getString(KEY_RESTAURANT_ID)
            ?: throw IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID)

        // Initialize Firestore
        mFirestore = FirebaseUtil.firestore

        // Get reference to the restaurant
        mRestaurantRef = mFirestore!!.collection("restaurants").document(restaurantId)

        // Get ratings
        val ratingsQuery = mRestaurantRef!!
            .collection("ratings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        // RecyclerView
        mRatingAdapter = object : RatingAdapter(ratingsQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    binding.recyclerRatings.visibility = View.GONE
                    binding.viewEmptyRatings.visibility = View.VISIBLE
                } else {
                    binding.recyclerRatings.visibility = View.VISIBLE
                    binding.viewEmptyRatings.visibility = View.GONE
                }
            }
        }
        binding.recyclerRatings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mRatingAdapter

        }

        mRatingDialog = RatingDialogFragment()
    }

    public override fun onStart() {
        super.onStart()
        mRatingAdapter.startListening()
        mRestaurantRegistration = mRestaurantRef!!.addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()
        mRatingAdapter.stopListening()
        if (mRestaurantRegistration != null) {
            mRestaurantRegistration!!.remove()
            mRestaurantRegistration = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.restaurant_button_back ->  onBackPressed()
            R.id.fab_show_rating_dialog -> mRatingDialog.show(supportFragmentManager, RatingDialogFragment.TAG)
        }
    }

    private fun addRating(restaurantRef: DocumentReference?, rating: Rating): Task<Void> {
        // TODO(developer): Implement
        return Tasks.forException(Exception("not yet implemented"))
    }

    /**
     * Listener for the Restaurant document ([.mRestaurantRef]).
     */
    override fun onEvent(value: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e)
            return
        }
        onRestaurantLoaded(value?.toObject(Restaurant::class.java))
    }

    private fun onRestaurantLoaded(restaurant: Restaurant?) {

        restaurant?.let {
            binding.restaurantName.text = restaurant.name
            binding.restaurantRating.rating = restaurant.avgRating
            binding.restaurantNumRatings.text =
                getString(R.string.fmt_num_ratings, restaurant.numRatings)
            binding.restaurantCity.text = restaurant.city
            binding.restaurantCategory.text = restaurant.category
            binding.restaurantPrice.text = RestaurantUtil.getPriceString(restaurant)

            // Background image
            Glide.with(this)
                .load(restaurant.photo)
                .into(binding.restaurantImage)
        }
    }


    override fun onRating(rating: Rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mRestaurantRef, rating)
            .addOnSuccessListener(this) {
                Log.d(TAG, "Rating added")

                // Hide keyboard and scroll to top
                hideKeyboard()
                binding.recyclerRatings.smoothScrollToPosition(0)
            }
            .addOnFailureListener(this) { e ->
                Log.w(TAG, "Add rating failed", e)

                // Show failure message and hide keyboard
                hideKeyboard()
                Snackbar.make(
                    findViewById(android.R.id.content), "Failed to add rating",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        private const val TAG = "RestaurantDetail"
        const val KEY_RESTAURANT_ID = "key_restaurant_id"
    }

}
