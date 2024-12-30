package es.uma.quiziosity.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
            checkLoginStatusAndProceed()
        }

        binding.imageButtonMulti.setOnClickListener {
            showNameInputDialog()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkLoginStatusAndProceed() {
        val isLoggedIn = UserUtils.isUserLoggedIn()

        if (isLoggedIn) {
            startGame(false)
        } else {
            showLoginConfirmationDialog()
        }
    }

    private fun showLoginConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.user_not_logged_in_title)
            .setMessage(R.string.user_not_logged_in_message)
            .setPositiveButton(R.string.goto_login) { _: DialogInterface, _: Int ->
                findNavController().navigate(R.id.nav_login)
            }
            .setNegativeButton(R.string.play_as_guest) { _: DialogInterface, _: Int ->
                startGame(false)
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

    private fun showNameInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_name_input, null)
        val editTextPlayer1 = dialogLayout.findViewById<EditText>(R.id.editTextNamePlayer1)
        val editTextPlayer2 = dialogLayout.findViewById<EditText>(R.id.editTextNamePlayer2)

        builder.setView(dialogLayout)
        builder.setTitle(R.string.enter_names)
        builder.setPositiveButton(R.string.start_game) { _: DialogInterface, _: Int ->
            val playerName1 = editTextPlayer1.text.toString()
            val playerName2 = editTextPlayer2.text.toString()
            if (playerName1.isNotEmpty() && playerName2.isNotEmpty()) {
                QuiziosityApp.getSharedPreferences()
                    .edit()
                    .putString("player_name_1", playerName1)
                    .putString("player_name_2", playerName2)
                    .apply()
                startGame(isMultiplayer = true)
            } else {
                Toast.makeText(requireContext(), R.string.names_required, Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.go_back) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.show()
    }
}
