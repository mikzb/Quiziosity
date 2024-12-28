package es.uma.quiziosity.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import es.uma.quiziosity.databinding.FragmentCategoriesBinding
import es.uma.quiziosity.ui.slideshow.SlideshowViewModel

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

        // Trigger loading of categories
        viewModel.loadCategories()

        return binding.root
    }

    private fun setupUI() {
        // Initialize the ListView adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        binding.categoriesListView.adapter = adapter
    }

    private fun observeViewModel() {
        // Observe the categories LiveData
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories == null) {
                // Handle the error case (e.g., show a Toast)
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
            } else {
                // Update the adapter with the categories
                adapter.clear()
                adapter.addAll(categories)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
