package es.uma.quiziosity.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import es.uma.quiziosity.databinding.FragmentCategoriesBinding
import es.uma.quiziosity.ui.slideshow.SlideshowViewModel


class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this)[SlideshowViewModel::class.java]

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textCategories
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}