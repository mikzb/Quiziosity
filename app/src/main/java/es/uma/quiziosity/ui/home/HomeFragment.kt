// HomeFragment.kt
package es.uma.quiziosity.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.R
import es.uma.quiziosity.databinding.FragmentHomeBinding
import es.uma.quiziosity.utils.UserUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.imageButtonSolo.setOnClickListener {
            if (!UserUtils.isUserLoggedIn()) {
                showLoginDialog()
            } else {
                QuiziosityApp.getSharedPreferences().edit().putBoolean("multiplayer", false).apply()
                findNavController().navigate(R.id.action_nav_home_to_nav_categories)
            }
        }

        binding.imageButtonMulti.setOnClickListener {
            QuiziosityApp.getSharedPreferences().edit().putBoolean("multiplayer", true).apply()
            findNavController().navigate(R.id.action_nav_home_to_nav_categories)
        }

        return root
    }

    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Not Logged In")
        builder.setMessage("Since you are not logged in, the score won't be saved. Do you want to continue?")
        builder.setPositiveButton("Continue") { dialog, _ ->
            dialog.dismiss()
            QuiziosityApp.getSharedPreferences().edit().putBoolean("multiplayer", false).apply()
            findNavController().navigate(R.id.action_nav_home_to_nav_categories)
        }
        builder.setNegativeButton("Log In") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_nav_home_to_nav_login)
        }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}