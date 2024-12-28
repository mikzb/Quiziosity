package es.uma.quiziosity.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        // Set a button click listener to show selected categories
        binding.showSelectedButton.setOnClickListener {
            showSelectedCategories()
        }

        // Set a button click listener to check all categories
        binding.allButton.setOnClickListener {
            checkAllCategories()
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

    private fun showSelectedCategories() {
        val selectedCategories = mutableListOf<String>()
        for (i in 0 until binding.categoriesListView.count) {
            if (binding.categoriesListView.isItemChecked(i)) {
                val categoryName = adapter.getItem(i)
                val categoryValue = viewModel.categories.value?.get(resources.getIdentifier(categoryName, "string", requireContext().packageName))
                if (categoryValue != null) {
                    selectedCategories.add(categoryValue)
                }
            }
        }
        Toast.makeText(requireContext(), "Selected: ${selectedCategories.joinToString(", ")}", Toast.LENGTH_SHORT).show()
    }

    private fun checkAllCategories() {
        for (i in 0 until binding.categoriesListView.count) {
            binding.categoriesListView.setItemChecked(i, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}