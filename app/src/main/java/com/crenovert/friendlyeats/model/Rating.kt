package com.crenovert.friendlyeats.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


/**
 * Model POJO for a rating.
 */
class Rating {
    var userId: String? = null
    var userName: String? = null
    var rating: Float = 0.0F
    var text: String? = null

    @ServerTimestamp
    var timestamp: Date? = null

    constructor() {}
    constructor(user: FirebaseUser, rating: Float, text: String?) {
        userId = user.uid
        userName = user.displayName
        if (TextUtils.isEmpty(userName)) {
            userName = user.email
        }
        this.rating = rating
        this.text = text
    }
}
