package es.uma.quiziosity.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.uma.quiziosity.R

class CategoriesViewModel : ViewModel() {
    private val _categories = MutableLiveData<Map<Int, String>>()
    val categories: LiveData<Map<Int, String>> get() = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val predefinedCategories = mapOf(
            R.string.category_arts_literature to "arts_and_literature",
            R.string.category_film_tv to "film_and_tv",
            R.string.category_food_drink to "food_and_drink",
            R.string.category_general_knowledge to "general_knowledge",
            R.string.category_geography to "geography",
            R.string.category_history to "history",
            R.string.category_music to "music",
            R.string.category_science to "science",
            R.string.category_society_culture to "society_and_culture",
            R.string.category_sport_leisure to "sport_and_leisure",
            R.string.category_any to "any"
        )
        _categories.value = predefinedCategories
    }
}