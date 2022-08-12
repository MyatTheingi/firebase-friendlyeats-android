package com.crenovert.friendlyeats.util

import android.content.Context
import com.crenovert.friendlyeats.R
import com.crenovert.friendlyeats.model.Restaurant
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Utilities for Restaurants.
 */
object RestaurantUtil {
    private const val TAG = "RestaurantUtil"
    private val EXECUTOR = ThreadPoolExecutor(
        2, 4, 60,
        TimeUnit.SECONDS, LinkedBlockingQueue()
    )
    private const val RESTAURANT_URL_FMT =
        "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png"
    private const val MAX_IMAGE_NUM = 22
    private val NAME_FIRST_WORDS = arrayOf(
        "Foo",
        "Bar",
        "Baz",
        "Qux",
        "Fire",
        "Sam's",
        "World Famous",
        "Google",
        "The Best"
    )
    private val NAME_SECOND_WORDS = arrayOf(
        "Restaurant",
        "Cafe",
        "Spot",
        "Eatin' Place",
        "Eatery",
        "Drive Thru",
        "Diner"
    )

    /**
     * Create a random Restaurant POJO.
     */
    fun getRandom(context: Context): Restaurant {
        val restaurant = Restaurant()
        val random = Random()

        // Cities (first element is 'Any')
        var cities = context.resources.getStringArray(R.array.cities)
        cities = Arrays.copyOfRange(cities, 1, cities.size)

        // Categories (first element is 'Any')
        var categories = context.resources.getStringArray(R.array.categories)
        categories = categories.copyOfRange(1, categories.size)
        val prices = intArrayOf(1, 2, 3)
        restaurant.name = getRandomName(random)
        restaurant.city = getRandomString(cities, random)
        restaurant.category = getRandomString(categories, random)
        restaurant.photo = getRandomImageUrl(random)
        restaurant.price = getRandomInt(prices, random)
        restaurant.avgRating = getRandomRating(random)
        restaurant.numRatings = random.nextInt(20)
        return restaurant
    }

    /**
     * Get a random image.
     */
    private fun getRandomImageUrl(random: Random): String {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        val id = random.nextInt(MAX_IMAGE_NUM) + 1
        return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id)
    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(restaurant: Restaurant): String {
        return getPriceString(restaurant.price)
    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(priceInt: Int): String {
        return when (priceInt) {
            1 -> "$"
            2 -> "$$"
            3 -> "$$$"
            else -> "$$$"
        }
    }

    private fun getRandomRating(random: Random): Float {
        val min = 1.0f
        return min + random.nextFloat() * 4.0f
    }

    private fun getRandomName(random: Random): String {
        return (getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random))
    }

    private fun getRandomString(array: Array<String>, random: Random): String {
        val ind = random.nextInt(array.size)
        return array[ind]
    }

    private fun getRandomInt(array: IntArray, random: Random): Int {
        val ind = random.nextInt(array.size)
        return array[ind]
    }
}