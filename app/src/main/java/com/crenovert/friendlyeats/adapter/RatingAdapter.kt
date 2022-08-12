package com.crenovert.friendlyeats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crenovert.friendlyeats.databinding.ItemRatingBinding
import com.crenovert.friendlyeats.model.Rating
import com.google.firebase.firestore.Query


/**
 * RecyclerView adapter for a bunch of Ratings.
 */
open class RatingAdapter(query: Query) :
    FirestoreAdapter<RatingAdapter.ViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRatingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position).toObject(Rating::class.java))
    }

    inner class ViewHolder(private val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: Rating?) {
            rating?.let {
                binding.ratingItemName.text = rating.userName
                binding.ratingItemRating.rating = rating.rating
                binding.ratingItemText.text = rating.text
            }
        }
    }
}
