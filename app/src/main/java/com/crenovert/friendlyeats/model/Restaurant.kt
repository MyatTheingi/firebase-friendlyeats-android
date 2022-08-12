package com.crenovert.friendlyeats.model

import com.google.firebase.firestore.IgnoreExtraProperties


/**
 * Restaurant POJO.
 */
@IgnoreExtraProperties
class Restaurant {
    var name: String? = null
    var city: String? = null
    var category: String? = null
    var photo: String? = null
    var price = 0
    var numRatings = 0
    var avgRating = 0f

    constructor() {}
    constructor(
        name: String?, city: String?, category: String?, photo: String?,
        price: Int, numRatings: Int, avgRating: Float
    ) {
        this.name = name
        this.city = city
        this.category = category
        this.photo = photo
        this.price = price
        this.numRatings = numRatings
        this.avgRating = avgRating
    }

    companion object {
        const val FIELD_CITY = "city"
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_AVG_RATING = "avgRating"
    }
}
