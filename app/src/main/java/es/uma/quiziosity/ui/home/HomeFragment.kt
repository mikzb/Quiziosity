package es.uma.quiziosity.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
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
            checkLoginStatusAndProceed(isMultiplayer = false)
        }

        binding.imageButtonMulti.setOnClickListener {
            checkLoginStatusAndProceed(isMultiplayer = true)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkLoginStatusAndProceed(isMultiplayer: Boolean) {
        val isLoggedIn = QuiziosityApp.getSharedPreferences()
            .getBoolean("isLoggedIn", false) // Replace "isLoggedIn" with your actual preference key

        if (isLoggedIn) {
            startGame(isMultiplayer)
        } else {
            showLoginConfirmationDialog(isMultiplayer)
        }
    }

    private fun showLoginConfirmationDialog(isMultiplayer: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.user_not_logged_in_title)  //TODO: translate strings
            .setMessage(R.string.user_not_logged_in_message)
            .setPositiveButton(R.string.goto_login) { _: DialogInterface, _: Int ->
                findNavController().navigate(R.id.nav_login)
            }
            .setNegativeButton(R.string.play_as_guest) { _: DialogInterface, _: Int ->
                startGame(isMultiplayer)
            }
            .show()
    }

    private fun startGame(isMultiplayer: Boolean) {
        QuiziosityApp.getSharedPreferences()
            .edit()
            .putBoolean("multiplayer", isMultiplayer)
            .apply()
        findNavController().navigate(R.id.action_nav_home_to_nav_categories)
    }
}
