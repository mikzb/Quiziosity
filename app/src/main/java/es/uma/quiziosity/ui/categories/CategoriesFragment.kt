// CategoriesFragment.kt
package es.uma.quiziosity.ui.categories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import es.uma.quiziosity.GameActivity
import es.uma.quiziosity.MultiPlayerActivity
import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.R
import es.uma.quiziosity.SinglePlayerActivity
import es.uma.quiziosity.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        // Initialize the ListView adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        binding.categoriesListView.adapter = adapter
        binding.categoriesListView.choiceMode = android.widget.ListView.CHOICE_MODE_MULTIPLE

        // Set a button click listener to check/uncheck all categories
        binding.allButton.setOnClickListener {
            toggleCheckAllCategories()
        }

        // Set a button click listener to start the quiz
        binding.startQuizButton.setOnClickListener {
            startQuiz()
        }
    }

    private fun observeViewModel() {
        // Observe the categories LiveData
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories == null) {
                // Handle the error case (e.g., show a Toast)
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
            } else {
                // Update the adapter with the category names
                adapter.clear()
                adapter.addAll(categories.keys.map { getString(it) })
            }
        }
    }

    private fun toggleCheckAllCategories() {
        val allChecked = (0 until binding.categoriesListView.count).all { binding.categoriesListView.isItemChecked(it) }
        for (i in 0 until binding.categoriesListView.count) {
            binding.categoriesListView.setItemChecked(i, !allChecked)
        }
    }

    private fun startQuiz() {
        val selectedCategories = (0 until binding.categoriesListView.count)
            .filter { binding.categoriesListView.isItemChecked(it) }
            .map { adapter.getItem(it) }

        if (selectedCategories.isEmpty()) {
            // Show a dialog if no category is selected
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.no_category_selected))
                .setMessage(getString(R.string.NoCategorySelectedDialog))
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                    // Start the GameActivity
                    startGameActivity()
                }
                .setNegativeButton(getString(R.string.go_back)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            // Save selected categories to SharedPreferences
            val sharedPreferences = QuiziosityApp.getSharedPreferences()
            sharedPreferences.edit().putStringSet("categories", selectedCategories.toSet()).apply()

            startGameActivity()
        }
    }

    private fun startGameActivity() {
        val sharedPreferences = QuiziosityApp.getSharedPreferences()
        val intent = if (sharedPreferences.getBoolean("multiplayer", false)) {
            Intent(requireContext(), MultiPlayerActivity::class.java)
        } else {
            Intent(requireContext(), SinglePlayerActivity::class.java)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}