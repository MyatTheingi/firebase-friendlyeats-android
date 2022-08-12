package com.crenovert.friendlyeats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.crenovert.friendlyeats.databinding.DialogRatingBinding
import com.crenovert.friendlyeats.model.Rating
import com.crenovert.friendlyeats.util.FirebaseUtil


/**
 * Dialog Fragment containing rating form.
 */
class RatingDialogFragment : DialogFragment() {
    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!


    interface RatingListener {
        fun onRating(rating: Rating)
    }

    private var mRatingListener: RatingListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRatingBinding.inflate(inflater)
        binding.restaurantFormButton.setOnClickListener {
            val rating = Rating(
                FirebaseUtil.auth!!.currentUser!!,
                binding.restaurantFormRating.rating,
                binding.restaurantFormText.text.toString()
            )
            mRatingListener?.onRating(rating)
            dismiss()
        }
        binding.restaurantFormCancel.setOnClickListener { dismiss() }
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RatingListener) {
            mRatingListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "RatingDialog"
    }
}
