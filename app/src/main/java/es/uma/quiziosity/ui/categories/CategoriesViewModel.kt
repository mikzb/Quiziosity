package es.uma.quiziosity.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uma.quiziosity.data.repository.TriviaRepository
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<String>?>()
    val categories: LiveData<List<String>?> get() = _categories

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = TriviaRepository.fetchCategories()
                _categories.value = result // Assign null if the result is null
            } catch (e: Exception) {
                _categories.value = null // Treat exceptions as null data, ez to handle in the UI
            }
        }
    }
}
