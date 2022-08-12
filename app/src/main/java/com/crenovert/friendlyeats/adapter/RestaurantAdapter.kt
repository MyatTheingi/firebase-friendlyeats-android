package com.crenovert.friendlyeats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crenovert.friendlyeats.R
import com.crenovert.friendlyeats.databinding.ItemRestaurantBinding
import com.crenovert.friendlyeats.model.Restaurant
import com.crenovert.friendlyeats.util.RestaurantUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class RestaurantAdapter(query: Query?) :
    FirestoreAdapter<RestaurantAdapter.ViewHolder>(query) {

    private lateinit var mListener: OnRestaurantSelectedListener

    constructor(query: Query?, listener: OnRestaurantSelectedListener) : this(query) {
        mListener = listener
    }

    interface OnRestaurantSelectedListener {
        fun onRestaurantSelected(restaurant: DocumentSnapshot)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(snapshot: DocumentSnapshot) {
            val restaurant = snapshot.toObject(Restaurant::class.java)
            restaurant?.let {

                // Load image
                Glide.with(itemView)
                    .load(restaurant.photo)
                    .into(binding.restaurantItemImage)

                binding.restaurantItemName.text = restaurant.name
                binding.restaurantItemRating.rating = restaurant.avgRating as Float
                binding.restaurantItemCity.text = restaurant.city
                binding.restaurantItemCategory.text = restaurant.category
                binding.restaurantItemNumRatings.text = itemView.resources.getString(
                    R.string.fmt_num_ratings,
                    restaurant.numRatings
                )
                binding.restaurantItemPrice.text = RestaurantUtil.getPriceString(restaurant!!)

                // Click listener
                itemView.setOnClickListener { mListener.onRestaurantSelected(snapshot) }
            }
        }
    }
}